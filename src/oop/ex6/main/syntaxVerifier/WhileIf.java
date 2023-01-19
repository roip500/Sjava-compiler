package oop.ex6.main.syntaxVerifier;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WhileIf {
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
     * @return true if its valid and false if not
     */
    public static boolean checkIfWhile(String line, int scope){
//        Method method = why method?
        Matcher matcher = ifWhileRegex.matcher(line);
        if(!matcher.matches()){
            // TODO: throw invalid if/while line err
            return false;
        }
        String [] args = matcher.group(1).split("\\|\\||&&");
        for(String arg : args){
            arg = arg.trim();
            if(checkGenericConditions(arg)) continue;
            VarInfo info  = Variable.getInfo(arg);
            if(info == null){
                // TODO: throw non existing var error
                return false;
            }
            if(!info.isInitialized()){
                // TODO: throw non initialized var error
                return false;
            }
            String varType = info.getType();
            if(varType.equals(CHAR) || varType.equals(STRING)){
                // TODO: throw invalid type for condition error
                return false;
            }
        }
        Variable.addScope(scope);
        return true;
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
