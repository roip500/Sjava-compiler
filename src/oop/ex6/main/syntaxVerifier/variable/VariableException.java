package oop.ex6.main.syntaxVerifier.variable;

class GeneralVariableException extends Exception {
    public GeneralVariableException(String s) {
        super("Variable exception:\n" + s);
    }
}

