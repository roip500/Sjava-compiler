package oop.ex6.main.syntaxVerifier.method;

import oop.ex6.main.syntaxVerifier.variable.VarInfo;
import oop.ex6.main.syntaxVerifier.variable.Variable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Method {

    //exceptions text:
    private static final String ERROR1 = "line doesn't match the legal format for nitializing a method";
    private static final String ERROR2 = "method name already used in code";
    private static final String ERROR3 = "invalid variable declared in method declaration";
    private static final String ERROR4 = "invalid method declaration";
    private static final String ERROR5 = "invalid text - has no meaning";
    private static final String ERROR6 = "method doesn't exist";
    private static final String ERROR7 = "number of variables passed to the method is incorrect";

    //global:
    private static final int SUCCESS = 0;
    private static final int FAILED = 1;
    private static final String INT = "int";
    private static final String DOUBLE = "double";
    private static final String STRING = "String";
    private static final String BOOLEAN = "boolean";
    private static final String CHAR = "char";

    //regex:
    private static final Pattern METHOD_DECLARATION_REGEX = Pattern.compile("^\\s*void\\s+([a-zA-Z]+" +
            "\\w*)\\s*([(](?:\\s*(?:final\\s*)?\\w+\\s+\\w+,?)*[)])\\s*[{]$");
    private static final Pattern REMOVE_PARENTHESES_FROM_VAR_LIST = Pattern.compile("^[(](.*)[)]$");
    private static final Pattern ARG_DEC_LINE_REGEX = Pattern.compile("\\s*(final\\s+)?(int|boolean|" +
            "String|double|char)\\s+([a-zA-Z]+\\w*|_\\w+)\\s*");
    private static final Pattern VARIABLES_PASSED_TO_METHOD_REGEX = Pattern.compile(
            "\\s*([a-zA-Z]\\w*)\\s*[(]((?:\\s*[\\+-]?[\\w\\.'\"]+\\s*,?)*)[)]\\s*;$");
    // TODO: changed to support raw values at function calls

    //data-base:
    private static final HashMap<String, ArrayList<VarInfo>> methods = new HashMap<>();

    /**
     * adds a method declaration - checks that the method was declared ok
     *
     * @param line - String
     * @return - true if yes, false if no
     */
    public static boolean addMethod(String line) throws GeneralMethodException {
        line = line.trim();
        Matcher matcher = METHOD_DECLARATION_REGEX.matcher(line);
        if (!matcher.matches()) {
            throw new GeneralMethodException(ERROR1);
        }
        String methodName = matcher.group(1);
        if (methods.containsKey(methodName)) {
            throw new MethodDeclarationException(ERROR2);
        }
        String argListWithParentheses = matcher.group(2);
        matcher = REMOVE_PARENTHESES_FROM_VAR_LIST.matcher(argListWithParentheses);
        if (!matcher.matches()) return false;
        if (matcher.group(1) == null || matcher.group(1).isEmpty()) {
            methods.put(methodName, null);
            return true;
        }
        return parseArgList(methodName, matcher.group(1));
    }

    /**
     * checks the variables assigned to the method. if yes the variables are saved to the map
     *
     * @param methodName - String. will be the key in the map
     * @param argList    - String. represents the variables
     * @return true if success, false if no
     */
    private static boolean parseArgList(String methodName, String argList) throws MethodVariablesException {
        ArrayList<VarInfo> argListAndTypeInfo = new ArrayList<>();
        argList = argList.trim();
        Matcher matcher;
        String[] allGroups = argList.split(",");
        for (String group : allGroups) {
            group = group.trim();
            matcher = ARG_DEC_LINE_REGEX.matcher(group);
            if (!matcher.matches()) {
                throw new MethodVariablesException(ERROR3);
            }
            VarInfo varInfo = new VarInfo(matcher.group(3), matcher.group(2),
                    true, matcher.group(1) != null);
            argListAndTypeInfo.add(varInfo);
        }
        methods.put(methodName, argListAndTypeInfo);
        return true;
    }

    /**
     * adds all the functions inputs to the list of Variables in the functions scope
     *
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
     *
     * @param line  - String
     * @param scope - integer
     * @return true if succeeds, false no
     */
    public static boolean runMethod(String line, int scope) throws GeneralMethodException {
        Matcher matcher = METHOD_DECLARATION_REGEX.matcher(line);
        if (!matcher.matches()) {
            throw new GeneralMethodException(ERROR4);
        }
        String methodName = matcher.group(1);
        Method.addArguments(methodName, scope);
        return true;
    }

    /**
     * checks if the method exists, if yes checks that the variables that were sent were initialized
     *
     * @param line - String
     * @return - SUCCESS if everything is ok, FAILED if no
     */
    public static int checkMethodCall(String line) throws GeneralMethodException {
        line = line.trim();
        Matcher matcher = VARIABLES_PASSED_TO_METHOD_REGEX.matcher(line);
        if (!matcher.matches()) {
            throw new GeneralMethodException(ERROR5);
        }
        String name = matcher.group(1);
        if (!methods.containsKey(name)) {
            throw new MethodCalledException(ERROR6);
        }
        var lstOfArgs = methods.get(name);
        int size = lstOfArgs.size();
        String[] args = matcher.group(2).split(",");
        if (args.length != size) {
            throw new MethodVariablesException(ERROR7);
        }
        for (int i = 0; i < size; i++) {
            String arg = args[i].trim();
            VarInfo info = Variable.getInfo(arg);
            if (info != null) {
                checkInfoMatch(info, lstOfArgs.get(i));
            }
            else {
                try{
                    Variable.checkIfValueIsTheRightType(arg, lstOfArgs.get(i).getType());
                }catch (Exception e){
                    throw new MethodVariablesException(e.getMessage());
                }
            }
        }
        return SUCCESS;
    }

    /**
     * this function checks if the parameter given in a function call matches the parameter in the argument list
     * @param callInfo-VarInfo of called parameter
     * @param argInfo- VarInfo of the argument
     */
    private static void checkInfoMatch(VarInfo callInfo, VarInfo argInfo) throws MethodVariablesException {
        if (!callInfo.isInitialized()) throw new MethodVariablesException(
                "variable"+ callInfo.getName() +  "wasn't initialized");
        if (callInfo.isFinal() && !argInfo.isFinal()) throw new MethodVariablesException(
                "variable"+ callInfo.getName() +" isn't Final");
        String destType = argInfo.getType();
        switch (callInfo.getType()) {
            case INT:
                if(destType.equals(INT) || destType.equals(DOUBLE) || destType.equals(BOOLEAN)) return;
            case DOUBLE:
                if(destType.equals(DOUBLE) || destType.equals(BOOLEAN)) return;
            case BOOLEAN:
                if(destType.equals(BOOLEAN)) return;
            case STRING:
                if(destType.equals(STRING)) return;
            case CHAR:
                if(destType.equals(CHAR)) return;
        }
        throw new MethodVariablesException(
                "variable "+ callInfo.getName() +" type isn't correct");
    }

    //TODO: talk to omer about this
}
