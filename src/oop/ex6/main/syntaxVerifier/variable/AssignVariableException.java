package oop.ex6.main.syntaxVerifier.variable;

/**
 * exception used when variable is being assigned
 */
public class AssignVariableException extends GeneralVariableException {
    public AssignVariableException(String s) {
        super("invalid assignment -> " + s);
    }
}
