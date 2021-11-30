/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ensimag.trigo;

import java.io.IOException;
import java.math.*;

/**
 *
 * @author ensimag
 */
public class TestUlp {
    
    //Calcul de 2**pow
    static float power2(int pow){
        int index = 0;
        float res = 1.00e+00f;
        if(pow > 0){
            while(index < pow){
                index = index + 1;
                res = res*2;
            }
        }
        else{
            while(index < -pow){
                index = index + 1;
                res = res/2;
            }
        }
        return res;
    }

    //Calcul de l'exposant non-biaisé de f
    static int getExponent(float f){
        int res = 0;
        float pow = 1.00e+00f;
        if(f >= 1){
            res = -1;
            while(pow < f){
                pow = pow*2;
                res = res + 1;
            }
        }
        else{
            while(pow > f){
                pow = pow/2;
                res = res - 1;
            }
        }
        return res;
    }

    //Calcul de l'ulp de f
    static float ulp(float f){
        return power2(-23) * power2(getExponent(f));
    }
    
    public static void main(String[] args) throws IOException {
        
        System.out.println("Tests getExponent");
        for(float f = 0; f < 10000; f += 1.00e-1){
            System.out.println(f + ": " + getExponent(f) + " | " + Math.getExponent(f));
        }
        
        System.out.println("Tests power2");
        for(int f = -23; f < 24; f += 1){
            System.out.println(f + ": " + power2(f) + " | " + Math.pow(2,f));
        }
        
        System.out.println("Tests ulp");
        for(float f = 0; f < 10000; f += 1.00e-1){
            System.out.println(f + ": " + ulp(f) + " | " + Math.ulp(f));
        }
    }
    
}
