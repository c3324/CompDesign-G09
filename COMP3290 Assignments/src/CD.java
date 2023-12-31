// Joshua Burwood & Evangeline Hooper
// COMP3290 Project 3
// A2.java
// Due 22.10.2023

import java.io.IOException;
import java.util.LinkedList;

public class CD {
    
    public static void main(String[] args) throws IOException {

		//making sure the file is being read in properly, if not, exception will be thrown and the program will exit
		if(args.length < 1) {
			System.out.print("Error, usage: java CD inputfile");
			System.exit(1);
		}

		String filepath = args[0];
		String fileName = filepath.substring(0, filepath.lastIndexOf('.'));
		
        //System.out.println(fileName);
		// String filepath = "COMP3290 Assignments/testfiles/polygon.txt"; // for non-console use
		
		// Construct Scanner.
		CDScanner scanner = new CDScanner(filepath);

		//read file contents into program listing file, with line numbers.
		scanner.createProgramListing(fileName);

		// Spec structure used.
		System.out.print("SCANNER\n");
		while (!scanner.eof()){
			
			Token recieved_token = scanner.nextToken();
			scanner.printToken(recieved_token);
		}

		LinkedList<String> errorList = scanner.returnErrorList();
		if(!errorList.isEmpty()){ 	
			//if there are lexical errors present, it will print them out and not preceed to the Parser.
			System.out.println("\n LEXICAL ERRORS\n");
			System.out.println("-------------------------------------------------------------\n");
        	for(int i = 0; i < errorList.size() ; i++){
            	System.out.println(errorList.get(i));
        	}
			
		} else {
			//yay, there are no lexical Errors, so the Parsing Process can begin
			
			//This is where : CDParser parser = new CDParser(tokenList); can then go. 
			scanner = new CDScanner(filepath);
			CDParser parser = new CDParser(scanner);
			
			parser.parse();
			System.out.println("\n\nPARSER - Pre-Order Traversal:\n");
			parser.parser_Printing();
			parser.printErrorList();
			parser.addErrorstoFile(fileName);
			//parser.printSymbolTable(); //--> Uncomment this to see what has been stored in the Symbol Table

			if (parser.getSyntaxTree().containsErrors()){
				// stop
				return;
			}
			SyntaxTree syntaxTree = parser.getSyntaxTree();
			CodeGen codeGenerator = new CodeGen(syntaxTree, fileName);
			codeGenerator.run();
			codeGenerator.toFile(fileName + ".mod");

		}
	}
}

