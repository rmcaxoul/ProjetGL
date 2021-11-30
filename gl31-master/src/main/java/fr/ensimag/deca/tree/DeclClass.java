package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.GenUtils;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.*;
import org.apache.commons.lang.Validate;
import fr.ensimag.ima.pseudocode.instructions.*;


import java.io.PrintStream;

/**
 * Declaration of a class (<code>class name extends superClass {members}</code>).
 * 
 * @author gl31
 * @date 01/01/2021
 */
public class DeclClass extends AbstractDeclClass {

    final private AbstractIdentifier name;
    final private SymbolTable.Symbol symbol;
    private AbstractIdentifier sclasse;
    private ClassDefinition currentClass;
    private ClassDefinition superClass;
    private ClassType type;
    private ListDeclField declField;
    private ListDeclMeth declMeth;

    public DeclClass(AbstractIdentifier name, AbstractIdentifier sclasse,
                     ListDeclField declField, ListDeclMeth declMeth) {
        Validate.notNull(name);
        Validate.notNull(declField);
        Validate.notNull(declMeth);
        this.name = name;
        this.sclasse = sclasse;
        this.symbol = name.getName();
        this.declField = declField;
        this.declMeth = declMeth;
    }

    /**
     * Static method for declaring Object class
     *
     * @param compiler
     * @throws ContextualError
     */

    public static void declObject(DecacCompiler compiler) throws ContextualError{
        SymbolTable.Symbol s = DecacCompiler.symbolTable.get("Object");
        ListInst bodyEquals = new ListInst();
        Identifier other = new Identifier(DecacCompiler.symbolTable.create("other"));

        bodyEquals.add(new Return(new Equals(new This(), other)));

        ListDeclParam paramDecl = new ListDeclParam();
        DeclParam objectParam = new DeclParam(new Identifier(s), other);
        paramDecl.add(objectParam);

        DeclMeth equals = new DeclMeth(
                new Identifier(DecacCompiler.symbolTable.get("boolean")),
                new Identifier(DecacCompiler.symbolTable.create("equals")),
                paramDecl, bodyEquals, new ListDeclVar());

        ClassType objectType = (ClassType) compiler.envType.get(s).getType();
        other.setType(objectType);

        equals.verifyDeclMeth(compiler, objectType.getDefinition().getMembers(), objectType.getDefinition());
        objectType.getDefinition().setNumberOfMethods(1);
        objectType.getDefinition().setNumberOfFields(0);
        objectType.getDefinition().listOfMethods.add(equals);
    }


    @Override
    public void decompile(IndentPrintStream s) {
        s.print("class ");
        name.decompile(s);
        if(sclasse != null){
            s.print(" extends ");
            sclasse.decompile(s);
            s.println(" {");
        }
        else{
            s.println(" {");
        }
        s.indent();
        declField.decompile(s);
        declMeth.decompile(s);
        s.unindent();
        s.println("}");
    }

    @Override
    protected void verifyClass(DecacCompiler compiler) throws ContextualError {
        //Add the class name in environment and create local env
        if (sclasse != null) {
            superClass = (ClassDefinition) compiler.envType.get(sclasse.getName());
            sclasse.setDefinition(superClass);
            if (superClass == null)
                throw new ContextualError("SuperClass non defined [1.3]", getLocation());
        }

        ClassDefinition tmp;
        if (sclasse == null) {
            // Set Object class as superclass
            tmp = (ClassDefinition) compiler.envType.get(DecacCompiler.symbolTable.get("Object"));
            sclasse = new Identifier(tmp.getType().getName());
            sclasse.setDefinition(tmp);
            superClass = tmp;
        }
        else {
            tmp = superClass;
        }

        this.type = new ClassType(symbol, getLocation(), tmp);

        try {
            compiler.envType.declare(symbol, type.getDefinition());

        } catch (Exception e) {
            throw new ContextualError("Class with same name already declared", getLocation());
        }
        currentClass = type.getDefinition();
        name.setDefinition(currentClass);
        currentClass.setNumberOfFields(tmp.getNumberOfFields());
        currentClass.setNumberOfMethods(tmp.getNumberOfMethods());
    }


    @Override
    protected void verifyClassMembers(DecacCompiler compiler)
            throws ContextualError {
        EnvironmentExp localEnv = currentClass.getMembers();
        if (currentClass.getSuperClass() != null){
            currentClass.setNumberOfFields(currentClass.getSuperClass().getNumberOfFields());
            currentClass.setNumberOfMethods(currentClass.getSuperClass().getNumberOfMethods());

            // Copy des methods de la superclass.
            currentClass.listOfMethods.addAll(superClass.listOfMethods);
        }
        declField.verifyListDeclField(compiler, localEnv, currentClass);
        declMeth.verifyListDeclMeth(compiler, localEnv, currentClass);
    }
    
    @Override
    protected void verifyClassBody(DecacCompiler compiler) throws ContextualError {
        TypeDefinition t = compiler.envType.get(name.getName());
        if (t!=null){
            declMeth.verifyListDeclMethBody(compiler, currentClass.getMembers(), currentClass);
        }
        else {
            throw new ContextualError("Could not find class in environment", getLocation());
        }
    }

    // Creation du code de la class Object.
    public static void codeGenObject(DecacCompiler compiler, ClassDefinition objectClass){
        Validate.isTrue(objectClass.getType().getName().getName().equals("Object"), "Cette méthode statique sert uniquement à initialiser la class Object.");
        
        compiler.addComment("Code de la table de méthodes de Object");
        compiler.addInstruction(new LOAD(new NullOperand(), Register.getR(0)));
        objectClass.setPileOffset(compiler.pile.getOffset());
        RegisterOffset objectAddr =  new RegisterOffset(compiler.pile.getOffset() , Register.GB);
        objectClass.setOperand(objectAddr);

        compiler.addInstruction(new STORE(Register.getR(0), objectAddr));
        compiler.pile.incOffset();
        compiler.pile.incrementTSTO();

        String nom = "Code.Object.equals";
        compiler.putLabel(nom);
        compiler.addInstruction(new LOAD(new LabelOperand(compiler.getLabel(nom)), Register.getR(0)));
        compiler.addInstruction(new STORE(Register.getR(0), new RegisterOffset(compiler.pile.getOffset() , Register.GB)));
        compiler.pile.incOffset();
        compiler.pile.incrementTSTO();

    }
    
    @Override
    protected void codeGenTab(DecacCompiler compiler){
        compiler.addComment("Code de la table de méthode de " + this.name.getName().getName());
        compiler.addInstruction(new LEA(new RegisterOffset(superClass.getPileOffset(), Register.GB), Register.getR(0)));
        currentClass.setPileOffset(compiler.pile.getOffset());
        RegisterOffset objectAddr =  new RegisterOffset(compiler.pile.getOffset() , Register.GB);
        currentClass.setOperand(objectAddr);

        compiler.addInstruction(new STORE(Register.getR(0), objectAddr));
        compiler.pile.incOffset();
        compiler.pile.incrementTSTO();
        for (AbstractDeclMeth meth : currentClass.listOfMethods){
            meth.codeGenTab(compiler, currentClass);
        }
    }

    @Override
    protected void codeGenMeth(DecacCompiler compiler){
        //On itère sur toutes les méthodes de classe
        for(AbstractDeclMeth meth : declMeth.getList()){
            meth.codeGenMeth(compiler, currentClass);
        }
    }

    @Override
    public void initFields(DecacCompiler compiler){

        // Creation du label d'initialisation
        compiler.clearProg();
        compiler.pile.resetValTsto();
        String initLabel = "init." + this.name.getName().getName();
        compiler.putLabel(initLabel);

        // Si la classe a une super classe autre qu'objet (qui ne contient pas de fields).
        if (currentClass.getSuperClass() != null && currentClass.getSuperClass() != compiler.envType.get(DecacCompiler.symbolTable.get("Object"))){

            RegisterOffset instanceAddr = new RegisterOffset(-2, Register.LB);
            compiler.addInstruction(new LOAD(instanceAddr, Register.R1));
            declField.initFieldsZero(compiler);

            compiler.addComment("Initialisation des champs de la superclass " + sclasse.getName().getName());

            // Un push/pop et BSR fait 2 stockage dans la pile.
            compiler.pile.incrementTSTO(3);

            compiler.addInstruction(new PUSH(Register.getR(1)));
            compiler.addInstruction(new BSR(compiler.getLabel("init." + currentClass.getSuperClass().getType().getName().getName())));
            compiler.addInstruction(new SUBSP(new ImmediateInteger(1)));
        }

        RegisterOffset instanceAddr = new RegisterOffset(-2, Register.LB);
        compiler.addInstruction(new LOAD(instanceAddr, Register.R1));
        declField.initFields(compiler);
        compiler.addInstruction(new RTS());

        // Ajout TSTO et label.
        if (compiler.pile.getValTsto() != 0 && !(compiler.getCompilerOptions().getNoCheck())) {
            compiler.getProgram().addFirst(new BOV(GenUtils.getErrorLabel("stack_overflow_error")));
            compiler.getProgram().addFirst(new TSTO(compiler.pile.getValTsto()));
        }
        compiler.getProgram().addFirst(new Line(compiler.getLabel(initLabel)));

        compiler.mergeProgs();
        compiler.clearProg();
    }


    @Override
    protected
    void iterChildren(TreeFunction f) {
        name.iter(f);
        if (sclasse != null) {
            sclasse.iter(f);
        }
        declField.iter(f);
        declMeth.iter(f);

    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        name.prettyPrint(s, prefix, false);
        if (sclasse != null) {
            sclasse.prettyPrint(s, prefix, false);
        }
        declField.prettyPrint(s, prefix, false);
        declMeth.prettyPrint(s, prefix, true);
    }

}
