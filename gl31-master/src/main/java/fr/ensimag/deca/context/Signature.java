package fr.ensimag.deca.context;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Signature of a method (i.e. list of arguments)
 *
 * @author gl31
 * @date 01/01/2021
 */
public class Signature {
    List<Type> args = new ArrayList<Type>();

    public void add(Type t) {
        args.add(t);
    }
    public Type paramNumber(int n) {
        return args.get(n);
    }
    
    public int size() {
        return args.size();
    }

    public List<Type> getArgs(){
        return args;
    }

    public boolean subSign(Signature superSign){
        if (args.size() != superSign.size()){
            return false;
        }
        for (int i=0;i<args.size(); i++){
            Type t1 = paramNumber(i);
            Type t2 = superSign.paramNumber(i);
            if (!Type.subType(t1, t2))
                return false;
        }
        return true;
    }
}
