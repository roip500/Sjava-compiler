package oop.ex6.main.syntaxVerifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Method {

    //global:
    private static final int SUCCESS = 0;
    private static final int FAILED = 1;

    //regex:
    private static final Pattern METHOD_DECLARATION_REGEX = Pattern.compile("^\\s*void\\s+([a-zA-Z]+" +
            "\\w*)\\s+([(](?:\\s*(?:final\\s*)?\\w+\\s+\\w+,?)*[)])\\s*[{]$");
    private static final Pattern REMOVE_PARENTHESES_FROM_VAR_LIST = Pattern.compile("^[(](.*)[)]$");
    private static final Pattern ARG_DEC_LINE_REGEX = Pattern.compile("\\s*(final)?\\s+(int|boolean|" +
            "String|double|char)\\s+([a-zA-Z]+\\w*|_\\w+)\\s*");
    private static final Pattern VARIABLES_PASSED_TO_METHOD_REGEX = Pattern.compile(
            "\\s*([a-zA-Z]\\w*)\\s*[(]((?:\\s*\\w+\\s*,?))[)]\\s;$");

    //data-base:
    private static final HashMap<String, ArrayList<VarInfo>> methods = new HashMap<>();

    /**
     * adds a method declaration - checks that the method was declared ok
     * @param line - String
     * @return - true if yes, false if no
     */
    public static boolean addMethod(String line){
        line = line.trim();
        Matcher matcher = METHOD_DECLARATION_REGEX.matcher(line);
        if(!matcher.matches()){
            //TODO: throw exception invalid method declaration
            return false;
        }
        String methodName = matcher.group(1);
        if(methods.containsKey(methodName)){
            //TODO: throw 2 same named methods error
            return false;
        }
        String argListWithParentheses = matcher.group(2);
        matcher = REMOVE_PARENTHESES_FROM_VAR_LIST.matcher(argListWithParentheses);
        if(matcher.group(1) == null){
           methods.put(methodName, null);
           return true;
        }
        return parseArgList(methodName, matcher.group(1));
    }

    /**
     * checks the variables assigned to the method. if yes the variables are saved to the map
     * @param methodName - String. will be the key in the map
     * @param argList - String. represents the variables
     * @return true if success, false if no
     */
    private static boolean parseArgList(String methodName, String argList) {
        ArrayList<VarInfo> argListAndTypeInfo = new ArrayList<>();
        argList = argList.trim();
        Matcher matcher;
        String[] allGroups = argList.split(",");
        for(String group : allGroups) {
            group = group.trim();
            matcher = ARG_DEC_LINE_REGEX.matcher(group);
            if(!matcher.matches()){
                //TODO: throw invalid arg error
                return false;
            }
            VarInfo varInfo = new VarInfo(matcher.group(3), matcher.group(2), true, matcher.group(1) != null);
            argListAndTypeInfo.add(varInfo);
        }
        methods.put(methodName,argListAndTypeInfo);
        return true;
    }

    /**
     * adds all the functions inputs to the list of Variables in the functions scope
     * @param name - String
     * @param scope - integer
     */
    private static void addArguments(String name, int scope){
        var argListAndTypeInfo = methods.get(name);
        Variable.addScope(scope);
        for (var info: argListAndTypeInfo){
            Variable.addVariable(info.getName(), info, scope);
        }
    }

    /**
     * adds a new scope with the variables given to the function
     * @param line - String
     * @param scope - integer
     * @return true if succeeds, false no
     */
    public static boolean runMethod(String line, int scope){
        Matcher matcher = METHOD_DECLARATION_REGEX.matcher(line);
        if (!matcher.matches()) {
            //TODO: throw exception invalid method declaration
            return false;
        }
        String methodName = matcher.group(1);
        Method.addArguments(methodName, scope);
        return true;
    }

    /**
     * checks if the method exists, if yes checks that the variables that were sent were initialized
     * @param line - String
     * @return - SUCCESS if everything is ok, FAILED if no
     */
    public static int checkMethodCall(String line){
        line = line.trim();
        Matcher matcher = VARIABLES_PASSED_TO_METHOD_REGEX.matcher(line);
        if(!matcher.matches()){
            // TODO: exception - line has no meaning
            return FAILED;
        }
        String name = matcher.group(1);
        if(!methods.containsKey(name)){
            // TODO: exception - method doesn't exist
            return FAILED;
        }
        var lstOfArgs = methods.get(name);
        int size = lstOfArgs.size();
        String[] args = matcher.group(2).split(",");
        if(args.length != size){
            // TODO: exception - num of variables passed doesnt match request
            return FAILED;
        }
        for(int i=0; i<size;i++){
            String arg = args[i].trim();
            VarInfo info = Variable.getInfo(arg);
            if(info != null){

            }
            else{

            }

        }


    }
}
