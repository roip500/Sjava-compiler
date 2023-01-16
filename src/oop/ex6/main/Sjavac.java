package oop.ex6.main;

import oop.ex6.main.syntaxVerifier.Variable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sjavac {

    //regex string pattern:
    private static final String INVALID_FILE_ERR_MSG = "Invalid file name";
    private static final String BLANK_OR_COMMENT = "^//.*$|\\s*";
    private static final String END_OF_LINE_MODIFIERS = "^.*[;{]$"; //TODO: maybe unnecessary
    private static final String VAR_INITIALIZE_LINE = "^\\s*(final)?\\s*(?:int|double|String|boolean|char)\\s+.*;$";
    private static final String VAR_ASSIGNMENT_LINE = "^\\s*[a-zA-z_\\d]\\s*=\\s*.*?;$";
    //TODO: add multiple assignments -- doesn't matter, it recognises the first assignment and counts the rest
    // as part of the assignment, so technically it works for multiple stuff.
    private static final String METHOD_LINE = "^\\s*void.*[{]$";

    //pattern declaration:
    private static final Pattern varInitializeRegex = Pattern.compile(VAR_INITIALIZE_LINE);
    private static final Pattern endOfLineRegex = Pattern.compile(END_OF_LINE_MODIFIERS);
    private static final Pattern blankOrCommentRegex = Pattern.compile(BLANK_OR_COMMENT);
    private static final Pattern varAssignedRegex = Pattern.compile(VAR_ASSIGNMENT_LINE);
    private static final Pattern methodRegex = Pattern.compile(METHOD_LINE);
    private static final Pattern endOfScopeRegex = Pattern.compile("^.*}$");
    private static final Pattern ifWhileRegex = Pattern.compile("^\\s*(?:if|while).*[{]$");

    public static void main(String[] args) {
        String line;
        Matcher matcher;
        int scope = 0;
        if (!initialRead(args[0])) System.out.println(2);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(args[0]))) {
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                matcher = blankOrCommentRegex.matcher(line);
                if (matcher.matches()) continue;
                matcher = endOfLineRegex.matcher(line);
                if (!matcher.matches()) {
                    //TODO: check if necessary and if so throw an error
                    System.out.println(1);
                    return;
                }
                matcher = methodRegex.matcher(line);
                if (matcher.matches()) {
                    //TODO: call method class
                    continue;
                }
                matcher = varInitializeRegex.matcher(line);
                //TODO: possibly redundant because in scope 0 it is added in the initial reading
                if (matcher.matches()) {
                    Variable.initializeVar(line, scope, matcher.group(1) != null);
                    continue;
                }
                matcher = varAssignedRegex.matcher(line);
                //TODO: possibly redundant because in scope 0 it is handled in the initial reading
                if (matcher.matches()) {
                    Variable.assignVar(line, scope);
                    continue;
                }
                System.out.println();
                return; //TODO: finish
            }
        } catch (IOException e) {
            System.out.println(INVALID_FILE_ERR_MSG);
        }
    }

    private static boolean initialRead(String fileName) {
        String line;
        Matcher matcher;
        int openedScopes = 0;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
            while ((line = bufferedReader.readLine()) != null){
                line = line.trim();
                matcher = blankOrCommentRegex.matcher(line);
                if (matcher.matches()) continue;
                matcher = endOfScopeRegex.matcher(line);
                if(matcher.matches()){
                    openedScopes--;
                    if(openedScopes < 0){
                        //TODO: throw exception
                        return false;
                    }
                    continue;
                }
                matcher = ifWhileRegex.matcher(line);
                if(matcher.matches()){
                    openedScopes++;
                    continue;
                }
                if(openedScopes > 0) continue;
                matcher = methodRegex.matcher(line);
                if (matcher.matches()) {
                    openedScopes++;
                    //TODO: call method class
                    continue;
                }
                matcher = varInitializeRegex.matcher(line);
                if (matcher.matches() && openedScopes == 0) {
                    if(!Variable.initializeVar(line, 0, matcher.group(1) != null)) return false;
                    continue;
                }
                matcher = varAssignedRegex.matcher(line);
                if (matcher.matches() && openedScopes == 0) {
                    if(!Variable.assignVar(line, 0)) return false;
                }
            }
        }
        catch (IOException e) {
            System.out.println(INVALID_FILE_ERR_MSG);
            return false;
        }
        return true;
    }
}


