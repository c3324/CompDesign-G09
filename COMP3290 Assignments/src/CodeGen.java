// Joshua Burwood & Evangeline Hooper
// COMP3290 Project 2
// CodeGen.java
// Due 29.10.2023

import java.nio.charset.StandardCharsets;

public class CodeGen {

    private static int instruction_size = 4096; // easy way to assign memory.. for some reason can break if uses the last row?

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
        
        // Consts
        int int_count = 0;
        int float_count = 0;
        int string_count = 0; // note this is not the number of strings, but rather the number of words (8 bytes) used to store the strings, including end of string padding
        for (int i = 3; i < main_symbol_table.getNumRecords(); i++){ // note skips keywords..
            // note integer, real, boolean keywords skipped
            STRecord rec = main_symbol_table.getStRecord(i);
            if (rec.getType().equals("integer")){
                if(rec.getGlyph().equals("")){ // not initialised
                    mod.push("int_consts", rec.getGlyph() + "0");
                }
                else{
                    mod.push("int_consts", rec.getGlyph());
                }
                rec.setBase(instruction_size);
                rec.setOffset(int_count * 8);
                int_count++;
            }
            else if (rec.getType().equals("float")){
                if(rec.getGlyph().equals("")){ // not initialised
                    mod.push("float_consts", rec.getGlyph() + "0");
                }
                else{
                    mod.push("float_consts", rec.getGlyph());
                }
                rec.setBase(instruction_size);
                rec.setOffset((int_count + float_count) * 8);
                float_count++;
            }
            else if (rec.getType().equals("string")){ // ? probably not included in ST but rather at nodes.
                // string_count++;
                // System.out.println("allocating string literal for " + rec.getID());
                String byte_string = stringToByte(rec.getID());
                rec.setBase(instruction_size);
                rec.setOffset((int_count + float_count + string_count) * 8);
                string_count += countBytes(byte_string)/8;
                mod.push("string_consts", byte_string);
            }
        }
        mod.pushFront("int_consts", int_count);
        mod.pushFront("float_consts", float_count);
        mod.pushFront("string_consts", string_count);
        mod.push("instructions", InstructionSet.LB);
        mod.push("instructions", int_count+float_count+string_count);
        mod.push("instructions", InstructionSet.ALLOC);
        pc+=3;
        //mod.pad();

        // System.out.println("Testing intToByte for " + 11 + " -> " + intToBytes(11));
        // System.out.println("Testing intToByte for " + 11 + " -> " + intToBytes(564));

        main_symbol_table.printTable();
        
          
        
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

        // System.out.println("In NSTATS");

        // Left
        Node left = head.getLeftNode();
        STAT(left);

        // Right
        Node right = head.getRightNode();
        if (right.getId().equals("NSTATS ")){
            NSTATS(right);
        }
        STAT(right);

    }


    public void STAT(Node head){

        Node checkNode = head;

        // if (checkNode.getLeftNode() == null){
        //     return;
        // }

        // checkNode = checkNode.getLeftNode();

        if (checkNode.getId().equals("NASGNS ")){
            NASGNS(checkNode);
        }
        else if (checkNode.getId().equals("NASGN ")){
            NASGN(checkNode);
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
        else if (checkNode.getId().equals("NIFTH ")){
            
        }
        else if (checkNode.getId().equals("NIFTE ")){
            
        }
        else if (checkNode.getId().equals("NINPUT ")){
            
        }
        else if (checkNode.getId().equals("NOUTP ")){
            
        }
        else if (checkNode.getId().equals("NOUTL ")){
            NOUTL(checkNode);
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

        System.out.println("Finding offset: " + rec);
        // SM
        LA1(rec);

        expr(head.getRightNode());

        mod.push("instructions", InstructionSet.ST);
        pc++;



    }

    public void NOUTL(Node head){

        // print all of prlist -> get node names from NPRLIST -> NSTRG
        Node leftChild = head.getLeftNode();

        if (leftChild.getId().equals("NPRLIST ")){
            NPRLIST(leftChild);
        }
        else if(leftChild.getId().equals("NSTRG ")){
            NSTRG(leftChild);
        }
        else if (leftChild.getId().equals("NSIMV ")){
            NSIMV(leftChild);
            mod.push("instructions", InstructionSet.VALPR);
            pc++;
        }
        // TODO:
        else{ // else <expr>
            expr(leftChild);
            mod.push("instructions", InstructionSet.VALPR);
            pc++;
        }

        // SM
        mod.push("instructions", InstructionSet.NEWLN);
        pc++;
    }

    public void NPRLIST(Node head){

        // print all of prlist -> get node names from NPRLIST -> NSTRG
        Node leftChild = head.getLeftNode();

        if (leftChild.getId().equals("NSTRG ")){
            NSTRG(leftChild);
        }
        else if (leftChild.getId().endsWith("NSIMV ")){
            NSIMV(leftChild);
            mod.push("instructions", InstructionSet.VALPR);
            pc++;
        } // TODO: Arrays?
        else{ // else <expr>
            expr(leftChild);
            mod.push("instructions", InstructionSet.VALPR);
            pc++;
        }

        
        Node rightChild = head.getRightNode();
        if (rightChild.getId().equals("NPRLIST ")){
            NPRLIST(rightChild);
        }
        else if (rightChild.getId().equals("NSTRG ")){
            NSTRG(rightChild);
        }
        else if (rightChild.getId().endsWith("NSIMV ")){
            NSIMV(rightChild);
            mod.push("instructions", InstructionSet.VALPR);
            pc++;
        }
        else{ // else <expr>
            expr(rightChild);
            mod.push("instructions", InstructionSet.VALPR);
            pc++;
        }

    }

    public void NSTRG(Node head){

        // found const
        String literal = head.getSymbolValue();
        STRecord rec = main_symbol_table.find(literal);
        int n_bytes = countBytes(stringToByte(literal));

        System.out.println("Searching for literal: " + literal);
        main_symbol_table.printTable();

        // SM
        LA0(rec);
        mod.push("instructions", InstructionSet.STRPR);
        pc++;


        System.out.println("Loading string!");
        
    }

    public void NADD(Node head){

        expr(head.getLeftNode());
        expr(head.getRightNode());

        mod.push("instructions", InstructionSet.ADD);
        pc++;
    }

    private void expr(Node head){

        if (head.getId().equals("NILIT ")){
            int value = Integer.parseInt(head.getSymbolValue());
            mod.push("instructions", InstructionSet.LB);
            mod.push("instructions", value);
            pc+=2;
        }
        else if ( head.getId().equals("NFLIT ")){
            
            // TODO: Floats?
            // float value = Float.parseFloat(head.getSymbolValue());
            // mod.push("instructions", InstructionSet.LB);
            // mod.push("instructions", value);
            // pc+=2;
            
        }
        // TODO: ALL THE BELOW
        else if ( head.getId().equals("NADD ")){
            NADD(head);
        }
        else if ( head.getId().equals("NSUB ")){
            
        } 
        else if ( head.getId().equals("NMUL ")){
            
        }
        else if ( head.getId().equals("NDIV ")){
            
        }
        else if ( head.getId().equals("NMOD ")){
            
        }
        else if ( head.getId().equals("NPOW ")){
            
        }
        else if ( head.getId().equals("NSIMV ")){
            NSIMV(head);
        }
    }

    private void NSIMV(Node head){
        String var_name = head.getSymbolValue();
        STRecord rec = main_symbol_table.find(var_name);
        LV1(rec);
    }

    private void LA0(STRecord rec){
        // TODO handle more than 256 addresses.
        mod.push("instructions", InstructionSet.LA0);

        String bytes = intToBytes(rec.getBase() + rec.getOffset());
        String[] bytes_array = bytes.split(" ");
        mod.push("instructions", bytes_array[0]);
        mod.push("instructions", bytes_array[1]);
        mod.push("instructions", bytes_array[2]);
        mod.push("instructions", bytes_array[3]);
        pc+= 5;
    }

    private void LA1(STRecord rec){
        mod.push("instructions", InstructionSet.LA1);
        // mod.push("instructions", 0);
        // mod.push("instructions", 0);
        // mod.push("instructions", 0);
        
        // mod.push("instructions", offset);
        String bytes = intToBytes(rec.getOffset());
        String[] bytes_array = bytes.split(" ");
        mod.push("instructions", bytes_array[0]);
        mod.push("instructions", bytes_array[1]);
        mod.push("instructions", bytes_array[2]);
        mod.push("instructions", bytes_array[3]);
        pc+= 5;
    }

    public void LV1(STRecord rec){
        mod.push("instructions", InstructionSet.LV1);
        String bytes = intToBytes(rec.getOffset());
        String[] bytes_array = bytes.split(" ");
        mod.push("instructions", bytes_array[0]);
        mod.push("instructions", bytes_array[1]);
        mod.push("instructions", bytes_array[2]);
        mod.push("instructions", bytes_array[3]);
        pc+=5;
    }


    public void toFile(String filename){
        //stack.toFile(filename + "");
        // push pc
        if (pc % 8 != 0){
            int pad_len = 8 - pc % 8;
            for (int i = 0; i < pad_len; i++){
                mod.push("instructions", 0);
                pc++;
            }
        }
        if (pc < instruction_size){
            // pad memory
            int pad_len = instruction_size - pc;
            for (int i = 0; i < pad_len; i++){
                mod.push("instructions", 0);
                pc++;
            }
        }
        mod.pushFront("instructions", pc/8);
        mod.toFile(filename);
    }


    public String stringToByte(String inputString){
        byte[] bytes = inputString.getBytes(StandardCharsets.UTF_8);

        // System.out.println("Converting string to bytes:");
        String output = "\n";
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
        }
       
        // System.out.println(output);
        return output;
    }

    public String intToBytes(int inputInteger) {
        // Convert the integer to its 4-byte representation
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (inputInteger & 0xFF);
        bytes[1] = (byte) ((inputInteger >> 8) & 0xFF);
        bytes[2] = (byte) ((inputInteger >> 16) & 0xFF);
        bytes[3] = (byte) ((inputInteger >> 24) & 0xFF);
    
        // System.out.println("Converting integer to bytes:");
        String output = "";
        for (int i = bytes.length - 1; i >= 0; i--) {
            int decimalValue = bytes[i] & 0xFF;
            output += (decimalValue + " ");
        }
        
        return output;
    }

    public int countBytes(String inputString){
        // note includes padded bytes
        String[] chunks = inputString.split("\n");
        int count = chunks.length - 1; // note that all strings start with \n
        // System.out.println("Found " + count + " words.");
        return count * 8;
    }
    
}