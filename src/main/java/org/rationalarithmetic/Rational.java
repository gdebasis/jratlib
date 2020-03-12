/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rationalarithmetic;

import java.util.Random;

/**
 *
 * @author dganguly
 */
public class Rational {
    public int num;
    public int denom;
    
    public static Rational opResult = new Rational();
    public static float opResult_f;
    
    static final int SEED = 123456;
    static Random randomizer = new Random(SEED);

    public Rational() { }
    
    public Rational(int num, int denom) {
        this.num = num;
        this.denom = denom;
    }
    
    public void add(Rational that) {
        opResult.num = this.num*that.denom + that.num*this.denom;
        opResult.denom = that.denom*this.denom;
    }
    
    public void subtract(Rational that) {
        opResult.num = this.num*that.denom - that.num*this.denom;
        opResult.denom = that.denom*this.denom;
    }
    
    public void mediant(Rational that) {
        opResult.num = this.num + that.num;
        opResult.denom = this.denom + that.denom;
    }
    
    static public Rational mediant(Rational a, Rational b) {
        return new Rational(a.num + b.num, a.denom + b.denom);
    }
    
    public void mul(Rational that) {
        opResult.num = this.num*that.num;
        opResult.denom = this.denom*that.denom;
    }
    
    public void div(Rational that) {
        opResult.num = this.num*that.denom;
        opResult.denom = this.denom*that.num;
    }
    
    public float getValue() { return num/(float)denom; }
    
    @Override
    public String toString() { return num + "/" + denom; }
    
    public static Rational initRandom(int maxNumerator, int maxDenominator) {
        int num = 1 + randomizer.nextInt(maxNumerator);
        int denom = 1 + randomizer.nextInt(maxDenominator);
        return new Rational(num, denom);
    }    
}
