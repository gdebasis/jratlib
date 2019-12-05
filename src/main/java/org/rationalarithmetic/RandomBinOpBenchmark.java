/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rationalarithmetic;
import org.apache.commons.math3.fraction.Fraction;
import java.util.*;

/**
 * 
 * @author dganguly
 */

/*
class RandomFraction {
    Fraction x;
    static final int SEED = 123456;
    static Random randomizer;
    
    public RandomFraction(int maxNumerator, int maxDenominator) {
        int numerator = 1 + randomizer.nextInt(maxNumerator);
        int denominator = 1 + randomizer.nextInt(maxDenominator);
        x = new Fraction(numerator, denominator);
    }
    
    public static void setSeed() { randomizer = new Random(SEED); }
}
*/

class Rational {
    int num;
    int denom;
    
    public static Rational opResult = new Rational();
    public static float opResult_f;
    
    static final int SEED = 123456;
    static Random randomizer;

    public Rational() { }
    
    public Rational(int num, int denom) {
        this.num = num;
        this.denom = denom;
    }
    
    public void add(Rational that) {
        opResult.num = this.num*that.denom + that.num*this.denom;
        opResult.denom = that.denom*this.denom;
    }
    
    public void mediant(Rational that) {
        opResult.num = this.num + that.num;
        opResult.denom = this.denom + that.denom;
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
    public String toString() { return num + "/" + denom; }
    
    public static Rational initRandom(int maxNumerator, int maxDenominator) {
        int num = 1 + randomizer.nextInt(maxNumerator);
        int denom = 1 + randomizer.nextInt(maxDenominator);
        return new Rational(num, denom);
    }
    
    public static void setSeed() { randomizer = new Random(SEED); }
}

public class RandomBinOpBenchmark {
    static final int N = 100000000;
    static final int M = 100;
    enum OpCode { ADD, MEDIANT, MUL, DIV, FP_ADD, FP_MUL, FP_DIV };

    void doOperations(int N, OpCode opcode, boolean verbose) {
        long start, end;
        Rational a, b;
        
        start = System.currentTimeMillis();
        for (int i=0; i < N; i++) {
            a = Rational.initRandom(M, M);
            b = Rational.initRandom(M, M);
            switch (opcode) {
                case ADD: a.add(b); break;
                case MEDIANT: a.mediant(b); break;
                case MUL: a.mul(b); break;
                case DIV: a.div(b); break;
                
                case FP_ADD: Rational.opResult_f = a.getValue() + b.getValue(); break;
                case FP_MUL: Rational.opResult_f = a.getValue() * b.getValue(); break;
                case FP_DIV: Rational.opResult_f = a.getValue() / b.getValue(); break;
            }
            
            if (!verbose)
                continue;
            
            if (opcode==OpCode.DIV || opcode==OpCode.MUL || opcode==OpCode.ADD)
                System.out.println(String.format("%s + %s = %s\n", a.toString(), b.toString(), Rational.opResult.toString()));
            else
                System.out.println(String.format("%f + %f = %f\n", a.getValue(), b.getValue(), Rational.opResult_f));
        }
        
        end = System.currentTimeMillis();
        System.out.println(String.format("Time taken for %s: %d ms", opcode.name(), end-start));
    }
    
    public static void main(String[] args) {
        RandomBinOpBenchmark b = new RandomBinOpBenchmark();
        
        for (OpCode opcode: OpCode.values()) {
            Rational.setSeed();
            b.doOperations(N, opcode, false);
        }
    }
}
