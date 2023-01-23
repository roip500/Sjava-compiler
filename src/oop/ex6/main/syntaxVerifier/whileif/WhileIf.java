package oop.ex6.main.syntaxVerifier.whileif;

import oop.ex6.main.syntaxVerifier.variable.VarInfo;
import oop.ex6.main.syntaxVerifier.variable.Variable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WhileIf {

    //exceptions text:
    private static final String ERROR1 = "if statement doesn't match the legal format";
    private static final String ERROR2 = "parameter given doesn't exist";
    private static final String ERROR3 = "parameter given isn't initialized";
    private static final String ERROR4 = "the parameter given type doesn't match the required type";

    //constants
    private static final int CONDITIONS_GROUP = 1;
    private static final String CONDITIONS_SPLITTER = "\\|\\||&&";

    //general class objects:
    private static final String CHAR = "char";
    private static final String STRING = "String";
    private static final Pattern ifWhileRegex = Pattern.compile("^(?:if|while)\\s*[(](\\s*" +
            "(?:(?:true|false|\\w+|[+\\-]?(?:\\d+\\.?\\d*|\\d*\\.\\d+))+\\s*(?:\\|\\||&&)?\\s*)+" +
            "\\s*)[)]\\s*[{]$");
    private static final Pattern genericConstantsRegex = Pattern.compile("true|false|[+\\-]?" +
            "(?:\\d+\\.?\\d*|\\d*\\.\\d+)");

    /**
     * this function check if a while or if line is declared properly and if so adds a scope of variables
     * and returns true, if not it throws an exception and return false
     * @param line- String the line of the declaration
     * @param scope- the scope of the if or while
     */
    public static void checkIfWhile(String line, int scope) throws GeneralWhileIfException {
        Matcher matcher = ifWhileRegex.matcher(line);
        if(!matcher.matches()){
            throw new GeneralWhileIfException(ERROR1);
        }
        String [] args = matcher.group(CONDITIONS_GROUP).split(CONDITIONS_SPLITTER);
        for(String arg : args){
            arg = arg.trim();
            if(checkGenericConditions(arg)) continue;
            VarInfo info  = Variable.getInfo(arg);
            if(info == null){
                throw new WhileIfVariableException(ERROR2);
            }
            if(!info.isInitialized()){
                throw new WhileIfVariableException(ERROR3);
            }
            String varType = info.getType();
            if(varType.equals(CHAR) || varType.equals(STRING)){
                throw new WhileIfVariableException(ERROR4);
            }
        }
        Variable.addScope(scope);
    }

    /**
     * checks if a parameter in an if or while line declaration is an integer value double value or boolean
     * value
     * @param arg - String the parameter we're checking
     * @return true if it's a generic while value and false if not
     */
    private static boolean checkGenericConditions(String arg) {
        Matcher matcher = genericConstantsRegex.matcher(arg);
        return matcher.matches();
    }

}
