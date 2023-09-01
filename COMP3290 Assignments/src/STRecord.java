


public class STRecord {

    private String id, type, base, offset, glyph;
    private int line_number, col_number;

    public STRecord(
            String id, 
            int line_number, 
            int col_number, 
            String type, 
            String base, 
            String offset, 
            String glyph
        ){

        this.id = id;
        this.line_number = line_number;
        this.col_number = col_number;
        this.type = type;
        this.base = base;
        this.offset = offset;
        this.glyph = glyph;

    }
    
}
