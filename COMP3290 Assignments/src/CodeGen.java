// Joshua Burwood & Evangeline Hooper
// COMP3290 Project 2
// CodeGen.java
// Due 29.10.2023

import java.nio.charset.StandardCharsets;

public class CodeGen {

    private SyntaxTree syntaxTree;
    private Node root;
    private Node currentNode;
    private int offset;

    private Stack stack;
    private Mod mod;
    private int pc; //program_counter - MUST BE RIGHT OR WILL NOT WORK ON SM23
    private String filename;

    private SymbolTable main_symbol_table; // for global use

    public CodeGen(SyntaxTree syntaxTree, String mod_filename){

        this.syntaxTree = syntaxTree;
        this.root = syntaxTree.getRoot();
        this.currentNode = syntaxTree.getRoot();
        this.offset = 0;
        this.pc = 0;
        
        stack = new Stack();
        mod = new Mod();
    }

    public void run(){

        // Globals
        //TODO:

        // Functions
        // TODO:

        // Main
        currentNode = root.getRightNode();
        // ALLOC
        main_symbol_table = currentNode.getSymbolTable();
        System.out.println("Allocating stack");
        main_symbol_table.printTable();
        // Consts
        int int_count = 0;
        int float_count = 0;
        int string_count = 0;
        for (int i = 3; i < main_symbol_table.getNumRecords(); i++){ // note skips keywords..
            // note integer, real, boolean keywords skipped
            STRecord rec = main_symbol_table.getStRecord(i);
            if (rec.getType().equals("integer")){
                int_count++;
                mod.push("int_consts", 0);
            }
            else if (rec.getType().equals("float")){
                float_count++;
                mod.push("float_consts", 0);
            }
            else if (rec.getType().equals("string")){ // ? probably not included in ST but rather at nodes.
                string_count++;
                String byte_string = stringToByte(rec.getID());
                mod.push("string_consts", byte_string);
            }
        }
        mod.pushFront("int_consts", int_count);
        mod.pushFront("float_consts", float_count);
        mod.pushFront("string_consts", string_count);
        mod.push("instructions", InstructionSet.LB);
        mod.push("instructions", int_count+float_count+string_count); // TODO: is this actually char_count ?
        mod.push("instructions", InstructionSet.ALLOC);
        pc++;
        mod.pad();
        
          
        
        // mod.push(InstructionSet.ALLOC);
        // mod.pad();

        // int n_vars = st.getNumRecords()-3;
        // if (n_vars > 0){
        //     mod.push("integer", n_vars);
        //     mod.nextLine();
        //     mod.push(InstructionSet.LB);
        //     mod.push(n_vars);
        //     mod.push(InstructionSet.ALLOC);
        //     mod.pad();
        // }

        // for (int i = 3; i < st.getNumRecords(); i++){
        //     // note integer, real, boolean keywords skipped
        //     //ALLOC
        //     STRecord rec = st.getStRecord(i);
        //     stack.push(InstructionSet.LB);
        //     stack.push(rec.)
        // }

        
        
        // Declarations (then statements after - see below)
        // Currently the syntax tree pushes the values to the symbol table so this should not be needed
        // if (currentNode.getLeftNode() != null){

        //     if (currentNode.getLeftNode().getId().equals("NSDECL")){
        //         // only one declaration
        //         String lex = currentNode.getSymbolValue();
        //         SymbolTable st = currentNode.getSymbolTable();

        //     }   

        // }

        // Statements
        currentNode = root.getRightNode(); // NMAIN
        if (currentNode.getRightNode() == null){
            // no stats - done
            return;
        }

        currentNode = currentNode.getRightNode(); // NSTATS or <stats>

        if (!currentNode.getId().equals("NSTATS ")){
            //<stat> path
            STAT(currentNode);
            return;
        }
        NSTATS(currentNode);


    }

    public void NSTATS(Node head){

        // Left
        Node left = head.getLeftNode();
        if (left.getId().equals("NFORL ")){

        }
        else if (left.getId().equals("NREPT ")){
            
        }
        else if (left.getId().equals("NASGNS ")){
            NASGNS(left);
        }
        else if (left.getId().equals("NASGN ")){
            NASGN(left);
        }
        else if (left.getId().equals("NCALL ")){
            
        }
        else if (left.getId().equals("NRETN ")){
            
        }

        // Right
        Node right = head.getRightNode();
        if (right.getId().equals("NSTATS")){
            NSTATS(right);
        }
        else if(right.getId().equals("NFORL ")){

        }
        else if (right.getId().equals("NREPT ")){
            
        }
        else if (right.getId().equals("NASGNS ")){
            NASGNS(right);
        }
        else if (right.getId().equals("NASGN ")){
            NASGN(right);
        }
        else if (right.getId().equals("NCALL ")){
            
        }
        else if (right.getId().equals("NRETN ")){
            
        }
        else{
            // assume <stat> path //TODO: validate this is fari
            STAT(right);
        }
    }


    public void STAT(Node head){

        Node checkNode = head;

        if (checkNode.getLeftNode() == null){
            return;
        }

        checkNode = checkNode.getLeftNode();

        if (checkNode.getId().equals("NASGNS ")){
            NASGNS(head);
        }

        else if (checkNode.getId().equals("NASGN ")){

        }
        else if (checkNode.getId().equals("NPLEQ ")){

        }
        else if (checkNode.getId().equals("NMNEQ ")){
            
        }
        else if (checkNode.getId().equals("NSTEA ")){
            
        }
        else if (checkNode.getId().equals("NDVEQ ")){
            
        }
        else if (checkNode.getId().equals("NFORL ")){
            
        }
        else if (checkNode.getId().equals("NREPT ")){
            
        }
        else if (checkNode.getId().equals("NCALL ")){
            
        }
        else if (checkNode.getId().equals("NRETN ")){
            
        }

    }

    public void NASGNS(Node head){

        // Handle Left
        if (head.getLeftNode().getId().equals("NASGN ")){
            NASGN(head.getLeftNode());
        }
        else if (head.getLeftNode().getId().equals("NPLEQ ")){

        }
        else if (head.getLeftNode().getId().equals("NMNEQ ")){

        }
        else if (head.getLeftNode().getId().equals("NSTEA ")){

        }
        else if (head.getLeftNode().getId().equals("NDVEQ ")){

        }

        // Handle Right
        if ( head.getRightNode().getId().equals("NASGNS ")){
            NASGNS(head.getRightNode());
        }
        else if (head.getRightNode().getId().equals("NASGN ")){
            NASGN(head.getRightNode());
        }
        else if (head.getRightNode().getId().equals("NPLEQ ")){

        }
        else if (head.getRightNode().getId().equals("NMNEQ ")){

        }
        else if (head.getRightNode().getId().equals("NSTEA ")){

        }
        else if (head.getRightNode().getId().equals("NDVEQ ")){

        }

    }


    public void NASGN(Node head){

        Node leftNode = head.getLeftNode();

        String var = leftNode.getSymbolValue(); // TODO: Arrays
        SymbolTable st = head.getSymbolTable();
        st.printTable();
        STRecord rec = st.find(var);

        System.out.println("Finding offset: " + rec.getOffset());
        // LA var
        // <expr>
        // mod.push(InstructionSet.LV);
        // mod.push(1);
        // mod.push(rec.getOffset());
        // mod.push(InstructionSet.READI);



    }
    




    public void toFile(String filename){
        //stack.toFile(filename + "");
        // push pc
        mod.pushFront("instructions", pc);
        mod.toFile(filename);
    }


    public String stringToByte(String inputString){
        byte[] bytes = inputString.getBytes(StandardCharsets.UTF_8);

        System.out.println("Converting string to bytes:");
        String output = "";
        for (int i = 0; i < bytes.length; i++) {
            if (i > 0 && i % 8 == 0) {
                output += "\n";
            }
            // Convert the byte to its decimal representation
            int decimalValue = bytes[i] & 0xFF;
            output += (decimalValue + " ");
            
        }
        // Pad the last line with "0 " up to 8 bytes if needed
        if (bytes.length % 8 != 0) {
            int paddingCount = 8 - (bytes.length % 8);
            for (int j = 0; j < paddingCount; j++) {
                 output += ("0 ");
            }
             output += "\n";
        }
       
        System.out.print(output);
        return output;
    }
    
}