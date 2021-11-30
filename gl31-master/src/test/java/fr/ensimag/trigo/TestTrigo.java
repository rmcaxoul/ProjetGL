package fr.ensimag.trigo;

import java.io.IOException;

public class TestTrigo {
    
    public static void main(String[] args) throws IOException {  

        System.out.println("\n----- TEST SINUS -----");
        System.out.println("sin(0) = " + sin(0) + " (" + Math.sin(0) + ")"); 
        System.out.println("sin(0.5) = " + sin(0.5f) + " (" + Math.sin(0.5) + ")");       
        System.out.println("sin(1) = " + sin(1) + " (" + Math.sin(1) + ")");       
        System.out.println("sin(2) = " + sin(2) + " (" + Math.sin(2) + ")"); 
        System.out.println("sin(π/4) = " + sin(pi_4()) + " (" + Math.sin(pi_4()) + ")");      
        System.out.println("sin(π/2) = " + sin(pi_2()) + " (" + Math.sin(pi_2()) + ")");  
        System.out.println("sin(π) = " + sin(pi()) + " (" + Math.sin(pi()) + ")");  
        System.out.println("sin(-π/2) = " + sin((-1)* pi_2()) + " (" + Math.sin((-1) * pi_2()) + ")");   
        System.out.println("sin(-π) = " + sin((-1) * pi()) + " (" + Math.sin((-1) * pi()) + ")"); 
        System.out.println("sin(-30.5π) = " + sin((-30.5f) * pi()) + " (" + Math.sin((-30.5f) * pi()) + ")"); 
        
        System.out.println("\n----- TEST COSINUS -----");
        System.out.println("cos(0) = " + cos(0) + " (" + Math.cos(0) + ")");
        System.out.println("cos(0.5) = " + cos(0.5f) + " (" + Math.cos(0.5) + ")");       
        System.out.println("cos(1) = " + cos(1) + " (" + Math.cos(1) + ")");        
        System.out.println("cos(2) = " + cos(2) + " (" + Math.cos(2) + ")");   
        System.out.println("cos(π/4) = " + cos(pi_4()) + " (" + Math.cos(pi_4()) + ")");     
        System.out.println("cos(π/2) = " + cos(pi_2()) + " (" + Math.cos(pi_2()) + ")");   
        System.out.println("cos(π) = " + cos(pi()) + " (" + Math.cos(pi()) + ")");  
        System.out.println("cos(-π/2) = " + cos((-1)* pi_2()) + " (" + Math.cos((-1)* pi_2()) + ")");   
        System.out.println("cos(-π) = " + cos((-1) * pi()) + " (" + Math.cos((-1)* pi()) + ")");  
        System.out.println("cos(-30.7π) = " + cos((-30.7f) * pi()) + " (" + Math.cos((-30.7f) * pi()) + ")");
        
        System.out.println("\n----- TEST ARC TANGENTE -----");
        System.out.println("atan(0) = " + atan(0) + " (" + Math.atan(0) + ")");
        System.out.println("atan(1) = " + atan(1) + " (" + Math.atan(1) + ")");
        System.out.println("atan(-1) = " + atan(-1) + " (" + Math.atan(-1) + ")");
        System.out.println("atan(0.7) = " + atan(0.7f) + " (" + Math.atan(0.7f) + ")");
        System.out.println("atan(-0.4) = " + atan(-0.4f) + " (" + Math.atan(-0.4f) + ")");
        System.out.println("atan(2) = " + atan(2) + " (" + Math.atan(2) + ")");
        System.out.println("atan(-30) = " + atan(-30) + " (" + Math.atan(-30) + ")");
        
        System.out.println("\n----- TEST ARC SINUS -----");
        System.out.println("asin(0) = " + asin(0) + " (" + Math.asin(0) + ")");
        System.out.println("asin(0.0001) = " + asin(0.0001f) + " (" + Math.asin(0.0001) + ")");
        System.out.println("asin(0.1) = " + asin(0.1f) + " (" + Math.asin(0.1) + ")");
        System.out.println("asin(0.3) = " + asin(0.3f) + " (" + Math.asin(0.3) + ")");
        System.out.println("asin(0.5) = " + asin(0.5f) + " (" + Math.asin(0.5) + ")");
        System.out.println("asin(0.7) = " + asin(0.7f) + " (" + Math.asin(0.7) + ")");
        System.out.println("asin(0.9) = " + asin(0.9f) + " (" + Math.asin(0.9) + ")");
        System.out.println("asin(0.95) = " + asin(0.95f) + " (" + Math.asin(0.95) + ")");
        System.out.println("asin(0.9999) = " + asin(0.9999f) + " (" + Math.asin(0.9999) + ")");
        System.out.println("asin(1) = " + asin(1) + " (" + Math.asin(1) + ")");
        System.out.println("asin(-0.0001) = " + asin(-0.0001f) + " (" + Math.asin(-0.0001) + ")");
        System.out.println("asin(-0.1) = " + asin(-0.1f) + " (" + Math.asin(-0.1) + ")");
        System.out.println("asin(-0.3) = " + asin(-0.3f) + " (" + Math.asin(-0.3) + ")");
        System.out.println("asin(-0.5) = " + asin(-0.5f) + " (" + Math.asin(-0.5) + ")");
        System.out.println("asin(-0.7) = " + asin(-0.7f) + " (" + Math.asin(-0.7) + ")");
        System.out.println("asin(-0.9) = " + asin(-0.9f) + " (" + Math.asin(-0.9) + ")");
        System.out.println("asin(-0.95) = " + asin(-0.95f) + " (" + Math.asin(-0.95) + ")");
        System.out.println("asin(-0.9999) = " + asin(-0.9999f) + " (" + Math.asin(-0.9999) + ")");
        System.out.println("asin(-1) = " + asin(-1) + " (" + Math.asin(-1) + ")");
        
        System.out.println("\n----- TEST GRANDS NOMBRES -----");
        System.out.println("sin(100) = " + sin(100) + " (" + Math.sin(100) + ")");
        System.out.println("sin(1000) = " + sin(1000) + " (" + Math.sin(1000) + ")");
        System.out.println("sin(10000) = " + sin(10000) + " (" + Math.sin(10000) + ")");
        System.out.println("sin(100000) = " + sin(100000) + " (" + Math.sin(100000) + ")");
    }
    
    public static float abs(float a) {
        return (a < 0)? -a : a;
    }
    
    public static float pi() {
        return 3.1415927f;
    }
    
    public static float pi_2() {
        return 1.5707964f;
    }
    
        
    public static float pi_4() {
        return 0.7853982f;
    }
    
    public static float modulo(float a) {
        if (a < (-1) * pi()) {
            while (a < (-1) * pi()) {
                a += 2 * pi();
            }
        } else if (a > pi()) {
            while (a > pi()) {
                a -= 2 * pi();
            }
        }
        return a;
    }
    
    public static float sqrt(float a) {
        if (a <= 0)
            return 0;
        float temp = -1;
        float un = a;
        int i = 0;
        while ((un != temp) && (i < 32)) {
            temp = un;
            un = (un + a / un) / 2;
            i++;
        }
        return un;
    }
    
    public static float sin(float a) {
        float S1 = -0.16666667f;
        float S2 = 8.3333333e-3f;
        float S3 = -1.9841270e-4f;
        float S4 = 2.7557314e-6f;
        float S5 = -2.5050760e-8f;
        float S6 = 1.5896910e-10f;
        a = modulo(a);
        int sign = (a < 0)? -1 : 1;
        a = abs(a);
        if (a <= pi_4()) {
            float square = a * a;
            float cube = square * a;
            return sign * (a + cube * (S1 + square * (S2 + square * (S3 + square * (S4 + square * (S5 + square * S6))))));
        } else if (a < pi_2())
            return sign * (cos(pi_2() - a));
        else
            return sign * (cos(a - pi_2()));
    }
    
    public static float cos(float a) {
        float C1 = 0.041666668f;
        float C2 = -1.3888889e-3f;
        float C3 = 2.480158e-5f;
        float C4 = -2.7557314e-7f;
        float C5 = 2.0875723e-9f;
        float C6 = -1.1359648e-11f;
        a = modulo(a);
        int sign = ((a < (-1) * pi_2()) || (a > pi_2()))? -1 : 1;
        a = abs(a);
        if (a <= pi_4()) {
            float square = a * a;
            return sign * (1 - square * (0.5f + square * (C1 + square * (C2 + square * (C3 + square * (C4 + square * (C5 + square * C6)))))));
        } else if (a < pi_2())
            return sign * (sin(pi_2() - a));
        else
            return sign * (sin(a - pi_2()));
    }
    
    public static float atan(float a) {
        if ((a < -1) || (a > 1))
            return  2 * atan(a / (1 + sqrt(1 + a * a)));
        else
            return  a * (pi_4() + 0.273f * (1 - abs(a)));
    }
    
    public static float asin(float a) {
        int sign = (a < 0)? -1 : 1;
        a = abs(a);
        float square = a * a;
        float cube = square * a;
        if (a < 0.5) {
            float A1 = 0.16666667f;
            float A2 = 0.075f;
            float A3 = 4.4642857e-2f;
            return sign * (a + cube * (A1 + square * (A2 + square * A3)));
        } else {
            float A1 = 1.5707288f;
            float A2 = -0.2121144f;
            float A3 = 0.0742610f;
            float A4 = -0.0187293f;
            return sign * (pi_2() - sqrt(1 - a) * (A1 + a * A2 + square * A3 + cube * A4));
        }
    }
    
    // Retourne le résultat de 2 puissance pow
    public static float power2(int pow){
        int index = 0;
        float res = 1.00e+00f;
        if(pow > 0){
            while(index < pow){
                index = index + 1;
                res = res * 2;
            }
        }
        else{
            while(index < -pow){
                index = index + 1;
                res = res / 2;
            }
        }
        return res;
    }
    
    public static int getExponent(float f){
        int res = 0;
        float pow = 1.00e+00f;
        if(f >= 1){
            res = -1;
            while(pow < f){
                pow = pow * 2;
                res = res + 1;
            }
        }
        else{
            while(pow > f){
                pow = pow / 2;
                res = res - 1;
            }
        }
        return res;
    }
    
    // Retourne l'Unit in the Last Place de f
    public static float ulp (float f) {
        return power2(-23) * power2(getExponent(f));
    }
}
