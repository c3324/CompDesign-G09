
import java.util.ArrayList;

public class SyntaxTree {

    private Node root;
    private Node currentNode;
    private ArrayList<Token> tokenBuffer;
    private Token currentToken;
    private CDScanner cdScanner;
    private SymbolTable currentSymbolTable;

    public SyntaxTree(CDScanner scanner){

        root = new Node("NPROG");
        currentNode = root;

        tokenBuffer = new ArrayList<>();
        cdScanner = scanner;

        currentSymbolTable = new SymbolTable(true);

    }

    // Match current token
    private void match(){
        currentSymbolTable.processToken(currentToken);

        tokenBuffer.remove(0);
        getNextToken();
    }

    private void error(){
        System.out.println("Error found at line " + currentToken.getLn() + " in column " + currentToken.getCol()); // TODO: Error handling
    }

    private void error(String msg){
        System.out.println("Error!" + msg + "found at line " + currentToken.getLn() + " in column " + currentToken.getCol()); // TODO: Error handling
    }

    public void getNextToken(){
        tokenBuffer.add(cdScanner.nextToken());
        currentToken = tokenBuffer.get(0);
    }


    public void buildTree(){ // also <program>

        getNextToken();
        currentToken = tokenBuffer.get(0);

        if (!currentToken.getTokID().equals("TCD23")){
            error();
        }
        match(); // TCD23 token

        currentSymbolTable.processToken(currentToken, "<program>");

        
        root.setSymbolValue(currentToken.getLex());
        match(); // identifier token
        root.setLeftNode(globals());
        root.setMidNode(funcs());
        root.setRightNode(mainbody());

        if (!root.getRightNode().getSymbolVaue().equals(root.getSymbolVaue())){
            error("CD23 names differ at start and end of file");
        }


    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    // Grammar
    /////////////////////////////////////////////////////////////////////////////////////////////////
    public Node globals(){
        Node node = new Node("NGLOB");
        node.setLeftNode(consts());
        if ( node.getLeftNode() != null){
            match();
        }
        node.setMidNode(types());
        if ( node.getMidNode() != null){
            match();
        }
        node.setRightNode(arrays());
        if ( node.getRightNode() != null){
            match();
        }

        return node;
    }

    public Node consts(){
        
        if ( !currentToken.getTokID().equals("TCONS")){ // Note could alternatively search for types, arrays, or main to be more thorough.
            return null;
        }
        match();
        return initlist();
        
    }

    public Node initlist(){

        Node initlist = new Node("NILIST");
        Node init = init();
        Node initlist_r = initlist_r();
        if ( initlist_r == null){
            return init; // no need to form list
        }

        initlist.setLeftNode(init);
        initlist.setRightNode(initlist_r);
        match();

        return initlist;

    }

    public Node initlist_r(){

        if( currentToken.getTokID().equals("TTYPS") || currentToken.getTokID().equals("TARRS") || currentToken.getTokID().equals("TMAIN") || currentToken.getTokID().equals("TFUNC")){ // epsilon path
            return null;
        }
        if( !currentToken.getTokID().equals("TCOMA") ){ 
            error("Missing coma!");
        }
        match(); // Match comma
        return initlist();


    }

    public Node init(){

        Node init_node = new Node("NINIT");
        if( !currentToken.getTokID().equals("TIDEN")){
            error();
        }
        init_node.setLeftNode(init_node);
        match();
        if( !currentToken.getTokID().equals("TTTIS")){
            error();
        }
        match();
        init_node.setRightNode(expr());

    }

    public Node types(){

        if( currentToken.getTokID().equals("TARRS") || currentToken.getTokID().equals("TFUNC")  || currentToken.getTokID().equals("TMAIN")  ){ // epsilon path
            return null;
        }
        if (!currentToken.getTokID().equals("TTYPS")){
            error();
        }
        match(); // types keyword
        return typelist();

    }

    public Node arrays(){

        if(  currentToken.getTokID().equals("TFUNC") || currentToken.getTokID().equals("TMAIN") ){ // epsilon path
            return null;
        }
        if (!currentToken.getTokID().equals("TARRS")){
            error();
        }
        match(); // arrays keyword
        return arrdecls();

    }

    public Node funcs(){

        if( currentToken.getTokID().equals("TMAIN") ){ // epsilon path
            return null;
        }
        if (!currentToken.getTokID().equals("TFUNC")){
            error();
        }

        Node funcs_node = new Node("NFUNCS");
        funcs_node.setLeftNode(func());
        match();
        funcs_node.setRightNode(funcs());
        match();
        
    }

    public Node mainbody(){

        if (!currentToken.getTokID().equals("TMAIN")){
            error("Main symbol not found!");
        }
        match(); // Match main token
        Node main_node = new Node("NMAIN");
        main_node.setLeftNode(slist());
        if (!currentToken.getTokID().equals("TBEGN")){
            error("Begin symbol not found!");
        }
        match();  // match begin
        main_node.setMidNode(stats());
        if (!currentToken.getTokID().equals("TTEND")){
            error("End symbol not found!");
        }
        match(); //end
        if (!currentToken.getTokID().equals("TCD23")){
            error("CD23 symbol not found at end of file");
        }
        match(); //CD23
        main_node.setSymbolValue(currentToken.getLex());
        match(); // Program name
        
        
    }

    public Node slist(){

        Node slist_node = new Node("NSDLST");
        Node sdecl = sdecl();
        Node slist_r = slist_r();
        
        if ( slist_r == null){
            return sdecl;
        }

        slist_node.setLeftNode(sdecl);
        slist_node.setRightNode(slist_r);
        return slist_node;
        
    }

    public Node slist_r(){

        if ( currentToken.getTokID().equals("TBEGN")){ // epsilon path
            return null;
        }
        if ( !currentToken.getTokID().equals("TCOMA")){
            error("Comma not found during slist_r");
        }
        match(); //comma
        return slist();
        
    }

    public Node typelist(){

        Node type_node = type();
        Node typelist_r_node = typelist_r();
        if ( typelist_r_node == null){ // special case
            return type_node;
        }
        Node typelist_node = new Node("NTYPEL");
        typelist_node.setLeftNode(type_node);
        typelist_node.setRightNode(typelist_r_node);

        return typelist_node;
        
    }

    public Node typelist_r(){

        if(  currentToken.getTokID().equals("TARRS") || currentToken.getTokID().equals("TFUNC") || currentToken.getTokID().equals("TMAIN") ){ // epsilon path
            return null;
        }

        return typelist();
        
    }

    public Node type(){

        
        // TODO:// StructID and typeID??
        if ( currentToken.getTokID().equals("<structid>")){ // ?

            Node type_node = new Node("NRTYPE");
            //Process token to symbol table
            type_node.setSymbolValue(currentToken.getLex());
            
            match(); // <structid>
            if ( !currentToken.getTokID().equals("TTTIS")){
                error("Excpected 'is' keyword");
            }
            match(); // is
            fields();
            if ( !currentToken.getTokID().equals("TTEND")){
                error("Excpected 'end' keyword");
            }
            match(); // end
            return type_node;
        }
        if ( currentToken.getTokID().equals("<typeid>")){
            Node type_node = new Node("NRTYPE");
            //Process token to symbol table
            type_node.setSymbolValue(currentToken.getLex());
            
            match(); // <typeid>
            if ( !currentToken.getTokID().equals("TTTIS")){
                error("Excpected 'is' keyword");
            }
            match(); // is
            if ( !currentToken.getTokID().equals("TARAY")){
                error("Excpected 'array' keyword");
            }
            match(); // array
            if ( !currentToken.getTokID().equals("TLBRK")){
                error("Excpected '['.");
            }
            match(); // [
            type_node.setLeftNode(expr());
            if ( !currentToken.getTokID().equals("TRBRK")){
                error("Excpected ']'.");
            }
            match(); // ]
            if ( !currentToken.getTokID().equals("TTTOF")){
                error("Excpected 'of' keyword.");
            }
            match(); // of
            
            //TODO://
            match(); // <structid>

            if ( !currentToken.getTokID().equals("TTEND")){
                error("Excpected 'end' keyword.");
            }
            match(); // end
            return type_node;
        }
        
    }

    public Node fields(){

        Node fields = new Node("NFLIST");
        Node sdecl = sdecl();
        Node fields_r = fields_r();

        if ( fields_r == null){
            return sdecl;
        }

        fields.setLeftNode(sdecl);
        fields.setRightNode(fields_r);
        return fields;
        
    }

    public Node fields_r(){

        if(  currentToken.getTokID().equals("TTEND") ){ // epsilon path
            return null;
        }
        if ( !currentToken.getTokID().equals("TCOMA")){
            error("Missing comma");
        }
        match(); // ,
        return fields();

        
    }

    public Node sdecl(){
        
    }

    public Node arrdecls(){
        
    }

    public Node arrdecls_r(){
        
    }

    public Node arrdecl(){
        
    }

    public Node func(){
        
    }

    public Node rtype(){
        
    }

    public Node plist(){
        
    }

    public Node params(){
        
    }

    public Node params_r(){
        
    }

    public Node param(){
        
    }

    public Node funcbody(){
        
    }

    public Node locals(){
        
    }

    public Node dlist(){
        
    }

    public Node dlist_r(){
        
    }

    public Node decl(){
        
    }

    public Node stype(){
        
    }

    public Node stats(){
        
    }

    public Node stats_r(){
        
    }



    



    
    
}
