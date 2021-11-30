parser grammar DecaParser;

options {
    // Default language but name it anyway
    //
    language  = Java;

    // Use a superclass to implement all helper
    // methods, instance variables and overrides
    // of ANTLR default methods, such as error
    // handling.
    //
    superClass = AbstractDecaParser;

    // Use the vocabulary generated by the accompanying
    // lexer. Maven knows how to work out the relationship
    // between the lexer and parser and will build the
    // lexer before the parser. It will also rebuild the
    // parser if the lexer changes.
    //
    tokenVocab = DecaLexer;

}

// which packages should be imported?
@header {
    import fr.ensimag.deca.tree.*;
    import java.io.PrintStream;
    import fr.ensimag.deca.DecacCompiler;
    import fr.ensimag.deca.syntax.DecaRecognitionException;
}

@members {
    @Override
    protected AbstractProgram parseProgram() {
        return prog().tree;
    }
}

prog returns[AbstractProgram tree]
    : list_classes main EOF {
            assert($list_classes.tree != null);
            assert($main.tree != null);
            $tree = new Program($list_classes.tree, $main.tree);
            setLocation($tree, $list_classes.start);
        }
    ;

main returns[AbstractMain tree]
    : /* epsilon */ {
            $tree = new EmptyMain();
        }
    | block {
            assert($block.decls != null);
            assert($block.insts != null);
            $tree = new Main($block.decls, $block.insts);
            setLocation($tree, $block.start);
        }
    ;

block returns[ListDeclVar decls, ListInst insts]
    : OBRACE list_decl list_inst CBRACE {
            assert($list_decl.tree != null);
            assert($list_inst.tree != null);
            $decls = $list_decl.tree;
            $insts = $list_inst.tree;
        }
    ;

list_decl returns[ListDeclVar tree]
@init   {
            $tree = new ListDeclVar();
        }
    : decl_var_set[$tree]*
    ;

decl_var_set[ListDeclVar l]
    : type list_decl_var[$l,$type.tree] SEMI
    ;

list_decl_var[ListDeclVar l, AbstractIdentifier t]
    : dv1=decl_var[$t] {
        setLocation($dv1.tree, $dv1.start);
        $l.add($dv1.tree);
        } (COMMA dv2=decl_var[$t] {
            setLocation($dv2.tree, $dv2.start);
            $l.add($dv2.tree);
        }
      )*
    ;

decl_var[AbstractIdentifier t] returns[AbstractDeclVar tree]
@init   {
            assert($t != null);
        }
    // On peut surement factoriser, mais j'arrive pas avec la syntax ANTLR.
    : i=ident {
            assert($i.tree != null);
            setLocation($i.tree, $ident.start);
            NoInitialization noIni = new NoInitialization();
            setLocation(noIni, $i.start);
            $tree = new DeclVar($t, $i.tree, noIni);
        }
      (EQUALS e=expr {
            assert($expr.tree != null);
            setLocation($i.tree, $ident.start);
            Initialization ini = new Initialization($expr.tree);
            setLocation(ini, $e.start);
            $tree = new DeclVar($t, $i.tree, ini);
        }
      )? {
        }
    ;

list_inst returns[ListInst tree]
@init   {
        $tree = new ListInst();
        }
    : (i=inst {
         assert($i.tree != null);
         $tree.add($i.tree);
        }
      )*
    ;

inst returns[AbstractInst tree]
    : e1=expr SEMI {
            assert($e1.tree != null);
            $tree = $e1.tree;
            setLocation($tree, $e1.start);
        }
    | SEMI {
            $tree = new NoOperation();
        }
    | PRINT OPARENT list_expr CPARENT SEMI {
            assert($list_expr.tree != null);
            /* Print takes a boolean argument, it is set to true for printx and printlnx only.*/
            $tree = new Print(false, $list_expr.tree);
            setLocation($tree, $list_expr.start);
        }
    | PRINTLN OPARENT list_expr CPARENT SEMI {
            assert($list_expr.tree != null);
            $tree = new Println(false, $list_expr.tree);
            setLocation($tree, $list_expr.start);
        }
    | PRINTX OPARENT list_expr CPARENT SEMI {
            assert($list_expr.tree != null);
            $tree = new Print(true, $list_expr.tree);
            setLocation($tree, $list_expr.start);

        }
    | PRINTLNX OPARENT list_expr CPARENT SEMI {
            assert($list_expr.tree != null);
            $tree = new Println(true, $list_expr.tree);
            setLocation($tree, $list_expr.start);
        }
    | if_then_else {
            assert($if_then_else.tree != null);
            $tree = $if_then_else.tree;
        }
    | WHILE OPARENT condition=expr CPARENT OBRACE body=list_inst CBRACE {
            assert($condition.tree != null);
            assert($body.tree != null);
            $tree = new While($condition.tree, $body.tree);
            setLocation($tree, $condition.start);
        }
    | RETURN expr SEMI {
            assert($expr.tree != null);
            $tree = new Return($expr.tree);
            setLocation($tree, $expr.start);
        }
    ;

if_then_else returns[IfThenElse tree]
@init {
        ListInst lastElse = new ListInst();
      }
    : if1=IF OPARENT condition=expr CPARENT OBRACE li_if=list_inst CBRACE {
        assert($condition.tree != null);
        assert($li_if.tree != null);
        $tree = new IfThenElse($condition.tree, $li_if.tree, lastElse);
        setLocation($tree, $condition.start);
        }
      (ELSE elsif=IF OPARENT elsif_cond=expr CPARENT OBRACE elsif_li=list_inst CBRACE {
        assert($elsif_cond.tree != null);
        ListInst temp = new ListInst();
        IfThenElse newBranch = new IfThenElse($elsif_cond.tree, $elsif_li.tree, temp);
        setLocation(newBranch, $elsif_cond.start);
        lastElse.add(newBranch);
        lastElse = temp;
        }
      )*
      (ELSE OBRACE li_else=list_inst CBRACE {
        for(AbstractInst inst : $li_else.tree.getList()) {
            lastElse.add(inst);
        }

        }
      )?
    ;

list_expr returns[ListExpr tree]
@init   {
            $tree = new ListExpr();
        }
    : (e1=expr {
            assert($e1.tree != null);
            $tree.add($e1.tree);
        }
       (COMMA e2=expr {
            assert($e2.tree != null);
            $tree.add($e2.tree);
        }
       )* )?
    ;

expr returns[AbstractExpr tree]
    : assign_expr {
            assert($assign_expr.tree != null);
            $tree = $assign_expr.tree;
            setLocation($tree, $assign_expr.start);
        }
    ;

assign_expr returns[AbstractExpr tree]
    : e=or_expr (
        /* condition: expression e must be a "LVALUE" */ {
            if (! ($e.tree instanceof AbstractLValue)) {
                throw new InvalidLValue(this, $ctx);
            }
        }
        EQUALS e2=assign_expr {
            assert($e.tree != null);
            assert($e2.tree != null);
            $tree = new Assign( (AbstractLValue) $e.tree, $e2.tree);
            setLocation($tree, $e.start);
        }
      | /* epsilon */ {
            assert($e.tree != null);
            $tree = $e.tree;
            setLocation($tree, $e.start);
        }
      )
    ;

or_expr returns[AbstractExpr tree]
    : e=and_expr {
            assert($e.tree != null);
            $tree = $e.tree;
            setLocation($tree, $e.start);

        }
    | e1=or_expr OR e2=and_expr {
            assert($e1.tree != null);
            assert($e2.tree != null);
            $tree = new Or($e1.tree, $e2.tree);
            setLocation($tree, $e1.start);
       }
    ;

and_expr returns[AbstractExpr tree]
    : e=eq_neq_expr {
            assert($e.tree != null);
            $tree = $e.tree;
            setLocation($tree, $e.start);
        }
    |  e1=and_expr AND e2=eq_neq_expr {
            assert($e1.tree != null);                         
            assert($e2.tree != null);
            $tree = new And($e1.tree, $e2.tree);
            setLocation($tree, $e1.start);
        }
    ;

eq_neq_expr returns[AbstractExpr tree]
    : e=inequality_expr {
            assert($e.tree != null);
            $tree = $e.tree;
            setLocation($tree, $e.start);
        }
    | e1=eq_neq_expr EQEQ e2=inequality_expr {
            assert($e1.tree != null);
            assert($e2.tree != null);
            $tree = new Equals($e1.tree, $e2.tree);
            setLocation($tree, $e1.start);
        }
    | e1=eq_neq_expr NEQ e2=inequality_expr {
            assert($e1.tree != null);
            assert($e2.tree != null);
            $tree = new NotEquals($e1.tree, $e2.tree);
            setLocation($tree, $e1.start);
        }
    ;

inequality_expr returns[AbstractExpr tree]
    : e=sum_expr {
            assert($e.tree != null);
            $tree = $e.tree;
            setLocation($tree, $e.start);
        }
    | e1=inequality_expr LEQ e2=sum_expr {
            assert($e1.tree != null);
            assert($e2.tree != null);
            $tree = new LowerOrEqual($e1.tree, $e2.tree);
            setLocation($tree, $e1.start);
        }
    | e1=inequality_expr GEQ e2=sum_expr {
            assert($e1.tree != null);
            assert($e2.tree != null);
            $tree = new GreaterOrEqual($e1.tree, $e2.tree);
            setLocation($tree, $e1.start);
        }
    | e1=inequality_expr GT e2=sum_expr {
            assert($e1.tree != null);
            assert($e2.tree != null);
            $tree = new Greater($e1.tree, $e2.tree);
            setLocation($tree, $e1.start);
        }
    | e1=inequality_expr LT e2=sum_expr {
            assert($e1.tree != null);
            assert($e2.tree != null);
            $tree = new Lower($e1.tree, $e2.tree);
            setLocation($tree, $e1.start);
        }
    | e1=inequality_expr INSTANCEOF type {
            assert($e1.tree != null);
            assert($type.tree != null);
            $tree = new InstanceOf($e1.tree, $type.tree);
            setLocation($tree, $e1.start);
        }
    ;


sum_expr returns[AbstractExpr tree]
    : e=mult_expr {
            assert($e.tree != null);
            $tree = $e.tree;
            setLocation($tree, $e.start);

        }
    | e1=sum_expr PLUS e2=mult_expr {
            assert($e1.tree != null);
            assert($e2.tree != null);
            $tree = new Plus($e1.tree, $e2.tree);
            setLocation($tree, $e1.start);
        }
    | e1=sum_expr MINUS e2=mult_expr {
            assert($e1.tree != null);
            assert($e2.tree != null);
            $tree = new Minus($e1.tree, $e2.tree);
            setLocation($tree, $e1.start);
        }
    ;

mult_expr returns[AbstractExpr tree]
    : e=unary_expr {
            assert($e.tree != null);
            $tree = $e.tree;
            setLocation($tree, $e.start);

        }
    | e1=mult_expr TIMES e2=unary_expr {
            assert($e1.tree != null);                                         
            assert($e2.tree != null);
            $tree = new Multiply($e1.tree, $e2.tree);
            setLocation($tree, $e1.start);
        }
    | e1=mult_expr SLASH e2=unary_expr {
            assert($e1.tree != null);                                         
            assert($e2.tree != null);
            $tree = new Divide($e1.tree, $e2.tree);
            setLocation($tree, $e1.start);
        }
    | e1=mult_expr PERCENT e2=unary_expr {
            assert($e1.tree != null);                                                                          
            assert($e2.tree != null);
            $tree = new Modulo($e1.tree, $e2.tree);
            setLocation($tree, $e1.start);
        }
    ;

unary_expr returns[AbstractExpr tree]
    : op=MINUS e=unary_expr {
            assert($e.tree != null);
            $tree = new UnaryMinus($e.tree);
            setLocation($tree, $e.start);
        }
    | op=EXCLAM e=unary_expr {
            assert($e.tree != null);
            $tree = new Not($e.tree);
            setLocation($tree, $e.start);
        }
    | select_expr {
            assert($select_expr.tree != null);
            $tree = $select_expr.tree;
            setLocation($tree, $select_expr.start);
        }
    ;

select_expr returns[AbstractExpr tree]
    : e=primary_expr {
            assert($e.tree != null);
            $tree = $e.tree;
            setLocation($tree, $e.start);
        }
    | e1=select_expr DOT i=ident {
            assert($e1.tree != null);
            assert($i.tree != null);
            setLocation($i.tree, $ident.start);

        }
        (o=OPARENT args=list_expr CPARENT {
            // we matched "e1.i(args)"
            assert($args.tree != null);
            $tree = new MethodCall($e1.tree, $i.tree, $args.tree);
            setLocation($tree, $e1.start);
        }
        | /* epsilon */ {
            // we matched "e.i"
            $tree = new Selection($e1.tree, $i.tree);
            setLocation($tree, $e1.start);
        }
        )
    ;

primary_expr returns[AbstractExpr tree]
    : ident {
            assert($ident.tree != null);
            $tree = $ident.tree;
            setLocation($tree, $ident.start);

        }
    | m=ident OPARENT args=list_expr CPARENT {
            assert($args.tree != null);
            assert($m.tree != null);
            $tree = new MethodCall(null, $m.tree, $args.tree);
            setLocation($tree, $m.start);
        }
    | OPARENT expr CPARENT {
            assert($expr.tree != null);
            $tree = $expr.tree;
            setLocation($tree, $expr.start);
        }
    | READINT OPARENT CPARENT {
            $tree = new ReadInt();
        }
    | READFLOAT OPARENT CPARENT {
            $tree = new ReadFloat();
        }
    | NEW ident OPARENT CPARENT {
            assert($ident.tree != null);
            $tree = new New($ident.tree);
            setLocation($tree, $ident.start);
        }
    | cast=OPARENT type CPARENT OPARENT expr CPARENT {
            assert($type.tree != null);
            assert($expr.tree != null);
            $tree = new Cast($type.tree, $expr.tree);
            setLocation($tree, $type.start);
        }
    | literal {
            assert($literal.tree != null);
            $tree = $literal.tree;
            setLocation($tree, $literal.start);
        }
    ;

type returns[AbstractIdentifier tree]
    : ident {
            assert($ident.tree != null);
            $tree = $ident.tree;
            setLocation($tree, $ident.start);
        }
    ;

literal returns[AbstractExpr tree]
    : INT {
        try{
            $tree = new IntLiteral(Integer.parseInt($INT.text));
            } catch (NumberFormatException e) {
                throw new ParsingError(this, $INT, "Entier non inclus entre -2^31 + 1 et 2^31 - 1");
            }
        }
    | fd=FLOAT {
        try{
            $tree = new FloatLiteral(Float.parseFloat($fd.text));
            } catch (NumberFormatException e) {
                throw new ParsingError(this, $fd, "Flottant non valide");
            }
        }
    | STRING {
       $tree = new StringLiteral($STRING.text);
        }
    | TRUE {
        $tree = new BooleanLiteral(Boolean.parseBoolean($TRUE.text));
        }
    | FALSE {
        $tree = new BooleanLiteral(Boolean.parseBoolean($FALSE.text));
        }
    | THIS {
        $tree = new This();
        }
    | NULL {
        $tree = new Null();
        }
    ;


ident returns[AbstractIdentifier tree]
    : IDENT {
          $tree = new Identifier(DecacCompiler.symbolTable.create($IDENT.text));
          setLocation($tree, $IDENT);
        }
    ;

/****     Class related rules     ****/

list_classes returns[ListDeclClass tree]
@init {
        $tree = new ListDeclClass();
      }:
      (c1=class_decl {
        $tree.add($c1.tree);
        }
      )*
    ;

class_decl returns [AbstractDeclClass tree]
    : CLASS name=ident superclass=class_extension OBRACE class_body CBRACE {
        assert($name.tree != null);
        $tree = new DeclClass($name.tree, $superclass.tree, $class_body.fields, $class_body.methods);
        setLocation($tree, $CLASS);
        setLocation($name.tree, $name.start);
        }
    ;

class_extension returns[AbstractIdentifier tree]
    : EXTENDS ident {
        $tree = $ident.tree;
        setLocation($tree, $ident.start);
        }
    | /* epsilon */ {
        $tree = null;
        }
    ;

class_body returns [ListDeclMeth methods, ListDeclField fields]
@init {
        $methods = new ListDeclMeth();
        $fields = new ListDeclField();
}
    : (m=decl_method {
        $methods.add($m.tree);
        }
      | f=decl_field_set[$fields]
      )*
    ;

decl_field_set[ListDeclField listFields]
    : visi=visibility t=type list_decl_field[$listFields, $visi.v, $t.tree] SEMI
    ;

visibility returns[Visibility v]
    : /* epsilon */ {
        $v = Visibility.PUBLIC;
        }
    | PROTECTED {
        $v = Visibility.PROTECTED;
        }
    ;

list_decl_field[ListDeclField listFields, Visibility v, AbstractIdentifier t]
    : dv1=decl_field[$v, $t]{
        setLocation($dv1.tree, $dv1.start);
        $listFields.add($dv1.tree);}
        (COMMA dv2=decl_field[$v, $t]{
            setLocation($dv2.tree, $dv2.start);
            $listFields.add($dv2.tree);
        }
      )*
    ;

decl_field[Visibility v, AbstractIdentifier t] returns [AbstractDeclField tree]
@init {
        assert($t != null);
      }
    : i=ident {
            assert($i.tree != null);
            setLocation($i.tree, $ident.start);
            NoInitialization noIni = new NoInitialization();
            setLocation(noIni, $i.start);
            $tree = new DeclField($v, $t, $i.tree, noIni);
        }
      (EQUALS e=expr {
                  assert($expr.tree != null);
                  setLocation($i.tree, $ident.start);
                  Initialization ini = new Initialization($expr.tree);
                  setLocation(ini, $e.start);
                  $tree = new DeclField($v, $t, $i.tree, ini);
        }
      )? {
        }
    ;

decl_method returns[AbstractDeclMeth tree]
@init {
}
    : type ident OPARENT params=list_params CPARENT (block {
        $tree = new DeclMeth($type.tree, $ident.tree, $params.listDeclParam, $block.insts, $block.decls);
        setLocation($tree, $type.start);
        setLocation($ident.tree, $ident.start);
        }
      | ASM OPARENT code=multi_line_string CPARENT SEMI {
        $tree = new DeclMethAss($type.tree, $ident.tree, $params.listDeclParam, $code.text);
        setLocation($tree, $type.start);
        setLocation($ident.tree, $ident.start);
        }
      ) {
        }
    ;

list_params returns[ListDeclParam listDeclParam]
@init {
        $listDeclParam = new ListDeclParam();
}
    : (p1=param {
        assert($p1.tree != null);
        $listDeclParam.add($p1.tree);
        } (COMMA p2=param {
                assert($p2.tree != null);
                $listDeclParam.add($p2.tree);
        }
      )*)?
    ;
    
multi_line_string returns[String text, Location location]
    : s=STRING {
            $text = $s.text;
            $location = tokenLocation($s);
        }
    | s=MULTI_LINE_STRING {
            $text = $s.text;
            $location = tokenLocation($s);
        }
    ;

param returns[AbstractDeclParam tree]
    : type ident {
        $tree = new DeclParam($type.tree, $ident.tree);
        setLocation($tree, $type.start);
        setLocation($ident.tree, $ident.start);
        }
    ;
