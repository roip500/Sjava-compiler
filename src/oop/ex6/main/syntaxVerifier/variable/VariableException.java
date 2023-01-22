package oop.ex6.main.syntaxVerifier.variable;

/**
 * general exception for the variable class
 */
class GeneralVariableException extends Exception {
    public GeneralVariableException(String s) {
        super("Variable exception -> " + s);
    }
}

/**
 * exception used when variable is being initialized
 */
class InitializeVariableException extends GeneralVariableException {
    public InitializeVariableException(String s) {
        super("invalid initialization -> " + s);
    }
}

/**
 * exception used when variable is being initialized and the value has an error
 */
class InitializeVariableValueException extends InitializeVariableException {
    public InitializeVariableValueException(String s) {
        super("value given isn't legal -> " + s);
    }
}

/**
 * exception used when variable is being assigned
 */
class AssignVariableException extends GeneralVariableException {
    public AssignVariableException(String s) {
        super("invalid assignment -> " + s);
    }
}

/**
 * exception used when variable is being assigned and the value has an error
 */
class AssignVariableValueException extends AssignVariableException {
    public AssignVariableValueException(String s) {
        super("value given isn't legal -> " + s);
    }
}