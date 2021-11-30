lexer grammar DecaLexer;

options {
   language=Java;
   // Tell ANTLR to make the generated lexer class extend the
   // the named class, which is where any supporting code and
   // variables will be placed.
   superClass = AbstractDecaLexer;
}

@members {
}

// Mots reserves Deca
ASM : 'asm';
CLASS : 'class';
EXTENDS : 'extends';
ELSE : 'else';
FALSE : 'false';
IF : 'if';
INSTANCEOF : 'instanceof';
NEW : 'new';
NULL : 'null';
READINT : 'readInt';
READFLOAT : 'readFloat';
PRINT : 'print';
PRINTLN : 'println';
PRINTLNX : 'printlnx';
PRINTX : 'printx';
PROTECTED : 'protected';
RETURN : 'return';
THIS : 'this';
TRUE : 'true';
WHILE : 'while';

// Symboles speciaux Deca
LT : '<';
GT : '>';
EQUALS : '=';
PLUS : '+';
MINUS : '-';
TIMES : '*';
SLASH : '/';
PERCENT : '%';
DOT : '.';
COMMA : ',';
SEMI : ';';
OPARENT : '(';
CPARENT : ')';
OBRACE : '{';
CBRACE : '}';
EXCLAM : '!';
EQEQ : '==';
NEQ : '!=';
LEQ : '<=';
GEQ : '>=';
AND : '&&';
OR : '||';

// Identificateurs
fragment LETTER : ('a' .. 'z' | 'A' .. 'Z');
fragment DIGIT  : '0' .. '9';
IDENT           : (LETTER | '$' | '_')(LETTER | DIGIT | '$' | '_')*;

// Litteraux entiers
fragment POSITIVE_DIGIT : '1' .. '9';
INT                     : '0' |  POSITIVE_DIGIT DIGIT*;

// Litteraux flottants
fragment NUM        : DIGIT+;
fragment SIGN       : ('+' | '-')?;
fragment EXP        : ('E' | 'e') SIGN NUM;
fragment DEC        : NUM '.' NUM;
fragment FLOATDEC   : (DEC | DEC EXP) ('F' | 'f')?;
fragment DIGITHEX   : ('0' .. '9')|('A' .. 'F')|('a' .. 'f');
fragment NUMHEX     : DIGITHEX+;
fragment FLOATHEX   : ('0x' | '0X') NUMHEX '.' NUMHEX ('p' | 'P') SIGN NUM ('F' | 'f')?;

FLOAT : FLOATDEC | FLOATHEX;

// Chaines de caracteres
fragment STRING_CAR : ~('\\'|'"'|'\n');
STRING              : '"' (STRING_CAR | '\\"' | '\\\\')* '"';
MULTI_LINE_STRING   : '"' (STRING_CAR | '\n' | '\\"' | '\\\\')* '"';

// Commentaires
CLASSIC_COMM    : '/*' .*? '*/' {skip(); };
LINE_COMM       : '//' .*? '\n' {skip(); };

// Separateurs
WS  :   (' ' | '\t'| '\r' | '\n') {skip(); };

// Inclusion
fragment FILENAME : (LETTER|DIGIT|'.'|'-'|'_')+;
INCLUDE : '#include' (' ')* '"' FILENAME '"' {doInclude(getText()); };