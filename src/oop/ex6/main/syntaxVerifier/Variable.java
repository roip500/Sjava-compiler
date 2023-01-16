package oop.ex6.main.syntaxVerifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Variable {

    /**
     * class that holds the information on the variable
     */
    private static class VarInfo{

        private final String type;
        private boolean initialized;
        private final boolean isFinal;


        /**
         * constructor for VarInfo
         * @param type - assigned type value
         * @param initialized - assigned boolean value
         */
        public VarInfo(String type, boolean initialized, boolean isFinal){
            this.type = type;
            this.initialized = initialized;
            this.isFinal = isFinal;
        }

        /**
         * returns the type
         * @return type value
         */
        public String getType() {
            return type;
        }

        /**
         * returns the initialized argument value
         * @return boolean
         */
        public boolean isInitialized() {
            return initialized;
        }

        /**
         * sets the initialized argument to true
         */
        public void setInitialized(){
            initialized = true;
        }

        /**
         * returns if the variable is final
         * @return boolean
         */
        public boolean isFinal() {
            return isFinal;
        }
    }

    // Types allowed:
    private static final String INT = "int";
    private static final String DOUBLE = "double";
    private static final String STRING = "String";
    private static final String BOOLEAN = "boolean";
    private static final String CHAR = "char";
    private static final String FINAL = "final";

    //regex txt:
    private static final String VAR_WITH_INITIALIZE = "^([a-zA-Z]+\\w*|_\\w+)\\s*=\\s*(.*)\\s*$";
    private static final String VAR_WITHOUT_INITIALIZE = "^([a-zA-Z]+\\w*|_\\w+)\\s*;?\\s*$";

    //pattern declaration:
    private static final Pattern VAR_WITH_INITIALIZE_REGEX = Pattern.compile(VAR_WITH_INITIALIZE);
    private static final Pattern VAR_WITHOUT_INITIALIZE_REGEX = Pattern.compile(VAR_WITHOUT_INITIALIZE);
    private static final Pattern END_REGEX = Pattern.compile("^.*;$");
    private static final Pattern VALUE_IS_INT_REGEX = Pattern.compile("[-+]?\\d+");
    private static final Pattern VALUE_IS_STRING_REGEX = Pattern.compile("\".*\"");
    private static final Pattern VALUE_IS_CHAR_REGEX = Pattern.compile("'.'");
    private static final Pattern VALUE_IS_BOOLEAN_REGEX = Pattern.compile("true|false|[-+]?\\d+|" +
            "[-+]?\\d*[.]\\d+|[-+]?\\d+[.]\\d*");
    private static final Pattern VALUE_IS_DOUBLE_REGEX = Pattern.compile("[-+]?\\d*[.]\\d+|" +
            "[-+]?\\d+[.]\\d*");
    private static final Pattern LEGIT_NAME_REGEX = Pattern.compile("^(?:[a-zA-Z]+\\w*|_\\w+)\\s*$");

    //other variables:
    private static final ArrayList<HashMap<String, VarInfo>> listOfArgs= new ArrayList<>();

    /**
     * checks that a line that initializes a variable is legal
     * @param line - String
     * @param scope - int represents the scope the variable is in
     * @return true if legal, false no
     */
    public static boolean initializeVar(String line, int scope, boolean isFinal){
        String newLine = line.trim();
        String[] splitedLine = newLine.split("\\s", 2);
        if(splitedLine[0].equals(FINAL)){
            splitedLine[1] = splitedLine[1].trim();
            splitedLine = splitedLine[1].split("\\s", 2);
        }
        String type = splitedLine[0];
        String[] allGroups = splitedLine[1].split(",");
        if(allGroups.length == 0){
            //TODO: throw exception no variable declared
            return false;
        }
        Matcher matcher;
        for(int i = 0; i < allGroups.length ;i++){
            String group = allGroups[i].trim();
            matcher = END_REGEX.matcher(group);
            if(i < allGroups.length -1 && matcher.matches()) {
                //TODO: throw exception ; in middle of row
                return false;
            }
            matcher = VAR_WITH_INITIALIZE_REGEX.matcher(group);
            if(matcher.matches()){
                if(!listOfArgs.get(scope).containsKey(matcher.group(1))){
                    if(valueLegit(matcher.group(2), scope, type)){
                        var info = new VarInfo(type, true, isFinal);
                        listOfArgs.get(scope).put(matcher.group(1), info);
                        continue;
                    } else{
                        //TODO: throw exception value not ok
                        return false;
                    }
                }else{
                    //TODO: throw exception exists already in my scope
                    return false;
                }
            }
            matcher = VAR_WITHOUT_INITIALIZE_REGEX.matcher(group);
            if(matcher.matches()) {
                var info = new VarInfo(type, false, isFinal);
                listOfArgs.get(scope).put(matcher.group(1), info);
                continue;
            }
            //TODO: exception doesn't match any regex
            return false;
        }
        return true;
    }

    /**
     * checks that the value is the same type and in the right format
     * @param value - String
     * @param scope - integer
     * @param type - String
     * @return true if the value fits the type, false if not
     */
    private static boolean valueLegit(String value, int scope, String type) {
        Matcher matcher;
        switch (type){
            case (INT): {
                matcher = VALUE_IS_INT_REGEX.matcher(value);
                if(matcher.matches()){
                    return true;
                }
                break;
            }
            case (DOUBLE):{
                matcher = VALUE_IS_DOUBLE_REGEX.matcher(value);
                if(matcher.matches()){
                    return true;
                }
                break;
            }
            case (CHAR): {
                matcher = VALUE_IS_CHAR_REGEX.matcher(value);
                if(matcher.matches()){
                    return true;
                }
                break;
            }
            case (BOOLEAN):{
                matcher = VALUE_IS_BOOLEAN_REGEX.matcher(value);
                if(matcher.matches()){
                    return true;
                }
                break;
            }
            case (STRING):{
                matcher = VALUE_IS_STRING_REGEX.matcher(value);
                if(matcher.matches()){
                    return true;
                }
                break;
            }
        }
        matcher = LEGIT_NAME_REGEX.matcher(value);
        if(!matcher.matches()){
            //TODO: exception value type is not correct
            return false;
        }
        VarInfo varInfo = null;
        for(int i =scope; i>-1; i--){
            if (listOfArgs.get(i).containsKey(value)) {
                varInfo = listOfArgs.get(i).get(value);
                break;
            }
        }
        if (varInfo != null && varInfo.getType().equals(type) && varInfo.isInitialized()) {
            return true;
        }
        //TODO: exception object doesn't exist
        return false;
    }

    /**
     * checks if teh object has being initialized and if the value is the same type.
     * @param line - String
     * @param scope - integer
     * @return true if all is ok, false if something is wrong
     */
    public static boolean assignVar(String line, int scope){
        line = line.trim();
        String[] groups = line.split(",");
        Matcher matcher;
        VarInfo varInfo = null;
        for(String group: groups){
           matcher = VAR_WITH_INITIALIZE_REGEX.matcher(group);
           if(matcher.matches()) {
               int i;
               for (i = scope; i > -1; i--) {
                   if (listOfArgs.get(i).containsKey(matcher.group(1))) {
                       varInfo = listOfArgs.get(i).get(matcher.group(1));
                       break;
                   }
               }
               if (varInfo == null) {
                   //TODO: exception object doesn't exist
                   return false;
               }
               if (varInfo.isFinal()) {
                   //TODO: object is final
                   return false;
               }
               if (!valueLegit(matcher.group(2), scope, varInfo.getType())) {
                   //TODO: value not legit
                   return false;
               }
               varInfo.setInitialized();
               listOfArgs.get(i).put(matcher.group(1), varInfo);
           }
           else {
               //TODO: invalid syntax
               return false;
           }
        }
        return true;
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
    public static VarInfo getType(String name){
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

}