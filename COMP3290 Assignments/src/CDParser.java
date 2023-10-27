import java.util.ArrayList;
import java.util.LinkedList; 
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


//This will be the top level code for the scanner, that the A2 class will call to start parsing.

public class CDParser {

    private CDScanner cdScanner;
    private SyntaxTree syntaxTree;
    private FileWriter writer;
    private BufferedWriter bufferW;
    private PrintWriter printer;

    public CDParser(CDScanner scanner){
        cdScanner = scanner;
        syntaxTree = new SyntaxTree(cdScanner);

    }

    public void parse(){
        syntaxTree.buildTree();
    }

    public SyntaxTree getSyntaxTree(){
        return syntaxTree;
    }

    public void parser_Printing(){
         syntaxTree.printPreOrderTraversal();
    }

    public boolean checkErrorList(){
        LinkedList<String> errorList_Parser = syntaxTree.returnErrorList();
        boolean empty;
        empty = true;
        if(!errorList_Parser.isEmpty()){

            empty = false;
        }

        System.out.println("Empty: "+ empty);
        return empty;
    }

    public void printErrorList(){
        
        System.out.println("");
        LinkedList<String> errorList = syntaxTree.returnErrorList();
        for(int i = 0; i < errorList.size() ; i++){
            System.out.println(errorList.get(i));
        }
    }

    public void addErrorstoFile(){
        try {
            writer= new FileWriter("proglisting.txt", true);
            bufferW= new BufferedWriter(writer);
            printer = new PrintWriter(bufferW);

            printer.println("\n ERRORS \n");
            printer.println("-------------------------------------------------------------\n");

            LinkedList<String> errorList =  syntaxTree.returnErrorList();
            for(int i = 0; i < errorList.size() ; i++){
                printer.println(errorList.get(i));
            }

            printer.flush();
            writer.close();
            bufferW.close();
            printer.close();
        
        } catch (IOException io){}
        }  
    }



    /*public void printSymbolTable(){
        System.out.println("\nSYMBOL TABLE RECORDS");
        System.out.println("-------------------------------------------");
        syntaxTree.returnSymbolTableRecords();
    }*/
    

