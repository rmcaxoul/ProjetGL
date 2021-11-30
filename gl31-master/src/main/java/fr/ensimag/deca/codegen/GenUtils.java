package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tree.*;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;
import fr.ensimag.deca.DecacCompiler;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe de méthodes et attribus utiles à la génération de code assembleur
 */

public class GenUtils {

    private static Map<String, Label> errorLabelTable = new HashMap<String, Label>();
    private static Map<String, Boolean> errorLabelFlagTable = new HashMap<String, Boolean>();
    
    /**
     * Fonction d'initialisation des Lables d'erreur possibles dans le code
     * assembleur
     */
    public static void initLabels(){
        errorLabelTable.put("div_zero_error", new Label("div_zero_error"));
        errorLabelTable.put("overflow_error", new Label("overflow_error"));
        errorLabelTable.put("stack_overflow_error", new Label("stack_overflow_error"));
        errorLabelTable.put("io_error", new Label("io_error"));
        errorLabelTable.put("dereferencement.null", new Label("dereferencement.null"));
        errorLabelTable.put("tas_plein", new Label("tas_plein"));
        errorLabelTable.put("cast_error", new Label("cast_error"));



        errorLabelFlagTable.put("div_zero_error", false);
        errorLabelFlagTable.put("overflow_error", false);
        errorLabelFlagTable.put("stack_overflow_error", true);
        errorLabelFlagTable.put("io_error", false);
        errorLabelFlagTable.put("dereferencement.null", false);
        errorLabelFlagTable.put("tas_plein", false);
        errorLabelFlagTable.put("cast_error", false);



    }

    /**
     * Génère le code associé aux labels d'erreurs utilisés.
     * @param compiler
     */
    public static void generateErrorCode(DecacCompiler compiler){
        if (getErrorLabelFlag("div_zero_error")){
            divZeroError(compiler);
        }
        if (getErrorLabelFlag("io_error")){
            ioError(compiler);
        }
        if (getErrorLabelFlag("overflow_error")){
            overflowError(compiler);
        }
        if (getErrorLabelFlag("stack_overflow_error") && !compiler.getCompilerOptions().getNoCheck()){
            stackOverflowError(compiler);
        }
        if (getErrorLabelFlag("dereferencement.null") && !compiler.getCompilerOptions().getNoCheck()){
            dereferencementNull(compiler);
        }
        if (getErrorLabelFlag("tas_plein") && !compiler.getCompilerOptions().getNoCheck()){
            tasPlein(compiler);
        }
        if (getErrorLabelFlag("cast_error") && !compiler.getCompilerOptions().getNoCheck()){
            castError(compiler);
        }
    }
    
    /**
     * Fonction du code assembleur de castError
     * @param compiler 
     */
    public static void castError(DecacCompiler compiler) {
        compiler.addLabel(errorLabelTable.get("cast_error"));
        compiler.addInstruction(new WSTR("Erreur : impossible de réaliser le cast"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());
    }
    
    /**
     * Fonction du code assembleur de divZeroError
     * @param compiler 
     */
    public static void divZeroError(DecacCompiler compiler) {
        compiler.addLabel(errorLabelTable.get("div_zero_error"));
        compiler.addInstruction(new WSTR("Erreur : division par zéro"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());
    }

    /**
     * Fonction du code assembleur de tasPlein
     * @param compiler 
     */
    public static void tasPlein(DecacCompiler compiler) {
        compiler.addLabel(errorLabelTable.get("tas_plein"));
        compiler.addInstruction(new WSTR("Erreur : allocation impossible, tas plein"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());
    }
    
    /**
     * Fonction du code assembleur de dereferencementNull
     * @param compiler 
     */
    public static void dereferencementNull(DecacCompiler compiler) {
        compiler.addLabel(errorLabelTable.get("dereferencement.null"));
        compiler.addInstruction(new WSTR("Erreur : dereferencement de null"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());
    }
    
    /**
     * Fonction du code assembleur de ioError
     * @param compiler 
     */
    public static void ioError(DecacCompiler compiler) {
        compiler.addLabel(errorLabelTable.get("io_error"));
        compiler.addInstruction(new WSTR("Erreur : erreur Input/Output"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());
    }
    
    /**
     * Fonction du code assembleur de overflowError
     * @param compiler 
     */
    public static void overflowError(DecacCompiler compiler) {
        compiler.addLabel(errorLabelTable.get("overflow_error"));
        compiler.addInstruction(new WSTR("Erreur : depassement lors de operation arithmetique"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());
    }
    
    /**
     * Fonction du code assembleur de stackOverflowError
     * @param compiler 
     */
    public static void stackOverflowError(DecacCompiler compiler) {
        compiler.addLabel(errorLabelTable.get("stack_overflow_error"));
        compiler.addInstruction(new WSTR("Erreur : depassement de la taille de la pile"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());
    }
    
    /**
     * Fonction retournant le Label d'erreur en assembleur correspondant à la
     * string name
     * 
     * @return label
     * @param name
     */
    public static Label getErrorLabel(String name){
        Label label = errorLabelTable.get(name);
        if (label == null){
            throw new IllegalArgumentException("No error named: " + name);
        }
        return label;
    }
    
    /**
     * Fonction retournant le statut du Label d'erreur en assembleur
     * correspondant à la string name (codé ou pas encore codé)
     * 
     * @return flag
     * @param name
     */
    public static boolean getErrorLabelFlag(String name){
        Boolean flag = errorLabelFlagTable.get(name);
        if (flag == null){
            throw new IllegalArgumentException("No error named: " + name);
        }
        return flag;
    }

    /**
     * Set error label with BOV.
     * @param compiler
     * @param name
     */
    public static void setErrorLabel(DecacCompiler compiler, String name) {
        // Io_error are not disabled by -n option.
        if (name == "io_error") {
            Label errorLabel = getErrorLabel(name);
            compiler.addInstruction(new BOV(errorLabel));
            errorLabelFlagTable.replace(name, true);
        } else if (!compiler.getCompilerOptions().getNoCheck()){
            Label errorLabel = getErrorLabel(name);
            compiler.addInstruction(new BOV(errorLabel));
            errorLabelFlagTable.replace(name, true);
        }
    }

    /**
     * Set error label with BEQ.
     * @param compiler
     * @param name
     */
    public static void setErrorLabelBEQ(DecacCompiler compiler, String name) {
        // Io_error are not disabled by -n option.
        if (name == "io_error") {
            Label errorLabel = getErrorLabel(name);
            compiler.addInstruction(new BEQ(errorLabel));
            errorLabelFlagTable.replace(name, true);
        } else if (!compiler.getCompilerOptions().getNoCheck()){
            Label errorLabel = getErrorLabel(name);
            compiler.addInstruction(new BEQ(errorLabel));
            errorLabelFlagTable.replace(name, true);
        }
    }


    /**
     * Set error label with BRA.
     * @param compiler
     * @param name
     */
    public static void setErrorLabelBRA(DecacCompiler compiler, String name) {
        // Io_error are not disabled by -n option.
        if (name == "io_error") {
            Label errorLabel = getErrorLabel(name);
            compiler.addInstruction(new BRA(errorLabel));
            errorLabelFlagTable.replace(name, true);
        } else if (!compiler.getCompilerOptions().getNoCheck()){
            Label errorLabel = getErrorLabel(name);
            compiler.addInstruction(new BRA(errorLabel));
            errorLabelFlagTable.replace(name, true);
        }
    }

    /**
     * Atomic expression evaluation.
     * @param expr
     * @return
     */
    public static DVal dval(AbstractExpr expr){
        if (expr instanceof IntLiteral){
            return new ImmediateInteger(((IntLiteral) expr).getValue());
        } else if (expr instanceof Identifier) {
            return ((Identifier) expr).getExpDefinition().getOperand();
        } else if (expr instanceof FloatLiteral) {
            return new ImmediateFloat(((FloatLiteral) expr).getValue());
        } else if (expr instanceof BooleanLiteral) {
            return new ImmediateInteger(((BooleanLiteral) expr).getValue() ? 1 : 0);
        } else {
            return null;
        }
    }
    
    /**
     * Create arithmetic operations
     * @param op    the operation to do
     * @param val   the value of the DVal field
     * @param n     the register's number in the operation
     * @return      the instruction for the operation
     */
    public static Instruction mnemo(AbstractExpr op, DVal val, int n){
        if(op instanceof Plus){
            //Addition d'entiers et/ou de flottants
            return new ADD(val, Register.getR(n));
            
        } else if(op instanceof Minus){
            //Soustraction d'entiers et/ou de flottants
            return new SUB(val, Register.getR(n));
        
        } else if(op instanceof UnaryMinus) {
            //Soustraction unaire
            return new OPP(val, Register.getR(n));
            
        } else if(op instanceof Multiply){
            //Multiplication d'entiers et/ou de flottants
            return new MUL(val, Register.getR(n));

        } else if(op instanceof Divide){
            if(((Divide)op).getLeftOperand().getType().isInt() && ((Divide)op).getRightOperand().getType().isInt()){
                //Division entre des entiers
                return new QUO(val, Register.getR(n));
                
            } else {
                //Division entre des flottants
                return new DIV(val, Register.getR(n));
                
            }
        } else if(op instanceof Modulo){
            //Modulo entre des d'entiers
            return new REM(val, Register.getR(n));
            
        } else {
            return null;
        }
        
        
    }

}
