/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml;

import java.util.Arrays;
import java.util.Random;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author debforit
 */
public class RealValuedVec {
    int dimension;
    public float[] x; // data (the bias term is incorporated as the 0th component - so that we can take a dot product)
    public int y; // label
    public int[] oneHotEncoding;

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
        for (int i=0; i < dimension; i++)
            x[i] += del.x[i];
    }
    
    public float getSumOfComponents() {
        float z = 0;
        for (int k=0; k < dimension; k++) {
            z += x[k];
        }
        return z;
    }
    
    public Pair<Integer, Float> maxIndexAndValue() {
        float maxValue = 0;
        int maxIndex = 0;
        
        for (int i=0; i < dimension; i++) {
            if (x[i] > maxValue) {
                maxValue = x[i];
                maxIndex = i;
            }
        }
        return Pair.of(maxIndex, maxValue);
    }
    
    public boolean isZero() {
        for (int i=0; i < dimension; i++) {
            if (x[i] != 0) return false;
        }
        return true;
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
        for (int j=0; j < dimension; j++) {
            sum += this.x[j] * that.x[j];
        }
        return sum;
    }
    
    void oneHotEncode(int classId, int numClasses) {
        oneHotEncoding = new int[numClasses];
        Arrays.fill(oneHotEncoding, 0);
        oneHotEncoding[classId] = 1; // convert to 0 based index.. so 1<=>0, 10<=>9
    }
    
    public String toString() {
        StringBuffer buff  = new StringBuffer();
        for (int i=0; i < dimension; i++)
            buff.append(x[i]).append(",");
        
        return buff.toString();
    }
}
