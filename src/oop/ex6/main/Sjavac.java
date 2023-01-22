package oop.ex6.main;

import oop.ex6.main.syntaxVerifier.Method;
import oop.ex6.main.syntaxVerifier.Variable;
import oop.ex6.main.syntaxVerifier.WhileIf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sjavac {

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
        }
        else{
            result = this.secondRead(args[0]);
            System.out.println(result);
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
            if (!matcher.matches()) {
                //TODO: throw an error
                System.out.println(1);
                return FAILED;
            }
            matcher = endOfScopeRegex.matcher(line);
            if (matcher.matches()) {
                scopeNum--;
                if (scopeNum < 0) {
                    //TODO: throw exception
                    return FAILED;
                }
                return SUCCESS;
            }
            matcher = ifWhileRegex.matcher(line);
            if (matcher.matches()) {
                if (scopeNum > 0) {
                    scopeNum++;
                    return SUCCESS;
                }
                //TODO: throw exception
                return FAILED;
            }
            matcher = returnStatementRegex.matcher(line);
            if (matcher.matches() && scopeNum == 0) {
                //TODO: throw exception
                return FAILED;
            }
            if (scopeNum > 0) return SUCCESS;
            matcher = methodRegex.matcher(line);
            if (matcher.matches()) {
                scopeNum++;
                if (!Method.addMethod(line)) {
                    return FAILED;
                }
                return SUCCESS;
            }
            if (variableCheck(line)) {
                return SUCCESS;
            }
            //TODO: throw a general line error
            return FAILED;
        }
        catch (RuntimeException e){
            StringBuilder strToPrint = new StringBuilder("line " + numOfLine + ": " + e.getMessage() + "\n");
            for (var txt: e.getStackTrace()) {
                strToPrint.append(txt + "\n");
            }
            System.err.println(strToPrint);
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
                    return SUCCESS;
                }
                //TODO: exception - no return value at the end
                return FAILED;
            }
            if (scopeNum != 0) {
                if (variableCheck(line)) {
                    return SUCCESS;
                }
                return Method.checkMethodCall(line);
            }
            return SUCCESS;
        }
        catch (RuntimeException e){
            StringBuilder strToPrint = new StringBuilder("line " + numOfLine + ": " + e.getMessage() + "\n");
            for (var txt: e.getStackTrace()) {
                strToPrint.append(txt + "\n");
            }
            System.err.println(strToPrint);
            return FAILED;
        }
    }

    /**
     * initializes the method
     * @param line - String
     * @return true if success, false if no
     */
    private boolean startMethod(String line){
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
    private boolean variableCheck(String line){
        matcher = varInitializeRegex.matcher(line);
        if (matcher.matches()) {
            return Variable.initializeVar(line, scopeNum, matcher.group(1) != null);
        }
        matcher = varAssignedRegex.matcher(line);
        if (matcher.matches()) {
            return Variable.assignVar(line, scopeNum);
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


