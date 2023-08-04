

public class Keywords {

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

    public static Token checkKeyword(String lexeme){
        lexeme = lexeme.toLowerCase(); //keywords are not case sentive, but as they are listed as lower case in the hash map, the lexem needs to be converted into lower case to compare
		boolean tokFound = false;
		int tokVal = 0;

        for( int i = 0; i < lexeme.length(); i++){

        }
			Map.Entry mapElement = (Map.Entry)iterator.next(); //assigns 'mapelement' the hashmap entry the loop is up to
			if(lexeme.equals(mapElement.getKey())){ // if the lexeme being comapred equals an entry in the hashmap
				tokVal = (int)mapElement.getValue(); // set the token value of this token, to the one of the hshmap entry the lexeme equals
				tokFound = true;
			}
		
		if(tokFound == false){
			return -1; // if the lexeme doesn't match an entry in the hashmap, return -1 to let the program know it wasn't found
		} else {
			return tokVal; // if it is found, return the token value of the matched token
		}
    }
    
}
