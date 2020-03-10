/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir;

import java.util.Random;

/**
 *
 * @author dganguly
 */
public class PowerLawSampler {
    static final int SEED = 23456;
    static final float ALPHA = 1.5f;
    static Random randomizer = new Random(SEED);
    static final int vocabSize = 10000;
    static final int MIN_FREQ = 5;
    
    public static void main(String[] args) {
        float x;
        int df;
        
        for (int i=0; i < vocabSize; i++) {
            // generate a random df (following the power law) for this term...
            x = randomizer.nextFloat();
            df = Math.round(MIN_FREQ * (float)Math.pow((1-x), ALPHA));
            if (i%10==0)
                System.out.println();
            
            System.out.print(df + ", ");
        }
        System.out.println();
    }
}
