package oop.ex6.main.syntaxVerifier.method;

/**
 * exception used when method is being called
 */
public class MethodCalledException extends GeneralMethodException {
    public MethodCalledException(String s) {
        super("invalid call to method -> " + s);
    }
}
