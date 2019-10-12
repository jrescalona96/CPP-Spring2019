public class Translator {
    public static void main(String[] args)
    {
        String filename = args[0];
        Parser p = new Parser(filename);
        CodeWriter w = new CodeWriter(filename);
        System.out.println("\nFile Name: " + filename + '\n');

        while(p.hasMoreCommands())
        {
            if(p.commandType() != "NA")
            {
                //print(p.getCommand(), p.commandType(),p.arg1(), p.arg2());
                if(p.commandType() == "C_ARITHMETIC")
                {
                    w.writeArithmetic(p.getCurrentInst(), p.getCommand());
                }
                else if(p.commandType() == "C_PUSH" || p.commandType() == "C_POP")
                {

                    w.writePushPop(p.getCurrentInst(), p.getCommand(),p.arg1(),Integer.parseInt(p.arg2()));
                }
                else if(p.commandType() == "C_LABEL")
                {
                    
                }
                else if(p.commandType() == "C_GOTO")
                {
                    
                }
                else if(p.commandType() == "C_IF")
                {
                    
                }
                else if(p.commandType() == "C_FUNCTION")
                {
                    
                }
                else if(p.commandType() == "C_RETURN")
                {
                    
                }
                else if(p.commandType() == "C_CALL")
                {
                    
                }    
            }
            p.advance();
        }
        //print(p.getCommand(), p.commandType(),p.arg1(), p.arg2());
        w.writeArithmetic(p.getCurrentInst(), p.getCommand()); //write last command
        w.writeEnd();
        w.close();
        System.out.println("\nFinished Translating.");
    }

    public static void print(String inst, String type, String arg1, String arg2)
    {
        System.out.println("Translating:   " + inst);
        System.out.println("Type:   " + type);
        System.out.println("Arg1:   " + arg1);
        if(type != "C_ARITHMETIC" && type != "C_RETURN") 
        {
            System.out.println("Arg2:   " + arg2);
        }
    }
}   