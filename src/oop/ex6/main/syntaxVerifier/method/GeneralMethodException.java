package oop.ex6.main.syntaxVerifier.method;

/**
 * general exception for the method class
 */
public class GeneralMethodException extends Exception {
    public GeneralMethodException(String s) {
        super("Method exception -> " + s);
    }
}

