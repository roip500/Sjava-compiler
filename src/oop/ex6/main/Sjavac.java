package oop.ex6.main;

import oop.ex6.main.syntaxVerifier.Method;
import oop.ex6.main.syntaxVerifier.Variable;

import javax.print.DocFlavor;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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


    private int scopeNum = 0;
    private Matcher matcher;
    private String previousLine;

    /**
     * reads the file a first time - checks global variables and methods names
     * @param fileName - String
     * @return 0 if all ok, 1 if there is a mistake, 2 error in opening code
     */
    private int initialRead(String fileName) {
        String line;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
            while ((line = bufferedReader.readLine()) != null){
                int result = checkLineFirstRead(line);
                if(result != SUCCESS){
                    return result;
                }
            }
            if(scopeNum != 0){
                //TODO: exeption - didn't clode all scopes
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
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
            while ((line = bufferedReader.readLine()) != null) {
                int result = checkLineSecondRead(line);
                if(result != SUCCESS){
                    return result;
                }
                previousLine = line;
            }
        } catch (IOException e) {
            System.err.println(INVALID_FILE_ERR_MSG);
            return ERROR;
        }
        return SUCCESS;
    }



    /**
     * function checks if the line is blank or a comment
     * @param line - String
     * @return true if true, flase if false
     */
    private boolean checksBlankOrCommentLine(String line){
        matcher = blankOrCommentRegex.matcher(line);
        if (matcher.matches()) return true;
        return false;
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

    private int checkLineFirstRead(String line){
        if(checksBlankOrCommentLine(line)){
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
        if(matcher.matches()){
            scopeNum--;
            if(scopeNum < 0){
                //TODO: throw exception
                return FAILED;
            }
            return SUCCESS;
        }
        matcher = ifWhileRegex.matcher(line);
        if(matcher.matches()){
            if(scopeNum > 0){
                scopeNum++;
                return SUCCESS;
            }
            //TODO: throw exception
            return FAILED;
        }
        matcher = returnStatementRegex.matcher(line);
        if(matcher.matches() && scopeNum ==0){
            //TODO: throw exception
            return FAILED;
        }
        if(scopeNum > 0) return SUCCESS;
        matcher = methodRegex.matcher(line);
        if (matcher.matches()) {
            scopeNum++;
            if(!Method.addMethod(line)){
                return FAILED;
            }
            return SUCCESS;
        }
        if(variableCheck(line)){
            return SUCCESS;
        }
        //TODO: throw a general line error
        return FAILED;
    }

    private int checkLineSecondRead(String line){
        if(checksBlankOrCommentLine(line)){
            return SUCCESS;
        }
        line = line.trim();
        matcher = methodRegex.matcher(line);
        if (matcher.matches()) {
            scopeNum++;
            if(!startMethod(line)) {
                return FAILED;
            }
        }
        matcher = ifWhileRegex.matcher(line);
        if(matcher.matches()){
            scopeNum++;
            //TODO: while if class

        }
        matcher = returnStatementRegex.matcher(line);
        if(matcher.matches()){
            return SUCCESS;
        }
        matcher = endOfScopeRegex.matcher(line);
        if(matcher.matches()){
            matcher = returnStatementRegex.matcher(previousLine);
            if(matcher.matches()){
                Variable.removeScope(scopeNum);
                scopeNum--;
                return SUCCESS;
            }
            //TODO: exception - no return value at the end
            return FAILED;
        }
        if(scopeNum != 0) {
            if (variableCheck(line)) {
                return SUCCESS;
            }
            return Method.checkMethodCall(line);
        }
        return SUCCESS;
    }

    private boolean startMethod(String line){
        if(!Method.runMethod(line, scopeNum)){
            return false;
        }
        return true;
    }


    /**
     * main function
     * @param args - name of file with code
     */
    public static void main(String[] args) {
        var sJavac = new Sjavac();
        int result = sJavac.initialRead(args[0]);
        if (result != SUCCESS){
            System.out.println(result);
        }
        else{
            result = sJavac.secondRead(args[0]);
            System.out.println(result);
        }
    }
}


