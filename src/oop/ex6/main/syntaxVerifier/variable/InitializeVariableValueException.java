package oop.ex6.main.syntaxVerifier.variable;


/**
 * exception used when variable is being initialized and the value has an error
 */
public class InitializeVariableValueException extends InitializeVariableException {
    public InitializeVariableValueException(String s) {
        super("value given isn't legal -> " + s);
    }
}