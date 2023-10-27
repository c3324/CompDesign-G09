// Joshua Burwood & Evangeline Hooper
// COMP3290 Project 2
// Stack.java
// Due 29.10.2023

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

// Handles the Stack for code gen
public class Stack {

    private ArrayList<Integer> stack;

    public Stack(){

        stack = new ArrayList<>();
    }

    public void push(Integer value){

        stack.add(value);

    }



    public void toFile(String filename){

         try {
            FileWriter writer= new FileWriter(filename, false);
            BufferedWriter bufferW= new BufferedWriter(writer);
            PrintWriter printer = new PrintWriter(bufferW);

            for(int i = 0; i < stack.size() ; i++){
                printer.print(stack.get(i));
                printer.println(" "); // TODO: mod formatting
            }

            printer.flush();
            writer.close();
            bufferW.close();
            printer.close();
        
        } catch (IOException io){

        }

    }
    
}
