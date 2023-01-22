package oop.ex6.main.syntaxVerifier.whileif;

class GeneralWhileIfException extends Exception {
    public GeneralWhileIfException(String s) {
        super("Method exception:\n" + s);
    }
}

class WhileIfVariableException extends GeneralWhileIfException{
    public WhileIfVariableException(String s) {
        super("invalid variable used:\n" + s);
    }
}
