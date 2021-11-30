package fr.ensimag.ima.pseudocode;

import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.LinkedList;

/**
 * Abstract representation of an IMA program, i.e. set of Lines.
 *
 * @author Ensimag
 * @date 01/01/2021
 */
public class IMAProgram {
    private final LinkedList<AbstractLine> lines = new LinkedList<AbstractLine>();

    public void add(AbstractLine line) {
        lines.add(line);
    }

    public void addComment(String s) {
        lines.add(new Line(s));
    }

    public void addLabel(Label l) {
        lines.add(new Line(l));
    }

    public void addInstruction(Instruction i) {
        lines.add(new Line(i));
    }

    public void addInstruction(Instruction i, String s) {
        lines.add(new Line(null, i, s));
    }

    /**
     * Append the content of program p to the current program. The new program
     * and p may or may not share content with this program, so p should not be
     * used anymore after calling this function.
     */
    public void append(IMAProgram p) {
        lines.addAll(p.lines);
    }
    
    /**
     * Add a line at the front of the program.
     */
    public void addFirst(Line l) {
        lines.addFirst(l);
    }
    
    /**
     * Clear all the instructions of the program
     */
    public void clearProg(){
        this.lines.clear();
    }

    /**
     * Display the program in a textual form readable by IMA to stream s.
     */
    public void display(PrintStream s) {
        for (AbstractLine l: lines) {
            l.display(s);
        }
    }

    /**
     * Return the program in a textual form readable by IMA as a String.
     */
    public String display() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream s = new PrintStream(out);
        display(s);
        return out.toString();
    }

    public void addFirst(Instruction i) {
        addFirst(new Line(i));
    }
    
    public void addFirst(Instruction i, String comment) {
        addFirst(new Line(null, i, comment));
    }

    /**
     * Replace
     * Store Rx y(GB)
     * Load y(GB) Rx
     * by
     * Store Rx, y(GB)
     */
    public void storeLoadOptim(){
        LinkedList<AbstractLine> linesToRemove = new LinkedList<AbstractLine>();
        for (int i = 0; i < lines.size()-1; i+= 2){
            AbstractLine line1 = lines.get(i);
            AbstractLine line2 = lines.get(i+1);
            if (line1 instanceof Line && line2 instanceof Line){
                Instruction inst1 = ((Line) line1).getInstruction();
                Instruction inst2 = ((Line) line2).getInstruction();
                if (inst1 instanceof STORE && inst2 instanceof LOAD){

                    Operand reg1 = ((STORE) inst1).getOperand1();
                    Operand dAddr1 = ((STORE) inst1).getOperand2();

                    Operand dAddr2 = ((LOAD) inst2).getOperand1();
                    Operand reg2 = ((LOAD) inst2).getOperand2();



                    if (reg1.equals(reg2) && dAddr1.equals(dAddr2)) {
                        // On remove le load
                        linesToRemove.add(line2);
                    }
                }

            }
        }
        lines.removeAll(linesToRemove);
    }
}
