import java.util.ArrayList;
import java.util.LinkedList; 
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

//This will be the top level code for the scanner, that the A2 class will call to start parsing.

public class CDParser {

    private CDScanner cdScanner;
    private SyntaxTree syntaxTree;

    public CDParser(CDScanner scanner){
        cdScanner = scanner;
        syntaxTree = new SyntaxTree(cdScanner);

    }

    public void parse(){
        syntaxTree.buildTree();
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

    public void printSymbolTable(){
        syntaxTree.returnSymbolTableRecords();
    }
    
}
