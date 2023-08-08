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
		
	private static HashMap<String, Integer> reservedLexemes = new HashMap<String, Integer>(); //hashmap containing all lexemes.

    public LookUpTable(){
        reservedList();
    }

    //return token name string
    public static String intToTokenString(int i){
        return TPRINT[i];
    }

    //fill in Hashmaps, one for reserved keywords/identifiers, one for operators and one for deliminators
	public void reservedList(){
		if(reservedLexemes.isEmpty()){
			
			// Keywords
			reservedLexemes.put("cd23",     TCD23);
            reservedLexemes.put("constants",TCONS);
            reservedLexemes.put("types",    TTYPS);
            reservedLexemes.put("is",       TTTIS);
            reservedLexemes.put("arrays",   TARRS);
            reservedLexemes.put("main",     TMAIN);
            reservedLexemes.put("begin",    TBEGN);
            reservedLexemes.put("end",      TTEND);
            reservedLexemes.put("array",    TARAY);
            reservedLexemes.put("of",       TTTOF);
            reservedLexemes.put("func",     TFUNC);
            reservedLexemes.put("void",     TVOID);
            reservedLexemes.put("const",    TCNST);
            reservedLexemes.put("integer",  TINTG);
            reservedLexemes.put("real",     TREAL);
            reservedLexemes.put("boolean",  TBOOL);
            reservedLexemes.put("for",      TTFOR);
            reservedLexemes.put("repeat",   TREPT);
            reservedLexemes.put("until",    TUNTL);
            reservedLexemes.put("if",       TIFTH);
            reservedLexemes.put("else",     TELSE);
            reservedLexemes.put("in",       TINPT);
            reservedLexemes.put("out",      TOUTP);
            reservedLexemes.put("line",     TOUTL);
            reservedLexemes.put("return",   TRETN);
            reservedLexemes.put("true",     TTRUE);
            reservedLexemes.put("false",    TFALS);
			
			//delimiters and operators
			reservedLexemes.put(",", TCOMA);
			reservedLexemes.put(";", TSEMI);
			reservedLexemes.put("[", TLBRK);
			reservedLexemes.put("]", TRBRK);
			reservedLexemes.put("(", TLPAR);
			reservedLexemes.put(")", TRPAR);
			reservedLexemes.put("=", TEQUL);
			reservedLexemes.put("+", TPLUS);
			reservedLexemes.put("-", TMINS);
			reservedLexemes.put("*", TSTAR);
			reservedLexemes.put("/", TDIVD);
			reservedLexemes.put("%", TPERC);
			reservedLexemes.put("^", TCART);
			reservedLexemes.put("<", TLESS);
			reservedLexemes.put(">", TGRTR);
			reservedLexemes.put("!", TNOTT);
			reservedLexemes.put(":", TCOLN);
			reservedLexemes.put(".", TDOTT);
			
			reservedLexemes.put(">>", TGRGR);
			reservedLexemes.put("<<", TLSLS);
			reservedLexemes.put("<=", TLEQL);
			reservedLexemes.put(">=", TGEQL);
			reservedLexemes.put("!=", TNEQL);
			reservedLexemes.put("==", TEQEQ);
			reservedLexemes.put("+=", TPLEQ);
			reservedLexemes.put("-=", TMNEQ);
			reservedLexemes.put("*=", TSTEQ);
			reservedLexemes.put("/=", TDVEQ);
			reservedLexemes.put("&&", TTAND);
			reservedLexemes.put("||", TTTOR);
			reservedLexemes.put("&|", TTXOR);
				
			
		}
		
	}

    //check word against reserved words
	public int checkLexeme(String lexeme){
		lexeme = lexeme.toLowerCase(); //keywords are not case sentive, but as they are listed as lower case in the hash map, the lexem needs to be converted into lower case to compare
		boolean tokFound = false;
		int tokVal = -1; // Assume token not found

        tokFound = reservedLexemes.containsKey(lexeme);
		
		if(tokFound){
			tokVal = reservedLexemes.get(lexeme);
		} 
        return tokVal;
	}
}
