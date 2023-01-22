package oop.ex6.main.syntaxVerifier.whileif;

/**
 * general exception for the whileIF class
 */
class GeneralWhileIfException extends Exception {
    public GeneralWhileIfException(String s) {
        super("Method exception -> " + s);
    }
}

/**
 * exception used for the whileIf variables
 */
class WhileIfVariableException extends GeneralWhileIfException{
    public WhileIfVariableException(String s) {
        super("invalid variable used -> " + s);
    }
}
