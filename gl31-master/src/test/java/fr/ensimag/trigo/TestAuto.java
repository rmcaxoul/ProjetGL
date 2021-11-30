/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ensimag.trigo;
import static fr.ensimag.trigo.TestTrigo.*;
import java.io.IOException;
import java.math.*;
/**
 *
 * @author ensimag
 */
public class TestAuto {
    
    public static void main(String[] args) throws IOException {
        float diff, maxFloat = 0, minFloat = 0;
        float totalDiff = 0;
        int index = 0;
        float minDiff = 1;
        float maxDiff = 0;
        float minUlp = 0;
        float maxUlp = 0;
        
        System.out.println("Tests sinus:");
        for(float f = 0; f < 10000; f += 1.00e-1){
            diff = Math.abs(sin(f) - (float)Math.sin(f));
            totalDiff += diff;
            if(diff > maxDiff){
                maxDiff = diff;
                maxFloat = f;
            }
            if(diff < minDiff){
                minDiff = diff;
                minFloat = f;
            }
            index++;
        }
        maxUlp = maxDiff/ulp(sin(maxFloat));
        minUlp = minDiff/ulp(sin(minFloat));
        System.out.println("    Moyenne d'erreur = "+totalDiff/index);
        System.out.println("    Diff. max = "+maxDiff+" atteinte pour f = "+maxFloat+" soit "+maxUlp+" ulp");
        System.out.println("    Diff. min = "+minDiff+" atteinte pour f = "+minFloat+" soit "+minUlp+" ulp");
        index = 0; minDiff = 1; maxDiff = 0; maxFloat = 0; minFloat = 0; totalDiff = 0;
        System.out.println("");
        System.out.println("Tests cosinus");
        for(float f = 0; f < 10000; f += 1.00e-1){
            diff = Math.abs(cos(f) - (float)Math.cos(f));
            totalDiff += diff;
            if(diff > maxDiff){
                maxDiff = diff;
                maxFloat = f;
            }
            if(diff < minDiff){
                minDiff = diff;
                minFloat = f;
            }
            index++;
        }
        maxUlp = maxDiff/ulp(cos(maxFloat));
        minUlp = minDiff/ulp(cos(minFloat));
        System.out.println("    Moyenne d'erreur = "+totalDiff/index);
        System.out.println("    Diff. max = "+maxDiff+" atteinte pour f = "+maxFloat+" soit "+maxUlp+" ulp");
        System.out.println("    Diff. min = "+minDiff+" atteinte pour f = "+minFloat+" soit "+minUlp+" ulp");
        index = 0; minDiff = 1; maxDiff = 0; maxFloat = 0; minFloat = 0; totalDiff = 0;
        System.out.println("");
        System.out.println("Tests asin:");
        for(float f = 0; f < 10000; f += 1.00e-1){
            diff = Math.abs(asin(f) - (float)Math.asin(f));
            totalDiff += diff;
            if(diff > maxDiff){
                maxDiff = diff;
                maxFloat = f;
            }
            if(diff < minDiff){
                minDiff = diff;
                minFloat = f;
            }
            index++;
        }
        maxUlp = maxDiff/ulp(asin(maxFloat));
        minUlp = minDiff/ulp(asin(minFloat));
        System.out.println("    Moyenne d'erreur = "+totalDiff/index);
        System.out.println("    Diff. max = "+maxDiff+" atteinte pour f = "+maxFloat+" soit "+maxUlp+" ulp");
        System.out.println("    Diff. min = "+minDiff+" atteinte pour f = "+minFloat+" soit "+minUlp+" ulp");
        index = 0; minDiff = 1; maxDiff = 0; maxFloat = 0; minFloat = 0; totalDiff = 0;
        System.out.println("");
        System.out.println("Tests atan:");
        for(float f = 0; f < 10000; f += 1.00e-1){
            diff = Math.abs(atan(f) - (float)Math.atan(f));
            totalDiff += diff;
            if(diff > maxDiff){
                maxDiff = diff;
                maxFloat = f;
            }
            if(diff < minDiff){
                minDiff = diff;
                minFloat = f;
            }
            index++;
        }
        maxUlp = maxDiff/ulp(atan(maxFloat));
        minUlp = minDiff/ulp(atan(minFloat));
        System.out.println("    Moyenne d'erreur = "+totalDiff/index);
        System.out.println("    Diff. max = "+maxDiff+" atteinte pour f = "+maxFloat+" soit "+maxUlp+" ulp");
        System.out.println("    Diff. min = "+minDiff+" atteinte pour f = "+minFloat+" soit "+minUlp+" ulp");
    }
}
