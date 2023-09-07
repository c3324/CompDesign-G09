// Joshua Burwood & Evangeline Hooper
// COMP3290 Project 1B
// A1.java
// Due 13.08.2023

import java.io.IOException;

public class A1{
	public static void main(String[] args) throws IOException {

		//making sure the file is being read in properly, if not, exception will be thrown and the program will exit
		// if(args.length < 1) {
		// 	System.out.print("Error, usage: java A1 inputfile");
		// 	System.exit(1);
		// }

		// String filepath = args[0];
		String filepath = "COMP3290 Assignments/testfiles/g09.txt"; // for non-console use
		
		// Construct Scanner.
		CDScanner scanner = new CDScanner(filepath);

		// Spec structure used.
		while (!scanner.eof()){
			Token recieved_token = scanner.nextToken();

			scanner.printToken(recieved_token);
			//need to add a away to store the tokens in a list to pass to the Parser
		}

		//This is where : CDParser parser = new CDParser(tokenList); can then go. 
	}

}