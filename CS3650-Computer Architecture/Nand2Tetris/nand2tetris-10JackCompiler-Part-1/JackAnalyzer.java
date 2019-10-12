import java.io.File;
import java.io.PrintWriter;

public class JackAnalyzer
{
    public static void main(String[] args)
    {
        //Open .jack file and read
        String inFile = args[0];
        System.out.println("Compiling: "+ inFile);

        JackTokenizer token = new JackTokenizer(inFile); // Tokenizer
        while(token.hasMoreTokens())
        {
            System.out.println(token.getToken() + " = " + token.tokenType());
            token.advance();
        }

        //Create output XML File
        String outFile = inFile.replace(".jack", ".xml");
        //CompilationEngine parser = new CompilationEngine(outFilename); // Parser
    }
}