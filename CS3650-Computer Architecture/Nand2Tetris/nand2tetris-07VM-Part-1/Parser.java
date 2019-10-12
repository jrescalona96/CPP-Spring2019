import java.util.*;
import java.io.File;

public class Parser {
    //Fields
    private String currentInst = " "; // holds current string instruction
    //int addr = 0;
    Scanner reader; // keeps track of the address of each instruction

    // Constructor
    public Parser(String file) {
        try {
            File prog = new File(file);
            reader = new Scanner(prog);
        } catch (Exception e) {
            System.out.println("File not found!");
        }

        advance();
    }

    public Boolean hasMoreCommands() {
        if (reader.hasNext())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void advance() {
        currentInst = reader.nextLine(); // read current instruction
        if(currentInst.contains("//"))
        {
            int i = currentInst.indexOf("//");
            currentInst = currentInst.substring(0, i);
        }
    }

    public String commandType() //returns the type of VM Instruction
    {
        String type = "";
        String[] workingInst = currentInst.split("\\s");

        if(workingInst[0].contains("add") || workingInst[0].contains("sub") || workingInst[0].contains("neg")
            || workingInst[0].contains("eq") || workingInst[0].contains("gt") || workingInst[0].contains("lt")
            || workingInst[0].contains("and") || workingInst[0].contains("or") || workingInst[0].contains("not"))
        {
            type = "C_ARITHMETIC";
        }
        else if (workingInst[0].contains("push"))
        {
            type = "C_PUSH";
        }
        else if (workingInst[0].contains("pop"))
        {
            type = "C_POP";
        }
        else if (workingInst[0].contains("label"))
        {
            type = "C_LABEL";
        }
        else if (workingInst[0].contains("goto"))
        {
            type = "C_GOTO";
        }
        else if (workingInst[0].contains("if-goto"))
        {
            type = "C_IF";
        }
        else if (workingInst[0].contains("function"))
        {
            type = "C_FUNCTION";
        }
        else if (workingInst[0].contains("return"))
        {
            type = "C_RETURN";
        }
        else if (workingInst[0].contains("call"))
        {
            type = "C_CALL";
        }
        else
        {
            type = "NA";
        }
    
    return type; //return result
    }

    public String getCommand()
    {
        String[] ar = currentInst.split("\\s");
        return ar[0];
    }

    public String arg1() //returns first argument
    {
        String arg = "";
        if(commandType() != "NA")
        {
            String[] workingInst = currentInst.split("\\s");
            if(commandType() == "C_ARITHMETIC")
            {
                arg = currentInst;
            }
            else
            {
                arg = workingInst[1];
            }
        }
        return arg;
    }

    public String arg2() //returns second argument
    {
        String i = "";
        if(commandType() != "NA")
        {
            String[] workingInst = currentInst.split("\\s");
            if(commandType() != "C_ARITHMETIC" && commandType() != "C_RETURN")
            {
                i = workingInst[2];
            }
        }
        return i;
    }

    public String getCurrentInst()
    {
        return currentInst;
    }

}

