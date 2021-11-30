#! /bin/sh

#Script de test codegen pour le langage sans-objet de Deca

#Version initiale : 14/01/2021

#On compile une série de programmes en vérifiant que la sortie est celle
#attendue (résultats stockés dans les .txt), si les programmes sont invalides
#on vérifie qu'aucun fichier .ass n'a été créé (erreur à la compilation)

cd "$(dirname "$0")"/../../.. || exit 1

#Chemin vers les programmes .deca valides à tester
WAY=./src/test/deca/codegen

#Efface les eventuels fichiers .ass du dossier
remove_ass () {
    for cas_de_test in $WAY/valid/sansobjet/*.ass
    do
        rm "$cas_de_test" 2>/dev/null
    done
}

DE=.deca
AS=.ass
TE=.txt

echo "SANSOBJET CODEGEN"

remove_ass

echo "Tests valides:"

for cas_de_test in $WAY/valid/sansobjet/*.deca
do
    echo "$cas_de_test :"
    decac $cas_de_test
    if(! test -f "${cas_de_test%$DE}$AS")
    then
        echo "Le fichier .ass n'a pas été créé"
        remove_ass
        exit 1
    fi
    ima ${cas_de_test%$DE}$AS > $WAY/resultat.txt
    if diff $WAY/resultat.txt ${cas_de_test%$DE}$TE > /dev/null
    then
        echo "Le résultat est celui attendu"
        rm $WAY/resultat.txt
    else
        echo "Erreur: le résultat n'est pas celui attendu"
        echo "Attendu:"
        cat ${cas_de_test%$DE}$TE
        echo "Ce qui est sorti:"
        cat $WAY/resultat.txt
        rm $WAY/resultat.txt
        remove_ass
        exit 1
    fi
done

echo "Tests invalides:"

for cas_de_test in $WAY/invalid/sansobjet/*.deca
do
    echo "$cas_de_test :"
    decac $cas_de_test > /dev/null
    if(test -f "${cas_de_test%$DE}$AS")
    then
        echo "ERREUR: Un fichier .ass a été créé"
        rm ${cas_de_test%$DE}$AS
        exit 1
    else
        echo "Résultat attendu"
    fi
done

remove_ass

echo "SANSOBJET CODEGEN OK"
