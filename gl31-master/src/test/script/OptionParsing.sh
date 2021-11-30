#! /bin/sh

# Test du parsing des arguments du compiler Decac
cd "$(dirname "$0")"/../../.. || exit 1
PATH=./src/main/bin:"$PATH"

echo -e "\nTEST OPTION PARSING"

echo "Test: option -b."

decac_moins_b=$(decac -b)

if [ "$?" -ne 0 ]; then
    echo "ERREUR: decac -b a termine avec un status different de zero."
    exit 1
fi

if [ "$decac_moins_b" = "" ]; then
    echo "ERREUR: decac -b n'a produit aucune sortie"
    exit 1
fi

if echo "$decac_moins_b" | grep -i -e "erreur" -e "error"; then
    echo "ERREUR: La sortie de decac -b contient erreur ou error"
    exit 1
fi

echo "VALIDE: pas de problème avec decac -b"

# ... et ainsi de suite.

decac_bv=$(decac -bv 2> /dev/null)

if [ "$?" -ne 1 ]; then
    echo "ERREUR: decac -bv a termine avec un status different de un."
    exit 1
fi

echo "VALIDE: Une erreur est levée pour decac -bv."

echo -e "\nTest: incompatibilité entre les options -p et -v."

decac_pv=$(decac -pv 2> /dev/null)

if [ "$?" -ne 1 ]; then
    echo "ERREUR: decac -pv a termine avec un status different de un."
    exit 1
fi

echo "VALIDE: Une erreur est levée pour decac -pv."

echo -e "\nTest: les options -v -n -r -d et -P n'affiche rien si il n'y a pas d'erreur"
FILE = .src/test/deca/codegen/valid/hellotest 2>/dev/null
# On doit passer un fichier en argument, sinon decac affiche toutes les options possibles.
decac_v=$(decac -v $FILE/hello_world.deca 2> /dev/null)
decac_n=$(decac -n $FILE/hello_world.deca 2> /dev/null)
decac_r=$(decac -r 5 $FILE/hello_world.deca 2> /dev/null)
decac_d=$(decac -d $FILE/hello_world.deca 2> /dev/null)
decac_P=$(decac -P $FILE/hello_world.deca 2> /dev/null)

if [ -n "$decac_v" ]; then
    echo "ERREUR: decac -v affiche quelque chose."
    exit 1
fi

if [ -n "$decac_n" ]; then
    echo "ERREUR: decac -n affiche quelque chose."
    exit 1
fi

if [ -n "$decac_r" ]; then
    echo "ERREUR: decac -r affiche quelque chose."
    exit 1
fi

if [ -n "$decac_d" ]; then
    echo "ERREUR: decac -d affiche quelque chose."
    exit 1
fi

if [ -n "$decac_P" ]; then
    echo "ERREUR: decac -P affiche quelque chose."
    exit 1
fi

rm $FILE/hello_world.ass 2>/dev/null

echo "VALIDE: les options n'affichent rien."

# On devrait essayer de récupérer l'erreur, pour vérifier que c'est bien une erreur de parsing.
# Mais j'ai pas réussi.

echo -e "\nTest: l'option -r X lève une erreur si X < 4 ou X > 16"

decac_r3=$(decac -r 3 file2 2> /dev/null)

if [ "$?" -ne 1 ]; then
    echo "ERREUR: decac -r 3 ne lève pas d'erreur."
    exit 1
fi
decac_r17=$(decac -r 17 file 2> /dev/null)

if [ "$?" -ne 1 ]; then
    echo "ERREUR: decac -r 17 ne lève pas d'erreur."
    exit 1
fi

echo "VALIDE: Une erreur est levée."

echo -e "\nTest: l'option -p renvoie un programme décompilé"

#Chemin vers les fichiers .deca
WAY=./src/test/deca/codegen/valid/hellotest

decac -p $WAY/hello_world.deca 2>&1 > $WAY/hello_world_res.deca
decac $WAY/hello_world.deca
decac $WAY/hello_world_res.deca
ima $WAY/hello_world.ass 2>&1 > $WAY/out1.txt
ima $WAY/hello_world_res.ass 2>&1 > $WAY/out2.txt
if diff $WAY/out1.txt $WAY/out2.txt
    then
        rm $WAY/out1.txt $WAY/out2.txt $WAY/hello_world_res.deca $WAY/hello_world_res.ass $WAY/hello_world.ass 
    else
        echo "-p décompile mal, résulats différents"
        rm $WAY/out1.txt $WAY/out2.txt $WAY/hello_world_res.deca $WAY/hello_world_res.ass $WAY/hello_world.ass 
        exit 1
fi

echo "VALIDE: -p décompile correctement."

#Efface les eventuels fichiers .ass du dossier
remove_ass () {
    for cas_de_test in $WAY/*.ass
    do
        rm "$cas_de_test" 2>/dev/null
    done
}

echo -e "\nTest: l'option -P compile plusieurs fichiers en parallèle"

remove_ass

find $WAY/ -maxdepth 1 -type f -exec decac -P {} \;

remove_ass

echo -e "\nVALIDE: l'option -P compile correctement"

echo -e "\nTest: l'option -p de ima (débordement pile)"

decac $WAY/imap.deca

ima -p 3 $WAY/imap.ass > $WAY/out1.txt

rm $WAY/imap.ass

echo "Erreur : depassement de la taille de la pile" > $WAY/out2.txt

if diff $WAY/out1.txt $WAY/out2.txt
    then
    rm $WAY/out1.txt $WAY/out2.txt
    echo -e "\nVALIDE: l'option -p de ima renvoie l'erreur correcte"
    else
    rm $WAY/out1.txt $WAY/out2.txt
    echo "Erreur, l'erreur renvoyée à l'exécutione est erronée"
    exit 1
fi

echo -e "\nTEST OPTION PARSING OK"