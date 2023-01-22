package oop.ex6.main.syntaxVerifier.variable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Variable{

    //exceptions text:
    private static final String ERROR1 = "line is not a legal format";
    private static final String ERROR2 = "there is a end of line mark in the middle of the line";
    private static final String ERROR3 = "parameter name already exists in the scope";
    private static final String ERROR4 = "the value doesn't match the type required";
    private static final String ERROR5 = "the variable given doesn't exist";
    private static final String ERROR6 = "the variable can't be assigned because its a Final variable";
    private static final String ERROR7 = "variable %s type was incorrect";
    private static final String ERROR8 = "variable %s wasn't initialized";
    private static final String ERROR9 = "final variable must be initialized";

    // Types allowed:
    private static final String INT = "int";
    private static final String DOUBLE = "double";
    private static final String STRING = "String";
    private static final String BOOLEAN = "boolean";
    private static final String CHAR = "char";
    private static final String FINAL = "final";

    //pattern declaration:
    private static final Pattern VAR_WITH_INITIALIZE_REGEX = Pattern.compile(
            "^([a-zA-Z]+\\w*|_\\w+)\\s*=\\s*(.*)\\s*$");
    private static final Pattern VAR_WITHOUT_INITIALIZE_REGEX = Pattern.compile(
            "^([a-zA-Z]+\\w*|_\\w+)\\s*;?\\s*$");
    private static final Pattern END_REGEX = Pattern.compile("^(.*);$");
    private static final Pattern VALUE_IS_INT_REGEX = Pattern.compile("^[-+]?\\d+$");
    private static final Pattern VALUE_IS_STRING_REGEX = Pattern.compile("\".*\"");
    private static final Pattern VALUE_IS_CHAR_REGEX = Pattern.compile("'.'");
    private static final Pattern VALUE_IS_BOOLEAN_REGEX = Pattern.compile(
            "^true|false|[-+]?\\d+|[-+]?\\d*[.]\\d+|[-+]?\\d+[.]\\d*$");
    private static final Pattern VALUE_IS_DOUBLE_REGEX = Pattern.compile(
            "^[-+]?\\d*[.]\\d+|[-+]?\\d+[.]\\d*$");
    private static final Pattern LEGIT_NAME_REGEX = Pattern.compile(
            "^(?:[a-zA-Z]+\\w*|_\\w+)\\s*$");


    //other variables:
    private static final ArrayList<HashMap<String, VarInfo>> listOfArgs= new ArrayList<>();
    private static final ArrayList<String> initialisedInMethod = new ArrayList<>();
    private static Matcher matcher;

    /**
     * checks that a line that initializes a variable is legal
     * throws an exception if false
     * @param line  - String
     * @param scope - int represents the scope the variable is in
     */
    public static void initializeVar(String line, int scope, boolean isFinal) throws GeneralVariableException {
        String newLine = line.trim();
        String[] splitLine = newLine.split("\\s", 2);
        if(splitLine[0].equals(FINAL)){
            splitLine[1] = splitLine[1].trim();
            splitLine = splitLine[1].split("\\s", 2);
        }
        String type = splitLine[0];
        String[] allGroups = splitLine[1].split(",");
        if(allGroups.length == 0){
            throw new GeneralVariableException(ERROR1);
        }
        Matcher matcher;
        for(int i = 0; i < allGroups.length ;i++){
            String group = allGroups[i].trim();
            matcher = END_REGEX.matcher(group);
            if(matcher.matches()) {
                if(i < allGroups.length -1  || matcher.group(1) == null) {
                    throw new GeneralVariableException(ERROR2);
                }
                group = matcher.group(1);
            }
            matcher = VAR_WITH_INITIALIZE_REGEX.matcher(group);
            if(matcher.matches()){
                String value = matcher.group(2).trim();
                if(!listOfArgs.get(scope).containsKey(matcher.group(1))){
                    try {
                        valueLegit(value, scope, type);
                        var info = new VarInfo(matcher.group(1), type, true, isFinal);
                        listOfArgs.get(scope).put(matcher.group(1), info);
                        continue;
                    }catch (Exception e){
                        throw new InitializeVariableValueException(e.getMessage());
                    }
                }else{
                    throw new InitializeVariableException(ERROR3);
                }
            }
            matcher = VAR_WITHOUT_INITIALIZE_REGEX.matcher(group);
            if(matcher.matches() ) {
                if(listOfArgs.get(scope).containsKey(matcher.group(1))){
                    throw new InitializeVariableException(ERROR3);
                }
                if(isFinal) throw new InitializeVariableException(ERROR9);
                var info = new VarInfo(matcher.group(1), type,false, false);
                listOfArgs.get(scope).put(matcher.group(1), info);
                continue;
            }
            throw new GeneralVariableException(ERROR1);
        }
    }

    /**
     * checks that the value is the same type and in the right format
     * throws an exception if false
     * @param value - String
     * @param scope - integer
     * @param type - String
     */
    private static void valueLegit(String value, int scope, String type) throws Exception {
        if(Variable.checkIfValueIsTheRightType(value, type)){
            return;
        }
        matcher = LEGIT_NAME_REGEX.matcher(value);
        if(!matcher.matches()){
            throw new GeneralVariableException(ERROR4);
        }
        VarInfo varInfo = null;
        for(int i =scope; i>-1; i--){
            if (listOfArgs.get(i).containsKey(value)) {
                varInfo = listOfArgs.get(i).get(value);
                break;
            }
        }
        if(varInfo == null) throw new GeneralVariableException(ERROR5);
        Variable.checkInfoMatch(varInfo, type);
    }

    /**
     * this function checks if the parameter given is legal to be assigned
     * throws an exception if false
     * @param callInfo-VarInfo of called parameter
     * @param destType- String
     */
    public static void checkInfoMatch(VarInfo callInfo, String destType) throws Exception {
        if (!callInfo.isInitialized()) throw new GeneralVariableException(
                String.format(ERROR8, callInfo.getName()));
        switch (callInfo.getType()) {
            case INT:
                if(destType.equals(INT) || destType.equals(DOUBLE) || destType.equals(BOOLEAN)) return;
                break;
            case DOUBLE:
                if(destType.equals(DOUBLE) || destType.equals(BOOLEAN)) return;
                break;
            case BOOLEAN:
                if(destType.equals(BOOLEAN)) return;
                break;
            case STRING:
                if(destType.equals(STRING)) return;
                break;
            case CHAR:
                if(destType.equals(CHAR)) return;
                break;
        }
        throw new Exception(
                String.format(ERROR7, callInfo.getName()));
    }

    /**
     * checks if the value passed is the type required
     * @param value - String
     * @param type - String
     * @return true if yes, false if no
     */
    public static boolean checkIfValueIsTheRightType(String value, String type){
        switch (type) {
            case (INT): {
                matcher = VALUE_IS_INT_REGEX.matcher(value);
                if (matcher.matches()) return true;
                break;
            }
            case (DOUBLE): {
                matcher = VALUE_IS_DOUBLE_REGEX.matcher(value);
                if (matcher.matches()) return true;
                matcher = VALUE_IS_INT_REGEX.matcher(value);
                if (matcher.matches()) return true;
                break;
            }
            case (CHAR): {
                matcher = VALUE_IS_CHAR_REGEX.matcher(value);
                if (matcher.matches()) return true;
                break;
            }
            case (BOOLEAN): {
                matcher = VALUE_IS_BOOLEAN_REGEX.matcher(value);
                if (matcher.matches()) return true;
                matcher = VALUE_IS_DOUBLE_REGEX.matcher(value);
                if (matcher.matches()) return true;
                matcher = VALUE_IS_INT_REGEX.matcher(value);
                if (matcher.matches()) return true;
                break;
            }
            case (STRING): {
                matcher = VALUE_IS_STRING_REGEX.matcher(value);
                if (matcher.matches()) return true;
                break;
            }
        }
        return false;
    }

    /**
     * checks if teh object has being initialized and if the value is the same type.
     * throws an exception if false
     * @param line  - String
     * @param scope - integer
     */
    public static void assignVar(String line, int scope) throws GeneralVariableException {
        line = line.trim();
        String[] groups = line.split(",");
        Matcher matcher;
        VarInfo varInfo = null;
        for(String group: groups){
           group = group.trim();
            matcher = VAR_WITH_INITIALIZE_REGEX.matcher(group);
           if(matcher.matches()) {
               int i;
               for (i = scope; i > -1; i--) {
                   if (listOfArgs.get(i).containsKey(matcher.group(1))) {
                       varInfo = listOfArgs.get(i).get(matcher.group(1));
                       break;
                   }
               }
               if (varInfo == null) throw new AssignVariableException(ERROR5);
               if (varInfo.isFinal()) throw new AssignVariableException(ERROR6);
               String value = matcher.group(2);
               matcher = END_REGEX.matcher(value);
               if(matcher.matches()) {
                   if(matcher.group(1) == null) throw new GeneralVariableException(ERROR2);
                   value = matcher.group(1);
               }
               try{
                   valueLegit(value, scope, varInfo.getType());
                   if(!varInfo.isInitialized() && i == 0 && i != scope){
                       initialisedInMethod.add(matcher.group(1));
                   }
                   varInfo.setInitialized();
               }catch (Exception e){
                   throw new AssignVariableValueException(e.getMessage());
               }
           }
           else {
               throw new GeneralVariableException(ERROR1);
           }
        }
    }

    /**
     * creates a new scope in the variables list
     * @param scope - integer represents the scope
     */
    public static void addScope(int scope){
        if(listOfArgs.size() <= scope){
            listOfArgs.add(scope, new HashMap<>());
        }
    }

    /**
     * removes the scopes variables from list
     * @param scope - integer represents the scope
     */
    public static void removeScope(int scope){
        listOfArgs.remove(scope);
    }

    /**
     * returns the varInfo of the object if exists.
     * @param name - String
     * @return varInfo object
     */
    public static VarInfo getInfo(String name){
        Matcher matcher = LEGIT_NAME_REGEX.matcher(name);
        if(matcher.matches()){
            for (int i=listOfArgs.size()-1; i>-1; i--){
                if(listOfArgs.get(i).containsKey(name)){
                    return listOfArgs.get(i).get(name);
                }
            }
        }
        return null;
    }

    /**
     * adds a new variable to the scope in the list
     * @param name - String
     * @param info - VarInfo type object
     * @param scope - integer
     */
    public static void addVariable(String name, VarInfo info, int scope){
        listOfArgs.get(scope).put(name, info);
    }

    /**
     * function resets all the global variables
     */
    public static void removeAssignmentsAtEndOfMethod(){
        for (String varName : initialisedInMethod){
            VarInfo varInfo = listOfArgs.get(0).get(varName);
            varInfo.deAssign();
        }
        initialisedInMethod.clear();
    }

    /**
     * function returns true if the String is a legal name of a variable
     * @param name - String
     * @return true if yes, false if no
     */
    public static boolean isALegalVariableName(String name){
        matcher = LEGIT_NAME_REGEX.matcher(name);
        return matcher.matches();
    }

    /**
     * resets the databases of the class
     */
    public static void clearAllDataBases(){
        listOfArgs.clear();
        initialisedInMethod.clear();
    }

}