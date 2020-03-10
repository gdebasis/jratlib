/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml;

import java.io.IOException;
import java.util.Arrays;
import mnist.MnistDataReader;
import mnist.MnistMatrix;

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
    
    static final float ALPHA = 0.1f;
    static final float MAX_EXPONENT = 6;
    
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
    RealValuedVec sgd(int i, int k, RealValuedVec softMaxProbs) throws Exception {
        int delta = train[i].oneHotEncoding[k]; // one-hots gt label (1/0)
        float p = softMaxProbs.x[k]/softMaxProbs.getSumOfComponents();  // prediction ([0,1]).
        
        RealValuedVec gradient = new RealValuedVec(dimension);        
        for (int j=0; j < dimension; j++) {
            gradient.x[j] = -ALPHA * train[i].x[j] * (delta - p); // update the theta_k vector (for the k-th class)
        }
        return gradient;
    }
    
    float computeLoss() throws Exception {
        float loss = 0;
        
        for (int i=0; i < numTrainingSamples; i++) {
            RealValuedVec softMaxProbs = computeSoftMax(i);
            
            for (int k=0; k < numClasses; k++) {
                loss += train[i].oneHotEncoding[k] * softMaxProbs.x[k];
            }
        }
        return loss;
    }
    
    RealValuedVec computeSoftMax(int i) throws Exception { // is a function of the current parameters
        RealValuedVec softmaxProbs = new RealValuedVec(numClasses);
        
        for (int k=0; k < numClasses; k++) {
            float s = theta[k].dot(train[i]);
            if (s>=MAX_EXPONENT) {
                Arrays.fill(softmaxProbs.x, 0);
                softmaxProbs.x[k] = 1;
                return softmaxProbs;
            }
            float p = s<=-MAX_EXPONENT? 0 : (float)Math.exp(s);
            softmaxProbs.x[k] = p;
        }
        return softmaxProbs;
    }
    
    void printAndCheckParams() {
        for (int k=0; k < numClasses; k++) {
            System.out.println("Parameter vector " + k);
            for (int i=0; i < theta[k].dimension; i++) {
                System.out.print(theta[k].x[i] + " ");
                if (Float.isNaN(theta[k].x[i]))
                    System.out.println(String.format("theta[%d][%d] is NAN", k, i));
            }
            System.out.println();
        }
    }
    
    void epoch() throws Exception {
        
        for (int i=0; i < numTrainingSamples; i++) {
            RealValuedVec softMaxProbs = computeSoftMax(i);
            
            for (int k=0; k < numClasses-1; k++) {  // tie the \theta_k to all zeroes
                RealValuedVec gradient = sgd(i, k, softMaxProbs);
                theta[k].addBy(gradient); // update theta_k
            }
        }
        //printAndCheckParams();
    }
    
    void runEpochs(int iters) {
        try {
            for (int i=1; i <= iters; i++) {
                System.out.println("Epoch: " + i);
                epoch();
                System.out.println(String.format("Loss after %d iterations: %.4f", i, computeLoss()));
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
