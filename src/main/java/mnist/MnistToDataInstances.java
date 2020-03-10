/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mnist;

import java.io.IOException;
import ml.RealValuedVec;

/**
 *
 * @author debforit
 */
public class MnistToDataInstances {

    public static RealValuedVec[] loadMnist() throws IOException {
        final int numClasses = 10;
        
        MnistMatrix[] mnistMatrix = new MnistDataReader().readData("mnist/train-images.idx3-ubyte", "mnist/train-labels.idx1-ubyte");
        RealValuedVec[] instances = new RealValuedVec[mnistMatrix.length];
        int dimension = mnistMatrix[0].getNumberOfRows() * mnistMatrix[0].getNumberOfColumns();
                
        for (int i=0; i < mnistMatrix.length; i++) {
            MnistMatrix a =  mnistMatrix[i];
            instances[i] = new RealValuedVec(dimension);
            
            int p = 0;
            instances[i].x[p++] = 1;  // incorporate the bias term within the data
            int rows = a.getNumberOfRows();
            int cols = a.getNumberOfColumns();
            for (int j=0; j < rows; j++) {
                for (int k=0; k < cols; k++) {
                    instances[i].x[p++] = a.getValue(j, k);
                }
            }
            
            instances[i].setLabel(a.getLabel(), numClasses);
            instances[i].scale(0, 255);
        }
        return instances;
    }
}

