package oop.ex6.main;

/**
 * general exception for the sjava class
 */
public class GeneralSJavaException extends Exception {
    public GeneralSJavaException(String s) {
        super("file exception -> " + s);
    }
}
