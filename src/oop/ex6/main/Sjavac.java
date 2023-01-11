package oop.ex6.main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sjavac {

    private static final String INVALID_FILE_ERR_MSG = "Invalid file name";
    private static final String BLANK_OR_COMMENT = "^//.*$|\\s*";
    private static final String END_OF_LINE_MODIFIERS = "^.*(?:;|\\{)$"; //TODO: maybe unnecessary
    private static final String GENERAL_VAR_LINE = "^\\s*(?:(?:int|double|String|boolean|char)(?:\\s*" +
            "[a-zA-z_\\d]+\\s*(?:=)?,?)+|[a-zA-z_\\d]\\s*=\\s*.*?);$";
    private static final Pattern generalVarLineChecker = Pattern.compile(GENERAL_VAR_LINE);
    private static final Pattern endOfLineChecker = Pattern.compile(END_OF_LINE_MODIFIERS);
    private static final Pattern blankOrCommentRegex = Pattern.compile(BLANK_OR_COMMENT);
    private static Matcher matcher;

    public static void main(String[] args) {
        String line;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(args[0]))){
            while ((line = bufferedReader.readLine())!= null){
                matcher = endOfLineChecker.matcher(line);
                if(!matcher.matches()){
                    //TODO: check if necessary and if so throw an error
                    System.out.println(1);
                    return;
                }
                matcher =  blankOrCommentRegex.matcher(line);
                if(matcher.matches()) continue;
                matcher = generalVarLineChecker.matcher(line);
                if(matcher.matches()){
                    //TODO: call variable class
                    continue;
                }

            }
        }
        catch (FileNotFoundException e) {
            System.out.println(INVALID_FILE_ERR_MSG);
        }
        catch (IOException e) {
            System.out.println(INVALID_FILE_ERR_MSG);
        }

    }
}


