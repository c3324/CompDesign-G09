
import java.util.ArrayList; 
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Arrays;
import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter; 

public class CDScanner {
// Scanner iteratively acts on a file on a line by line basis
// Next line is called every nextToken()

    private File file;
    private BufferedReader reader;
    private FileWriter writer;
    private BufferedWriter bufferW;
    private PrintWriter printer;
    private java.util.Scanner sc;

    private ArrayList<Token> token_buffer;
    ErrorHandling errorList_Scanner;

    // States for use with state machine
	private enum STATE{ 
		START, 
        ALPHABETIC_STRING, VARIABLE, 
        DELIM_OPER, 
        STRING, SLCOMMENT, MLCOMMENT,
		INTEGER, FLOAT_POINT, FLOAT,  // Float point is precisely at the dot
        LEXICAL_ERROR, UNDEFINED_SYMBOL // Different error states may have different exit token cases or error handling.
	}
	private STATE currentState; //the current state of the state machine
	
	private int line_number, col_number; //the line the scanner is on, and which column the scanner is up to.
	private String buffer; // Characters existing buffer to be tokenized

    private LookUpTable look_up_table; // Contains on valid keywords, delimiters and operators
    private SymbolTable symbol_table; // Currently not implemented.
    private boolean isEOF;

    private int print_char_counter;

    public CDScanner(String filename){
        this.file = new File(filename);
        line_number = 0;
        col_number = 0;
        symbol_table = new SymbolTable(true);
        currentState = STATE.START;
        buffer = "";
        isEOF = false;
        token_buffer = new ArrayList<>();
        print_char_counter = 0;
        errorList_Scanner = ErrorHandling.getInstance();
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
        //System.out.println(buffer);
        buffer += "\r";
        evaluateState('\r');


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
            c == ':' ||
            c == '&'
        ){
            return true;
        }
        return false;
    }

    // Evaluate next character in string to determine if tokenizing is necessary.
    // tokenizeBuffer() is called before updating the state as the current state provides context for the tokenizer.
    private void evaluateState(char character){

        // System.out.println("Start state: " + currentState);

        // White space mostly tokenizes buffer and resets state
        if (character == ' ' && currentState != STATE.STRING && currentState != STATE.MLCOMMENT && currentState != STATE.SLCOMMENT){
            buffer = buffer.substring(0, buffer.length() - 1);
            tokenizeBuffer();
            currentState = STATE.START;
            return;
        }
        // Tab acts similarly to white space
        if (character == '\u0009' && currentState != STATE.STRING && currentState != STATE.MLCOMMENT && currentState != STATE.SLCOMMENT){
            buffer = buffer.substring(0, buffer.length() - 1);
            col_number += 3; // already increased by 1 for reading character.. add 3 more.
            tokenizeBuffer();
            currentState = STATE.START;
            return;
        }

        // Check if new line
        if (character == '\r' && currentState != STATE.MLCOMMENT){
            if (currentState == STATE.STRING){
                // New line is lexical error for strings.
                currentState = STATE.LEXICAL_ERROR;
                tokenizeBuffer();
                currentState = STATE.START;
                return;
            } //else..
            buffer = buffer.substring(0, buffer.length()-1);
            tokenizeBuffer();
            currentState = STATE.START;
            return;
        }

        // Evaluate if character is valid symbol outside of comments and strings
        if ((!(Character.isAlphabetic(character) || Character.isDigit(character) || Character.isWhitespace(character) || charIsDelimiter(character) || look_up_table.checkLexeme(buffer) != -1 || character == '"')) 
            && !(Arrays.asList(STATE.MLCOMMENT, STATE.SLCOMMENT, STATE.STRING).contains(currentState))){
            
            if (currentState != STATE.UNDEFINED_SYMBOL){
                // Tokenize buffer
                tokenizeBuffer(1);
                currentState = STATE.UNDEFINED_SYMBOL;
            }
            // System.out.println("Undefined symbol found in:" + buffer + "  with currState = " + currentState); // Debug printout
            return;
        }

        // Strings Mostly will always cause tokenizing
        if (character == '"' && currentState != STATE.MLCOMMENT && currentState != STATE.SLCOMMENT){
            if ( currentState == STATE.STRING){
                tokenizeBuffer();
                currentState = STATE.START;
            }
            else{
                tokenizeBuffer(1);
                currentState = STATE.STRING;
            }
            return;
        }

        // State machine evaluations.
        switch(currentState){
            case START:
                if (Character.isAlphabetic(character) || character == '_'){
                    currentState = STATE.ALPHABETIC_STRING;
                }
                else if (Character.isDigit(character)){
                    currentState = STATE.INTEGER;
                }
                else if (charIsDelimiter(character)){
                    currentState = STATE.DELIM_OPER;
                }
                break;
            case ALPHABETIC_STRING:
            // Letter could become a variable or a keyword
                
                if ( Character.isDigit(character)){
                    currentState = STATE.VARIABLE;
                }
                else if ( charIsDelimiter(character) ){
                    tokenizeBuffer(1);
                    currentState = STATE.DELIM_OPER;
                }
                break;
            case VARIABLE:
                if (charIsDelimiter(character)){
                    tokenizeBuffer(1);
                    currentState = STATE.DELIM_OPER;
                }
                break;
            case DELIM_OPER:
                if ( Character.isDigit(character)){
                    tokenizeBuffer(1);
                    currentState = STATE.INTEGER;
                }
                else if ( Character.isAlphabetic(character)){
                    tokenizeBuffer(1);
                    currentState = STATE.ALPHABETIC_STRING;
                }
                else if (character == ';'){
                    tokenizeBuffer(1);
                    currentState = STATE.START;
                }
                else if (buffer.equals("/--")){
                    currentState = STATE.SLCOMMENT;
                }
                else if (buffer.equals("/**")){
                    currentState = STATE.MLCOMMENT;
                } else if (buffer.length() == 3){
                    if (look_up_table.checkLexeme(buffer.substring(0, 2)) != -1){
                        tokenizeBuffer(1);
                    }
                    else{
                        tokenizeBuffer(2);
                    }
                }
                
                break;

            case STRING:
                break;

            case SLCOMMENT:
                if (character == '\r' ){
                    tokenizeBuffer();
                    currentState = STATE.START;
                }
                break;
            case MLCOMMENT:
                if ( buffer.length() >= 3){
                    
                    if (buffer.substring(buffer.length()-3, buffer.length()).equals("**/")){
                        tokenizeBuffer();
                        currentState = STATE.START;  
                    }
                }
                break;
            case INTEGER:
                if (character == '.'){
                    currentState = STATE.FLOAT_POINT;
                }
                else if (charIsDelimiter(character)){
                    tokenizeBuffer(1);
                    currentState = STATE.DELIM_OPER;
                }
                else if (Character.isAlphabetic(character)){
                    tokenizeBuffer(1);
                    currentState = STATE.ALPHABETIC_STRING;
                }
                break;

            case FLOAT_POINT:
                if (charIsDelimiter(character)){
                    tokenizeBuffer(2);
                    tokenizeBuffer(1);
                    currentState = STATE.START;    
                }
                else if (Character.isAlphabetic(character)){
                    tokenizeBuffer(2);
                    tokenizeBuffer(1);
                    currentState = STATE.ALPHABETIC_STRING;
                }
                else if (Character.isWhitespace(character)){
                    tokenizeBuffer(1);
                    buffer = "";
                    currentState = STATE.START;
                } if (Character.isDigit(character)){
                    currentState = STATE.FLOAT;
                }
                break;

            case FLOAT:
                if (charIsDelimiter(character)){
                    tokenizeBuffer(1);
                    currentState = STATE.DELIM_OPER;
                    evaluateState(character);    
                }
                else if (Character.isAlphabetic(character)){
                    tokenizeBuffer(1);
                    currentState = STATE.ALPHABETIC_STRING;
                }
                break;

            case UNDEFINED_SYMBOL:
                if (Character.isAlphabetic(character)){
                    tokenizeBuffer(1);
                    currentState = STATE.ALPHABETIC_STRING;
                }
                else if (Character.isDigit(character)){
                    tokenizeBuffer(1);
                    currentState = STATE.INTEGER;
                }
                else if (charIsDelimiter(character)){
                    tokenizeBuffer(1);
                    currentState = STATE.DELIM_OPER;
                }
                break;

            case LEXICAL_ERROR:   
                break;
            

        }

        // System.out.println("End state: " + currentState);

    }

    

    private void tokenizeBuffer(){

        // Ignore blank buffers (includes empty, white space and tabs).
        if (buffer.isBlank()){
            buffer = "";
            return;
        }

        int tokenValue = -1;
        String lex = "";
        // line number and col number already defined
        // System.out.println("Current state: " + currentState);
        // System.out.println("Checking lexeme: '" + buffer + "'.");
        if ( tokenValue == -1){ 
            tokenValue = look_up_table.checkLexeme(buffer);
        }
        if ( tokenValue == -1){
            tokenValue = look_up_table.checkLexeme(buffer);
        }
        if ( tokenValue == -1){
            tokenValue = look_up_table.checkLexeme(buffer);
        }
        // Edge case as some delimiters are held for pairing and some are not.
        if ( tokenValue == -1 && currentState == STATE.DELIM_OPER && !buffer.equals("/*")){
            if ( buffer.length() == 2 && tokenValue == -1){
                String del1 = buffer.substring(0, 1);
                String del2 = buffer.substring(1, 2);
                int tokenValue1 = look_up_table.checkLexeme(del1);
                int tokenValue2 = look_up_table.checkLexeme(del2);
                if ( tokenValue1 == -1 || tokenValue2 == -1){
                    tokenValue = 63; // Undefined
                    lex = "Undefined Operator" + buffer;
                }
                else {
                    Token tok1, tok2;
                    tok1 = new Token(tokenValue1, "", line_number, col_number-1, symbol_table);
                    tok2 = new Token(tokenValue2, "", line_number, col_number, symbol_table);
                    token_buffer.add(tok1);
                    token_buffer.add(tok2);
                    buffer = "";
                    return;
                }
            }
        }
        if ((currentState == STATE.VARIABLE || currentState == STATE.ALPHABETIC_STRING) && tokenValue == -1){
            lex = buffer;
            tokenValue = 59;
        }
        else if (currentState == STATE.INTEGER && tokenValue == -1){
            try {
                Long.parseLong(buffer);
                lex = buffer;
                tokenValue = 60;
            } catch (Exception e){
                // Cast failed.. 
                // undefined token
                currentState = STATE.UNDEFINED_SYMBOL;  // Overflow Error
            } 
        }
        else if (currentState == STATE.FLOAT && tokenValue == -1){
            try {
                Double.parseDouble(buffer);
                lex = buffer;
                tokenValue = 61;
            } catch (Exception e){
                // Cast failed.. 
                // undefined token
                lex = "Numerical overflow error: " + buffer;
                tokenValue = 63;
            } 
        }
        else if (currentState == STATE.FLOAT_POINT && tokenValue == -1){
            // Tokenize left of dot and dot
            if (buffer.equals(".")){
                lex = "";
                tokenValue = 44; // TDOTT
            }
            else {
                try {
                    Long.parseLong(buffer);
                    lex = buffer;
                    tokenValue = 14;
                } catch (Exception e){
                    // Cast failed.. 
                    // undefined token
                    lex = "Numerical overflow error: " + buffer;
                    tokenValue = 63;
                } 
            }
        }
        else if (currentState == STATE.STRING && tokenValue == -1){
            lex = buffer;
            tokenValue = 62;
        }
        else if (currentState == STATE.LEXICAL_ERROR && tokenValue == -1){
            tokenValue = 63; // Undefined Token
            lex = buffer;
        }
        else if ( currentState == STATE.MLCOMMENT || currentState == STATE.SLCOMMENT){
            buffer = "";
            return; // Don't create token for a comment;
        }
        if (currentState == STATE.UNDEFINED_SYMBOL && tokenValue == -1)
        {
            tokenValue = 63; // Undefined Token
            lex = "Undefined Token: " + buffer;
        }
        if (tokenValue == -1){
            if ( buffer.equals("") || buffer.isBlank()){ // Nothing ot tokenize
                buffer = "";
                return;
            }
            System.out.println("Unhandled Exception!!! at currentState = " + currentState + "  with buffer = '" + buffer + "'");
        }

        Token new_token = new Token(tokenValue, lex, line_number, (col_number - buffer.length()), symbol_table);
        if(new_token.getTokNum() == 63){
            error(new_token);
        }
        token_buffer.add(new_token);

        buffer = "";

    }

    private void tokenizeBuffer(int n_buffer_skips){
        // method keeps the last element of the buffer intact and does not tokenize it
        // helper method to reduce repeated code
            String temp_buffer =  buffer.substring(buffer.length() - n_buffer_skips, buffer.length());
            buffer = buffer.substring(0, buffer.length() - n_buffer_skips);
            tokenizeBuffer();
            buffer = temp_buffer;
        
    }

    public boolean eof(){
        return isEOF;
    }

    public void printToken(Token token){
        if (print_char_counter > 60){
             System.out.println();
            print_char_counter = 0;
        }
        if (token.getTokNum() == 63){
            System.out.println();
            System.out.print("TUNDF ");
            System.out.println();
            System.out.println("lexical error " + token.getLex());
            print_char_counter = 0;
            return;
        }
        String token_string = token.getString();
        for ( int i = 0; i < token_string.length() % 6; i++){
            token_string += " ";
        }
        print_char_counter += token_string.length();

        System.out.print(token_string);

    }

    //Better Error Handling for Lexical Errors

    private void error(Token current_Token){
        String errorString = "Lexical Error: (" + current_Token.getLn() + "," + current_Token.getCol() + "): " + current_Token.getLex();
        errorList_Scanner.addErrorToList(errorString);
        //System.out.println("Added to Error List");
        
    }

    public LinkedList<String> returnErrorList(){        
        return errorList_Scanner.getErrorList();
    } 

    public void createProgramListing(){
        int i =1;
        try {
			reader = new BufferedReader(new FileReader(file));
            writer = new FileWriter("proglisting.txt");
			String line = reader.readLine();

			while (line != null) {

				//System.out.println(i + ". " + line);
                writer.write(i + ". " + line + "\n");
				// read next line
				line = reader.readLine();
                i++;
			}

			reader.close();
            writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public void addErrorstoFile(){
        try {
            writer= new FileWriter("proglisting.txt", true);
            bufferW= new BufferedWriter(writer);
            printer = new PrintWriter(bufferW);

            printer.println("\n LEXICAL ERRORS \n");
            printer.println("-------------------------------------------------------------\n");

            LinkedList<String> errorList = errorList_Scanner.getErrorList();
            for(int i = 0; i < errorList.size() ; i++){
                printer.println(errorList.get(i));
            }

            printer.flush();
            writer.close();
            bufferW.close();
            printer.close();
        
        } catch (IOException io){}
        }  
    }



    


