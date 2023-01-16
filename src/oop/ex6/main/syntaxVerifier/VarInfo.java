package oop.ex6.main.syntaxVerifier;

public class VarInfo {
    /**
     * class that holds the information on the variable
     */

    private final String type;
    private boolean initialized;
    private final boolean isFinal;


    /**
     * constructor for VarInfo
     *
     * @param type        - assigned type value
     * @param initialized - assigned boolean value
     */
    public VarInfo(String type, boolean initialized, boolean isFinal) {
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
}

