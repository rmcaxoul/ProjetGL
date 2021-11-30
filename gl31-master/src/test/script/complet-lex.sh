#! /bin/sh

#Script de test lexical pour le langage complet de Deca

#Version initiale : 18/01/2021

#On teste d'abord un ensemble de fichiers valides lexicalement
#Ensuite un ensemble de fichiers invalides lexicalement

#/!\ n'est pertinent que pour le langage complet

cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"

#Chemin vers les fichiers .deca
WAY=./src/test/deca/lex

test_lex_invalide () {
    if test_lex "$1" 2>&1 | grep -q -e "$1:[0-9][0-9]*:"
    then
        echo "Echec attendu pour test_lex sur $1."
    else
        echo "Succes inattendu de test_lex sur $1."
        exit 1
    fi
}

test_lex_valide () {
    if test_lex "$1" 2>&1 | grep -q -e "$1:[0-9][0-9]*:"
    then
        echo "Echec inattendu pour test_lex sur $1."
        exit 1
    else
        echo "Succes attendu de test_lex sur $1."
    fi
}

echo "COMPLET LEX"

for cas_de_test in $WAY/valid/complet/*.deca
do
    test_lex_valide "$cas_de_test"
done

for cas_de_test in $WAY/invalid/complet/*.deca
do
    test_lex_invalide "$cas_de_test"
done

echo "COMPLET LEX OK"

