
import java.util.ArrayList;

public class SymbolTable {

    private ArrayList<SymbolTable> scoped_symbol_table;
    private ArrayList<STRecord> records;

    // id hashmap?

    public SymbolTable(){

        scoped_symbol_table = new ArrayList<>();
        records = new ArrayList<>();

        records.add(new STRecord("integer", 0, 0, "Keyword", null, null, null));
        records.add(new STRecord("real", 0, 0, "Keyword", null, null, null));
        records.add(new STRecord("boolean", 0, 0, "Keyword", null, null, null));

    }

    public SymbolTable(SymbolTable higher_scoped_symbol_table){

    }
    
}
