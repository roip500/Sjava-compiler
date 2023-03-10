package oop.ex6.main.syntaxVerifier.method;

import oop.ex6.main.syntaxVerifier.variable.VarInfo;
import oop.ex6.main.syntaxVerifier.variable.Variable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Method {

    //exceptions text:
    private static final String ERROR1 = "line doesn't match the legal format for initializing a method";
    private static final String ERROR2 = "method name already used in code";
    private static final String ERROR3 = "invalid variable declared in method declaration";
    private static final String ERROR4 = "invalid method declaration";
    private static final String ERROR5 = "invalid text - has no meaning";
    private static final String ERROR6 = "method doesn't exist";
    private static final String ERROR7 = "number of variables passed to the method is incorrect";
    private static final String ERROR8 = "variable %s doesn't exist";
    private static final String ERROR9 = "variable %s type was incorrect";

    //global:
    private static final int SUCCESS = 0;
    private static final String VAR_SPLITTER = ",";
    private static final int METHOD_NAME_GROUP = 1;
    private static final int ARG_LIST_GROUP = 2;
    private static final int NO_PARENTHESES_GROUP = 1;
    private static final int VAR_NAME_GROUP = 3;
    private static final int VAR_TYPE_GROUP = 2;
    private static final int VAR_FINAL_GROUP = 1;


    //regex:
    private static final Pattern METHOD_DECLARATION_REGEX = Pattern.compile("^\\s*void\\s+([a-zA-Z]+" +
            "\\w*)\\s*([(](?:\\s*(?:final\\s*)?\\w+\\s+\\w+\\s*,?)*\\s*[)])\\s*[{]$");
    private static final Pattern REMOVE_PARENTHESES_FROM_VAR_LIST = Pattern.compile("^[(](.*)[)]$");
    private static final Pattern ARG_DEC_LINE_REGEX = Pattern.compile("\\s*(final\\s+)?(int|boolean|" +
            "String|double|char)\\s+([a-zA-Z]+\\w*|_\\w+)\\s*");
    private static final Pattern VARIABLES_PASSED_TO_METHOD_REGEX = Pattern.compile(
            "\\s*([a-zA-Z]\\w*)\\s*[(]((?:\\s*[+-]?[\\w.'\"]+\\s*,?)*)[)]\\s*;$");
    private static final Pattern noArgumentsRegex = Pattern.compile("\\s*");

    //data-base:
    private static final HashMap<String, ArrayList<VarInfo>> methods = new HashMap<>();

    /**
     * adds a method declaration - checks that the method was declared ok
     * if false then the function throws an exception
     * @param line - String
     */
    public static void addMethod(String line) throws GeneralMethodException {
        line = line.trim();
        Matcher matcher = METHOD_DECLARATION_REGEX.matcher(line);
        if (!matcher.matches()) {
            throw new GeneralMethodException(ERROR1);
        }
        String methodName = matcher.group(METHOD_NAME_GROUP);
        if (methods.containsKey(methodName)) {
            throw new MethodDeclarationException(ERROR2);
        }
        String argListWithParentheses = matcher.group(ARG_LIST_GROUP);
        matcher = REMOVE_PARENTHESES_FROM_VAR_LIST.matcher(argListWithParentheses);
        if (!matcher.matches()) return;
        String args = matcher.group(NO_PARENTHESES_GROUP);
        matcher = noArgumentsRegex.matcher(args);
        if (matcher.matches()) {
            methods.put(methodName, null);
            return;
        }
        parseArgList(methodName, args);
    }

    /**
     * checks the variables assigned to the method. if yes the variables are saved to the map
     * if false then the function throws an exception
     * @param methodName - String. will be the key in the map
     * @param argList    - String. represents the variables
     */
    private static void parseArgList(String methodName, String argList) throws MethodVariablesException {
        ArrayList<VarInfo> argListAndTypeInfo = new ArrayList<>();
        argList = argList.trim();
        Matcher matcher;
        String[] allGroups = argList.split(VAR_SPLITTER);
        for (String group : allGroups) {
            group = group.trim();
            matcher = ARG_DEC_LINE_REGEX.matcher(group);
            if (!matcher.matches()) {
                throw new MethodVariablesException(ERROR3);
            }
            for (var info: argListAndTypeInfo) {
                if(info.getName().equals(matcher.group(VAR_NAME_GROUP))){
                    throw new MethodVariablesException(ERROR2);
                }
            }
            VarInfo varInfo = new VarInfo(matcher.group(VAR_NAME_GROUP), matcher.group(VAR_TYPE_GROUP),
                    true, matcher.group(VAR_FINAL_GROUP) != null);
            argListAndTypeInfo.add(varInfo);
        }
        methods.put(methodName, argListAndTypeInfo);
    }

    /**
     * adds all the functions inputs to the list of Variables in the functions scope
     * if false then the function throws an exception
     * @param name  - String
     * @param scope - integer
     */
    private static void addArguments(String name, int scope) {
        var argListAndTypeInfo = methods.get(name);
        Variable.addScope(scope);
        if (argListAndTypeInfo == null) return; // if method has no arguments
        for (var info : argListAndTypeInfo) {
            Variable.addVariable(info.getName(), info, scope);
        }
    }

    /**
     * adds a new scope with the variables given to the function
     * if false then the function throws an exception
     * @param line  - String
     * @param scope - integer
     * @return true if succeeds, false no
     */
    public static boolean runMethod(String line, int scope) throws GeneralMethodException {
        Matcher matcher = METHOD_DECLARATION_REGEX.matcher(line);
        if (!matcher.matches()) {
            throw new GeneralMethodException(ERROR4);
        }
        String methodName = matcher.group(METHOD_NAME_GROUP);
        Method.addArguments(methodName, scope);
        return true;
    }

    /**
     * checks if the method exists, if yes checks that the variables that were sent were initialized
     * if false then the function throws an exception
     * @param line - String
     * @return - SUCCESS if everything is ok, FAILED if no
     */
    public static int checkMethodCall(String line) throws GeneralMethodException {
        line = line.trim();
        Matcher matcher = VARIABLES_PASSED_TO_METHOD_REGEX.matcher(line);
        if (!matcher.matches()) {
            throw new GeneralMethodException(ERROR5);
        }
        String name = matcher.group(METHOD_NAME_GROUP);
        if (!methods.containsKey(name)) {
            throw new MethodCalledException(ERROR6);
        }
        String vars = matcher.group(ARG_LIST_GROUP);
        var lstOfArgs = methods.get(name);
        if(lstOfArgs == null){
            matcher = noArgumentsRegex.matcher(vars);
            if(!matcher.matches()) throw new MethodVariablesException(ERROR7);
            return SUCCESS;
        }
        int size = lstOfArgs.size();
        String[] args = vars.split(VAR_SPLITTER);
        if (args.length != size) {
            throw new MethodVariablesException(ERROR7);
        }
        for (int i = 0; i < size; i++) {
            String arg = args[i].trim();
            VarInfo info = Variable.getInfo(arg);
            if (info != null) {
                try {
                    Variable.checkInfoMatch(info, lstOfArgs.get(i).getType());
                } catch (Exception e){
                    throw new MethodCalledException(e.getMessage());
                }
            }
            else if (Variable.isALegalVariableName(arg)){
                throw new MethodVariablesException(String.format(ERROR8, arg));
            }
            else if(!Variable.checkIfValueIsTheRightType(arg, lstOfArgs.get(i).getType())){
                throw new MethodVariablesException(String.format(ERROR9, arg));
            }
        }
        return SUCCESS;
    }

    /**
     * function clears the databases in the class
     */
    public static void resetAllDataBases(){
        methods.clear();
    }
}
