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
public class MnistLogisticRegression {
    public static void main(String[] args) {
        try {
            RealValuedVec[] trainData = MnistToDataInstances.loadMnist();
            RealValuedVec[] testData = MnistToDataInstances.loadMnist();
            
            LogisticRegression lr = new LogisticRegression(trainData, testData, 10);
            lr.runEpochs(5);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
