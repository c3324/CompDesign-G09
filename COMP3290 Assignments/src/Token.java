//Evangeline Hooper
//COMP3290 Project 1B
//Token.java
//Due 15.08.2021

public class Token {
	
		
	private int tokNum, ln, col;
	private String lex;
	private SymbolTable symbol_table;

	
	//token structure. 
	public Token(){
		tokNum = 0;
		lex = "";
		ln = 0; //line number the token is found on - for errors
		col = 0; // column number of the token - for errors
	}
	
	//set token variables
	
	//initialising the current token
	public Token(int tokNum, String lex, int ln, int col, SymbolTable symbol_table){
		this.tokNum = tokNum;
		this.lex = lex;
		this.ln = ln;
		this.col = col;
		this.symbol_table = symbol_table;
	}
	
	//getters
	public int getTokNum(){
		return tokNum;
	}
	
	public int getLn(){
		return ln;
	}
	
	public int getCol(){
		return col;
	}
	
	public String getLex(){
		return lex;
	}

	public void print(){
		String str = LookUpTable.intToTokenString(tokNum);
		if ( !lex.equals( "")){
			str += lex + " ";
		}

		System.out.print(str);
	}
	
}
