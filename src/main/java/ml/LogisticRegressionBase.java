/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml;

/**
 *
 * @author debforit
 */
public abstract class LogisticRegressionBase {
    RealValuedVec[] train;
    RealValuedVec[] test;
    int dimension;
    int numTrainingSamples;
    float alpha;  // learning rate

    final float MAX_EXPONENT = 15;
    final float MAX_VALUE = (float)Math.exp(MAX_EXPONENT);
    final float MIN_VALUE = (float)Math.exp(-MAX_EXPONENT);
    
    public LogisticRegressionBase(RealValuedVec[] train, RealValuedVec[] test, float alpha) {
        this.train = train;
        this.test = test;
        this.dimension = train[0].getDimension();
        numTrainingSamples = train.length;
        this.alpha = alpha;
    }    
    
    float boundedExp(RealValuedVec theta, RealValuedVec x, int sign) throws Exception {
        float s = theta.dot(x);
        if (s<=-MAX_EXPONENT)
            s = -MAX_EXPONENT;
        if (s>=MAX_EXPONENT)
            s = MAX_EXPONENT;
        float p = (float)Math.exp(s*sign);
        return p;
    }
    
    void epoch(int batchSize) {
        int start = 0;
        int end = start + batchSize;
        
        while (end <= numTrainingSamples) {
            epoch(start, end);
            start = end;
            end = start + batchSize;
        }
    }
    
    abstract void epoch(int batchStartIndex, int batchEndIndex);
    abstract float computeLoss();
    abstract void epoch();
    
    void runSGDEpochs(int iters) {
        for (int i=1; i <= iters; i++) {
            System.out.println(String.format("Epoch %d: Loss = %.2f", i, computeLoss()));
            epoch();
        }
    }
    
    void runBGDEpochs(int batchSize, int iters) {
        for (int i=1; i <= iters; i++) {
            System.out.println(String.format("Epoch %d: Loss = %.2f", i, computeLoss()));
            epoch(batchSize);
        }
    }
    
    abstract void evaluate(boolean printInstances);
    
    void evaluate() {
        evaluate(false);
    }
}
