// Joshua Burwood & Evangeline Hooper
// COMP3290 Project 2
// A2.java
// Due 13.08.2023

import java.io.IOException;
import java.util.LinkedList;

public class A2 {
    
    public static void main(String[] args) throws IOException {

		//making sure the file is being read in properly, if not, exception will be thrown and the program will exit
		// if(args.length < 1) {
		// 	System.out.print("Error, usage: java A2 inputfile");
		// 	System.exit(1);
		// }

		// String filepath = args[0];
		String filepath = "COMP3290 Assignments/testfiles/a2_example_file.txt"; // for non-console use
		
		// Construct Scanner.
		CDScanner scanner = new CDScanner(filepath);

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
			System.out.println("PARSER\n");

			//this is just here at the moment to make sure the error list is working, will remove/alter when error recovery is happenign
			if(parser.checkErrorList()){
				System.out.println("No Errors\n");
				parser.parser_Printing();
			} else {
				System.out.println("Errors");
				parser.printErrorList();
			}			
		}
	}
}
