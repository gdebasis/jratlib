/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rationalarithmetic;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 *
 * @author dganguly
 */

class DocIdAndWeight {
    int docId;
    Rational weight;

    public DocIdAndWeight(int docId, Rational weight) {
        this.docId = docId;
        this.weight = weight;
    }
}

class RationalComparatorByDiff implements Comparator<DocIdAndWeight> {

    @Override
    public int compare(DocIdAndWeight a, DocIdAndWeight b) {        
        // a number a=p/q is higher than b=x/y if p/q-x/y > 0
        int num = a.weight.num*b.weight.denom - b.weight.num*a.weight.denom;
        return num > 0? -1 : num==0? 0 : 1; // descending
    }
}


class InvListRcd {
    public static int MAX_SIZE;  // maximum number of entries in a postings aka max number of docs a term can occur in
    public static DocIdAndWeight[] tfArray; // list of relative term frequencies, i.e., tf/doc_len
    public static int EOB;  // the buffer end marker
    
    int start_buff;
    int df; // document frequency of this term, or in other words the size of the buffer tfArray buffer for this term
    
    public static void initInvRcds(int maxBuffSize) {
        InvListRcd.MAX_SIZE = maxBuffSize;
        tfArray = new DocIdAndWeight[MAX_SIZE];
        EOB = 0;
    }
    
    public InvListRcd(int maxDocId, int maxTf, int df) {
        this.df = df;
        start_buff = EOB;
        
        // simulate the tf values of df entries, i.e. tf (t, d) for all d's, t the current term
        for (int i=0; i < df; i++) {            
            int docId = (int)(Math.random() * maxDocId);
            Rational w = Rational.initRandom(maxTf, maxTf<<2);  // tf/doc_len < 1/4
            
            if (EOB < MAX_SIZE)
                tfArray[EOB] = new DocIdAndWeight(docId, w);
            
            EOB++;
        }
    }
}

// Comprises of multiple inv-list records, one for each term
class Index {
    int maxTf;
    int maxDf;
    int vocabSize;
    int numDocs;
    InvListRcd[] postings;
    boolean status;
    
    static final int MAXBUFFSIZE = 5000000;
    
    Index(int numDocs, int numTerms, int maxDf, int maxTf) {
        this.numDocs = numDocs;
        this.vocabSize = numTerms;
        this.maxTf = maxTf;
        this.maxDf = maxDf;
        
        postings = new InvListRcd[numTerms];
        
        InvListRcd.initInvRcds(MAXBUFFSIZE);
        status = createPostings();
    }

    boolean getStatus() { return status; }
    
    int getVocabSize() { return vocabSize; }
    
    final boolean createPostings() {
        final int SEED = 23456;
        
        Random randomizer = new Random(SEED);
        float x;
        int df;
        final int MIN_FREQ = 5;
        final int MAX_FREQ = (maxDf>>8) - MIN_FREQ;
        
        for (int i=0; i < vocabSize; i++) {
            // generate a random df (following the power law) for this term...
            df = MIN_FREQ + randomizer.nextInt(MAX_FREQ);            
            postings[i] = new InvListRcd(numDocs, maxTf, df);
            if (InvListRcd.EOB >= InvListRcd.MAX_SIZE) {
                return false;
            }
        }
        return true;
    }
}

class Query {
    static final int MAX_QLEN = 5;
    static final int SEED = 54321;
    static Random randomQryConstructor = new Random(SEED);
    
    int nQueryTerms;
    int[] queryTermIds;
    
    Query(int vocabSize) {
        nQueryTerms = 1 + randomQryConstructor.nextInt(MAX_QLEN);
        queryTermIds = new int[nQueryTerms];
        
        for (int i=0; i < nQueryTerms; i++) {
            queryTermIds[i] = randomQryConstructor.nextInt(vocabSize);
        }
    }
}

// Compute the standard operations (inner products) on this simulated index
public class TfIdfBenchmark {
    Index index;
    
    public TfIdfBenchmark(int numDocs, int numTerms, int maxDf, int maxTf) {
        index = new Index(numDocs, numTerms, maxDf, maxTf);
    }
    
    void accumulateScore(HashMap<Integer, DocIdAndWeight> scoreBuff, InvListRcd thisTermPostings) {
        Rational tfidf = null;
        //Rational idf = new Rational(
        //        31 - Integer.numberOfLeadingZeros(index.numDocs),
        //        31 - Integer.numberOfLeadingZeros(thisTermPostings.df));
        Rational idf = new Rational(1, thisTermPostings.df);  // represent idf as 1/df to avoid overflow of numerator
        
        for (int j=0; j < thisTermPostings.df; j++) {
            // Get the weight of this query term in a document it belongs to
            DocIdAndWeight w = InvListRcd.tfArray[thisTermPostings.start_buff + j];
            // First perform the multiplication operation
            w.weight.mul(idf); // value stored in opResult buff
            tfidf = Rational.opResult; // a pointer assignment -- faster op
            
            // Add its contribution to the score-buff
            DocIdAndWeight prevDocIdAndScore = scoreBuff.get(w.docId);
            if (prevDocIdAndScore == null) {
                prevDocIdAndScore = new DocIdAndWeight(w.docId, idf);
                scoreBuff.put(w.docId, prevDocIdAndScore);
            }
            else {
                Rational prevScore = prevDocIdAndScore.weight;
                prevScore.add(idf);
                //prevScore.mediant(idf);
                prevDocIdAndScore.weight = Rational.opResult; // updated with this term's tfidf
            }
        }
    }
    
    void simulateRetrievalForOneQuery(int qid, Query q, int nwanted) {
        
        HashMap<Integer, DocIdAndWeight> scoreBuff = new HashMap<>();
        
        for (int i=0; i < q.nQueryTerms; i++) {
            // get the corresponding postings
            InvListRcd thisTermPostings = index.postings[q.queryTermIds[i]];
            accumulateScore(scoreBuff, thisTermPostings);
        }
        
        // Sort the scores to report top 1000
        nwanted = Math.min(scoreBuff.values().size(), nwanted);
        
        List<DocIdAndWeight> sortedList = scoreBuff.values().stream()
                .sorted(new RationalComparatorByDiff())
                .collect(Collectors.toList()).subList(0, nwanted);
        
        // Debugging -- Turn the printing off
        System.out.println("Query-id\tDoc-id\tScore");
        for (DocIdAndWeight rt: sortedList) {
            System.out.println(String.format("%d\t%d\t%s", qid, rt.docId, rt.weight.toString()));
        }
    }
    
    void simulateRetrievalForAllQueries(int nQueries, int nwanted) {
        for (int i=1; i <= nQueries; i++) {
            Query q = new Query(index.getVocabSize());
            simulateRetrievalForOneQuery(i, q, nwanted);
        }
    }
    
    public static void main(String[] args) {
        final int numDocs = 500000;
        final int vocabSize = 100000;
        final int maxDf = 20000; // excluding stop-words
        final int maxTf = 50;  // somewhat related to doc len
        final int nwanted = 1000; // value of k for top-k
        final int nQueries = 2;
        
        TfIdfBenchmark benchmark = new TfIdfBenchmark(numDocs, vocabSize, maxDf, maxTf);
        if (!benchmark.index.getStatus()) {
            System.err.println("Buffer overflow! Increase buffer size...");
            return;
        }
            
        benchmark.simulateRetrievalForAllQueries(nQueries, nwanted);
    }
}
