//Evangeline Hooper
//COMP3290 Project 1B
//Token.java
//Due 15.08.2021

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

public class Token {
	
	//copied from given CD21 .txt file. was updated as '55' was missing, as well as TGRGR and TLSLS
	public static final int

		T_EOF =  0,	  // Token value for end of file

		// The 31 keywords

		TCD21 =  1,	TCONS = 2,	TTYPS = 3,	TTTIS = 4,	TARRS = 5,	TMAIN = 6,
		TBEGN =  7,	TTEND = 8,	TARAY = 9,	TTTOF = 10,	TFUNC = 11,	TVOID = 12,
		TCNST = 13,	TINTG = 14,	TREAL = 15,	TBOOL = 16,	TTFOR = 17,	TREPT = 18,
		TUNTL = 19,	TIFTH = 20,	TELSE = 21,	TINPT = 22,	TOUTP = 23,	TOUTL = 24,
		TRETN = 25,	TNOTT = 26,	TTAND = 27,	TTTOR = 28,	TTXOR = 29,	TTRUE = 30,
		TFALS = 31,

		// the operators and delimiters
				    TCOMA = 32,	TLBRK = 33,	TRBRK = 34,	TLPAR = 35,	TRPAR = 36,
		TEQUL = 37,	TPLUS = 38,	TMINS = 39,	TSTAR = 40,	TDIVD = 41,	TPERC = 42,
		TCART = 43,	TLESS = 44,	TGRTR = 45,	TCOLN = 46,	TLEQL = 47,	TGEQL = 48,
		TNEQL = 49,	TEQEQ = 50,	TPLEQ = 51,	TMNEQ = 52,	TSTEQ = 53,	TDVEQ = 54,
		TSEMI = 55, TDOTT = 56, TGRGR = 57, TLSLS = 58,

		// the tokens which need tuple values

		TIDEN = 59,	TILIT = 60,	TFLIT = 61,	TSTRG = 62,	TUNDF = 63;

		// 	Corresponding output tokens, padded for ease of use!

		public static final String TPRINT[] = {  
			"T_EOF ",
			"TCD21 ",	"TCONS ",	"TTYPS ",	"TTTIS ",	"TARRS ",	"TMAIN ",
			"TBEGN ",	"TTEND ",	"TARAY ",	"TTTOF ",	"TFUNC ",	"TVOID ",
			"TCNST ",	"TINTG ",	"TREAL ",	"TBOOL ",	"TTFOR ",	"TREPT ",
			"TUNTL ",	"TIFTH ",	"TELSE ",	"TINPT ",	"TOUTP ",	"TOUTL ",
			"TRETN ",	"TNOTT ",	"TTAND ",	"TTTOR ",	"TTXOR ",	"TTRUE ",
			"TFALS ",	
						"TCOMA ",	"TLBRK ",	"TRBRK ",	"TLPAR ",	"TRPAR ",
			"TEQUL ",	"TPLUS ",	"TMINS ",	"TSTAR ",	"TDIVD ",	"TPERC ",
			"TCART ",	"TLESS ",	"TGRTR ",	"TCOLN ",	"TLEQL ",	"TGEQL ",
			"TNEQL ",	"TEQEQ ",	"TPLEQ ",	"TMNEQ ",	"TSTEQ ",	"TDVEQ ",
			"TSEMI ",	"TDOTT ",	"TGRGR ",	"TLSLS ",
			
			"TIDEN ",	"TILIT ",	"TFLIT ",	"TSTRG ",	"TUNDF "};
		
	private int tokNum, ln, col;
	private String lex;
	private static HashMap<String, Integer> reservedKeyTokens = new HashMap<String, Integer>(); //hashmap containing all keyowrd, deliminators and operators - hasmap easier to store its appearence in a file (key), and the token value that goes with it (value)
	private static HashMap<String, Integer> reservedOpsTokens = new HashMap<String, Integer>(); //hashmpa for just the operators
	private static HashMap<String, Integer> reservedDelsTokens = new HashMap<String, Integer>(); //hashmap for just the deliminators
	
	//token structure. 
	public Token(){
		tokNum = 0;
		lex = "";
		ln = 0; //line number the token is found on - for errors
		col = 0; // column number of the token - for errors
	}
	
	//set token variables
	
	//initialising the current token
	public Token(int tokNum, String lex, int ln, int col){
		this.tokNum = tokNum;
		this.lex = lex;
		this.ln = ln;
		this.col = col;
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
	
	//fill in Hashmaps, one for reserved keywords/identifiers, one for operators and one for deliminators
	public static void reservedList(){
		if(reservedKeyTokens.isEmpty()){
			
			//keywords
			reservedKeyTokens.put("cd21",     TCD21);
            reservedKeyTokens.put("constants",TCONS);
            reservedKeyTokens.put("types",    TTYPS);
            reservedKeyTokens.put("is",       TTTIS);
            reservedKeyTokens.put("arrays",   TARRS);
            reservedKeyTokens.put("main",     TMAIN);
            reservedKeyTokens.put("begin",    TBEGN);
            reservedKeyTokens.put("end",      TTEND);
            reservedKeyTokens.put("array",    TARAY);
            reservedKeyTokens.put("of",       TTTOF);
            reservedKeyTokens.put("func",     TFUNC);
            reservedKeyTokens.put("void",     TVOID);
            reservedKeyTokens.put("const",    TCNST);
            reservedKeyTokens.put("integer",  TINTG);
            reservedKeyTokens.put("real",     TREAL);
            reservedKeyTokens.put("boolean",  TBOOL);
            reservedKeyTokens.put("for",      TTFOR);
            reservedKeyTokens.put("repeat",   TREPT);
            reservedKeyTokens.put("until",    TUNTL);
            reservedKeyTokens.put("if",       TIFTH);
            reservedKeyTokens.put("else",     TELSE);
            reservedKeyTokens.put("in",       TINPT);
            reservedKeyTokens.put("out",      TOUTP);
            reservedKeyTokens.put("line",     TOUTL);
            reservedKeyTokens.put("return",   TRETN);
            reservedKeyTokens.put("not",      TNOTT);
            reservedKeyTokens.put("and",      TTAND);
            reservedKeyTokens.put("or",       TTTOR);
            reservedKeyTokens.put("xor",      TTXOR);
            reservedKeyTokens.put("true",     TTRUE);
            reservedKeyTokens.put("false",    TFALS);
			
			//delimiters and operators
			reservedKeyTokens.put(",", TCOMA);
			reservedKeyTokens.put("[", TLBRK);
			reservedKeyTokens.put("]", TRBRK);
			reservedKeyTokens.put("(", TLPAR);
			reservedKeyTokens.put(")", TRPAR);
			reservedKeyTokens.put("=", TEQUL);
			reservedKeyTokens.put("+", TPLUS);
			reservedKeyTokens.put("-", TMINS);
			reservedKeyTokens.put("*", TSTAR);
			reservedKeyTokens.put("/", TDIVD);
			reservedKeyTokens.put("%", TPERC);
			reservedKeyTokens.put("^", TCART);
			reservedKeyTokens.put("<", TLESS);
			reservedKeyTokens.put(">", TGRTR);
			reservedKeyTokens.put(":", TCOLN);
			reservedKeyTokens.put("<=", TLEQL);
			reservedKeyTokens.put(">=", TGEQL);
			reservedKeyTokens.put("!=", TNEQL);
			reservedKeyTokens.put("==", TEQEQ);
			reservedKeyTokens.put("+=", TPLEQ);
			reservedKeyTokens.put("-=", TMNEQ);
			reservedKeyTokens.put("*=", TSTEQ);
			reservedKeyTokens.put("/=", TDVEQ);
			reservedKeyTokens.put(";", TSEMI);
			reservedKeyTokens.put(".", TDOTT);
			reservedKeyTokens.put(">>", TGRGR);
			reservedKeyTokens.put("<<", TLSLS);
			
		}
		
		//give the operators their own list - easier access to them
		if(reservedOpsTokens.isEmpty()){
			reservedOpsTokens.put("<=", TLEQL);
			reservedOpsTokens.put(">=", TGEQL);
			reservedOpsTokens.put("!=", TNEQL);
			reservedOpsTokens.put("==", TEQEQ);
			reservedOpsTokens.put("+=", TPLEQ);
			reservedOpsTokens.put("-=", TMNEQ);
			reservedOpsTokens.put("*=", TSTEQ);
			reservedOpsTokens.put("/=", TDVEQ);
			reservedOpsTokens.put(">>", TGRGR);
			reservedOpsTokens.put("<<", TLSLS);
		}
		
		//give delimiters their own list for easier access
		if(reservedDelsTokens.isEmpty()){
			reservedDelsTokens.put(",", TCOMA);
			reservedDelsTokens.put("[", TLBRK);
			reservedDelsTokens.put("]", TRBRK);
			reservedDelsTokens.put("(", TLPAR);
			reservedDelsTokens.put(")", TRPAR);
			reservedDelsTokens.put("=", TEQUL);
			reservedDelsTokens.put("+", TPLUS);
			reservedDelsTokens.put("-", TMINS);
			reservedDelsTokens.put("*", TSTAR);
			reservedDelsTokens.put("/", TDIVD);
			reservedDelsTokens.put("%", TPERC);
			reservedDelsTokens.put("^", TCART);
			reservedDelsTokens.put("<", TLESS);
			reservedDelsTokens.put(">", TGRTR);
			reservedDelsTokens.put(":", TCOLN);
			reservedDelsTokens.put(";", TSEMI);
			reservedDelsTokens.put(".", TDOTT);
		}
		
	}
	
	//check word against reserved words
	public static int checkKeyWords(String lexeme){
		lexeme = lexeme.toLowerCase(); //keywords are not case sentive, but as they are listed as lower case in the hash map, the lexem needs to be converted into lower case to compare
		boolean tokFound = false;
		int tokVal = 0;
		Iterator iterator = reservedKeyTokens.entrySet().iterator(); //iterator to allow for traversal through hashmap
		
		while(iterator.hasNext()){
			Map.Entry mapElement = (Map.Entry)iterator.next(); //assigns 'mapelement' the hashmap entry the loop is up to
			if(lexeme.equals(mapElement.getKey())){ // if the lexeme being comapred equals an entry in the hashmap
				tokVal = (int)mapElement.getValue(); // set the token value of this token, to the one of the hshmap entry the lexeme equals
				tokFound = true;
			}
		}
		
		if(tokFound == false){
			return -1; // if the lexeme doesn't match an entry in the hashmap, return -1 to let the program know it wasn't found
		} else {
			return tokVal; // if it is found, return the token value of the matched token
		}
	}
	
	//check possible operation again operations defined (works the same as checkKeyWords())
	public static int checkOps(String lexeme){
		boolean tokFound = false;
		int tokVal = 0;
		Iterator iterator = reservedOpsTokens.entrySet().iterator();
		
		while(iterator.hasNext()){
			Map.Entry mapElement = (Map.Entry)iterator.next();
			if(lexeme.equals(mapElement.getKey())){
				tokVal = (int)mapElement.getValue();
				tokFound = true;
			}
		}		
		if(tokFound == false){
			return -1;
		} else {
			return tokVal;
		}
	}
	//check possible Deliminator again delimiters defined (works the same as checkKeyWords())
	public static int checkDels(String lexeme){
		boolean tokFound = false;
		int tokVal = 0;
		Iterator iterator = reservedDelsTokens.entrySet().iterator();
		while(iterator.hasNext()){
			Map.Entry mapElement = (Map.Entry)iterator.next();
			if(lexeme.equals(mapElement.getKey())){
				tokVal = (int)mapElement.getValue();
				tokFound = true;
			}
		}		
		if(tokFound == false){
			return -1;
		} else {
			return tokVal;
		}
	}
	
}
