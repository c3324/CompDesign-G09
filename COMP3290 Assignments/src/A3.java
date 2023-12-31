// Joshua Burwood & Evangeline Hooper
// COMP3290 Project 2
// A2.java
// Due 15.09.2023

import java.io.IOException;
import java.util.LinkedList;

public class A3 {
    
    public static void main(String[] args) throws IOException {

		//making sure the file is being read in properly, if not, exception will be thrown and the program will exit
		// if(args.length < 1) {
		// 	System.out.print("Error, usage: java A2 inputfile");
		// 	System.exit(1);
		// }

		// String filepath = args[0];
		String filepath = "COMP3290 Assignments/testfiles/code_gen_testing"; // for non-console use
		String filepath_txt = filepath + ".cd"; // for non-console use
		
		
		// Construct Scanner.
		CDScanner scanner = new CDScanner(filepath_txt);

		//read file contents into program listing file, with line numbers.
		scanner.createProgramListing();


		// Spec structure used.
		System.out.print("SCANNER\n");
		while (!scanner.eof()){
			
			Token recieved_token = scanner.nextToken();
			scanner.printToken(recieved_token); // removed for now for testing
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
			scanner = new CDScanner(filepath_txt);
			CDParser parser = new CDParser(scanner);
			
			parser.parse();
			System.out.println("\n\nPARSER - Pre-Order Traversal:\n");
			parser.parser_Printing();
			parser.printErrorList();
			parser.addErrorstoFile(fileName);
			//parser.printSymbolTable(); //--> Uncomment this to see what has been stored in the Symbol Table


			// Code Gen
			if (parser.getSyntaxTree().containsErrors()){
				// stop
				return;
			}
			SyntaxTree syntaxTree = parser.getSyntaxTree();
			CodeGen codeGenerator = new CodeGen(syntaxTree, "output");
			codeGenerator.run();
			codeGenerator.toFile("output.mod");

		}

		
	}
}
