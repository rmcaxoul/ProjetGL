package fr.ensimag.deca.codegen;

/**
 * Classe de gestion des indices de registres
 */

public class RegisterGestion {
    
    private int nbRegister = 16;
    
    private int currentRegId = 2;
    
    private int initRegister = 2;
    
    private int mainRegId = 2;
    
    public int getMainRegId(){
        return this.mainRegId;
    }
    
    public void setMainRegId(int i){
        this.mainRegId = 1;
    }
    
    public void incrementMainRegId(){
        this.mainRegId++;
    }
    
    public int getCurrentRegId(){
        return this.currentRegId;
    }
    
    public void resetCurrentRegId(){
        this.currentRegId = 2;
    }
    
    public void setCurrentRegId(int i){
        this.currentRegId = i;
    }
    
    public void incrementCurRegId(){
        this.currentRegId++;
    }

    public void decrementCurRegId() { this.currentRegId--;}
    
    public void setNbRegister(int i){
        nbRegister = i;
    }
    
    public int getNbRegister() {
        return nbRegister;
    }
    
    public int getInitRegister() {
        return initRegister;
    }
}
