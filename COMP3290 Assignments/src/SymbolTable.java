
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class SymbolTable {

    private ArrayList<SymbolTable> scoped_symbol_table;
    private ArrayList<STRecord> records;

    // id hashmap?
    private HashMap<String, Integer> keywordsIndex;
    
    private int number_of_records;

    public SymbolTable(boolean push_keywords){

        scoped_symbol_table = new ArrayList<>();
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

    }

    public int getRecordIndex(String id){
        if (keywordsIndex.containsKey(id)){
            return keywordsIndex.get(id);
        }
        return -1;
    }

    public void processToken(Token token, String type){

        int tokenIndex = getRecordIndex(token.getTokID());
        if ( tokenIndex != -1){
            records.add(new STRecord(token, type));
            keywordsIndex.put(token.getLex(), number_of_records);
            number_of_records++;
            return;
        }
        else {
            
        }
    }



    
}
