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

    public static boolean checkLine(String line){
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
            String varType = info.getType();
            if(varType.equals(CHAR) || varType.equals(STRING)){
                // TODO: throw invalid type for condition error
                return false;
            }
        }
        return true;
    }

    private static boolean checkGenericConditions(String arg) {
        Matcher matcher = genericConstantsRegex.matcher(arg);
        return matcher.matches();
    }


}
