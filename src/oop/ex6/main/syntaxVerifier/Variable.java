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

    private static final String VAR_WITH_INITIALIZE = "^([a-zA-Z]+\\w*|_\\w+)\\s*=\\s*(.*)\\s*$";
    //TODO: capturing group has been refined
    private static final String VAR_WITHOUT_INITIALIZE = "^([a-zA-Z]+\\w*|_\\w+)\\s*;?\\s*$";
    //TODO: updated now supports ; in the end of a sentence and capturing groups has been refined
    private static final String END = "^.*;$";

    //pattern declaration:
    private static final Pattern VAR_WITH_INITIALIZE_REGEX = Pattern.compile(VAR_WITH_INITIALIZE);
    private static final Pattern VAR_WITHOUT_INITIALIZE_REGEX = Pattern.compile(VAR_WITHOUT_INITIALIZE);
    private static final Pattern END_REGEX = Pattern.compile(END);

    //other variables:
    private static final ArrayList<HashMap<String, VarInfo>> listOfArgs= new ArrayList<>();
    private static final HashMap<String, VarInfo> problem = new HashMap<>();

    /**
     * checks that a line that initializes a variable is legal
     * @param line - String
     * @param scope - int represents the scope the variable is in
     * @return true if legal, false no
     */
    public static boolean initializeVar(String line, int scope){
        String newLine = line.trim();
        String[] splitedLine = newLine.split("\\s", 2);
        String type = splitedLine[0];
        String[] allGroups = splitedLine[1].split(",");
        if(allGroups.length == 0){
            //TODO: throw exception no variable declared
            return false;
        }
        Matcher matcher;
        for(int i=0; i < allGroups.length ;i++){
            String group = allGroups[i].trim();
            matcher = END_REGEX.matcher(group);
            if(i < allGroups.length -1 && matcher.matches()) {
                //TODO: throw exception ; in middle of row
                return false;
            }
            matcher = VAR_WITH_INITIALIZE_REGEX.matcher(group);
            if(matcher.matches()) {
                insertVarWithValue(matcher.group(1), matcher.group(2), scope, type);
                continue;
            }
            matcher = VAR_WITHOUT_INITIALIZE_REGEX.matcher(group);
            if(matcher.matches()) {
                insertVarWithoutValue(matcher.group(1), scope, type);
                continue;
            }
            //TODO: exceptionn doesn't match any regex
            return false;

            }
        return true;
    }

    private static void insertVarWithoutValue(String varName, int scope, String type) {
        //TODO: implement
    }

    private static void insertVarWithValue(String varName, String value, int scope, String type) {
        //TODO: implement

    }

    public static boolean assignVar(String line, int scope){

        return true;
    }

    public static boolean checkScope(){

        return true;
    }

    /**
     * returns true if the problem list is not empty
     * @return true if yes, false if no
     */
    public static boolean areThereProblems(){
        return problem.size() != 0;
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

    public static boolean getType(){

        return true;
    }



}


//String name = "";
//        while(allChars[i] != '=' || allChars[i] != ';' || allChars[i] != ',' || allChars[i] != ' '){
//        name = name + allChars[i];
//        i++;
//        }
//        while(allChars[i] == ' '){
//        i++;
//        }
//        if(allChars[i] == ';' || allChars[i] == ','){
//        VarInfo varInfo = new VarInfo(type, false);
//        listOfArgs.get(i).put(name, varInfo);
//        }
//        if(allChars[i] == '='){
//        String value = "";
//        while(allChars[i] != ';' || allChars[i] != ','){
//        value = value + allChars[i];
//        i++;
//        }
//        if(checkValue(value, type)){
//        VarInfo varInfo = new VarInfo(type, true);
//        listOfArgs.get(i).put(name, varInfo);
//        }
//        else{
//        //TODO: error wrong type
//        return false;
//        }