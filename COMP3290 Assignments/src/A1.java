//Evangeline Hooper
//COMP3290 Project 1B
//A1.java
//Due 15.08.2021

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.nio.file.*; 

public class A1{
	public static void main(String[] args) throws IOException {
		//making sure the file is being read in properly, if not, exception will be thrown and the program will exit
		if(args.length < 1) {
			System.out.print("Error, usage: java A1 inputfile");
			System.exit(1);
		}
		
		//reading in the file into a string
		String sourcefile = new String(Files.readAllBytes(Paths.get(args[0])));
		CDScan inputScan;
		inputScan = new CDScan(sourcefile);
		do{
			do{
				try{
					Token curTok = inputScan.scan();
					inputScan.printTok(curTok);
				}catch(Exception e) {}
				
			}while(!inputScan.isEof()); //the scanner is not at the end of the file
		}while(!inputScan.bufferIsEmpty()); //the buffer still contains some characters in it 
		
		inputScan.printTok(inputScan.eofTok());					
	}		
}