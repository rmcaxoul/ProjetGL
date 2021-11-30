#! /bin/sh

#Script de test pour la commande decac

#Version initiale : 10/01/2021

cd "$(dirname "$0")"/../../.. || exit 1

#Chemin vers les programmes .deca à tester
WAY=./src/test/deca/codegen

#Efface les eventuels fichiers .ass du dossier
remove_ass () {
    for cas_de_test in $WAY/valid/hellotest/*.ass
    do
        rm "$cas_de_test" 2>/dev/null
    done
}

echo "TEST DECA"

remove_ass

#Compile les fichiers .deca
for cas_de_test in $WAY/valid/hellotest/*.deca
do
    if ! decac "$cas_de_test"
    then
        echo "Erreur à la compilation de $cas_de_test"
        exit 1
    fi
done

echo "Compilation OK"

#Exécute les fichiers .ass
for cas_de_test in $WAY/valid/hellotest/*.ass
do
    if ! ima "$cas_de_test" >/dev/null
    then
        echo "Erreur à l'exécution de $cas_de_test"
        remove_ass
        exit 1
    fi
done

echo "Exécution OK"

remove_ass

for cas_de_test in $WAY/invalid/hellotest/*
do
    if decac "$cas_de_test"
    then
        echo "Erreur, $cas_de_test ne doit pas être compilable"
        exit 1
    fi
done

echo "Vérification extension .deca OK"

echo "TEST DECA OK"

