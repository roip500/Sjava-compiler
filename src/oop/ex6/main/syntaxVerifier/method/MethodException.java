package oop.ex6.main.syntaxVerifier.method;

class GeneralMethodException extends Exception {
    public GeneralMethodException(String s) {
        super("Method exception:\n" + s);
    }
}

class MethodDeclarationException extends GeneralMethodException {
    public MethodDeclarationException(String s) {
        super("invalid method declaration:\n" + s);
    }
}

class MethodVariablesException extends GeneralMethodException {
    public MethodVariablesException(String s) {
        super("invalid variable initialized:\n" + s);
    }
}

class MethodCalledException extends GeneralMethodException {
    public MethodCalledException(String s) {
        super("invalid call to method:\n" + s);
    }
}
