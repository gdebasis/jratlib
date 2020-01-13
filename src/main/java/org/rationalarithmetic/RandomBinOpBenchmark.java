/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rationalarithmetic;
import java.util.*;

/**
 * 
 * @author dganguly
 */

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
            b.doOperations(N, opcode, false);
        }
    }
}
