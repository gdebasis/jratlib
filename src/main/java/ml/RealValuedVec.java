/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml;

import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author debforit
 */
public class RealValuedVec {
    int dimension;
    public float[] x; // data (the bias term is incorporated as the 0th component - so that we can take a dot product)
    public int y; // label
    public int[] oneHotEncoding;

    public float sumOfComponents;
    static int SEED = 110781;
    static Random r = new Random(SEED);

    public RealValuedVec(int dimension) {
        this(dimension, false);
    }
    
    public void setLabel(int label, int numClasses) {
        this.y = label;
        oneHotEncode(label, numClasses);
    }

    public int getDimension() { return dimension; }
    
    public void addBy(RealValuedVec del) {
        for (int i=0; i < dimension; i++) {
            x[i] += del.x[i];
            if (Float.isNaN(x[i]))
                i=i;
        }
    }
    
    public float getSumOfComponents() {
        if (sumOfComponents > 0) return sumOfComponents;
        
        float z = 0;
        for (int k=0; k < dimension; k++) {
            z += x[k];
        }
        this.sumOfComponents = z;
        return z;
    }
    
    public void scale(float min, float max) {
        float z = max - min;
        for (int j=0; j < dimension; j++) {
            x[j] = (x[j]-min)/z;
        }
    }
    
    public RealValuedVec(int dimension, boolean isParams) {
        this.dimension = !isParams? dimension+1 : dimension;
        this.x = new float[this.dimension];
    }
    
    public void randomInit() {
        // randomly initialize... this will be useful for the parameter instantiation
        for (int i=0; i < this.dimension; i++)
            x[i] = -1 + 2*r.nextFloat();
    }

    public float dot(RealValuedVec that) throws Exception {
        if (this.dimension != that.dimension)
            throw new Exception(String.format("Dimensions %d and %d don't match", this.dimension, that.dimension));
        
        float sum = 0;
        int nzero = 0;
        for (int j=0; j < dimension; j++) {
            if (that.x[j] > 0) {
                nzero++;
                sum += this.x[j] * that.x[j];
                //System.out.println(String.format("sum += %.4f * %.4f (%.4f)", this.x[j], that.x[j], sum));
            }
        }
        //System.out.println("Non-zeroes " + nzero);
        return sum;
    }
    
    void oneHotEncode(int classId, int numClasses) {
        oneHotEncoding = new int[numClasses];
        Arrays.fill(oneHotEncoding, 0);
        oneHotEncoding[classId] = 1; // convert to 0 based index.. so 1<=>0, 10<=>9
    }
}
