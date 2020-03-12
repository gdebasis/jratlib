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
public class SoftmaxLogisticRegression extends LogisticRegressionBase {
    RealValuedVec[] theta;
    int numClasses;
    
    public SoftmaxLogisticRegression(RealValuedVec[] train, RealValuedVec[] test, int numClasses, float alpha) {
        super(train, test, alpha);
        
        this.numClasses = numClasses;        
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
            gradient.x[j] = alpha * train[i].x[j] * (delta - p); // update the theta_k vector (for the k-th class)
        }
        
        return gradient;
    }
    
    @Override
    float computeLoss() {
        float loss = 0;
        
        try {
            for (int i=0; i < numTrainingSamples; i++) {
                RealValuedVec softMaxProbs = computeSoftMax(train[i]);
                float z = softMaxProbs.getSumOfComponents();

                for (int k=0; k < numClasses; k++) {
                    loss += train[i].oneHotEncoding[k] * Math.log(softMaxProbs.x[k]/z);
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return -loss;
    }
    
    RealValuedVec computeSoftMax(RealValuedVec x) throws Exception { // is a function of the current parameters
        RealValuedVec softmaxProbs = new RealValuedVec(numClasses, true);
        
        for (int k=0; k < numClasses; k++) {
            softmaxProbs.x[k] = boundedExp(theta[k], x, 1);
        }        
        return softmaxProbs;
    }

    @Override
    void epoch(int batchStartIndex, int batchEndIndex) {
        
        try {
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
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    void epoch() {
        try {
            for (int i=0; i < numTrainingSamples; i++) {
                RealValuedVec softMaxProbs = computeSoftMax(train[i]);
                float z = softMaxProbs.getSumOfComponents();

                for (int k=0; k < numClasses-1; k++) {  // tie the \theta_k to all zeroes            
                    RealValuedVec gradient = sgd(i, k, softMaxProbs, z);
                    theta[k].addBy(gradient); // update theta_k
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    void evaluate(boolean printInstances) {
        
        try {
            int numTestSamples = test.length;
            int correct = 0;
            
            for (int i=0; i < numTestSamples; i++) {
                RealValuedVec softmax = computeSoftMax(test[i]);
                float z = softmax.getSumOfComponents();
                Pair<Integer, Float> maxIndexAndValue = softmax.maxIndexAndValue();
                if (maxIndexAndValue.getLeft() == test[i].y)
                    correct++;
                
                if (printInstances)
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
