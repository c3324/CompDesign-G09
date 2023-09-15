
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

    public void processToken(Token token, String type){

        int tokenIndex = getRecordIndex(token.getTokID());
        //System.out.println("In Process Token");
        if ( tokenIndex == -1){     //if record doesn't already exist
            records.add(new STRecord(token, type));
            keywordsIndex.put(token.getLex(), number_of_records);
            number_of_records++;
           // System.out.println(number_of_records);
            return;
        }
        else {
            
        }
    }

    public int getNumRecords(){
        return number_of_records;
    }

    public String returnSTRecords(int recordNum){
        STRecord record = records.get(recordNum);
        String recordOutput = "Symbol Table Record "+ recordNum + ": " + record.getID() + record.getType() + "\n";
        return recordOutput;       
    }
   
}
