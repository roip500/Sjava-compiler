package oop.ex6.main.syntaxVerifier.variable;

/**
 * general exception for the variable class
 */
public class GeneralVariableException extends Exception {
    public GeneralVariableException(String s) {
        super("Variable exception -> " + s);
    }
}


