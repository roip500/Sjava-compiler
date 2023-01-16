package oop.ex6.main.syntaxVerifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Method {

    // Types allowed:
    private static final String INT = "int";
    private static final String DOUBLE = "double";
    private static final String STRING = "String";
    private static final String BOOLEAN = "boolean";
    private static final String CHAR = "char";
    private static final String FINAL = "final";

    private static final Pattern METHOD_DECLARATION_REGEX = Pattern.compile("^\\s*void\\s*([a-zA-z]+" +
            "\\w*)\\s*([(](?:\\s*(?:final\\s*)?\\w+\\s+\\w+,?)*[)])\\s*[{]$");
    private static final Pattern REMOVE_PARENTHESES_FROM_VAR_LIST = Pattern.compile("^[(](.*)[)]$");
    private static final Pattern ARG_DEC_LINE_REGEX = Pattern.compile("\\s*(final)?\\s*(int|boolean|" +
            "String|double|char)\\s*([a-zA-Z]+\\w*|_\\w+)");
    private static final HashMap<String, HashMap<String,VarInfo>> methods = new HashMap<>();

    public static boolean addMethod(String line){
        String newLine = line.trim();
        Matcher matcher = METHOD_DECLARATION_REGEX.matcher(line);
        if(!matcher.matches()){
            //TODO: throw exception invalid method declaration
            return false;
        }
        String methodName = matcher.group(1);
        String argListWithParentheses = matcher.group(2);
        matcher = REMOVE_PARENTHESES_FROM_VAR_LIST.matcher(argListWithParentheses);
        if(matcher.group(1) == null){
           methods.put(methodName, null);
           return true;
        }
        String argList = matcher.group(1);
        if(!parseArgList(methodName, argList)) return false;
        return true;
    }

    private static boolean parseArgList(String methodName, String argList) {
        HashMap<String,VarInfo> argListAndTypeInfo = new HashMap<>();
        argList = argList.trim();
        Matcher matcher;
        String[] allGroups = argList.split(",");
        if(allGroups.length == 0){
            //TODO: throw invalid arg list error
            return false;
        }
        for(String group : allGroups) {
            group = group.trim();
            matcher = ARG_DEC_LINE_REGEX.matcher(group);
            if(!matcher.matches()){
                //TODO: throw invalid arg error
                return false;
            }
            VarInfo varInfo = new VarInfo(matcher.group(2), true, matcher.group(1) != null);
            argListAndTypeInfo.put(matcher.group(3), varInfo);
        }
        methods.put(methodName,argListAndTypeInfo);
        return true;
    }
}
