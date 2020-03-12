/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rationalarithmetic;

/**
 *
 * @author debforit
 */
public class SternBrocotApproximator {
    
    public static void main(String[] args) {
        float[] test_values = {2.718281828459f, 3.14159265358979f, 0.142857f};
        float epsilon = 0.0000001f;
        
        for (float x: test_values) {
            Rational left  = new Rational(0, 1);
            Rational right = new Rational(1, 0);
            Rational best = left;

            float bestError = Math.abs(x);
            System.out.println(best + " = " + best.getValue() + ", error = " + bestError);

            // do Stern-Brocot binary search
            while (bestError > epsilon) {

                // compute next possible rational approximation
                Rational mediant = Rational.mediant(left, right);
                if (x < mediant.getValue())
                    right = mediant;              // go left
                else
                    left = mediant;              // go right

                // check if better and update champion
                float error = Math.abs(mediant.getValue()- x); 
                if (error < bestError) {
                    best = mediant;
                    bestError = error;
                    System.out.print(String.format("<%s (%.10f)> ", best, error));
                }
            }
            System.out.println();
       }
    }
}