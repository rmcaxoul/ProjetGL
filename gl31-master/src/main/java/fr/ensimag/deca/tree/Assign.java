package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * Assignment, i.e. lvalue = expr.
 *
 * @author gl31
 * @date 01/01/2021
 */
public class Assign extends AbstractBinaryExpr {

    @Override
    public AbstractLValue getLeftOperand() {
        // The cast succeeds by construction, as the leftOperand has been set
        // as an AbstractLValue by the constructor.
        return (AbstractLValue)super.getLeftOperand();
    }

    public Assign(AbstractLValue leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type type = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        setRightOperand(getRightOperand().verifyRValue(compiler, localEnv, currentClass, type));
        setType(type);
        return type;
    }

    @Override
    public void codeGenCondition(DecacCompiler compiler, Boolean bool, Label elseLabel) {

    }

    @Override
    protected void codeGenExpr(DecacCompiler compiler){

        if (getLeftOperand() instanceof Identifier) {
            Definition def = ((Identifier) this.getLeftOperand()).getDefinition();

            if (def.isField()){
                // On charge l'objet dans le registre courant
                RegisterOffset instanceAddr = new RegisterOffset(-2, Register.LB);
                compiler.addInstruction(new LOAD(instanceAddr, Register.getR(compiler.reg.getCurrentRegId())));
                // On génère l'adresse du champ.
                RegisterOffset fieldAddr = new RegisterOffset(((FieldDefinition) def).getIndex(), Register.getR(compiler.reg.getCurrentRegId()));
                // On calcul l'expression de droite dans le register n+1
                compiler.reg.incrementCurRegId();

                this.getRightOperand().codeGenExpr(compiler);
                // On load dans l'adresse du champ.
                compiler.addInstruction(new STORE(Register.getR(compiler.reg.getCurrentRegId()), fieldAddr));

            } else {
                // On calcul l'expression de droite
                this.getRightOperand().codeGenExpr(compiler);
                // On génère l'adresse de l'identifier

                DAddr varAddr = ((Identifier) this.getLeftOperand()).getExpDefinition().getOperand();
                // On charge la valeur de droite dans l'adresse
                compiler.addInstruction(new STORE(Register.getR(compiler.reg.getCurrentRegId()), varAddr));
            }

        } else if (getLeftOperand() instanceof Selection){
            // On charge l'objet dans le registre courant
            Selection select = (Selection) getLeftOperand();
            select.codeGenAssign(compiler);
            // On génère l'adresse du champ.
            RegisterOffset fieldAddr = new RegisterOffset(select.getAttribute().getFieldDefinition().getIndex(), Register.getR(compiler.reg.getCurrentRegId()));
            // On calcul l'expression de droite dans le register n+1
            compiler.reg.incrementCurRegId();
            this.getRightOperand().codeGenExpr(compiler);
            // On load dans l'adresse du champ.
            compiler.addInstruction(new STORE(Register.getR(compiler.reg.getCurrentRegId()), fieldAddr));
        }
    }
    
    @Override
    public void decompile(IndentPrintStream s) {
        getLeftOperand().decompile(s);
        s.print(" " + getOperatorName() + " ");
        getRightOperand().decompile(s);
    }

    @Override
    protected String getOperatorName() {
        return "=";
    }

}
