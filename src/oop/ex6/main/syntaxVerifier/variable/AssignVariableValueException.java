package oop.ex6.main.syntaxVerifier.variable;

/**
 * exception used when variable is being assigned and the value has an error
 */
public class AssignVariableValueException extends AssignVariableException {
    public AssignVariableValueException(String s) {
        super("value given isn't legal -> " + s);
    }
}
