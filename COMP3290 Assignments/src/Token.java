

public class Token {
	
		
	private int tokNum, ln, col;
	private String lex, tokID;
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
		this.tokID = LookUpTable.intToTokenString(tokNum);
		this.lex = lex;
		this.ln = ln;
		this.col = col;
		this.symbol_table = symbol_table;
	}
	
	//getters
	public int getTokNum(){
		return tokNum;
	}

	public String getTokID(){
		return tokID;
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

	public void setLex(String lex){
		this.lex = lex;
	}

	public void print(){
		String str = LookUpTable.intToTokenString(tokNum);
		if ( !lex.equals( "")){
			str += lex + " ";
		}

		System.out.print(str);
	}

	public String getString(){
		// Same as print but instead returns the string.
		String str = LookUpTable.intToTokenString(tokNum);
		if ( !lex.equals( "")){
			str += lex + " ";
		}

		return str;
	}
	
}
