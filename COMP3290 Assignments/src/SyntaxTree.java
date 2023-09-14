
import java.util.ArrayList;
import java.util.LinkedList;

public class SyntaxTree {

    private Node root;
    private Node currentNode;
    private ArrayList<Token> tokenBuffer;
    private Token currentToken;
    private CDScanner cdScanner;
    private SymbolTable currentSymbolTable;
    ErrorHandling errorList;

    public SyntaxTree(CDScanner scanner){

        root = new Node("NPROG ");
        currentNode = root;

        tokenBuffer = new ArrayList<>();
        cdScanner = scanner;

        currentSymbolTable = new SymbolTable(true);

        errorList = ErrorHandling.getInstance();

    }

    // Match current token
    private void match(){
        //TODO: currentSymbolTable.processToken(currentToken);
        System.out.println(currentToken.getTokID());

        tokenBuffer.remove(0);
        getNextToken();
    }

    private void error(){
        String errorString = "Error found at line " + currentToken.getLn() + " in column " + currentToken.getCol();
        errorList.addErrorToList(errorString);    
    }

    private void error(String msg){
        String errorString = "Error! " + msg + " found at line " + currentToken.getLn() + " in column " + currentToken.getCol();
        errorList.addErrorToList(errorString);
        
    }

    /*This is just chilling here for the moment to test it works. not sure if it should stay here or not*/
    public LinkedList<String> returnErrorList(){
        return errorList.getErrorList();
    }

    

    public void getNextToken(){
        tokenBuffer.add(cdScanner.nextToken());
        currentToken = tokenBuffer.get(0);
    }


    public void buildTree(){ // also <program>


        getNextToken();
        currentToken = tokenBuffer.get(0);

        if (!currentToken.getTokID().equals("TCD23 ")){
            error("File must start with 'TCD23' token");
            return;
        }
        if(errorList.is_Empty()){ //if CD23 is not at the beginning, it will not continue with the Parsing Process
            match(); // TCD23 token

            root.setSymbolValue(currentToken.getLex());
            currentSymbolTable.processToken(currentToken, "<program>");
            match(); // identifier token

            root.setLeftNode(globals());
            root.setMidNode(funcs());
            root.setRightNode(mainbody());

        }
        
        
    }

    public void printPreOrderTraversal(){
        root.printTree();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    // Grammar
    /////////////////////////////////////////////////////////////////////////////////////////////////
    public Node globals(){
        Node node = new Node("NGLOB ");
        node.setLeftNode(consts());
        if ( node.getLeftNode() != null){
            // match();
        }
        node.setMidNode(types());
        if ( node.getMidNode() != null){
            // match(); // matched later to ensure TTYPS keyword
        }
        node.setRightNode(arrays());
        if ( node.getRightNode() != null){
            // match();
        }

        return node;
    }

    public Node consts(){
        
        if ( !currentToken.getTokID().equals("TCONS ")){ // Note could alternatively search for types, arrays, or main to be more thorough.
            return null;
        }
        match();
        return initlist();
        
    }

    public Node initlist(){

        Node initlist = new Node("NILIST ");
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

        if( currentToken.getTokID().equals("TTYPS ") || currentToken.getTokID().equals("TARRS ") || currentToken.getTokID().equals("TMAIN ") || currentToken.getTokID().equals("TFUNC ")){ // epsilon path
            return null;
        }
        if( !currentToken.getTokID().equals("TCOMA ") ){ 
            error("Missing coma!");
            return null;
        }
        match(); // Match comma
        return initlist();


    }

    public Node init(){

        Node NINIT = new Node("NINIT ");
        if( !currentToken.getTokID().equals("TIDEN ")){
            error("Initialisation must start with identifier");
            return new Node("NERROR");
        }
        NINIT.setSymbolValue(currentToken.getLex());
        match(); // <id>
        if( !currentToken.getTokID().equals("TTTIS ")){
            error("missing 'is' keyword in id initialisation");
            return new Node("NERROR");
        }
        match(); // is
        NINIT.setRightNode(expr());
        
        return NINIT;

    }

    public Node types(){

        if( currentToken.getTokID().equals("TARRS ") || currentToken.getTokID().equals("TFUNC ")  || currentToken.getTokID().equals("TMAIN ")  ){ // epsilon path
            return null;
        }
        if (!currentToken.getTokID().equals("TTYPS ")){
            error("Missing types keyword in types declaration");
            return null;
        }
        match(); // types keyword
        return typelist();

    }

    public Node arrays(){

        if(  currentToken.getTokID().equals("TFUNC ") || currentToken.getTokID().equals("TMAIN ") ){ // epsilon path
            return null;
        }
        if (!currentToken.getTokID().equals("TARRS ")){
            error("Arrays declaration must begin with 'arrays' keyword");
            return null;
        }
        match(); // arrays keyword
        return arrdecls();

    }

    public Node funcs(){

        if( currentToken.getTokID().equals("TMAIN ") ){ // epsilon path
            return null;
        }
        if (!currentToken.getTokID().equals("TFUNC ")){
            error("Function declaration must begin with 'func' keyword.");
            return null;
        }

        Node NFUNCS = new Node("NFUNCS ");
        NFUNCS.setLeftNode(func());
        NFUNCS.setRightNode(funcs());

        return NFUNCS;
        
    }

    public Node mainbody(){

        if (!currentToken.getTokID().equals("TMAIN ")){
            error("Main symbol not found!");
            return new Node("NERROR");
        }
        match(); // main 
        Node NMAIN = new Node("NMAIN ");
        NMAIN.setLeftNode(slist());
        if (!currentToken.getTokID().equals("TBEGN ")){
            error("Begin symbol not found!");
            return new Node("NERROR");
        }
        match();  // match begin
        NMAIN.setRightNode(stats());
        if (!currentToken.getTokID().equals("TTEND ")){
            error("End symbol not found!");
            return new Node("NERROR");
        }
        match(); //end
        if (!currentToken.getTokID().equals("TCD23 ")){
            error("CD23 symbol not found at end of file");
            return new Node("NERROR");
        }
        
        match(); // CD23

        // TODO: symbol table lookup for CD23 <id>;
        //NMAIN.setSymbolValue(currentToken.getLex());
        if (!currentToken.getLex().equals(root.getSymbolVaue())){
            error("CD23 names differ at start and end of file");
            return new Node("NERROR");
        }
        match(); // <id>
        
        return NMAIN;
        
    }

    public Node slist(){

        Node slist_node = new Node("NSDLST ");
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

        if ( currentToken.getTokID().equals("TBEGN ")){ // epsilon path
            return null;
        }
        if ( !currentToken.getTokID().equals("TCOMA ")){
            error("Comma not found during slist_r");
            return null;
        }
        match(); //comma
        return slist();
        
    }

    public Node typelist(){

        Node NTYPEL = new Node("NTYPEL ");
        Node type = type();
        Node typelist_r = typelist_r();

        if ( typelist_r == null || type == null){ // special case
            return type;
        }

        NTYPEL.setLeftNode(type);
        type.setRightNode(typelist_r);

        return NTYPEL;
        
    }

    public Node typelist_r(){

        if(  currentToken.getTokID().equals("TARRS ") || currentToken.getTokID().equals("TFUNC ") || currentToken.getTokID().equals("TMAIN ") ){ // epsilon path
            return null;
        }

        return typelist();
        
    }

    public Node type(){


        // TODO:// StructID and typeID??
        if ( !currentToken.getTokID().equals("TIDEN ")){ // ? <structid> / <typeid>??
            error("Expected identifier token for type declaration");
            return null;
        }
        Node type_node = new Node("<placeholder> ");
        type_node.setSymbolValue(currentToken.getLex());
        //Process token to symbol table
        match(); // <id>

        if ( !currentToken.getTokID().equals("TTTIS ")){
            error("Excpected 'is' keyword");
            return new Node("NERROR");
        }
        match(); // is

        if ( currentToken.getTokID().equals("TARAY ")){
            // NATYPE path
            type_node.setId("NATYPE ");
            match(); // array

            if ( !currentToken.getTokID().equals("TLBRK " )){
                error("Excpected '['.");
                return null;
            }
            match(); // [
            type_node.setLeftNode(expr());
            if ( !currentToken.getTokID().equals("TRBRK ")){
                error("Excpected ']'.");
                return null;
            }
            match(); // ]
            if ( !currentToken.getTokID().equals("TTTOF ")){
                error("Excpected 'of' keyword.");
                return null;
            }
            match(); // of
            
            //TODO://
            if ( !currentToken.getTokID().equals("TIDEN ")){ // ? <structid> / <typeid>??
                error("Expected identifier token for type declaration");
                return null;
            }
            match(); // <id>

            if ( !currentToken.getTokID().equals("TTEND ")){
                error("Excpected 'end' keyword.");
                return new Node("NERROR");
            }
            match(); // end
            return type_node;
        }
        else{
            // NRTYPE path
            type_node.setId("NRTYPE ");

            type_node.setLeftNode(fields());
            if ( !currentToken.getTokID().equals("TTEND " )){
                error("Excpected 'end' keyword");
                return null;
            }
            match(); // end
            return type_node;


        }
        
    }

    public Node fields(){

        Node fields = new Node("NFLIST ");
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

        if(  currentToken.getTokID().equals("TTEND ") ){ // epsilon path
            return null;
        }
        if ( !currentToken.getTokID().equals("TCOMA ")){
            error("Missing or extra comma in fields declaration");
            return null;
        }
        match(); // ,
        return fields();

        
    }

    public Node sdecl(){

        Node nsdecl = new Node("NSDECL ");

        if(currentToken.getTokID() != "TIDEN "){
            error("Statement declaration must begin with identifier");
            return null;
        }
        nsdecl.setSymbolValue(currentToken.getLex());
        match(); // TIDEN

        if(currentToken.getTokID() != "TCOLN "){
            error();
            return new Node("NERROR");
        }
        match(); // TCOLN
        
        stype();
        // TODO: Insert to symbol table.
        
        return nsdecl;
        
    }

    public Node arrdecls(){

        Node nalist = new Node("NALIST ");
        Node narrd = arrdecl();
        Node arrdecls_r = arrdecls_r();

        if ( arrdecls_r == null){
            return narrd;
        }

        nalist.setLeftNode(narrd);
        nalist.setRightNode(arrdecls_r);
        return nalist;
        
    }

    public Node arrdecls_r(){

        if(  currentToken.getTokID().equals("TMAIN ") || currentToken.getTokID().equals("TFUNC ") ){ // epsilon path
            return null;
        }
        if ( !currentToken.getTokID().equals("TCOMA ")){

            error("Missing coma");
            return null;
        }
        match(); // ,
        return arrdecls();
        
    }

    public Node arrdecl(){

        Node NARRD = new Node("NARRD ");

        if(currentToken.getTokID() != "TIDEN "){
            error();
            return new Node("NERROR");
        }
        NARRD.setSymbolValue(currentToken.getLex());
        match(); // TIDEN

        if(currentToken.getTokID() != "TCOLN "){
            error("Missing ':' in array declaration");
            return new Node("NERROR");
        }
        match(); // TCOLN
        
        if(currentToken.getTokID() != "<typeid> "){ // TODO: <typeid>
            error("Expected identifier");
            return new Node("NERROR");
        }
        // TODO: Insert to symbol table.
        match(); // <stype>
        
        return NARRD;
        
    }

    public Node func(){

        Node NFUND = new Node("NFUND ");

        if(currentToken.getTokID() != "TFUNC "){
            error();
            return new Node("NERROR");
        }
        match(); // TFUNC

        if(currentToken.getTokID() != "TIDEN "){
            error();
            return new Node("NERROR");
        }
        NFUND.setSymbolValue(currentToken.getLex());
        match(); // <id>

        if(currentToken.getTokID() != "TLPAR "){
            error("Missing '(' in function declaration");
            return new Node("NERROR");
        }
        match(); // (

        NFUND.setLeftNode(plist());

        if(currentToken.getTokID() != "TRPAR "){
            error("Missing ')' in function declaration");
            return new Node("NERROR");
        }
        match(); // )

        if(currentToken.getTokID() != "TCOLN "){
            error("Missing ':' in function declaration");
            return new Node("NERROR");
        }
        match(); // :

        // TODO: Symbol table for func rtype
        rtype();

        NFUND.setRightNode(funcbody());

        return NFUND;

    }

    public Node rtype(){

        // TODO: Symbol table
        if (currentToken.getTokID().equals("TVOID ")){
            match(); // void   
        }
        
        else if (currentToken.getTokID().equals("TIDEN ") || currentToken.getTokID().equals("TINTG ") || currentToken.getTokID().equals("TREAL ") || currentToken.getTokID().equals("TBOOL ")){
            return stype(); 
        }

        return null;
        
        
    }

    public Node plist(){

        if (currentToken.getTokID().equals("TLPAR ")){ // epsilon path
            return null;
        }

        return params();
        
    }

    public Node params(){

        Node NPLIST = new Node("NPLIST ");
        Node param = param();
        Node params_r = params_r();

        if ( params_r == null || param == null){
            return param;
        }

        NPLIST.setLeftNode(param);
        NPLIST.setRightNode(params_r);
        return NPLIST;
        
    }

    public Node params_r(){

        if(  currentToken.getTokID().equals("TRPAR ")  ){ // epsilon path
            return null;
        }
        if ( !currentToken.getTokID().equals("TCOMA ")){
            error("Missing coma");
            return new Node("NERROR");
        }
        match(); // ,
        return params();
        
    }

    public Node param(){

        // TODO: Fix Grammar <sdecl> and <arrdecl> both start with <id> :
        if ( currentToken.getTokID().equals("TCNST ")){
            match(); // const
            Node NARRC = new Node("NARRC ");
            NARRC.setLeftNode(arrdecl());
            return NARRC;
        }
        if ( !currentToken.getTokID().equals("TIDEN ")){
            error("expected identifier as a parameter");
            return new Node("NERROR");
        }
        String id_lex = currentToken.getLex();
        // TODO: // Symbol Table
        match(); // <id>
        if ( !currentToken.getTokID().equals("TCOLN ")){
            error("Missing ':'");
            return new Node("NERROR");
        }
        match(); // :

        Node param_delayed = param_delayed(); // Returns NSIMP with right child <stype>, or NARRP with right child <typeid>
        param_delayed.setSymbolValue(id_lex);

        return param_delayed;

        
    }

    public Node param_delayed(){

        if ( currentToken.getTokID().equals("TIDEN ")){ // TODO: <stype>

            Node NSIMP = new Node("NSIMP ");
            Node NSDECL = new Node("NSDECL");
            NSDECL.setSymbolValue(currentToken.getLex());

            NSIMP.setRightNode(NSDECL); 
            match(); // id

            return NSIMP;
        }
        else if (currentToken.getTokID().equals("TINTG ") || currentToken.getTokID().equals("TREAL ") || currentToken.getTokID().equals("TBOOL ")){ // TODO: <typeid>
        
            Node NARRP = new Node("NARRP ");
            Node NARRD = new Node("NSDECL");
            NARRD.setSymbolValue(currentToken.getLex());

            NARRP.setRightNode(NARRD); //TODO: <typeid>
            match(); // id

            return NARRP;

        }
        else {
            error("Expected parameter type of <stype> or <typeid>.");
            return new Node("NERROR ");
        }

    }

    public Node funcbody(){

        // 'Special node' but has two potential children in <locals> and <stats>
        // TODO: Handle <funcbody>
        Node funcbody = new Node("<funcbody>");
        funcbody.setLeftNode(locals());
        if ( !currentToken.getTokID().equals("TBEGN ")){ 
            error("Expecterd 'begin' keyword for function body declaration");
            return new Node("NERROR");
        }
        match(); // begin
        funcbody.setRightNode(stats());
        if ( !currentToken.getTokID().equals("TTEND ")){ 
            error("Expecterd 'TTEND' keyword for function body declaration");
            return new Node("NERROR");
        }
        match(); // end
        return funcbody;
        
    }

    public Node locals(){

        if(  currentToken.getTokID().equals("TBEGN ")  ){ // epsilon path
            return null;
        }
        return dlist();
        
    }

    public Node dlist(){

        Node NDLIST = new Node("NDLIST ");
        Node decl = decl();
        Node dlist_r = dlist_r();

        if ( dlist_r == null || decl == null){
            return decl;
        }

        NDLIST.setLeftNode(decl);
        NDLIST.setRightNode(dlist_r);
        return NDLIST;
        
    }

    public Node dlist_r(){

        if(  currentToken.getTokID().equals("TBEGN ")  ){ // epsilon path
            return null;
        }
        if ( !currentToken.getTokID().equals("TCOMA ")){
            error("Missing coma");
            return new Node("NERROR");
        }
        match(); // ,
        return dlist();
        
    }

    public Node decl(){

        if(  !currentToken.getTokID().equals("TIDEN ") || currentToken.getTokID().equals("TINTG ") || currentToken.getTokID().equals("TREAL ") || currentToken.getTokID().equals("TBOOL ") ){ 
            error("Expected <id> for declaration");
            return new Node("NERROR");
        }
        String id_lex = currentToken.getLex();
        match(); // <id>

        if(  !currentToken.getTokID().equals("TCOLN ")  ){ 
            error("Expected ':' for declaration");
            return new Node("NERROR");
        }
        match(); // :

        Node decl_delayed = decl_delayed();
        if (decl_delayed == null){
            return new Node("NERROR");
        }
        decl_delayed.setSymbolValue(id_lex);

        // TODO: Symbol Table
        return decl_delayed;
        
    }

    public Node decl_delayed(){

        if ( currentToken.getTokID().equals("TIDEN ")){ // TODO: <stype>

            Node NSDECL = new Node("NSDECL ");
            NSDECL.setSymbolValue(currentToken.getLex());
            //NSIMP.setRightNode(); //TODO: <stype>
            match(); // <id>

            return NSDECL;
        }
        else if (currentToken.getTokID().equals("TINTG ") || currentToken.getTokID().equals("TREAL ") || currentToken.getTokID().equals("TBOOL ")){ 
            // TODO: <typeid>
        
            Node NSDECL = new Node("NSDECL ");
            NSDECL.setSymbolValue(currentToken.getLex());
            match(); //

            return NSDECL;

        }
        else {
            error("Expected paramater type of <stype> or <typeid>.");
            return null;
        }

    }

    public Node stype(){

        // TODO: Insert into symbol table
        if (currentToken.getTokID().equals("TINTG ")){
            match(); // integer | real | boolean
        }
        else if (currentToken.getTokID().equals("TREAL ")){
            match(); // integer | real | boolean
        }
        else if (currentToken.getTokID().equals("TBOOL ")){
            match(); // integer | real | boolean
        }
        else {
            error("Expected type keyword");
        }

        // if( !(currentToken.getTokID().equals("TINTG ") ||  currentToken.getTokID().equals("TREAL ") ||  currentToken.getTokID().equals("TBOOL "))){ 
        //     error("Expected an integer, real, or boolean token.");
        // }
        
        return null;
        
    }

    public Node stats(){

        Node NSTATS = new Node("NSTATS ");

        // <stat>;<stats_r> | <strstat><stats_r>
        if (currentToken.getTokID().equals("TIFTH ") || currentToken.getTokID().equals("TTFOR ")){ // <strstat> path
            Node strsrtat = strstat();

            if(  currentToken.getTokID().equals("TUNTL ") ||  currentToken.getTokID().equals("TELSE ") ||  currentToken.getTokID().equals("TTEND ")){ // epsilon path
                //strsrtat.setLeftNode();
                return strsrtat;
            }

            NSTATS.setLeftNode(strsrtat);
            NSTATS.setRightNode(stats_r());
            return NSTATS;

        }

        else if ( !(currentToken.getTokID().equals("TREPT ") || currentToken.getTokID().equals("TIDEN ") || currentToken.getTokID().equals("TOUTP ") 
        || currentToken.getTokID().equals("TINPT ") || currentToken.getTokID().equals("TRETN ") || currentToken.getTokID().equals("TOUTL ") 
        || currentToken.getTokID().equals("TSEMI "))){
            error("Invalid statement");
            return new Node("NERROR");
        }

        Node stat = stat();
        
        if(  !currentToken.getTokID().equals("TSEMI ") ){ 
            error("Missing semi-colon");
            return new Node("NERROR");
        }
        match(); // ;

        Node stats_r = stats_r();
        if ( stats_r == null){
            return stat;
        }

        NSTATS.setLeftNode(stat);
        NSTATS.setRightNode(stats_r);

        return NSTATS;
        
    }

    public Node stats_r(){

        if(  currentToken.getTokID().equals("TUNTL ") ||  currentToken.getTokID().equals("TELSE ") ||  currentToken.getTokID().equals("TTEND ")){ // epsilon path
            return null;
        }

        return stats();
        
    }

    public Node strstat(){

        if (!(currentToken.getTokID().equals("TIFTH ") || currentToken.getTokID().equals("TTFOR "))){
            error("Expected for or if statement.");
        }
        if (currentToken.getTokID().equals("TIFTH ")){
            return ifstat();
        }
        if (currentToken.getTokID().equals("TTFOR ")){
            return forstat();
        }

        return null;
        
    }

    public Node stat(){

        if ( currentToken.getTokID().equals("TREPT ") ){
            return repstat();
        }

        if ( currentToken.getTokID().equals("TIDEN ") ){
            return asgn_call_stat_delayed();
        }

        if ( currentToken.getTokID().equals("TOUTP ") || currentToken.getTokID().equals("TINPT ") ){
            return iostat();
        }

        if ( currentToken.getTokID().equals("TRETN ")  ){
            return returnstat();
        }

        error("Invalid statement");
        return null;
        
    }

    public Node asgn_call_stat_delayed(){

        if (!(currentToken.getTokID().equals("TIDEN ") )){
            error("Missing Identifier");
            return new Node("NERROR");
        }
        String token_lex = currentToken.getLex();
        match(); // <id>
        

        if ( currentToken.getTokID().equals("TLPAR ")  ){
            
            Node node =  callstat_delayed();
            node.setSymbolValue(token_lex);
            return node;
        }

        // else <var_r><asgnop><bool>
        Node var_r =  var_r();
        var_r.setSymbolValue(token_lex);
        Node asgnop = asgnop();
        Node bool = bool();
        asgnop.setLeftNode(var_r);
        asgnop.setRightNode(bool);
        return asgnop;
        
    }

    public Node forstat(){

        Node NFORL = new Node("NFORL");

        if (!(currentToken.getTokID().equals("TTFOR ") )){
            error("Missing 'for' statement");
            return new Node("NERROR");
        }
        match(); // for

        if (!(currentToken.getTokID().equals("TLPAR ") )){
            error("Missing '(' in for statement");
            return new Node("NERROR");
        }
        match(); // (

        NFORL.setLeftNode(asgn_for_list());

        if (!(currentToken.getTokID().equals("TSEMI ") )){
            error("Missing ';' in for statement");
            return new Node("NERROR");
        }
        match(); // ;

        NFORL.setMidNode(bool());

        if (!(currentToken.getTokID().equals("TRPAR ") )){
            error("Missing ')' in for statement");
            return new Node("NERROR");
        }
        match(); // )

        NFORL.setRightNode(stats());

        if (!(currentToken.getTokID().equals("TTEND ") )){
            error("Missing end in for statement");
            return new Node("NERROR");
        }
        match(); // end

        return NFORL;
        
    }

    public Node repstat(){

        Node NREPT = new Node("NREPT");

        if (!(currentToken.getTokID().equals("TREPT ") )){
            error("Missing 'repeat' statement");
            return new Node("NERROR");
        }
        match(); // repeat

        if (!(currentToken.getTokID().equals("TLPAR ") )){
            error("Missing '(' in for statement");
            return new Node("NERROR");
        }
        match(); // (

        NREPT.setLeftNode(asgn_rep_list());

        if (!(currentToken.getTokID().equals("TRPAR ") )){
            error("Missing '(' in for statement");
            return new Node("NERROR");
        }
        match(); // )

        NREPT.setMidNode(stats());

        if (!(currentToken.getTokID().equals("TUNTL ") )){
            error("Missing 'until' in for statement");
            return new Node("NERROR");
        }
        match(); // until

        NREPT.setRightNode(bool());

        return NREPT;
        
    }

    public Node asgn_for_list(){

        if(  currentToken.getTokID().equals("TESMI ") ){ // epsilon path
            return null;
        }

        return alist_for();
        
    }

    public Node asgn_rep_list(){

        if(  currentToken.getTokID().equals("TRPAR ") ){ // epsilon path
            return null;
        }

        return alist_rep();
        
    }

    public Node alist_for(){

        Node NASGNS = new Node("NASGNS ");
        Node asgnstat = asgnstat();
        Node alist_for_r = alist_for_r();

        if ( alist_for_r == null || asgnstat == null){ // epsilon path
            return asgnstat;
        }

        NASGNS.setLeftNode(asgnstat);
        NASGNS.setRightNode(alist_for_r);

        return NASGNS;
        
    }

    public Node alist_for_r(){

        if(  currentToken.getTokID().equals("TSEMI ")){ // epsilon path
            return null;
        }

        return alist_for();
        
    }

    public Node alist_rep(){

        Node NASGNS = new Node("NASGNS ");
        Node asgnstat = asgnstat();
        Node alist_rep_r = alist_rep_r();

        if ( alist_rep_r == null || asgnstat == null){ // epsilon path
            return asgnstat;
        }

        NASGNS.setLeftNode(asgnstat);
        NASGNS.setRightNode(alist_rep_r);

        return NASGNS;
        
    }

    public Node alist_rep_r(){

        if(  currentToken.getTokID().equals("TRPAR ")){ // epsilon path
            return null;
        }

        return alist_for();
        
    }

    public Node ifstat(){

        Node NIFTH = new Node("NIFTH ");

        if( !currentToken.getTokID().equals("TIFTH ")){ // epsilon path
            error("Missing 'if' statement");
            return new Node("NERROR");
        }

        if (!(currentToken.getTokID().equals("TLPAR ") )){
            error("Missing '(' in for statement");
            return new Node("NERROR");
        }
        match(); // (

        NIFTH.setLeftNode(bool());

        if (!(currentToken.getTokID().equals("TRPAR ") )){
            error("Missing '(' in for statement");
            return new Node("NERROR");
        }
        match(); // )

        NIFTH.setMidNode(stats());

        NIFTH.setRightNode(ifstat_opt_end());

        if (!(currentToken.getTokID().equals("TTEND ") )){
            error("Missing '(' in for statement");
            return new Node("NERROR");
        }
        match(); // end

        return NIFTH;

    }

    public Node ifstat_opt_end(){

        if( currentToken.getTokID().equals("TTEND ")){ // epsilon path
            return null;
        }

        if( !currentToken.getTokID().equals("TELSE ")){ 
            error("Undefined 'if' sequence.");
            return null;
        }
        match(); // else
        
        return stats();
        
    }

    public Node asgnstat(){

        Node var = var();
        Node op = asgnop();
        Node bool = bool();

        op.setLeftNode(var);
        op.setRightNode(bool);

        return op;
        
    }

    public Node asgnop(){

        Node op_node = null;

        if (currentToken.getTokID().equals("TEQUL ")){
            op_node = new Node("NASGN ");
            match(); // =
        }
        else if(currentToken.getTokID().equals("TPLEQ ")){
            op_node = new Node("NPLEQ ");
            match(); // +=
        }
        else if(currentToken.getTokID().equals("TMNEQ ")){
            op_node = new Node("NMNEQ ");
            match(); // -=
        }
        else if(currentToken.getTokID().equals("TSTEQ ")){
            op_node = new Node("NSTEA ");
            match(); // *=
        }
        else if(currentToken.getTokID().equals("TDVEQ ")){
            op_node = new Node("NDVEQ ");
            match(); // /=
        }
        else{
            error("invalid assignment operator");
            return new Node("NERROR");
        }

        return op_node;
        
    }

    public Node iostat(){

        Node node = null;

        if ( currentToken.getTokID().equals("TINPT ")){ // In >> path

            node = new Node("NINPUT ");

            match(); // in

            if ( !currentToken.getTokID().equals("TGRGR ")){
                error("Expected '>>' token");
                return new Node("NERROR");
            }
            match(); // >>

            node.setLeftNode(vlist());

            return node;

        }
        else if (currentToken.getTokID().equals("TOUTP ")){ // Out path

            match(); // out

            if ( !currentToken.getTokID().equals("TLSLS ")){
                error("Expected '<<' token");
                return new Node("NERROR");
            }
            match(); // <<

            if ( currentToken.getTokID().equals("TOUTL ") ){ // Line path
                
                node = new Node("NOUTL ");
                match(); // Line
                return node;

            }

            Node prlist = prlist();

            if ( currentToken.getTokID().equals("TLSLS ")){ // Out << <prlist> << Line path
                match(); //  <<
                
                node = new Node("NOUTL ");
                node.setLeftNode(prlist);
                
                if ( !currentToken.getTokID().equals("TOUTL ")){
                    error("Expected 'Line' statement after second '<<' token.");
                    return new Node("NERROR");
                }
                match(); // Line

                return node;

            }

            else if ( currentToken.getTokID().equals("TSEMI ")){ //epsilon path

                node = new Node("NOUTP ");
                return node;

            }
            
        }

        // else
        error("Invalid i/o sequence");
        return new Node("NERROR");
        
    }

    public Node callstat(){

        Node NCALL = new Node("NCALL ");

        if ( !currentToken.getTokID().equals("TIDEN ")){
            error("Expected identifier token");
            return new Node("NERROR");
        }
        NCALL.setSymbolValue(currentToken.getLex());
        match(); // <id>

        if ( !currentToken.getTokID().equals("TLPAR ")){
            error("Expected '(' token");
            return new Node("NERROR");
        }
        match(); // (

        if ( currentToken.getTokID().equals("TRPAR ")){ // epsilon path
        }
        else {
            Node elist = elist();
            NCALL.setLeftNode(elist);
        }

        if ( !currentToken.getTokID().equals("TRPAR ")){
            error("Expected ')' token");
            return new Node("NERROR");
        }
        match(); // )

        return NCALL;
        
    }

    public Node callstat_delayed(){

        Node NCALL = new Node("NCALL ");

        if ( !currentToken.getTokID().equals("TLPAR ")){
            error("Expected '(' token");
            return new Node("NERROR");
        }
        match(); // (

        if ( currentToken.getTokID().equals("TRPAR ")){ // epsilon path
        }
        else {
            Node elist = elist();
            NCALL.setLeftNode(elist);
        }

        if ( !currentToken.getTokID().equals("TRPAR ")){
            error("Expected ')' token");
            return new Node("NERROR");
        }
        match(); // )

        return NCALL;
        
    }

    public Node returnstat(){

        if ( !currentToken.getTokID().equals("TRETN ")){
            error("Expected 'return' keyword");
            return new Node("NERROR");
        }
        match(); // return

        Node NRETN = new Node("NRETN ");
        NRETN.setLeftNode(returnstat_r());
        
        return NRETN;
        
    }

    public Node returnstat_r(){

        if (currentToken.getTokID().equals("TVOID ")){

            return null;

        }
        else if (currentToken.getTokID().equals("TIDEN ") || currentToken.getTokID().equals("TILIT ") || currentToken.getTokID().equals("TFLIT ") || currentToken.getTokID().equals("TRUE ") || currentToken.getTokID().equals("TFALS ") || currentToken.getTokID().equals("TLPAR ")){
            // ^ If valid <expr> path
            return expr();
        }

        else{
            error("Invalid return identifier");
            return null;
        }
        
    }

    public Node vlist(){

        Node NVLIST = new Node("NVLIST ");
        Node var = var();
        Node vlist_r = vlist_r();
        NVLIST.setLeftNode(var);

        if (currentToken.getTokID().equals("TSEMI ")){ // epsilon path
            return var;
        }
        NVLIST.setLeftNode(var);
        NVLIST.setRightNode(vlist_r);
        return NVLIST;

    }

    public Node vlist_r(){

        if (currentToken.getTokID().equals("TSEMI ")){ // epsilon path
            return null;
        }
        if (!currentToken.getTokID().equals("TCOMA ")){ 
            error("Expected coma after variable");
            return new Node("NERROR");
        }
        match(); // ,

        return vlist();
        
    }

    public Node var(){

        if (!currentToken.getTokID().equals("TIDEN ")){ 
            error("Expected identifier variable.");
            return new Node("NERROR");
        }
        String id_lex = currentToken.getLex();
        match(); // <id>

        Node var_r = var_r();
        if ( var_r.getSymbolVaue().equals("")){ 
            var_r.setSymbolValue(id_lex);
        }
        else{
            var_r.setSymbolValue(id_lex + "." + var_r.getSymbolVaue()); // TODO: Handle Array identifiers. Is this correct?
        }
        
        

        return var_r;

    }

    public Node var_r(){

        if (currentToken.getTokID().equals("TEQUL ") || currentToken.getTokID().equals("TPLEQ ") || currentToken.getTokID().equals("TMNEQ ") 
        || currentToken.getTokID().equals("TSTEQ ") || currentToken.getTokID().equals("TDVEQ ") || currentToken.getTokID().equals("TCART ") 
        || currentToken.getTokID().equals("TSEMI ")
        ){ 
            // epsilon path
            Node NSIMV = new Node("NSIMV ");
            return NSIMV;
        }

        if (!currentToken.getTokID().equals("TLBRK ")){ 
            error("Expected '['.");
            return new Node("NERROR");
        }
        match(); // [

        Node expr = expr();

        if (!currentToken.getTokID().equals("TRBRK ")){ 
            error("Expected ']'.");
            return new Node("NERROR");
        }
        match(); // ]

        Node var_r_r = var_r_r();
        var_r_r.setLeftNode(expr);

        return var_r_r;
     
    }

    public Node var_r_r(){

        if (currentToken.getTokID().equals("TEQUL ") || currentToken.getTokID().equals("TPLEQ ") || currentToken.getTokID().equals("TMNEQ ") || currentToken.getTokID().equals("TSTEQ ") || currentToken.getTokID().equals("TDVEQ ") || currentToken.getTokID().equals("TCART ") ){ 
            // epsilon path
            Node NAELT = new Node("NAELT ");
            return NAELT;
        }

        if ( !currentToken.getTokID().equals("TDOTT ")){
            error("Invalid variable expression");
            return new Node("NERROR");
        }
        match(); // .

        if ( !currentToken.getTokID().equals("TIDEN ")){
            error("Expected variable identifier");
            return new Node("NERROR");
        }
        Node NARRV = new Node("NARRV ");
        NARRV.setSymbolValue(currentToken.getLex()); // TODO: Handle Array identifiers. Is this correct?
        match(); // <id>
        return NARRV;
        
    }

    public Node elist(){

        Node NEXPL = new Node("NEXPL ");
        Node bool = bool();
        Node elist_r = elist_r();

        NEXPL.setLeftNode(bool);

        if (currentToken.getTokID().equals("RPAR ") ){ 
            // epsilon path
            return NEXPL;
        }

        NEXPL.setRightNode(elist_r);
        return NEXPL;

    }

    public Node elist_r(){

        if (currentToken.getTokID().equals("TRPAR ") ){ // epsilon path
            return null;
        }

        if( !currentToken.getTokID().equals("TCOMA ") ){     
            error("Missing coma");
            return null;
        }
        match(); // Match comma

        return elist();


    }

    public Node bool(){

        Node NBOOL = new Node("NBOOL ");
        Node rel = rel();
        Node bool_r = bool_r();

        

        if (bool_r == null || rel == null){
            return rel;
        }

        NBOOL.setLeftNode(rel);
        NBOOL.setRightNode(bool_r);
        return NBOOL;

        
    }

    public Node bool_r(){

        if (currentToken.getTokID().equals("TCOMA ") || currentToken.getTokID().equals("TRPAR ") || currentToken.getTokID().equals("TSEMI ")
        || currentToken.getTokID().equals("TEQEQ ") || currentToken.getTokID().equals("TNEQL ") || currentToken.getTokID().equals("TGRTR ") 
        || currentToken.getTokID().equals("TLESS ") || currentToken.getTokID().equals("TLEQL ") || currentToken.getTokID().equals("TGEQL ") ){ // epsilon path
            return null;
        }

        Node logop = logop();
        Node rel = rel();
        if ( logop == null || rel == null){
            error();
            return new Node("NERROR");
        }
        Node bool_r = bool_r();

        rel.setLeftNode(logop);
        rel.setRightNode(bool_r);

        return rel;
        
    }

    public Node rel(){

        Node node;
        Node expr;
        Node rel_r;

        if (currentToken.getTokID().equals("TNOTT ") ){
            node = new Node("NNOT ");
            match(); // !

            expr = expr();
            rel_r = rel_r(); // returns <relop> with right child as the other expression if exists

            if (rel_r == null){
                node.setLeftNode(expr);
                return node;
            }
            
            rel_r.setLeftNode(expr);
            return rel_r;
            
        }

        expr = expr();
        rel_r = rel_r(); // returns <relop> with right child as the other expression if exists

        if (rel_r == null){
            return expr;
        }

        rel_r.setLeftNode(expr);
        return rel_r;
        
    }

    public Node rel_r(){

        if (currentToken.getTokID().equals("TTAND ") || currentToken.getTokID().equals("TTTOR ") || currentToken.getTokID().equals("TTXOR ")  
        || currentToken.getTokID().equals("TCOMA ") || currentToken.getTokID().equals("TRPAR ") || currentToken.getTokID().equals("TSEMI ")){ 
            // epsilon path
            return null;
        }

        Node relop = relop();
        Node expr = expr();
        relop.setRightNode(expr);

        return relop;
        
    }

    public Node logop(){

        if (currentToken.getTokID().equals("TTAND ")){
            match();
            return new Node("NAND ");
        }
        else if (currentToken.getTokID().equals("TTTOR ")){
            match();
            return new Node("NOR ");
        }
        else if (currentToken.getTokID().equals("TTXOR ")){
            match();
            return new Node("NXOR ");
        }
        else{
            error("Invalid logical operator");
            return null;
        }
        
    }

    public Node relop(){

        if (currentToken.getTokID().equals("TEQEQ ")){
            match();
            return new Node("NEQL ");
        }
        else if (currentToken.getTokID().equals("TNEQL ")){
            match();
            return new Node("NNEQ ");
        }
        else if (currentToken.getTokID().equals("TGRTR ")){
            match();
            return new Node("NGRT ");
        }
        else if (currentToken.getTokID().equals("TLESS ")){
            match();
            return new Node("NLSS ");
        }
        else if (currentToken.getTokID().equals("TLEQL ")){
            match();
            return new Node("NLEQ ");
        }
        else if (currentToken.getTokID().equals("TGEQL ")){
            match();
            return new Node("NGEQ ");
        } 
        else{
            error("Invalid relational operator");
            return new Node("NERROR");
        }
        
    }

    public Node expr(){

        Node term = term();
        Node expr_r = expr_r(); // returns parent logical operator and right child expression(s)

        if ( expr_r == null){
            return term;
        }

        expr_r.setLeftNode(term);
        return expr_r;
        
    }

    public Node expr_r(){

        Node expr_r;
        Node right_expr;

        if (currentToken.getTokID().equals("TPLUS ")){
            expr_r = new Node("NADD ");
            match(); // +
            right_expr = expr();
            expr_r.setRightNode(right_expr);
            return expr_r;
        }
        else if ( currentToken.getTokID().equals("TMINS ") ) {
            expr_r = new Node("NSUB ");
            match(); // +
            right_expr = expr();
            expr_r.setRightNode(right_expr);
            return expr_r;
        }
        else if (currentToken.getTokID().equals("TTAND ") || currentToken.getTokID().equals("TTTOR ") || currentToken.getTokID().equals("TTXOR ") 
        || currentToken.getTokID().equals("TEQEQ ") || currentToken.getTokID().equals("TNEQL ") || currentToken.getTokID().equals("TGRTR ") 
        || currentToken.getTokID().equals("TLESS ") || currentToken.getTokID().equals("TLEQL ") || currentToken.getTokID().equals("TGEQL ") 
        || currentToken.getTokID().equals("TCOMA ") || currentToken.getTokID().equals("TRPAR ") || currentToken.getTokID().equals("TSEMI ")
        || currentToken.getTokID().equals("TLSLS ") || currentToken.getTokID().equals("TGRGR ") || currentToken.getTokID().equals("TTYPS ")
        || currentToken.getTokID().equals("TRBRK ")){
            // epsilon path
            return null;
        }
        else {
            error("Invalid expression");
            return null;
        }

    }

    public Node term(){

        Node fact = fact();
        Node term_r = term_r();

        if ( term_r == null){
            return fact;
        }

        term_r.setLeftNode(fact);
        return term_r;
        
    }

    public Node term_r(){

        Node term_r;
        Node right_fact;

        if (currentToken.getTokID().equals("TSTAR ")){
            term_r = new Node("NMUL ");
            match(); // *
            right_fact = fact();
            term_r.setRightNode(right_fact);
            return term_r;
        }
        else if ( currentToken.getTokID().equals("TDIVD ") ) {
            term_r = new Node("NDIV ");
            match(); // /
            right_fact = fact();
            term_r.setRightNode(right_fact);
            return term_r;
        }
        else if ( currentToken.getTokID().equals("TPERC ") ) {
            term_r = new Node("NMOD ");
            match(); // %
            right_fact = fact();
            term_r.setRightNode(right_fact);
            return term_r;
        }
        else if (currentToken.getTokID().equals("TPLUS ") || currentToken.getTokID().equals("TMINS ") 
        || currentToken.getTokID().equals("TTAND ") || currentToken.getTokID().equals("TTTOR ") || currentToken.getTokID().equals("TTXOR ")
        || currentToken.getTokID().equals("TEQEQ ") || currentToken.getTokID().equals("TNEQL ") || currentToken.getTokID().equals("TGRTR ") 
        || currentToken.getTokID().equals("TLESS ") || currentToken.getTokID().equals("TLEQL ") || currentToken.getTokID().equals("TGEQL ")
        || currentToken.getTokID().equals("TCOMA ") || currentToken.getTokID().equals("TRPAR ") || currentToken.getTokID().equals("TSEMI ") 
        || currentToken.getTokID().equals("TLSLS ") || currentToken.getTokID().equals("TGRGR ") || currentToken.getTokID().equals("TTYPS ")
        || currentToken.getTokID().equals("TRBRK ")) {
            // epsilon path
            return null;
        }
        else {
            error("Invalid term");
            return null;
        }
        
    }

    public Node fact(){

        Node left_exponent = exponent();
        Node fact_r = fact_r();

        if ( fact_r == null){
            return left_exponent;
        }

        fact_r.setLeftNode(left_exponent);
        return fact_r;
        
    }

    public Node fact_r(){

        if ( currentToken.getTokID().equals("TCART ")){
            Node NPOW = new Node("NPOW ");
            match(); // ^
            Node right_fact = fact();
            NPOW.setRightNode(right_fact);
            return NPOW;
        }
        else if (currentToken.getTokID().equals("TPLUS ") || currentToken.getTokID().equals("TMINS ") 
        || currentToken.getTokID().equals("TTAND ") || currentToken.getTokID().equals("TTTOR ") || currentToken.getTokID().equals("TTXOR ")
        || currentToken.getTokID().equals("TEQEQ ") || currentToken.getTokID().equals("TNEQL ") || currentToken.getTokID().equals("TGRTR ") 
        || currentToken.getTokID().equals("TLESS ") || currentToken.getTokID().equals("TLEQL ") || currentToken.getTokID().equals("TGEQL ")
        || currentToken.getTokID().equals("TCOMA ") || currentToken.getTokID().equals("TRPAR ") || currentToken.getTokID().equals("TSEMI ")
        || currentToken.getTokID().equals("TSTAR ") || currentToken.getTokID().equals("TDIVD ") || currentToken.getTokID().equals("TPERC ")
        || currentToken.getTokID().equals("TLSLS ") || currentToken.getTokID().equals("TGRGR ") || currentToken.getTokID().equals("TTYPS ")
        || currentToken.getTokID().equals("TRBRK ")) {
            // epsilon path - note inherits term_r epsilon path + START(term_R)
            return null;
        }
        else{
            error("Invalid term");
            return null;
        }
        
    }

    public Node exponent(){

        if ( currentToken.getTokID().equals("TIDEN ")){
            return exponent_delayed();
        }
        else if ( currentToken.getTokID().equals("TILIT ")){
            Node NILIT = new Node("NILIT ");
            NILIT.setSymbolValue(currentToken.getLex());
            // TODO: symbol table
            match(); // <intlit>
            return NILIT;
        }
        else if ( currentToken.getTokID().equals("TFLIT ")){
            Node NFLIT = new Node("NFLIT ");
            NFLIT.setSymbolValue(currentToken.getLex());
            // TODO: symbol table
            match(); // <reallit>
            return NFLIT;
        }
        else if ( currentToken.getTokID().equals("TTRUE ")){
            Node NTRUE = new Node("NTRUE ");
            NTRUE.setSymbolValue(currentToken.getLex());
            // TODO: symbol table
            match(); // true
            return NTRUE;
        }
        else if ( currentToken.getTokID().equals("TFALS ")){
            Node NFALS = new Node("NFALS ");
            NFALS.setSymbolValue(currentToken.getLex());
            // TODO: symbol table
            match(); // false
            return NFALS;
        }
        else if ( currentToken.getTokID().equals("TLPAR ")){
            // '( <bool> )' case
            match(); // (
            
            Node bool = bool();

            if ( !currentToken.getTokID().equals("TRPAR ")){
                error("Unclosed parenthesis");
                return new Node("NERROR");
            }
            match(); // )
            return bool;
        }
        else{
            error("Invalid exponent");
            return null;
        }
        
    }

    public Node exponent_delayed(){

        String id_lex = currentToken.getLex();
        match(); // <id>

        if (currentToken.getTokID().equals("TLPAR ")){
            //fncall path
            Node NFCALL = new Node("NFCALL ");
            NFCALL.setSymbolValue(id_lex);
            match(); // (

            if (currentToken.getTokID().equals("TRPAR ")){
                match(); // )
                return NFCALL;
            }

            Node elist = elist();
            NFCALL.setLeftNode(elist);

            if (!currentToken.getTokID().equals("TRPAR ")){
                error("Unclosed parenthises in function call");
                return new Node("NERROR");
            }
            match(); // )
            return NFCALL;
            
        }
        else if (currentToken.getTokID().equals("TLBRK ")){

            // This is effectively var_r()
            if (!currentToken.getTokID().equals("TLBRK ")){ 
                error("Expected '['.");
                return new Node("NERROR");
            }
            match(); // [

            Node expr = expr();

            if (!currentToken.getTokID().equals("TRBRK ")){ 
                error("Expected ']'.");
                return new Node("NERROR ");
            }
            match(); // ]

            Node var_r_r = var_r_r();
            var_r_r.setLeftNode(expr);
            if ( !var_r_r.getSymbolVaue().equals("")){
                var_r_r.setSymbolValue(id_lex + "." + var_r_r.getSymbolVaue()); // TODO: Handle Array identifiers. Is this correct?
            }

            return var_r_r;

        } else if (currentToken.getTokID().equals("TCART ") || currentToken.getTokID().equals("TPLUS ") || currentToken.getTokID().equals("TMINS ") 
        || currentToken.getTokID().equals("TTAND ") || currentToken.getTokID().equals("TTTOR ") || currentToken.getTokID().equals("TTXOR ")
        || currentToken.getTokID().equals("TEQEQ ") || currentToken.getTokID().equals("TNEQL ") || currentToken.getTokID().equals("TGRTR ") 
        || currentToken.getTokID().equals("TLESS ") || currentToken.getTokID().equals("TLEQL ") || currentToken.getTokID().equals("TGEQL ")
        || currentToken.getTokID().equals("TCOMA ") || currentToken.getTokID().equals("TRPAR ") || currentToken.getTokID().equals("TSEMI ")
        || currentToken.getTokID().equals("TSTAR ") || currentToken.getTokID().equals("TDIVD ") || currentToken.getTokID().equals("TPERC ")
        || currentToken.getTokID().equals("TLSLS ") || currentToken.getTokID().equals("TGRGR ") || currentToken.getTokID().equals("TTYPS ")
        || currentToken.getTokID().equals("TRBRK ")){
            // epsilon path
            Node NSIMV = new Node("NSIMV ");
            NSIMV.setSymbolValue(id_lex);
            return NSIMV;
        }
        else {
            error("Invalid exponent sequence");
            return null;
        }
        
    }

    // currently redundent
    // public Node fncall(){
        
    // }

    public Node prlist(){

        Node NPRLIST = new Node("NPRLIST ");
        Node printitem = printitem();
        Node prlist_r = prlist_r();

        
        if (prlist_r == null){
            return printitem;
        }

        NPRLIST.setLeftNode(printitem);
        NPRLIST.setRightNode(prlist_r);
        return NPRLIST;
        
    }

    public Node prlist_r(){

        if ( currentToken.getTokID().equals("TCOMA ")){
            match(); // ,
            return prlist();
        }
        else if (currentToken.getTokID().equals("TLSLS ") || currentToken.getTokID().equals("TSEMI ")){
            // epsilon path
            return null;
        } 
        else {
            error("Invalid print list");
            return null;
        }
        
    }

    public Node printitem(){

        if ( currentToken.getTokID().equals("TSTRG ") ){
            Node NSTRG = new Node("NSTRG ");
            NSTRG.setSymbolValue(currentToken.getLex());
            match(); // <String>
            return NSTRG;
        }

        else if ( currentToken.getTokID().equals("TILIT ") || currentToken.getTokID().equals("TFLIT ") || currentToken.getTokID().equals("TIDEN ") 
        || currentToken.getTokID().equals("TTRUE ") || currentToken.getTokID().equals("TFALS ") || currentToken.getTokID().equals("TLPAR ") ){
            // <expr> path
            return expr();
        }
        else {
            error("Invalid print item");
            return null;
        }
        
    }    
    
}
