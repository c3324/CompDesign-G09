// Joshua Burwood & Evangeline Hooper
// COMP3290 Project 2
// Stack.java
// Due 29.10.2023


import java.util.LinkedList;
import java.io.BufferedWriter;
import java.io.File;  // Import the File class
import java.io.FileWriter;
import java.io.IOException;  // Import the IOException class to handle errors
import java.io.PrintWriter;

// Handles the Stack for code gen
public class Mod {

    // private String mod; // using String like array list to be easily written to file
    private String instructions;
    private String int_consts;
    private String float_consts;
    private String string_consts;
    // can append to front with mod = x + mod
    // or back with mod += x

    private int instruction_col; 
    private int int_col; 
    private int float_col; 
    private int string_col; 

    public Mod(){

        // mod = new ArrayList<>();
        // mod = "";
        instructions = "";
        int_consts = "";
        float_consts = "";
        string_consts = "";

        instruction_col = 0;
        int_col = 0;
        float_col = 0;
        string_col = 0;

    }

    public void push(String section, Integer value){

        if (section.equals("instructions")){
            if ( instruction_col == 8){
                instructions += '\n' + value;
                instruction_col = 1;
            }
            else{
                instructions = instructions + value + " ";
                instruction_col++;
            }
            
        }
        else if (section.equals("int_consts")){
            if (int_consts.equals("")){
                int_consts += value;
                return;
            }
            int_consts =  int_consts + "\n" + integerToString(value);
        }
        else if (section.equals("float_consts")){
            if (float_consts.equals("")){
                float_consts += value;
                return;
            }
            if ( float_col == 8){
                float_consts += '\n';
                float_col = 0;
            }
            float_consts = float_consts + " " + value;
            float_col++;
        }
        else if (section.equals("string_consts")){
            if (string_consts.equals("")){
                string_consts += value;
                return;
            }
            if ( string_col == 8){
                int_consts += '\n';
                string_col = 0;
            }
            string_consts = string_consts + " " + value;
            string_col++;
        }
        

    }

    // For accepting string inputs
    public void push(String section, String value){

        if (section.equals("instructions")){
            if ( instruction_col == 8){
                instructions += '\n' + value;
                instruction_col = 1;
            }
            else{
                instructions = instructions + value + " ";
                instruction_col++;
            }
            
        }
        else if (section.equals("int_consts")){
            if (int_consts.equals("")){
                int_consts += value;
                return;
            }
            int_consts =  int_consts + "\n" + value;
        }
        else if (section.equals("float_consts")){
            if (float_consts.equals("")){
                float_consts += value;
                return;
            }
            if ( float_col == 8){
                float_consts += '\n';
                float_col = 0;
            }
            float_consts = float_consts + " " + value;
            float_col++;
        }
        else if (section.equals("string_consts")){
            if (string_consts.equals("")){
                string_consts += value;
                return;
            }
            if ( string_col == 8){
                int_consts += '\n';
                string_col = 0;
            }
            string_consts = string_consts + " " + value;
            string_col++;
        }
        

    }

    // Ideally only needed for instructions, but updated just in case
    public void pushFront(String section, Integer value){

        // mod = value + "\n" + mod;

        if (section.equals("instructions")){
            if (instructions.equals("")){
                instructions += value;
                return;
            }
            instructions = value + "\n" + instructions;
        }
        else if (section.equals("int_consts")){ // TODO: This isn't technically correct. Needs to be converted to (8) bytes
            if (int_consts.equals("")){
                int_consts += value;
                return;
            }
            int_consts = value + "\n" + int_consts;
        }
        else if (section.equals("float_consts")){ // TODO: This isn't technically correct. Needs to be converted to (8) bytes
            if (float_consts.equals("")){
                float_consts += value;
                return;
            }
            float_consts = value + "\n" + float_consts;
        }
        else if (section.equals("string_consts")){ // TODO: This isn't technically correct. Needs to be converted to char -> bytes
            if (string_consts.equals("")){
                string_consts += value;
                return;
            }
            string_consts = value + "\n" + string_consts;
        }

    }

    // Should only be needed for instructions.
    public void pad(){

        // for ( int i = 0; i <= col % 8; i++){
        //     mod += " 0";
        // }
        // mod += "\n";
        // col = 0;

        for ( int i = 0; i < 8 - instruction_col; i++){
            instructions += "0 ";
        }
        instruction_col = 8;

    }

    public String integerToString(int value){
        String numberStr = String.valueOf(value); // Convert the integer to a string
        StringBuilder formattedStr = new StringBuilder();
    
        for (int i = 0; i < numberStr.length(); i++) {
            char digit = numberStr.charAt(i); // Get each digit as a character
            formattedStr.append((int)digit).append(' '); // Append the ASCII value of the digit and a space
        }
    
        return formattedStr.toString().trim(); // Remove the trailing space and return the formatted string
    
    }



    public void toFile(String filename){

        // Finally push a HALT
        // mod += "\n0";
        String mod = instructions + "\n" + int_consts + "\n" +float_consts + "\n" + string_consts + "\n" + "0";

        try {
            FileWriter writer     = new FileWriter(filename, false);
            BufferedWriter bufferW= new BufferedWriter(writer);
            PrintWriter printer = new PrintWriter(bufferW);

            printer.print(mod);

            printer.flush();
            writer.close();
            bufferW.close();
            printer.close();
        
        } catch (IOException io){

        }

    }
    
}
