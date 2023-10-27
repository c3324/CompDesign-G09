


public class STRecord {

    private String id, type, base, glyph;
    private int line_number, col_number, offset; // note -1 offset means uninitialised
    private String scope; // currently just used to check function params

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
        this.offset = -1;
        this.glyph = glyph;
        this.scope = "";

    }


    public STRecord( Token token, String type){

        this.id = token.getLex();
        this.line_number = token.getLn();
        this.col_number = token.getCol();
        this.type = type;
        this.base = "";
        this.offset = -1;
        this.glyph = "";
        this.scope = "";

    }

    public STRecord( Token token, String type, String scope){

        this.id = token.getLex();
        this.line_number = token.getLn();
        this.col_number = token.getCol();
        this.type = type;
        this.base = "";
        this.offset = -1;
        this.glyph = "";
        this.scope = scope;

    }

    public STRecord( Token token, String type, String scope, int offset){

        this.id = token.getLex();
        this.line_number = token.getLn();
        this.col_number = token.getCol();
        this.type = type;
        this.base = "";
        this.offset = offset;
        this.glyph = "";
        this.scope = scope;

    }

    public String getID(){
        return id;
    }

    public String getType(){
        return type;
    }

    public void setGlyph(String glpyh){
        this.glyph = glpyh;
    }

    public String getGlyph(){
        return glyph;
    }

    public void setOffset(int offset){
        this.offset = offset;
    }

    public int getOffset(){
        return offset;
    }



    public void print(){
        // helper method for printing records
        System.out.println(
            this.id + " " + this.type + " " + this.glyph + " scope: " + this.scope
        );
    }

    public void setScope(String scope){
        this.scope = scope;
    }

    public String getScope(){
        return scope;
    }
    
}
