#! /bin/sh
PATH=./src/test/script/launchers:./src/main/bin:"$PATH"

PATH=/matieres/4MMPGL/GL/global:"$PATH"

hello-lex.sh && hello-synt.sh && test_deca.sh && OptionParsing.sh && sansobjet-lex.sh && sansobjet-synt.sh && sansobjet-context.sh && sansobjet-codegen.sh && complet-lex.sh && complet-synt.sh && complet-context.sh && complet-codegen.sh
