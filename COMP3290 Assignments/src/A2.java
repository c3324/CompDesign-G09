// Joshua Burwood & Evangeline Hooper
// COMP3290 Project 2
// A2.java
// Due 13.08.2023

import java.io.IOException;

public class A2 {
    
    public static void main(String[] args) throws IOException {

		//making sure the file is being read in properly, if not, exception will be thrown and the program will exit
		// if(args.length < 1) {
		// 	System.out.print("Error, usage: java A2 inputfile");
		// 	System.exit(1);
		// }

		// String filepath = args[0];
		String filepath = "COMP3290 Assignments/testfiles/g09.txt"; // for non-console use
		
		// Construct Scanner.
		CDScanner scanner = new CDScanner(filepath);

        //create list? array list for the tokens generated by the scanner to be stored in 

		// Spec structure used.
		while (!scanner.eof()){
			Token recieved_token = scanner.nextToken();

			scanner.printToken(recieved_token);
			//store recevied token into the list created.
            //put a check in place to see if an error is thrown while scanning, as we don't want to continue to parsing if a token returned is an error token (TUNDF i think it is)
		}

        

		//This is where : CDParser parser = new CDParser(tokenList); can then go. 
        //if there is no token errors
        //  create parser 'object' containing the generated token list
        // then start parsing. 
        //else, 
        //  print -> present lexical errors ad terminate program
	}
}
