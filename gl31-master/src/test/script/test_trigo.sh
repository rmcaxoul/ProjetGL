#! /bin/sh

#Script de test pour l'extension trigo

#Version initiale : 25/01/2021

cd "$(dirname "$0")"/../../.. || exit 1

#Chemin vers les fichiers
WAY=./src/test/deca/trigo

#Efface les eventuels fichiers .ass du dossier
remove_ass () {
    for cas_de_test in $WAY/*.ass
    do
        rm "$cas_de_test" 2>/dev/null
    done
}

remove_ass

echo "TRIGO"

echo "Tests basiques"

decac $WAY/sin.deca
if(! test -f "$WAY/sin.ass")
    then
        echo "Le fichier sin.ass n'a pas été créé"
        remove_ass
        exit 1
fi
decac $WAY/cos.deca
if(! test -f "$WAY/cos.ass")
    then
        echo "Le fichier cos.ass n'a pas été créé"
        remove_ass
        exit 1
fi
decac $WAY/asin.deca
if(! test -f "$WAY/asin.ass")
    then
        echo "Le fichier asin.ass n'a pas été créé"
        remove_ass
        exit 1
fi
decac $WAY/atan.deca
if(! test -f "$WAY/atan.ass")
    then
        echo "Le fichier atan.ass n'a pas été créé"
        remove_ass
        exit 1
fi
#Exécute les fichiers .ass
for cas_de_test in $WAY/*.ass
do
    ima "$cas_de_test"
done

remove_ass

echo "Tests comparatifs"

decac $WAY/test_sin.deca
ima $WAY/test_sin.ass > $WAY/deca_sin_res.txt
echo "sin:"
diff $WAY/java_sin_res.txt $WAY/deca_sin_res.txt

decac $WAY/test_cos.deca
ima $WAY/test_cos.ass > $WAY/deca_cos_res.txt
echo "cos:"
diff $WAY/java_cos_res.txt $WAY/deca_cos_res.txt

decac $WAY/test_asin.deca
ima $WAY/test_asin.ass > $WAY/deca_asin_res.txt
echo "asin:"
diff $WAY/java_asin_res.txt $WAY/deca_asin_res.txt

decac $WAY/test_atan.deca
ima $WAY/test_atan.ass > $WAY/deca_atan_res.txt
echo "atan:"
diff $WAY/java_atan_res.txt $WAY/deca_atan_res.txt

remove_ass

rm $WAY/deca_sin_res.txt $WAY/deca_cos_res.txt $WAY/deca_asin_res.txt $WAY/deca_atan_res.txt

echo "TRIGO OK"
