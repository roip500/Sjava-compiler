package oop.ex6.main.syntaxVerifier.whileif;

/**
 * exception used for the whileIf variables
 */
public class WhileIfVariableException extends GeneralWhileIfException{
    public WhileIfVariableException(String s) {
        super("invalid variable used -> " + s);
    }
}