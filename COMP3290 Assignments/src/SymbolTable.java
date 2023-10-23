
import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {

    private SymbolTable parentSymbolTable;
    private ArrayList<STRecord> records;

    // id hashmap?
    private HashMap<String, Integer> keywordsIndex;
    
    private int number_of_records;

    public SymbolTable(boolean push_keywords){

        parentSymbolTable = null;
        records = new ArrayList<>();
        keywordsIndex = new HashMap<>();
        number_of_records = 0;

        if (push_keywords){
            records.add(new STRecord("integer", 0, 0, "Keyword", null, null, null));
            records.add(new STRecord("real", 0, 0, "Keyword", null, null, null));
            records.add(new STRecord("boolean", 0, 0, "Keyword", null, null, null));

            keywordsIndex.put("integer", 0);
            keywordsIndex.put("real", 1);
            keywordsIndex.put("boolean", 2);

            number_of_records = 3;
        }

    }

    public SymbolTable(SymbolTable higher_scoped_symbol_table){

        parentSymbolTable = higher_scoped_symbol_table;
        records = new ArrayList<>();
        keywordsIndex = new HashMap<>();
        number_of_records = higher_scoped_symbol_table.getNumRecords();

    }

    public int getRecordIndex(String id){
        if (keywordsIndex.containsKey(id)){
            return keywordsIndex.get(id);
        }
        if (parentSymbolTable == null){
            return -1;
        }
        return parentSymbolTable.getRecordIndex(id);
    }

    // Handles a type declaration of a token. If the declaration isn't valid returns an error
    // Returns an integer - 0 is success - -1 is variable type not valid (technically should not occur)
    public int processTokenDeclaration(Token identifier, Token typeToken){

        System.out.println("Processing token declaration!");

        String type;
        if (typeToken.getTokID().equals("TINTG ")){
            type = "integer";
        }
        else if (typeToken.getTokID().equals("TREAL ")){
            type = "real";
        }
        else if (typeToken.getTokID().equals("TBOOL ")){
            type = "bool";
        }
        else{
            // System.out.println("Invalid type decl found: Recieved token: " + identifier.getLex() + " received type: " + typeToken.getTokID());
            System.out.println("Invalid type found! type found: " + typeToken.getTokID() + " with lex " + typeToken.getLex() + " with identifier " + identifier.getLex());
            return -1; // invalid type decl
        }

        int tokenIndex = getRecordIndex(identifier.getTokID());
        //System.out.println("In Process Token");
        if ( tokenIndex == -1){     //if record doesn't already exist
            System.out.println("Test");
            records.add(new STRecord(identifier, type));
            keywordsIndex.put(identifier.getLex(), number_of_records);
            number_of_records++;
           // System.out.println(number_of_records);
            return 0;
        }
        else {
            
        }
        return -1;
    }

    // Handles assigning a value to a token - if the token is not defined this returns an error
    // Returns an integer - 0 is success - -1 is variable not declared -2 type does not match declaration
    // This has a requirement that the variable already exists in the symbol table
    public int processVariable(Token identifier, Token literal){

        // Semantic checking
        // check if type declaration exists
        int tokenIndex = getRecordIndex(identifier.getTokID());
        if ( tokenIndex == -1){     // No type Declaration!
            //System.out.println("Invalid type decl found: Recieved token: " + identifier.getLex() +  " received type: " + literal.getTokID() + " in line " + identifier.getLn());
            //printTable();
            return -1;
        }
        STRecord typeDeclRecord = records.get(tokenIndex);
        // Ensure literal matches type
        if (typeDeclRecord.getType().equals("integer")){
            try {
                int literal_value = Integer.parseInt(literal.getLex());
            }
            catch (Exception e){ // TODO: check if converting float to int
                // not an integer!
                return -2;
            }
        }
        if ( typeDeclRecord.getType().equals("real")){
            try {
                float literal_value = Float.parseFloat(literal.getLex());
            }
            catch (Exception e){ 
                return -2;
            }
        }
        if (  typeDeclRecord.getType().equals("bool")){
            if (!(literal.getTokID() == "TFALS " || literal.getTokID() == "TTRUE ")){
                return -2;
            }
        }

        // Valid type
        // Update record
        typeDeclRecord.setGlyph(literal.getLex());
        return 0;
    }

    public int getNumRecords(){
        return number_of_records;
    }

    public String returnSTRecords(int recordNum){
        STRecord record = records.get(recordNum);
        String recordOutput = "Symbol Table Record "+ recordNum + ": " + record.getID() + " " + record.getType();
        return recordOutput;       
    }

    public void printTable(){
        // helper method for debugging
        for ( int i = 0; i < records.size(); i++){
            records.get(i).print();
        }
    }

    // This forces a variable into the symbol table
    public int processVariableForce(Token identifier, Token literal){

        // check if type declaration exists
        int tokenIndex = getRecordIndex(identifier.getTokID());
        if ( tokenIndex == -1){     // No type Declaration!
            //System.out.println("Invalid type decl found: Recieved token: " + identifier.getLex() +  " received type: " + literal.getTokID() + " in line " + identifier.getLn());
            //printTable();
            return -1;
        }
        STRecord typeDeclRecord = records.get(tokenIndex);
        // Ensure literal matches type
        if (typeDeclRecord.getType().equals("integer")){
            try {
                int literal_value = Integer.parseInt(literal.getLex());
            }
            catch (Exception e){ // TODO: check if converting float to int
                // not an integer!
                return -2;
            }
        }
        if ( typeDeclRecord.getType().equals("real")){
            try {
                float literal_value = Float.parseFloat(literal.getLex());
            }
            catch (Exception e){ 
                return -2;
            }
        }
        if (  typeDeclRecord.getType().equals("bool")){
            if (!(literal.getTokID() == "TFALS " || literal.getTokID() == "TTRUE ")){
                return -2;
            }
        }

        // Valid type
        // Update record
        typeDeclRecord.setGlyph(literal.getLex());
        return 0;
    }
   
}
