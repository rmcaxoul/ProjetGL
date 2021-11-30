#! /bin/sh

#Script de test syntaxique pour le langage complet de Deca

#Version initiale : 18/01/2021

#On teste d'abord un ensemble de fichiers invalides syntaxiquement
#Ensuite un ensemble de fichiers valides syntaxiquement
#Si leur syntaxe est en effet valide, on appelle decac -p et on stocke le
#résultat (programme né de la décompilation de l'arbre) dans un fichier .deca
#Ensuite on appelle decac -p à nouveau sur le fichier .deca créé à l'étape
#précédente et on stocke le résultat dans un deuxième fichier .deca
#Enfin on vérifie que les fichiers .deca sont identiques. Si non, il y a un
#problème dans la création de l'arbre syntaxique

cd "$(dirname "$0")"/../../.. || exit 1

#Chemin vers les fichiers .deca
WAY=./src/test/deca/syntax

PATH=./src/test/script/launchers:"$PATH"

test_synt_invalide () {
    if test_synt "$1" 2>&1 | grep -q -e "$1:[0-9][0-9]*:"
    then
        echo "Echec attendu pour test_synt sur $1."
    else
        echo "Succes inattendu de test_synt sur $1."
        exit 1
    fi
}

test_synt_valide () {
    if test_synt "$1" 2>&1 | grep -q -e "$1:[0-9][0-9]*:"
    then
        echo "Echec inattendu pour test_synt sur $1."
        rm $WAY/program_from_decompile1.deca $WAY/program_from_decompile2.deca 2>/dev/null
        exit 1
    else
        echo "Succes attendu de test_synt sur $1."
        decac -p $1 2>&1 > $WAY/program_from_decompile1.deca
        decac -p $WAY/program_from_decompile1.deca 2>&1 > $WAY/program_from_decompile2.deca
        if diff $WAY/program_from_decompile1.deca $WAY/program_from_decompile2.deca
        then
            echo "Arbre de $1 correct"
        else
            rm $WAY/program_from_decompile1.deca $WAY/program_from_decompile2.deca
            echo "Arbre de $1 incorrect"
            exit 1
        fi
    fi
}

echo "COMPLET-SYNT"

for cas_de_test in $WAY/invalid/complet/*.deca
do
    test_synt_invalide "$cas_de_test"
done

for cas_de_test in $WAY/valid/complet/*.deca
do
    test_synt_valide "$cas_de_test"
done

rm $WAY/program_from_decompile1.deca $WAY/program_from_decompile2.deca

echo "COMPLET-SYNT OK"
