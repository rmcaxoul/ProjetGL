/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ensimag.deca;

import org.apache.log4j.Logger;
import java.io.File;
import java.util.concurrent.*;
/**
 * Classe du thread utilisé pour la parralélisation de decac -P
 *
 * @author gl31
 * @date 12/01/2021
 */
public class DecacThread implements Callable<Boolean> {

    CompilerOptions options;
    File source;

    public DecacThread(CompilerOptions options, File source){
        this.options = options;
        this.source = source;
    }

    @Override
    public Boolean call(){
        DecacCompiler compiler = new DecacCompiler(options, source);
        if (compiler.compile()) {
            return true;
        }
        return false;
    }
}
