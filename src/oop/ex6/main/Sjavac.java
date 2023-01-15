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
    private static final String VAR_ASSIGMENT_LINE = "^\\s*[a-zA-z_\\d]\\s*=\\s*.*?;$";//TODO: add multiple assignments
    private static final String METHOD_LINE = "^\\s*void.*[{]$";

    //pattern declaration:
    private static final Pattern varInitializeRegex = Pattern.compile(VAR_INITIALIZE_LINE);
    private static final Pattern endOfLineRegex = Pattern.compile(END_OF_LINE_MODIFIERS);
    private static final Pattern blankOrCommentRegex = Pattern.compile(BLANK_OR_COMMENT);
    private static final Pattern varAssignedRegex = Pattern.compile(VAR_ASSIGMENT_LINE);
    private static final Pattern methodRegex = Pattern.compile(METHOD_LINE);

    public static void main(String[] args) {
        String line;
        Matcher matcher;
        int scope = 0;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(args[0]))){
            while ((line = bufferedReader.readLine())!= null){
                line = line.trim();
                matcher =  blankOrCommentRegex.matcher(line);
                if(matcher.matches()) continue;
                matcher = endOfLineRegex.matcher(line);
                if(!matcher.matches()){
                    //TODO: check if necessary and if so throw an error
                    System.out.println(1);
                    return;
                }
                matcher = methodRegex.matcher(line);
                if(matcher.matches()){
                    //TODO: call method class
                    continue;
                }
                matcher = varInitializeRegex.matcher(line);
                if(matcher.matches()){
                    Variable.initializeVar(line, scope, matcher.group(1) != null);
                    continue;
                }
                matcher = varAssignedRegex.matcher(line);
                if(matcher.matches()){
                    Variable.assignVar(line, scope);
                    continue;
                }
                System.out.println();
                return ; //TODO: finish
            }
        } catch (IOException e) {
            System.out.println(INVALID_FILE_ERR_MSG);
        }
    }
}


