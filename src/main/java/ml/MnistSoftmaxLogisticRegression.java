/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml;

import mnist.MnistToDataInstances;

/**
 *
 * @author debforit
 */
public class MnistSoftmaxLogisticRegression {
    static final float LEARNING_MOMENTUM = 0.1f;
    static final int EPOCHS = 5;
    static final int BATCH_SIZE = 1000;
    
    public static void main(String[] args) {
        try {
            RealValuedVec[] trainData = MnistToDataInstances.loadMnist();
            System.out.println("#training images: " + trainData.length);
            
            RealValuedVec[] testData = MnistToDataInstances.loadMnist();
            
            SoftmaxLogisticRegression lr = new SoftmaxLogisticRegression(trainData, testData, 10, LEARNING_MOMENTUM);
            lr.runBGDEpochs(BATCH_SIZE, EPOCHS);
            //lr.runSGDEpochs(EPOCHS);
            
            lr.evaluate();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
