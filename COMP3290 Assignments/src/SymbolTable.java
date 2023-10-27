
import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {

    private SymbolTable parentSymbolTable;
    private ArrayList<STRecord> records;

    // id hashmap?
    private HashMap<String, Integer> keywordsIndex;
    
    private int number_of_records;
    private int offset;

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
        number_of_records = 0;

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

    public STRecord getStRecord(int idx){
        if (idx >= number_of_records){
            return parentSymbolTable.getStRecord(idx-number_of_records);
        }
        return records.get(idx);
    }

    public int getNumRecords(){
        return number_of_records;
    }

    public int totalRecords(){
        int r = 0;
        if (parentSymbolTable != null){
            r += parentSymbolTable.totalRecords();
        }
        r += number_of_records;
        return r;
    }

    public STRecord find(String var){

        for (int i = 0; i < number_of_records; i++){
            STRecord rec = records.get(i);
            if (rec.getID().equals(var)){
                return rec;
            }
        }
        parentSymbolTable.find(var);
        return null;
        
    }

    public SymbolTable dropScope(){
        return parentSymbolTable;
    }

    // returns true if symbol table contains identifier and value for token
    public boolean contains(Token tok){

        int index = getRecordIndex(tok.getLex());

        if ( index == -1){     //if record doesn't already exist
            return false;
        }

        STRecord stRecord = records.get(index);
        if (stRecord.getGlyph() == null){
            return false;
        }

        return true;

    }

    // returns true if symbol table contains identifier and value for token
    public boolean typeCheckedContains(String var, String type){

        int index = getRecordIndex(var);

        if ( index == -1){     //if record doesn't already exist
            return false;
        }

        STRecord stRecord = records.get(index);
        if (stRecord.getType().equals(type)){
            return true;
        }

        return false;

    }

    public String returnSTRecords(int recordNum){
        STRecord record = records.get(recordNum);
        String recordOutput = "Symbol Table Record "+ recordNum + ": " + record.getID() + " " + record.getType();
        return recordOutput;       
    }

    public void printTable(){
        // helper method for debugging
        if (parentSymbolTable != null){
            parentSymbolTable.printTable();
        }
        System.out.println("------");
        for ( int i = 0; i < records.size(); i++){
            records.get(i).print();
        }
    }

    // Handles a type declaration of a token. If the declaration isn't valid returns an error
    // Returns an integer - 0 is success - -1 is variable type not valid (technically should not occur)
    public int processTokenDeclaration(Token identifier, Token typeToken, String scope){

        // System.out.println("Processing token declaration!");

        String type;
        int offset_used = offset;
        if (typeToken.getTokID().equals("TINTG ")){
            type = "integer";
            offset += 8;
        }
        else if (typeToken.getTokID().equals("TREAL ")){
            type = "real";
            offset += 8;
        }
        else if (typeToken.getTokID().equals("TBOOL ")){
            type = "bool";
            // TODO: bool offsets
        }
        else if (typeToken.getTokID().equals("TIDEN ")){
            type = typeToken.getLex();
            // TODO: iden offsets
        }
        else{
            // System.out.println("Invalid type decl found: Recieved token: " + identifier.getLex() + " received type: " + typeToken.getTokID());
            // System.out.println("Invalid type decl found! type found: " + typeToken.getTokID() + " with lex " + typeToken.getLex() + " with identifier " + identifier.getLex());
            return -1; // invalid type decl
        }

        int tokenIndex = getRecordIndex(identifier.getTokID());

        if ( tokenIndex == -1){     //if record doesn't already exist
            records.add(new STRecord(identifier, type, scope, offset_used));
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
    public int processVariable(Token identifier, Token literal, String scope){

        // System.out.println("Processing variable! iden: " + identifier.getLex() + " literal_type: " + literal.getTokID() + " and value: " + literal.getLex());

        // Semantic checking
        // check if type declaration exists
        int tokenIndex = getRecordIndex(identifier.getLex());
        if ( tokenIndex == -1){     // No type Declaration!
            // System.out.println("Variable not declared!: Recieved token: " + identifier.getLex() +  " received type: " + literal.getTokID() + " in line " + identifier.getLn());
            // printTable();
            return -1;
        }
        
        STRecord typeDeclRecord = getStRecord(tokenIndex);
        String literal_value = "";
        // Ensure literal matches type
        if (typeDeclRecord.getType().equals("integer")){
            try {
                literal_value = Integer.toString(Integer.parseInt(literal.getLex()));
            }
            catch (Exception e){ // TODO: check if converting float to int
                // not an integer!
                return -2;
            }
        }
        if ( typeDeclRecord.getType().equals("real")){
            try {
                
                literal_value = Float.toString(Float.parseFloat(literal.getLex()));
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
        typeDeclRecord.setGlyph(literal_value);
        // if (typeDeclRecord.getScope().equals(scope)){
        //     typeDeclRecord.setGlyph(literal_value);
        // }
        // else{
        //     // create new scoped reference
        //     records.add(new STRecord(identifier, typeDeclRecord.getType(), scope));
        //     keywordsIndex.put(identifier.getLex(), number_of_records);
        //     number_of_records++;
        // }
        
        
        // System.out.println("Added variable: " + identifier.getLex() +  " with value: " + literal.getLex() );
        // printTable();
        return 0;
    }


    // Used by <init> to force identifier into symbol table without first being declared
    public int processVariableForce(Token identifier, Token literal){

        // System.out.println("Processing variable! iden: " + identifier.getLex() + " literal_type: " + literal.getTokID() + " and value: " + literal.getLex());

        if (literal.getTokID().equals("TILIT ")){
            STRecord new_record = new STRecord(identifier, "integer");
            new_record.setGlyph(literal.getLex());
            records.add(new_record);
            keywordsIndex.put(identifier.getLex(), number_of_records);
            number_of_records++;
            // System.out.println(number_of_records);
            return 0;
        }

        if (literal.getTokID().equals("TFLIT ")){
            STRecord new_record = new STRecord(identifier, "real");
            new_record.setGlyph(literal.getLex());
            records.add(new_record);
            keywordsIndex.put(identifier.getLex(), number_of_records);
            number_of_records++;
            // System.out.println(number_of_records);
            return 0;
        }

        if (literal.getTokID().equals("TTRUE ")){
            // Update record
            records.add(new STRecord(identifier, "true"));
            keywordsIndex.put(identifier.getLex(), number_of_records);
            number_of_records++;
            // System.out.println(number_of_records);
            return 0;
        }

        if (literal.getTokID().equals("TFALS ")){
            // Update record
            records.add(new STRecord(identifier, "false"));
            keywordsIndex.put(identifier.getLex(), number_of_records);
            number_of_records++;
            // System.out.println(number_of_records);
            return 0;
        }

        if (literal.getTokID().equals("TIDEN ")){
            // Update record
            records.add(new STRecord(identifier, literal.getLex()));
            keywordsIndex.put(identifier.getLex(), number_of_records);
            number_of_records++;
            // System.out.println(number_of_records);
            return 0;
        }

        // otherwise error
        return -1;

        
    }

    // Used by function params to identify types
    public int processParamForce(Token identifier, Token literal, String scope){

        // System.out.println("Processing variable! iden: " + identifier.getLex() + " literal_type: " + literal.getTokID() + " and value: " + literal.getLex());

        if (literal.getTokID().equals("TINTG ")){
            STRecord new_record = new STRecord(identifier, "integer");
            new_record.setScope(scope);
            new_record.setGlyph(literal.getLex());
            records.add(new_record);
            keywordsIndex.put(identifier.getLex(), number_of_records);
            number_of_records++;
            // System.out.println(number_of_records);
            return 0;
        }

        if (literal.getTokID().equals("TREAL ")){
            STRecord new_record = new STRecord(identifier, "real");
            new_record.setScope(scope);
            new_record.setGlyph(literal.getLex());
            records.add(new_record);
            keywordsIndex.put(identifier.getLex(), number_of_records);
            number_of_records++;
            // System.out.println(number_of_records);
            return 0;
        }

        if (literal.getTokID().equals("TIDEN ")){
            STRecord new_record = new STRecord(identifier, literal.getLex());
            new_record.setScope(scope);
            records.add(new_record);
            keywordsIndex.put(identifier.getLex(), number_of_records);
            number_of_records++;
            // System.out.println(number_of_records);
            return 0;
        }

        // otherwise error
        return -1;
        
    }

    // Used by <init> to force identifier into symbol table without first being declared
    public void pushFunction(Token identifier){

        STRecord new_record = new STRecord(identifier, "FUNCTION_UNKNOWN_TYPE");
        records.add(new_record);
        keywordsIndex.put(identifier.getLex(), number_of_records);
        number_of_records++;
        return;

    }

    public boolean containsFunction(Token identifier){

        int index = getRecordIndex(identifier.getLex());

        if ( index == -1){     //if record doesn't already exist
            return false;
        }

        // STRecord stRecord = records.get(index);
        // if (stRecord.getGlyph() == null){
        //     return false;
        // }

        return true;

    }

    public boolean equals(SymbolTable st){
        for (int i = 0; i < st.getNumRecords(); i++){
            STRecord stRecord = st.getStRecord(i);
            if (!this.typeCheckedContains(stRecord.getID(), stRecord.getType())){
                return false;
            }
        }
        return true;
    }

    public boolean funcCallParamsAreValid(Token function_iden){

        return true;
    //     printTable();

    //     String scope = function_iden.getLex();
    //     System.out.println("Checking function parameters are valid. Found scope: " + scope);
    //     for(int i = 0; i < totalRecords(); i++){
    //         STRecord currRecord = getStRecord(i);
    //         System.out.println("Current record scope: " + currRecord.getScope());
    //         if (currRecord.getScope().equals(scope)){
    //             // find _param ST records
    //             for(int param_index = 0; param_index < totalRecords(); param_index++){
    //                 STRecord paramRecord = getStRecord(param_index);
    //                 if (paramRecord.getScope().equals(scope+"_param")){

    //                     // Found both STs. Now compare
    //                     while (i-1 < totalRecords() && param_index-1 < totalRecords() && currRecord.getScope().equals(scope) && paramRecord.getScope().equals(scope+"_param")){
    //                         // type check
    //                         if (!currRecord.getType().equals(paramRecord.getType())){
    //                             return false;
    //                         }
    //                         i++;
    //                         currRecord = getStRecord(i);
    //                         param_index++;
    //                         paramRecord = getStRecord(param_index);
    //                     }
    //                     // if reached here. success
    //                     return true;
    //                 }
    //             }
    //         }
    //     }
        

    //     return true;

    }
   
}
