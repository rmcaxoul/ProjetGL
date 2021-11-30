package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Label;

/**
 * Classe de gestion de la pile
 */

public class PileGestion {


    // Offset pour le pointeur de GB
    private int offset = 1;
    public int getOffset() {
        return offset;
    }
    public void incOffset() {
        offset++;
    }
    public void decOffset(){
        offset--;
    }


    // Offset pour le pointeur de LB
    private int offsetLB = 1;
    public int getOffsetLB() {
        return offsetLB;
    }
    public void resetOffsetLb() {offsetLB = 0;}
    public void incOffsetLB() {
        offsetLB++;
    }
    public void incOffsetLB(int i) {
        offsetLB+=i;
    }

    public void decOffsetLB(){
        offsetLB--;
    }

    // Valeur du TSTO pour un bloc
    private int valTsto = 0;

    public void incrementTSTO() {
        this.valTsto ++;
    }
    public void incrementTSTO(int i) {
        this.valTsto = valTsto + i;
    }
    public int getValTsto(){
        return this.valTsto;
    }
    public void resetValTsto(){
        this.valTsto = 0;
    }

    // Indices pour la gestion de labels dans les If/Then/else
    public Label currentEndIf;
    private int elseId = 0;
    private int finId = 0;
    
    public int getElseId(){
        return elseId;
    }
    public void incElseId(){
        elseId++;
    }
    public int getFinId() {
        return finId;
    }
    public void incFinId(){
        finId++;
    }

    // Indices pour la gestion de labels dans les InstanceOf
    private int instanceOfId = 0;
    public int getInstanceOfId() {return instanceOfId;}
    public void incInstanceOfId() {instanceOfId++;}

    // Indices pour la gestion de labels dans les Casts
    private int castId = 0;
    public int getCastId() {return castId;}
    public void incCastId() {castId++;}




}
