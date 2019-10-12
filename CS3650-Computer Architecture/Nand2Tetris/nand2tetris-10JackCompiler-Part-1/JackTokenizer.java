import java.io.File;
import java.io.FileInputStream;
import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;
import java.util.regex.*;

public class JackTokenizer {
    InputStreamReader reader;
    private Vector<String> tokenVector = new Vector<String>();
    private String currentToken = null;
    private int currentIndex = 0;

    public JackTokenizer(String filename) {
        try {
            InputStream file = new FileInputStream(filename); // Open .jack File
            reader = new InputStreamReader(file);
            vectorizeTokens();       
            reader.close();
            currentToken = tokenVector.get(currentIndex); 
        } catch (Exception e) {
            System.out.println(filename + " : Error : " + e.getMessage());
        }
    }

    public boolean hasMoreTokens() {
        boolean more = false;
        if (currentIndex < tokenVector.size()-1) {
            more = true;
        }
        return more;
    }

    public void advance() {

        if (hasMoreTokens()) {
            currentIndex++; //increment current index
            currentToken = tokenVector.get(currentIndex); //set current token
        } else {
            System.out.println("\nDONE.....");
        }
    }

    public String tokenType() // returns type of token
    {
        Vector<String> key = new Vector<String>();
                        key.addElement("class");
                        key.addElement("method"); 
                        key.addElement("function"); 
                        key.addElement("constructor");                   
                        key.addElement("int"); 
                        key.addElement("boolean"); 
                        key.addElement("char"); 
                        key.addElement("void"); 
                        key.addElement("var"); 
                        key.addElement("static");             
                        key.addElement("field"); 
                        key.addElement("let"); 
                        key.addElement("do"); 
                        key.addElement("if"); 
                        key.addElement("else"); 
                        key.addElement("while");
                        key.addElement("return");
                        key.addElement("true");
                        key.addElement("false");
                        key.addElement("null");
                        key.addElement("this");
                        key.addElement("Main");

        String idenPattern = "^[a-zA-z_\\s]*$";    
        String aplhaNumericPattern = "^[a-zA-z0-9_]*$";               
        String sym = "{}()[].,;+-*/&|<>=~\"";
        String numbers = "0123456789";
        String type = "INVALID_TOKEN";

            if(sym.contains(currentToken))
            {
                type = "SYMBOL";
            }
            else if(key.contains(currentToken))
            {
                type = "KEYWORD";
            }
            else if(Pattern.matches(aplhaNumericPattern,currentToken) && !numbers.contains(Character.toString(currentToken.charAt(0))))
            {
                type = "IDENTIFIER";
            }
            else if( !(Pattern.matches(idenPattern,currentToken)) )
            {
                type = "INT_CONST";
            }
            else if(tokenVector.get(currentIndex) == "\"") //TODO: not being recognized
            {
                type = "STRING_CONST";
            }
        return type;
    }

    // HELPER FUNCTIONS
    private void vectorizeTokens() {
        StringBuilder tok = new StringBuilder();
        char c; // hold current character
        int cint;
        int i = 0; // Token counter
        String symbols = "{}()[].,;+-*/&|<>=~";
        try {
            cint = reader.read(); //read character int
            c = (char) cint;
            while (cint != -1) // read while not end of stream
            {
                if (c != '\t' && c != ' ' && c != '\n' && cint != 13)
                {
                    if(!symbols.contains( Character.toString(c)))
                    {
                        if (c != '"') // check if start of string constant
                        {
                            while(c != '\t' && c != ' ' && c != '\n' && cint != 13 && !symbols.contains(Character.toString(c)))
                            {
                                tok.append(c); // add character to working string
                                c = (char) reader.read();
                            } 
                            tokenVector.add(tok.toString()); //accept token
                            i++;
                            tok = tok.delete(0, tok.length()); //reinitialize tok
                            if(symbols.contains(Character.toString(c)))
                            {
                                tokenVector.add(Character.toString(c)); //accept symbol token
                                i++; //incerement token counter
                            }
                        } else { //string constant
                            tokenVector.add(Character.toString(c)); // accept start of string constant token (")
                            i++; //incerement token counter
                            c = (char) reader.read(); // read next character

                            while (c != '"') {
                                tok.append(c); // add current character
                                c = (char) reader.read(); // read next character
                            }
                            tokenVector.add(tok.toString()); // accept String const to tokenVector
                            i++; // increment current index
                            tok = tok.delete(0, tok.length()); //reinitialize string builder
                            
                            tokenVector.add(Character.toString(c)); // accept end of string constant token (")
                            i++; // increment current index
                        }

                    } else { // current character is a symbol
                        if (c == '/') // test for comment "//"
                        {
                            tokenVector.add(Character.toString(c)); //accept '/' to tokenVector
                            i++;
                            c = (char) reader.read(); // read next

                            if (c == '/') // comment
                            {
                                i--; // decrement counter
                                tokenVector.remove(i); // remove prev added '/' symbol
                                while (c != '\n') // while not newline
                                {
                                    //ignoring characters
                                    c = (char) reader.read();
                                }
                            } else if (c == '*') { //check firs '*'
                                c = (char) reader.read(); //check for following '*'
                                if(c == '*')
                                {
                                    i--; // decrement counter
                                    tokenVector.remove(i); // remove prev added '/' symbol
                                    c = (char) reader.read(); //start of string comment
                                    tok.append (c);
                                    while (!tok.toString().contains("*/")) // while not '*/'
                                    {
                                        //ignoring characters
                                        tok.append((char) reader.read());
                                    }
                                    tok.delete(0, tok.length()); //reinitialize tokens
                                }
                            }
                        } else {
                            tokenVector.add(Character.toString(c)); //accept symbol
                            i++;
                        }
                    }
                }
                //read and cast next character
                cint = reader.read(); 
                c = (char) cint;
            }
            // end of stream
        } catch (Exception e) {
            System.out.println("Err reading stream: " + e.getMessage());
        }
        //printTokens(); 
    }

    public String getToken()
    {
        return currentToken;
    }

    public void printTokens()
    {
        System.out.println("\nTotal of " + tokenVector.size() + " Tokens\n");
        for(int j = 0; j < tokenVector.size(); j++)
        {
            System.out.println(j + ": " + tokenVector.get(j));
        }
    }  
}

