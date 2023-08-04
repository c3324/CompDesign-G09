
import java.util.ArrayList; 
import java.io.File;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.lang.*;
import java.util.Scanner;

public class CDScanner {
// Scanner iteratively acts on a file on a line by line basis
// Next line is called every nextToken()

    private File file;
    private java.util.Scanner sc;

    private ArrayList<Token> token_buffer;

    //list of possible states the scanner might find in the input file
	private enum STATE{ 
		START, LETTER, VARIABLE, DELIM_OPER, COMMENT, SLCOMMENT, MLCOMMENT,
		INTEGER, FLOAT, STRING, ERROR
	}
	private STATE currentState; //the current state of the state machine
	
	private int lnNum, colNum; //the line the scanner is on, and which column the scanner is up to.
	
	private int curChar, numofChars; // a pointer to the character that the scanner is currently up to, and total number of characters in the file(used for eof checking)
	
	private String text, buffer; //the input file stored as a string, and a buffer to evalute before returning a token.
	
	private int outChar; //pointer to character tp be printed.
	private String outbuffer; //the output buffer.

    public CDScanner(String filename){
        this.file = new File(filename);
        lnNum = 0;
        colNum = 0;
        currentState = STATE.START;
        buffer = "";
        try{
            sc = new Scanner(file);
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        } 
        
    }

    Token nextToken(){
        while (true){
            if (token_buffer.size() == 0){
                scanLine();
            }
            else {
                Token next_token = token_buffer.get(0);
                token_buffer.remove(0);

                return next_token; // May return EOF
            }
        }
    }

    private void scanLine(){
        lnNum++;
        String next_line = sc.nextLine();
        for ( int i = 0; i < next_line.length(); i++){
            // Add char to buffer
            char next_char = next_line.charAt(i)
            buffer += next_char;
            evaluateState(next_char);

        }


    }

    private boolean charIsDelimiter(char c){
        if(
            c == ',' ||
            c == '[' ||
            c == ']' ||
            c == '(' ||
            c == ')' ||
            c == '=' ||
            c == '+' ||
            c == '-' ||
            c == '*' ||
            c == '/' ||
            c == '%' ||
            c == '^' ||
            c == '>' ||
            c == '<' || 
            c == ';' ||
            c == '.' 
        ){
            return true;
        }
        return false;
    }

    // Evaluate next character in string to determine if tokenizing is necessary.
    private void evaluateState(char character){

        switch(currentState){
            case START:
                if (Character.isAlphabetic(character)){
                    currentState = STATE.LETTER;
                }
                break;
            case LETTER:
            // Letter could become a variable or a keyword
                if (character == ' '){
                    currentState = STATE.START;
                    tokenizeBuffer();
                }
                else if ( Character.isDigit(character)){
                    currentState = STATE.VARIABLE;
                }
                else if ( charIsDelimiter(character)){
                    currentState = STATE.DELIM_OPER;
                    tokenizeBuffer();
                }
                
                break;
            
            

        }

    }

    private void tokenizeBuffer(){

        int tokenValue = -1;
        tokenValue = Token.checkKeyWords(buffer);
        if ( tokenValue == -1){
            tokenValue = Token.checkDels(buffer);
        }
        if ( tokenValue == -1){
            tokenValue = Token.checkOps(buffer);
        }
        else {
            tokenValue = 63; // Undefined Token
        }

    }

    private void 
    
}
