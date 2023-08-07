import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LookUpTable {

    //copied from given CD23 .txt file
	public static final int

	TTEOF =  0,	  // Token value for end of file

    // The 27 keywords

    TCD23 =  1,	TCONS = 2,	TTYPS = 3,	TTTIS = 4,	TARRS = 5,	TMAIN = 6,
    TBEGN =  7,	TTEND = 8,	TARAY = 9,	TTTOF = 10,	TFUNC = 11,	TVOID = 12,
    TCNST = 13,	TINTG = 14,	TREAL = 15,	TBOOL = 16,	TTFOR = 17,	TREPT = 18,
    TUNTL = 19,	TIFTH = 20,	TELSE = 21,	TINPT = 22,	TOUTP = 23,	TOUTL = 24,
    TRETN = 25,	TTRUE = 26,     TFALS = 27,

    // the operators and delimiters
    TCOMA = 28,     TSEMI = 29,	TLBRK = 30,	TRBRK = 31,	TLPAR = 32,	TRPAR = 33,     
    TEQUL = 34,	TPLUS = 35,	TMINS = 36,	TSTAR = 37,	TDIVD = 38,	TPERC = 39,
    TCART = 40,	TLESS = 41,	TGRTR = 42,	TCOLN = 43,	TDOTT = 44,     TLEQL = 45,	
            TGEQL = 46,     TNEQL = 47,	TEQEQ = 48,	TPLEQ = 49,	TMNEQ = 50,	TSTEQ = 51,	
            TDVEQ = 52,     TNOTT = 53,	TTAND = 54,	TTTOR = 55,	TTXOR = 56,     TGRGR = 57,     
            TLSLS = 58,

    // the tokens which need tuple values

    TIDEN = 59,	TILIT = 60,	TFLIT = 61,	TSTRG = 62,	TUNDF = 63;

    // 	Corresponding output tokens, padded for ease of use!

    public static final String TPRINT[] = {  
    "TTEOF ",
    "TCD23 ",	"TCONS ",	"TTYPS ",	"TTTIS ",	"TARRS ",	"TMAIN ",
    "TBEGN ",	"TTEND ",	"TARAY ",	"TTTOF ",	"TFUNC ",	"TVOID ",
    "TCNST ",	"TINTG ",	"TREAL ",	"TBOOL ",	"TTFOR ",	"TREPT ",
    "TUNTL ",	"TIFTH ",	"TELSE ",	"TINPT ",	"TOUTP ",	"TOUTL ",
    "TRETN ",	"TTRUE ",       "TFALS ",	"TCOMA ",	"TSEMI ",       "TLBRK ",	
    "TRBRK ",	"TLPAR ",	"TRPAR ",	"TEQUL ",	"TPLUS ",	"TMINS ",
    "TSTAR ",	"TDIVD ",	"TPERC ",	"TCART ",	"TLESS ",	"TGRTR ",
    "TCOLN ",	"TDOTT ",	"TLEQL ",	"TGEQL ",	"TNEQL ",	"TEQEQ ",	
    "TPLEQ ",	"TMNEQ ",	"TSTEQ ",	"TDVEQ ",	"TNOTT ",	"TTAND ",
    "TTTOR ",	"TTXOR ",	"TGRGR ",       "TLSLS ",	"TIDEN ",	"TILIT ",
    "TFLIT ",	"TSTRG ",	"TUNDF "};
		
	private static HashMap<String, Integer> reservedKeyTokens = new HashMap<String, Integer>(); //hashmap containing all keyowrd, deliminators and operators - hasmap easier to store its appearence in a file (key), and the token value that goes with it (value)
	private static HashMap<String, Integer> reservedOpsTokens = new HashMap<String, Integer>(); //hashmpa for just the operators
	private static HashMap<String, Integer> reservedDelsTokens = new HashMap<String, Integer>(); //hashmap for just the deliminators

    public LookUpTable(){
        reservedList();
    }

    //return token name string
    public static String intToTokenString(int i){
        return TPRINT[i];
    }

    //fill in Hashmaps, one for reserved keywords/identifiers, one for operators and one for deliminators
	public void reservedList(){
		if(reservedKeyTokens.isEmpty()){
			
			//keywords
			// TODO: Update to new spec
			reservedKeyTokens.put("cd23",     TCD23);
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
	public int checkKeyWords(String lexeme){
		lexeme = lexeme.toLowerCase(); //keywords are not case sentive, but as they are listed as lower case in the hash map, the lexem needs to be converted into lower case to compare
		boolean tokFound = false;
		int tokVal = -1; // Assume token not found

        System.out.println("Checking lexeme: '" + lexeme + "'.");
        tokFound = reservedKeyTokens.containsKey(lexeme);
		
		if(tokFound){
			tokVal = reservedKeyTokens.get(lexeme);
		} 
        return tokVal;
	}
	
	//check possible operation again operations defined (works the same as checkKeyWords())
	public int checkOps(String lexeme){
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
	public int checkDels(String lexeme){
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
