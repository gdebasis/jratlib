/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml;

import java.util.ArrayList;
import java.util.List;
import mnist.MnistToDataInstances;

/**
 *
 * @author debforit
 */
public class Mnist3vs8Classifier {
    static final float LEARNING_MOMENTUM = 0.1f;
    static final int EPOCHS = 5;
    static final int BATCH_SIZE = 20;
    
    static RealValuedVec[] get3And8s(RealValuedVec[] instances) {
        List<RealValuedVec> subset = new ArrayList<>();
        for (RealValuedVec v: instances) {
            if (v.y == 3 || v.y == 8) {
                v.y = v.y == 3? 0 : 1;
                subset.add(v);
            }
        }
        RealValuedVec[] subsetArray = new RealValuedVec[subset.size()];
        return subset.toArray(subsetArray);
    }
    
    public static void main(String[] args) {
        try {
            RealValuedVec[] trainData = get3And8s(MnistToDataInstances.loadMnist());
            System.out.println("#training images: " + trainData.length);
            
            RealValuedVec[] testData = get3And8s(MnistToDataInstances.loadMnist());
            
            BinaryLogisticRegression lr = new BinaryLogisticRegression(trainData, testData, LEARNING_MOMENTUM);
            //lr.runBGDEpochs(BATCH_SIZE, EPOCHS);
            lr.runSGDEpochs(EPOCHS);
            
            lr.evaluate();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
}
