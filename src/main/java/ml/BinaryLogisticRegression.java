/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml;

import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author debforit
 */
public class BinaryLogisticRegression extends LogisticRegressionBase {
    RealValuedVec theta;
    
    public BinaryLogisticRegression(RealValuedVec[] train, RealValuedVec[] test, float alpha) {
        super(train, test, alpha);
        theta = new RealValuedVec(dimension, true);
        theta.randomInit();
    }

    float sigmoid(RealValuedVec x) {
        try {
            float y = boundedExp(theta, x, -1);   // exp(-theta.x)
            return 1/(1+y);
        }
        catch (Exception ex) { ex.printStackTrace(); }
        return 0;
    }
    
    void updateParamsBySGD(RealValuedVec dataInstance) throws Exception {
        float y_hat = sigmoid(dataInstance);
        float del = dataInstance.y - y_hat;
        
        for (int j=0; j < dimension; j++) {
            theta.x[j] += alpha * dataInstance.x[j] * del; // update the theta_k vector (for the k-th class)
        }
    }

    void updateParamsByBGD(int batchStartIndex, int batchEndIndex) throws Exception {
        if (batchEndIndex > numTrainingSamples)
            batchEndIndex = numTrainingSamples;

        RealValuedVec delTheta = new RealValuedVec(dimension);
        
        for (int i=batchStartIndex; i < batchEndIndex; i++) {
            RealValuedVec dataInstance = train[i];
            float y_hat = sigmoid(dataInstance);
            float del = dataInstance.y - y_hat;
            
            for (int j=0; j < dimension; j++) {
                delTheta.x[j] = alpha * dataInstance.x[j] * del;
            }
        }
        theta.addBy(delTheta);
    }
    
    @Override
    float computeLoss() {
        float loss = 0;
        for (int i=0; i < numTrainingSamples; i++) {
            RealValuedVec dataInstance = train[i];
            float y_hat = sigmoid(dataInstance);
            loss += dataInstance.y * (float)Math.log(y_hat) + (1-dataInstance.y)*(float)Math.log(1-y_hat); 
        }
        return -loss;
    }

    @Override
    void epoch(int batchStartIndex, int batchEndIndex) {
        try {
            if (batchEndIndex > numTrainingSamples)
                batchEndIndex = numTrainingSamples;

            for (int i=batchStartIndex; i < batchEndIndex; i++) {
                updateParamsByBGD(batchStartIndex, batchEndIndex);
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
                updateParamsBySGD(train[i]);
            }
        }
        catch (Exception ex) { ex.printStackTrace(); }
    }

    @Override
    void evaluate(boolean printInstances) {
        
        try {
            int numTestSamples = test.length;
            int correct = 0;
            
            for (int i=0; i < numTestSamples; i++) {
                float prob = sigmoid(test[i]);
                int y_hat = prob > 0.5? 1 : 0;
                if (y_hat == test[i].y)
                    correct++;
                
                if (printInstances)
                    System.out.println(
                        String.format(
                            "Test (%d): y=%d y_p=%d (%.4f)",
                            i, test[i].y, y_hat, prob
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
