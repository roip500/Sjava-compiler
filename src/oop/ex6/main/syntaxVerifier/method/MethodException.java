package oop.ex6.main.syntaxVerifier.method;

/**
 * general exception for the method class
 */
class GeneralMethodException extends Exception {
    public GeneralMethodException(String s) {
        super("Method exception -> " + s);
    }
}

/**
 * exception used for method declaration
 */
class MethodDeclarationException extends GeneralMethodException {
    public MethodDeclarationException(String s) {
        super("invalid method declaration -> " + s);
    }
}

/**
 * exception used for method variable setup
 */
class MethodVariablesException extends GeneralMethodException {
    public MethodVariablesException(String s) {
        super("invalid variable initialized -> " + s);
    }
}

/**
 * exception used when method is being called
 */
class MethodCalledException extends GeneralMethodException {
    public MethodCalledException(String s) {
        super("invalid call to method -> " + s);
    }
}
