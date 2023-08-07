
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
		START, 
        ALPHABETIC_STRING, VARIABLE, 
        DELIM_OPER, 
        STRING, SLCOMMENT, MLCOMMENT,
		INTEGER, FLOAT, 
        ERROR, UNDEFINED_SYMBOL // Different error states may have different exit token cases.
	}
	private STATE currentState; //the current state of the state machine
	
	private int line_number, col_number; //the line the scanner is on, and which column the scanner is up to.
	private int curChar, numofChars; // a pointer to the character that the scanner is currently up to, and total number of characters in the file(used for eof checking)
	private String text, buffer; //the input file stored as a string, and a buffer to evalute before returning a token.
	private int outChar; //pointer to character tp be printed.
	private String outbuffer; //the output buffer.

    private LookUpTable look_up_table;
    private SymbolTable symbol_table;
    private boolean isEOF;
    private boolean done;

    public CDScanner(String filename){
        this.file = new File(filename);
        line_number = 0;
        col_number = 0;
        symbol_table = new SymbolTable();
        currentState = STATE.START;
        buffer = "";
        isEOF = false;
        token_buffer = new ArrayList<>();
        done = false;
        try{
            sc = new Scanner(file);
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        } 
        look_up_table = new LookUpTable();
        
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
        line_number++;
        if (!sc.hasNextLine()){
            Token eofToken = new Token(0, "", line_number, col_number, symbol_table);
            token_buffer.add(eofToken);
            isEOF = true;
            return;
        }
        String next_line = sc.nextLine();
        for ( int i = 0; i < next_line.length(); i++){
            col_number = i;
            // Add char to buffer
            char next_char = next_line.charAt(i);
            //System.out.println("next char: " + next_char);
            buffer += next_char;
            evaluateState(next_char);
        }
        System.out.println(buffer);
        buffer += "\n";
        evaluateState('\n');


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
            c == '.' ||
            c == '!' ||
            c == ':'
        ){
            return true;
        }
        return false;
    }

    // Evaluate next character in string to determine if tokenizing is necessary.
    // tokenizeBuffer() is called before updating the state as the current state provides context for the tokenizer.
    private void evaluateState(char character){

        // System.out.println("Start state: " + currentState);
        // Check if new line
        if (character == '\n'){
            buffer = buffer.substring(0, buffer.length()-1);
            tokenizeBuffer();
            currentState = STATE.START;
            System.out.println("Found new line character");
            return;
        }
        
        // Evaluate if character is valid symbol
        if (!(Character.isAlphabetic(character) || Character.isDigit(character) || Character.isWhitespace(character) || charIsDelimiter(character))){
            currentState = STATE.UNDEFINED_SYMBOL;
        }


        switch(currentState){
            case START:
                if (Character.isAlphabetic(character)){
                    currentState = STATE.ALPHABETIC_STRING;
                }
                else if (Character.isDigit(character)){
                    currentState = STATE.INTEGER;
                }
                else if (charIsDelimiter(character)){
                    currentState = STATE.DELIM_OPER;
                }
                else if (character == '"'){
                    currentState = STATE.STRING;
                } else if (character == ' '){
                    buffer = buffer.substring(1, buffer.length());
                }
                break;
            case ALPHABETIC_STRING:
            // Letter could become a variable or a keyword
                if (character == ' '){
                    buffer = buffer.substring(0, buffer.length() - 1);
                    tokenizeBuffer();
                    currentState = STATE.START;
                }
                else if ( Character.isDigit(character)){
                    currentState = STATE.VARIABLE;
                }
                else if ( charIsDelimiter(character)){
                    tokenizeBuffer();
                    currentState = STATE.DELIM_OPER;
                }
                break;
            case VARIABLE:
                if (character == ' '){
                    buffer = buffer.substring(0, buffer.length() - 1);
                    tokenizeBuffer();
                    currentState = STATE.START;
                }
                else if (charIsDelimiter(character)){
                    tokenizeBuffer();
                    currentState = STATE.DELIM_OPER;
                }
            case DELIM_OPER:
                if (character == ' '){
                    buffer = buffer.substring(0, buffer.length() - 1);
                    tokenizeBuffer();
                    currentState = STATE.START;
                }
                else if ( Character.isDigit(character)){
                    tokenizeBuffer();
                    currentState = STATE.INTEGER;
                }
                else if ( Character.isAlphabetic(character)){
                    currentState = STATE.ALPHABETIC_STRING;
                }
                else if (buffer.equals("/--")){
                    currentState = STATE.SLCOMMENT;
                }
                else if (buffer.equals("/**")){
                    currentState = STATE.MLCOMMENT;
                }
                else if (character == '"'){
                    currentState = STATE.STRING;
                }
            case STRING:
                if (character == '"'){
                    tokenizeBuffer();
                    currentState = STATE.START; 
                }
            case SLCOMMENT:
                if (character == '\n' ){
                    tokenizeBuffer();
                    currentState = STATE.START;
                }
            case MLCOMMENT:
                if ( buffer.equals("**/")){
                    tokenizeBuffer();
                    currentState = STATE.START;  
                }
            case INTEGER:
                if (character == ' '){
                    //buffer = buffer.substring(0, buffer.length() - 1);
                    tokenizeBuffer();
                    currentState = STATE.START;
                }
                else if (character == '.'){
                    currentState = STATE.FLOAT;
                }
                else if (charIsDelimiter(character)){
                    tokenizeBuffer();
                    currentState = STATE.DELIM_OPER;
                }
                else if (Character.isAlphabetic(character)){
                    tokenizeBuffer();
                    currentState = STATE.ALPHABETIC_STRING;
                }

            case FLOAT:
                if (character == ' '){
                    //buffer = buffer.substring(0, buffer.length() - 1);
                    tokenizeBuffer();
                    currentState = STATE.START;
                }
                else if (charIsDelimiter(character)){
                    tokenizeBuffer();
                    currentState = STATE.START;    
                }

            case UNDEFINED_SYMBOL:
                if (character == ' '){
                    //buffer = buffer.substring(0, buffer.length() - 1);
                    tokenizeBuffer();
                    currentState = STATE.START;
                }

            case ERROR:    
            

        }

        // System.out.println("End state: " + currentState);

    }

    private void tokenizeBuffer(){

        // Ignore white space tokens.
        if (buffer.equals(" ") || buffer.equals("")){
            buffer = "";
            return;
        }

        int tokenValue = -1;
        String lex = "";
        // line number and col number already defined
        System.out.println("Current state: " + currentState);

        if ( tokenValue == -1){ 
            tokenValue = look_up_table.checkKeyWords(buffer);
        }
        if ( tokenValue == -1){
            tokenValue = look_up_table.checkDels(buffer);
        }
        if ( tokenValue == -1){
            tokenValue = look_up_table.checkOps(buffer);
        }
        if ((currentState == STATE.VARIABLE || currentState == STATE.ALPHABETIC_STRING) && tokenValue == -1){
            lex = buffer;
            tokenValue = 59;
            System.out.println("Variable found: " + tokenValue);
        }
        else if (currentState == STATE.INTEGER && tokenValue == -1){
            lex = buffer;
            tokenValue = 14;
        }
        else if (currentState == STATE.FLOAT && tokenValue == -1){
            lex = buffer;
            tokenValue = 15;
        }
        else if (currentState == STATE.STRING && tokenValue == -1){
            lex = buffer;
            tokenValue = 62;
        }
        else if (currentState == STATE.ERROR && tokenValue == -1){
            //TODO: May not be undefined -> may be another error.
            tokenValue = 63; // Undefined Token
        }
        else if (currentState == STATE.UNDEFINED_SYMBOL && tokenValue == -1)
        {
            tokenValue = 63; // Undefined Token
        }
        System.out.println("Token found: " + tokenValue);

        Token new_token = new Token(tokenValue, lex, line_number, col_number, symbol_table);
        token_buffer.add(new_token);

        buffer = "";

    }

    public boolean eof(){
        return isEOF;
    }

    
    
}
