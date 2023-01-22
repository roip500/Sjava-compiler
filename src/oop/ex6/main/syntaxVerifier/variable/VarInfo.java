package oop.ex6.main.syntaxVerifier.variable;

public class VarInfo {
    /**
     * class that holds the information on the variable
     */
    private final String name;
    private final String type;
    private boolean initialized;
    private final boolean isFinal;


    /**
     * constructor for VarInfo
     *
     * @param type        - assigned type value
     * @param initialized - assigned boolean value
     */
    public VarInfo(String name, String type, boolean initialized, boolean isFinal) {
        this.name = name;
        this.type = type;
        this.initialized = initialized;
        this.isFinal = isFinal;
    }

    /**
     * returns the type
     *
     * @return type value
     */
    public String getType() {
        return type;
    }

    /**
     * returns the initialized argument value
     *
     * @return boolean
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * sets the initialized argument to true
     */
    public void setInitialized() {
        initialized = true;
    }

    /**
     * returns if the variable is final
     *
     * @return boolean
     */
    public boolean isFinal() {
        return isFinal;
    }

    /**
     * returns the name of the variable
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * this function is used to deAssign global vars who had been initialised inside a method' and now that
     * the method is over they no longer have been assigned a value
     */
    public void deAssign(){
        this.initialized = false;
    }
}

