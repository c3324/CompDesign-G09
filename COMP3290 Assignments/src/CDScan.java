//Evangeline Hooper
//COMP3290 Project 1B
//CDScan.java
//Due 15.08.2021

import java.util.ArrayList; 
import java.io.File;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.lang.*;


public class CDScan{
	
	//list of possible states the scanner might find in the input file
	private enum STATE{ 
		START, WHITESPACE, KEYWORD, DELIM_OPER, COMMENT, SLCOMMENT, MLCOMMENT,
		INTEGER, FLOAT, STRING, ERROR
	}
	private STATE curState; //the current state of the state machine
	
	private int lnNum, colNum; //the line the scanner is on, and which column the scanner is up to.
	
	private int curChar, numofChars; // a pointer to the character that the scanner is currently up to, and total number of characters in the file(used for eof checking)
	
	private String text, buffer; //the input file stored as a string, and a buffer to evalute before returning a token.
	
	private int outChar; //pointer to character tp be printed.
	private String outbuffer; //the output buffer.
	
	//structure of a scan object
	//takes in the string that was generated in main
	public CDScan(String text){
		this.text = text;
		numofChars = text.length();
		buffer = "";
		curChar = 0;
		curState = STATE.START;
		
		Token.reservedList();
		
		lnNum = 1;
		colNum = 1;
		outChar = 0;
		outbuffer = "";		
	}
	
	//create an EOF Token
	public Token eofTok(){
		return new Token(Token.T_EOF, null, lnNum, colNum);
	}
	
	//main scanning function
	public Token scan() throws Exception{		
		
		Token found = null;
		//System.out.println("STATE before loop: " + curState + "\n"); /*debugging*/
		
		while(found == null){	
			//Thread.sleep(100); /*debugging*/
			//System.out.print("Char: " + text.charAt(curChar)+"\n"); /*debugging*/
			if(buffer.equals(".")){
				found = new Token(Token.TDOTT, buffer, lnNum, colNum-1);
				buffer = "";
				break;
			}
			switch(curState){
				case START:
					stateTransition(text.charAt(curChar));
					break;			
				case WHITESPACE:
					stateTransition(text.charAt(curChar));
					break;			
				case KEYWORD:
					if(Character.isAlphabetic(text.charAt(curChar)) || Character.isDigit(text.charAt(curChar))){ //cause we can have numbers in a keyword as well... according to the spec
						buffer += String.valueOf(text.charAt(curChar));
						colNum++;
						curChar++;
					} else {
						if(Token.checkKeyWords(buffer) == -1){ //was going to do another case for TIDEN, but it seems to work here 
							found = new Token(Token.TIDEN, buffer, lnNum, colNum - buffer.length());
							buffer = "";
						} else {
							found = new Token(Token.checkKeyWords(buffer), buffer, lnNum, colNum - buffer.length()); //when a reserved keyword has been found
							buffer = "";
						}
					}
					//System.out.println("keyword state buffer: " + buffer + "\n"); /*debugging*/
					break;	
					
				case DELIM_OPER:
					if(buffer.isEmpty()){
						buffer += (String.valueOf(text.charAt(curChar)));
						colNum++;
						curChar++;
						//System.out.println("buffer = " + buffer); /*debugging*/
					} else { 
						String tmp = buffer + String.valueOf(text.charAt(curChar));
						//System.out.print("tmp = " + tmp + "\n"); /*debugging*/
						if (Token.checkOps(tmp) != -1) { //operator
							found = new Token(Token.checkOps(tmp), tmp, lnNum, colNum -1);
							buffer = "";
							colNum++;
							curChar++;	
						} else if(Token.checkDels(buffer) != -1){ //deliminator
							found = new Token((Token.checkDels(buffer)), buffer, lnNum, colNum - 1);
							buffer = "";						
						} else if ((Token.checkDels(String.valueOf(text.charAt(curChar))) != -1) && (Token.checkOps(tmp) == -1)){ //if there is a deliminator next to another character that isn't another deliminator that makes an operator 
							found = new Token(Token.checkDels(buffer), buffer, lnNum, colNum -1);
							buffer = String.valueOf(text.charAt(curChar));
							colNum++;
							curChar++;
						}
					}
					break;
				
				case COMMENT: //before we decide what type of comment it is - single line or multi line 
					if(buffer.isEmpty()){
						buffer += String.valueOf(text.charAt(curChar));
						colNum++;
						curChar++;
					} else if(buffer.length() == 1){ //slash already in buffer
						if(text.charAt(curChar) == '-'){ //single line, possibly
							buffer += String.valueOf(text.charAt(curChar));
							colNum++;
							curChar++;
							curState = STATE.SLCOMMENT;
						} else if (text.charAt(curChar) == '*'){ //multi line, possibly
							buffer += String.valueOf(text.charAt(curChar));
							colNum++;
							curChar++;
							curState = STATE.MLCOMMENT;
						} else {
							found = new Token(Token.checkDels(buffer), buffer, lnNum, colNum - 1);
							buffer = "";
						}
					}
					break; 
					
				case SLCOMMENT:
					if(buffer.equals("/-")){
						if(text.charAt(curChar) == '-') { // chcking for '/--'
							buffer = "";
							colNum++;
							curChar++;
						} else {
							found = new Token(Token.TDIVD, "/", lnNum, colNum - 1);
							buffer = "-";
						}
					} else {
						if( (int)text.charAt(curChar) == 13) { //decimal value for a carriage return (Enter), we have either reached the end of the comment, or the comment has gone over the line...
							curChar++;
							curState = STATE.WHITESPACE;
						} else {
							colNum++;
							curChar++;
						}
					}
					break;					
				
				case MLCOMMENT:
					if(buffer.equals("/*")){
						if(text.charAt(curChar) == '*'){ //looking for /**
							buffer = "";
							colNum++;
							curChar++;
						} else {
							found = new Token(Token.TDIVD, "/", lnNum, colNum - 1);
							buffer = "*";
						}
					} else if (buffer.equals("")){ //looking for *, possible end of multi line commment
						if(text.charAt(curChar) == '*'){
							buffer = "*";
							colNum++;
							curChar++;
						} else {
							colNum++;
							curChar++;
						}
					} else if (buffer.equals("*")){ //looking for **, closer to end of MLcomment
						if(text.charAt(curChar) == '*'){
							buffer = "**";
							colNum++;
							curChar++;
						} else {
							buffer = "";
							colNum++;
							curChar++;
						}
					} else if (buffer.equals("**")){ //looking for **/, the end of ML Comment
						if(text.charAt(curChar) == '/'){
							buffer = "";
							colNum++;
							curChar++;
							
							//check if ML comment is at the end of the file
							if(!isEof()){								
								stateTransition(text.charAt(curChar)); //if not at end of file, the state will be changed							
							}
						} else { //all the characters in the comment will be ignored and not stored in the buffer, but will continue to travers the string
							buffer = "";
							colNum++;
							curChar++;
						}
					}
					break;
				
				case INTEGER: 
					if(Character.isDigit(text.charAt(curChar))){
						buffer += String.valueOf(text.charAt(curChar));
						colNum++;
						curChar++;
					}else if(text.charAt(curChar) == '.'){ //checking for float value
						buffer += String.valueOf(text.charAt(curChar));
						colNum++;
						curChar++;
						curState = STATE.FLOAT; // the state needs to be changed so the correct token can be assigned
					} else { //no number printed, end of integer
						found = new Token( Token.TILIT, buffer, lnNum, colNum - buffer.length());
						buffer = "";
					}
					break;
				
				case FLOAT:
					if(buffer.endsWith(".")){ //e.g buffer = 123. 
						if(Character.isDigit(text.charAt(curChar))){
							buffer += String.valueOf(text.charAt(curChar));
							colNum++;
							curChar++;
						} else { //if anything but an int comes after '.'
							found = new Token( Token.TILIT, buffer, lnNum, colNum - buffer.length());
							buffer = ".";
						}
					} else {
						if(Character.isDigit(text.charAt(curChar))){ //e.g buffer = 123.4
							buffer += String.valueOf(text.charAt(curChar));
							colNum++;
							curChar++;
						} else { 
							found = new Token( Token.TFLIT, buffer, lnNum, colNum - buffer.length());
							buffer = "";
						}
					}
					break;
					
				case STRING:
					if((int) text.charAt(curChar) == 34){ // 34 = " 
						found = new Token(Token.TSTRG, buffer, lnNum, colNum - buffer.length()); // assign the " to a TSTRG token
						buffer = "";
						colNum++;
						curChar++;
					} else if( (int) text.charAt(curChar) == 10) { //checking for next line, to update the line number
						lnNum++;
						curChar++;
						found = new Token(Token.TUNDF, buffer, lnNum, colNum - buffer.length());
						colNum = 0;
						buffer = "";
					} else if ( Character.isWhitespace(text.charAt(curChar))) { //all other white space
						curChar++;
					} else {
						buffer += String.valueOf(text.charAt(curChar));
						colNum++;
						curChar++;
					}
					break;	
				
				case ERROR:
					if(stateError(text.charAt(curChar))){
						buffer += String.valueOf(text.charAt(curChar));
						colNum++;
						curChar++;
					} else {
						found = new Token(Token.TUNDF, buffer, lnNum, colNum - buffer.length());
						buffer = "";
					}
				default:			
				
			}
			
			//System.out.println("end of switch, buffer = " + buffer + "\n"); /*debugging*/
			if(!buffer.isEmpty()){ //buffer still contains something
				if(isEof() && found == null){ //at end of file but haven't found a token
				//System.out.println("if out of loop"); /*debugging*/
					if(Token.checkKeyWords(buffer) == -1){			 //if the buffer contains characters not equal to a keyword, then check the current state of the program to determine it is assigned with the right token	
						if(curState == STATE.MLCOMMENT){  //comments aren;t printed, so we can just wipe everthing clean and ignore them 
							buffer = "";
						} else if( curState == STATE.SLCOMMENT){
							buffer = "";
						} else if (curState == STATE.FLOAT){
							found = new Token( Token.TFLIT, buffer, lnNum, colNum - buffer.length());
							buffer = "";
						} else if (curState == STATE.INTEGER){
							found = new Token( Token.TILIT, buffer, lnNum, colNum - buffer.length());
							buffer = "";
						} else if (curState == STATE. STRING){
							found = new Token( Token.TUNDF, buffer, lnNum, colNum - buffer.length());
							buffer = "";
						} else {
							found = new Token( Token.TIDEN, buffer, lnNum, colNum - buffer.length());
							buffer = "";
						}
					} else if (Token.checkKeyWords(buffer) != -1 && curState == STATE.SLCOMMENT){ //again, even if it equals a keyword, if its in a comment, we ignore it 
						buffer = "";
					} else if(Token.checkKeyWords(buffer) != -1 && curState == STATE.MLCOMMENT){
						buffer = "";
					} else {
						found = new Token( Token.checkKeyWords(buffer), buffer, lnNum, colNum); //and we've found a keyword at the end of the file that hasn;t been given a token
						buffer = "";
					}
					break;			
				}
			} 
		}//while loop
		
		//System.out.println("Out of while loop\n"); /*debugging*/
		//System.out.println("buffer = " + buffer + "\n"); /*debugging*/
		
		//this if statement allows for the changing of states whilst still reading through the file String
		if(!isEof()){
			if(buffer.isEmpty()){ 
				stateTransition(text.charAt(curChar));
			}
		}
		
		if(found != null){
			return found;
		} else {
			throw new Exception("Exception Message");
		}		
	}

	//this is how we change states
	public void stateTransition(char c){
		//System.out.println("State Transition func\n"); /*debugging*/
		//System.out.println("Char = " + c + "\n"); /*debugging*/
		int intchar = (int) c;
		
		if(Character.isWhitespace(c)){
			curState = STATE.WHITESPACE;	
			
			if(c  == 10 ){ //line feed, needed to be tested seperatly so the line number could be updated 
				lnNum++;
				colNum = 0;
				curChar++;
			} else if( c == 13) { //carriage return
				curChar++;
			} else if (c == 9) {// tab 
				curChar++;
			} else { //any ofther whitespace
				colNum++;
				curChar++;
			}			
		}
		else if(Character.isAlphabetic(c)){
			curState = STATE.KEYWORD;
		}		
		else if(c =='/'){ //THIS ONE MUST GO BEFORE CHECKING FOR DELIM_OPER, OTHERWISE WE END UP WITH NO COMMENTS- DON'T FORGET.... took way to long to figure out this error
			curState = STATE.COMMENT;
		}	
		else if(isDelOp(c)){
			curState = STATE.DELIM_OPER;
		}		
		else if(Character.isDigit(c)){
			curState = STATE.INTEGER;
		}
		else if(intchar == 34){ // 34 equals ", easier to check the decimal value over the actual character
			curState = STATE.STRING;
			curChar++;
			colNum++;
		}
		else{
			curState = STATE.ERROR;
		}	
		//System.out.print("State: " + curState + "\n"); /*debugging*/
	}
	
	//doubling checking if there was actually an error
	public boolean stateError(char c) {
		int intchar = (int) c;
		
		if(Character.isWhitespace(c)){
			return false;		
		}
		else if(Character.isAlphabetic(c)){
			return false;
		}
		else if(isDelOp(c)){
			return false;
		}
		else if(c =='/'){
			return false;
		}		
		else if(Character.isDigit(c)){
			return false;
		}
		else if(intchar == 34){
			return false;
		}
		else{
			return true;
		}
	}
	
	//is the file up to the end of file character.
	//used in the main file 
	public boolean isEof(){
		if(curChar == numofChars){
			return true;
		} else {
			return false;
		}
	}
	//is the buffer empty.... again with the naming, so original
	//used in the main class as the buffer is a private String 
	public boolean bufferIsEmpty(){
		if(buffer.isEmpty()){
			return true;
		} else {
			return false;
		}
	}
	//checking if the character being read is a deliminator 
	public boolean isDelOp(char c){
		if(Token.checkDels(String.valueOf(c)) != -1){
			return true;
		} else {
			return false;
		}
	}
	
	//function to print the tokens as they are found
	//reminder to self, 'println' prints eash token on a different line, instead of just print.... don't forget this :') 
	public void printTok(Token ptok){
		
		//keeps track of how many characters are on each line
		if(outChar >= 60){
			System.out.print("\n");
			outChar = 0;
		}
		
		if(ptok.getTokNum() == 59 || ptok.getTokNum() == 60 || ptok.getTokNum() == 61 || ptok.getTokNum() == 62) { //checking specifically for the tokens that have needed extra infor store with them  i.e TSTRG has the string stored with it, and needs to be displayed
			outbuffer += Token.TPRINT[ptok.getTokNum()]; // adds the toke, not the token value, to the outbuffer.... i'm great at naming things aye 
			outbuffer += "";
			if(ptok.getTokNum() == 62){
				outbuffer += "\"";
				outbuffer += ptok.getLex();
				outbuffer += "\"";
				outbuffer += " ";
			} else {
				outbuffer += ptok.getLex();
				outbuffer += " ";
			}
			
			outChar += outbuffer.length(); //incrememnting the amount of characters on the line
			System.out.print(outbuffer);
		} else if( ptok.getTokNum() == 63){ //we love printing errors
			outbuffer += Token.TPRINT[ptok.getTokNum()];
			outbuffer += "Lexical Error: "; 
			outbuffer += ptok.getLex();
			outbuffer += " ";
			
			outChar += outbuffer.length();
			System.out.print(outbuffer);
		} else { //every other token that could be printed, i.e. the keywords, operators and deliminators 
			outbuffer += Token.TPRINT[ptok.getTokNum()];
			outChar += outbuffer.length();
			System.out.print(outbuffer);
		}
		outbuffer = "";
	}

}










