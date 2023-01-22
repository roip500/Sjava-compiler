package oop.ex6.main;

import oop.ex6.main.syntaxVerifier.method.Method;
import oop.ex6.main.syntaxVerifier.variable.Variable;
import oop.ex6.main.syntaxVerifier.whileif.WhileIf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sjavac {

    //exceptions text:
    private static final String ERROR1 = "line doesn't contain an end of line mark";
    private static final String ERROR2 = "to many }.";
    private static final String ERROR3 = "not allowed to call a while/if function outside a method";
    private static final String ERROR4 = "used a return statement outside a method";
    private static final String ERROR5 = "line doesn't fit any legal pattern";
    private static final String ERROR6 = "method doesn't end with a return line";
    private static final String ERROR7 = "not allowed to call a method outside a method";

    //global:
    private static final int SUCCESS = 0;
    private static final int FAILED = 1;
    private static final int ERROR = 2;

    //regex string pattern:
    private static final String INVALID_FILE_ERR_MSG = "Invalid file name";
    private static final String BLANK_OR_COMMENT = "^//.*$|\\s*";
    private static final String END_OF_LINE_MODIFIERS = "^.*[;{}]$"; //TODO: maybe unnecessary
    private static final String VAR_INITIALIZE_LINE = "^\\s*(final)?\\s*(?:int|double|String|boolean|char)\\s+.*;$";
    private static final String VAR_ASSIGNMENT_LINE = "^\\s*[a-zA-Z_\\d]\\s*=\\s*.*?;$";
    //TODO: add multiple assignments -- doesn't matter, it recognises the first assignment and counts the rest
    // as part of the assignment, so technically it works for multiple stuff.
    private static final String METHOD_LINE = "^\\s*void.*[{]$";

    //pattern declaration:
    private static final Pattern varInitializeRegex = Pattern.compile(VAR_INITIALIZE_LINE);
    private static final Pattern endOfLineRegex = Pattern.compile(END_OF_LINE_MODIFIERS);
    private static final Pattern blankOrCommentRegex = Pattern.compile(BLANK_OR_COMMENT);
    private static final Pattern varAssignedRegex = Pattern.compile(VAR_ASSIGNMENT_LINE);
    private static final Pattern methodRegex = Pattern.compile(METHOD_LINE);
    private static final Pattern endOfScopeRegex = Pattern.compile("^\\s*}\\s*$");
    private static final Pattern ifWhileRegex = Pattern.compile("^\\s*(?:if|while).*[{]$");
    private static final Pattern returnStatementRegex = Pattern.compile("^\\s*return\\s*;$");

    // class arguments:
    private int scopeNum = 0;
    private Matcher matcher;
    private String previousLine;
    private int numOfInnerWhileOrIf = 0;


    /**
     * main func - checks the sJava file and print to the screen the result
     * @param args - String
     */
    public void checkCode(String[] args){
        if(args.length == 0){
            // TODO - exception file was not given
            System.out.println(ERROR);
        }
        int result = this.firstRead(args[0]);
        if (result != SUCCESS){
            System.out.println(result);
            Variable.clearAllDataBases();
            Method.resetAllDataBases();
        }
        else{
            result = this.secondRead(args[0]);
            System.out.println(result);
            Variable.clearAllDataBases();
            Method.resetAllDataBases();
        }
    }

    /**
     * reads the file a first time - checks global variables and methods names
     * @param fileName - String
     * @return 0 if all ok, 1 if there is a mistake, 2 error in opening code
     */
    private int firstRead(String fileName) {
        String line;
        int numOfLine = 1;
        Variable.addScope(scopeNum);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
            while ((line = bufferedReader.readLine()) != null){
                int result = checkLineFirstRead(line, numOfLine);
                if(result != SUCCESS){
                    return result;
                }
                numOfLine++;
            }
            if(scopeNum != 0){
                //TODO: exception - didn't close all scopes
                return FAILED;
            }
        }
        catch (IOException e) {
            System.out.println(INVALID_FILE_ERR_MSG);
            return ERROR;
        }
        return SUCCESS;
    }

    /**
     * reads the file a second time - makes sure all the inner code is legit
     * @param fileName - String
     * @return 0 if all ok, 1 if there is a mistake, 2 error in opening code
     */
    private int secondRead(String fileName){
        String line;
        int numOfLine = 1;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
            while ((line = bufferedReader.readLine()) != null) {
                int result = checkLineSecondRead(line, numOfLine);
                if(result != SUCCESS){
                    return result;
                }
                previousLine = line;
                numOfLine++;
            }
        } catch (IOException e) {
            System.err.println(INVALID_FILE_ERR_MSG);
            return ERROR;
        }
        return SUCCESS;
    }

    /**
     * performs the checks of the code in the first round
     * checks: empty/comments, global variable, end of line and scopes
     * @param line - String
     * @return SUCCESS if written properly, FAILED if not
     */
    private int checkLineFirstRead(String line, int numOfLine) {
        try{
            if (checksBlankOrCommentLine(line)) {
                return SUCCESS;
            }
            line = line.trim();
            matcher = endOfLineRegex.matcher(line);
            if (!matcher.matches()) throw new GeneralSJavaException(ERROR1);
            matcher = endOfScopeRegex.matcher(line);
            if (matcher.matches()) {
                scopeNum--;
                if (scopeNum < 0) throw new GeneralSJavaException(ERROR2);
                return SUCCESS;
            }
            matcher = ifWhileRegex.matcher(line);
            if (matcher.matches()) {
                if (scopeNum > 0) {
                    scopeNum++;
                    return SUCCESS;
                }
                throw new GeneralSJavaException(ERROR3);
            }
            matcher = returnStatementRegex.matcher(line);
            if (matcher.matches() && scopeNum == 0) {
                throw new GeneralSJavaException(ERROR4);
            }
            if (scopeNum > 0) return SUCCESS;
            matcher = methodRegex.matcher(line);
            if (matcher.matches()) {
                scopeNum++;
                Method.addMethod(line);
                return SUCCESS;
            }
            if (variableCheck(line)) {
                return SUCCESS;
            }
            if(Method.checkMethodCall(line) == SUCCESS){
                throw new GeneralSJavaException(ERROR7);
            }
            throw new GeneralSJavaException(ERROR5);
        }
        catch (Exception e){
            printException(e, numOfLine);
            return FAILED;
        }
    }

    /**
     * performs the checks of the code in the second round
     * checks: teh methods and while/if functions, return statements and scopes
     * @param line - String
     * @return SUCCESS if written properly, FAILED if not
     */
    private int checkLineSecondRead(String line, int numOfLine){
        try {
            if (checksBlankOrCommentLine(line)) {
                return SUCCESS;
            }
            line = line.trim();
            matcher = methodRegex.matcher(line);
            if (matcher.matches()) {
                scopeNum++;
                if (!startMethod(line)) {
                    return FAILED;
                }
                return SUCCESS;
            }
            matcher = ifWhileRegex.matcher(line);
            if (matcher.matches()) {
                scopeNum++;
                numOfInnerWhileOrIf++;
                WhileIf.checkIfWhile(line, scopeNum);
                return SUCCESS;
            }
            matcher = returnStatementRegex.matcher(line);
            if (matcher.matches()) {
                return SUCCESS;
            }
            matcher = endOfScopeRegex.matcher(line);
            if (matcher.matches()) {
                matcher = returnStatementRegex.matcher(previousLine);
                if (matcher.matches() || numOfInnerWhileOrIf > 0) {
                    Variable.removeScope(scopeNum);
                    numOfInnerWhileOrIf--;
                    scopeNum--;
                    if(scopeNum == 0) Variable.removeAssignmentsAtEndOfMethod();
                    return SUCCESS;
                }
                throw new GeneralSJavaException(ERROR6);
            }
            if (scopeNum != 0) {
                if (variableCheck(line)) {
                    return SUCCESS;
                }
                return Method.checkMethodCall(line);
            }
            return SUCCESS;
        }
        catch (Exception e){
            printException(e, numOfLine);
            return FAILED;
        }
    }

    /**
     * function prints the exception
     * @param e - exception
     * @param numOfLine - Integer
     */
    private void printException(Exception e, int numOfLine){
        System.err.println("line " + numOfLine + ": " + e.getMessage());
    }

    /**
     * initializes the method
     * @param line - String
     * @return true if success, false if no
     */
    private boolean startMethod(String line) throws Exception {
        return Method.runMethod(line, scopeNum);
    }

    /**
     * function checks if the line is blank or a comment
     * @param line - String
     * @return true if true, false if false
     */
    private boolean checksBlankOrCommentLine(String line){
        matcher = blankOrCommentRegex.matcher(line);
        return matcher.matches();
    }

    /**
     * checks if the line is a statement that initializes a variable or assigns a variable
     * @param line string
     * @return true if succeeded, false if no
     */
    private boolean variableCheck(String line) throws Exception {
        matcher = varInitializeRegex.matcher(line);
        if (matcher.matches()) {
            Variable.initializeVar(line, scopeNum, matcher.group(1) != null);
            return true;
        }
        matcher = varAssignedRegex.matcher(line);
        if (matcher.matches()) {
            Variable.assignVar(line, scopeNum);
            return true;
        }
        return false;
    }

    /**
     * main function
     * @param args - name of file with code
     */
    public static void main(String[] args) {
        var sJavac = new Sjavac();
        sJavac.checkCode(args);
    }
}


