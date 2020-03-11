/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml;

import java.util.Arrays;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author debforit
 */

// A simple linear classification using sigmoid
public class LogisticRegression {
    RealValuedVec[] train;
    RealValuedVec[] test;
    RealValuedVec[] theta;
    int numTrainingSamples;
    int dimension;
    int numClasses;
    
    static final float ALPHA = 0.01f;
    static final float MAX_EXPONENT = 15;
    static final float MAX_VALUE = (float)Math.exp(MAX_EXPONENT);
    static final float MIN_VALUE = (float)Math.exp(-MAX_EXPONENT);
    
    public LogisticRegression(RealValuedVec[] train, RealValuedVec[] test, int numClasses) {
        this.train = train;
        this.test = test;
        this.numClasses = numClasses;
        this.dimension = train[0].getDimension();
        numTrainingSamples = train.length;
        
        initParams();
    }
    
    final void initParams() { // random initialization of the parameters
        theta = new RealValuedVec[numClasses];
        for (int k=0; k < numClasses-1; k++) {
            theta[k] = new RealValuedVec(dimension, true);
            theta[k].randomInit();
        }
        theta[numClasses-1] = new RealValuedVec(dimension, true);
        Arrays.fill(theta[numClasses-1].x, 0); // theta_k is a zero vector (not learnable)
    }

    // derivative with respect to the ith instance and the kth class
    RealValuedVec sgd(int i, int k, RealValuedVec softMaxProbs, float z) throws Exception {
        int delta = train[i].oneHotEncoding[k]; // one-hots gt label (1/0)
        float p = softMaxProbs.x[k]/z;  // prediction ([0,1]).
        
        RealValuedVec gradient = new RealValuedVec(dimension);        
        for (int j=0; j < dimension; j++) {
            gradient.x[j] = ALPHA * train[i].x[j] * (delta - p); // update the theta_k vector (for the k-th class)
        }
        
        return gradient;
    }
    
    float computeLoss() throws Exception {
        float loss = 0;
        
        for (int i=0; i < numTrainingSamples; i++) {
            RealValuedVec softMaxProbs = computeSoftMax(train[i]);
            float z = softMaxProbs.getSumOfComponents();
            
            for (int k=0; k < numClasses; k++) {
                loss += train[i].oneHotEncoding[k] * Math.log(softMaxProbs.x[k]/z);
            }
        }
        return -loss;
    }
    
    RealValuedVec computeSoftMax(RealValuedVec x) throws Exception { // is a function of the current parameters
        RealValuedVec softmaxProbs = new RealValuedVec(numClasses, true);
        
        for (int k=0; k < numClasses; k++) {
            float s = theta[k].dot(x);
            if (s<=-MAX_EXPONENT)
                s = -MAX_EXPONENT;
            if (s>=MAX_EXPONENT)
                s = MAX_EXPONENT;
            float p = (float)Math.exp(s);
            softmaxProbs.x[k] = p;
        }        
        return softmaxProbs;
    }

    void epoch(int batchSize) throws Exception {
        int start = 0;
        int end = start + batchSize;
        
        while (end <= numTrainingSamples) {
            epoch(start, end);
            start = end;
            end = start + batchSize;
        }
    }
    
    void epoch(int batchStartIndex, int batchEndIndex) throws Exception {
        
        //System.out.println(String.format("Batch [%d, %d]", batchStartIndex, batchEndIndex-1));
        if (batchEndIndex > numTrainingSamples)
            batchEndIndex = numTrainingSamples;
        
        for (int k=0; k < numClasses-1; k++) {  // tie the \theta_k to all zeroes            
            RealValuedVec batchGradient = new RealValuedVec(dimension);

            for (int i=batchStartIndex; i < batchEndIndex; i++) {
                
                RealValuedVec softMaxProbs = computeSoftMax(train[i]);
                float z = softMaxProbs.getSumOfComponents();
                
                RealValuedVec gradient = sgd(i, k, softMaxProbs, z);
                batchGradient.addBy(gradient);
            }
            theta[k].addBy(batchGradient); // update theta_k
        }
    }

    void epoch() throws Exception {
        
        for (int i=0; i < numTrainingSamples; i++) {
            RealValuedVec softMaxProbs = computeSoftMax(train[i]);
            float z = softMaxProbs.getSumOfComponents();

            for (int k=0; k < numClasses-1; k++) {  // tie the \theta_k to all zeroes            
                RealValuedVec gradient = sgd(i, k, softMaxProbs, z);
                theta[k].addBy(gradient); // update theta_k
            }
        }
        
    }
    
    void runSGDEpochs(int iters) {
        try {
            for (int i=1; i <= iters; i++) {
                System.out.println(String.format("Epoch %d: Loss = %.2f", i, computeLoss()));
                epoch();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    void runBGDEpochs(int batchSize, int iters) {
        try {
            for (int i=1; i <= iters; i++) {
                System.out.println(String.format("Epoch %d: Loss = %.2f", i, computeLoss()));
                epoch(batchSize);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    void evaluate() {
        
        try {
            int numTestSamples = test.length;
            int correct = 0;
            
            for (int i=0; i < numTestSamples; i++) {
                RealValuedVec softmax = computeSoftMax(test[i]);
                float z = softmax.getSumOfComponents();
                Pair<Integer, Float> maxIndexAndValue = softmax.maxIndexAndValue();
                if (maxIndexAndValue.getLeft() == test[i].y)
                    correct++;
                System.out.println(
                    String.format(
                        "Test (%d): y=%d y_p=%d (%.4f)",
                        i, test[i].y, maxIndexAndValue.getLeft(),
                        maxIndexAndValue.getRight()/z
                    )
                );
            }
            System.out.println("Accuracy = " + correct/(float)numTestSamples);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
