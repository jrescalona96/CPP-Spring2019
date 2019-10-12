import java.io.File;
import java.io.PrintWriter;

public class CodeWriter
{
    static PrintWriter writer;
    private int labelCounter = 0;

    public CodeWriter(String program)
    {
        //Create File
        program = program.replaceAll(".vm", ".asm"); //replace extension
        try
        {
            File file = new File(program);
            writer = new PrintWriter(file);
        } catch (Exception e) {
            System.out.println("File not created");
        }
    }

    public void writeArithmetic(String instruction, String command)
    {
        String inst = "";

        if(command.contains("add")) 
        {
            inst = twoFromStack() + "M=M+D";
        }
        else if(command.contains("sub"))
        {
            inst = twoFromStack() + "M=M-D";
        }
        else if(command.contains("neg"))
        {
            inst = oneFromStack() + "M=-M\n"; //-value and re-increment stack pointer
        }   
        else if(command.contains("gt"))
        {
            inst = twoFromStack() + compare("JGT", labelCounter); 
            labelCounter++;
        }
        else if(command.contains("lt"))
        {
            inst = twoFromStack() + compare("JLT", labelCounter); 
            labelCounter++;
        }
        else if(command.contains("eq"))
        {
            inst = twoFromStack() + compare("JEQ", labelCounter);
            labelCounter++; 
        }
        else if(command.contains("and"))
        {
            inst = twoFromStack() + "M=M&D";
        }
        else if(command.contains("or"))
        {
            inst = twoFromStack() + "M=M|D";
        }
        else if(command.contains("not"))
        {
            inst = oneFromStack() + "M=!M\n"; //!value and re-increment stack pointer
        }
        else 
        {
            System.out.println("Unable to write: " + command);
        }
        writer.println("\n//" + instruction);
        writer.println(inst);
        writer.flush();
    }

    public void writePushPop(String instruction,String com, String seg, int index)
    {
        String inst = "";
        int staticVar = 16;
        int temp = 5;

        if(com.contains("push")) //push command
        {
            if(seg.contains("constant"))
            {
                inst = "@" + index + "\n" //point to proper value at constant segment
                     + "D=A\n" // save constant
                     + "@SP\n" // point to SP
                     + "A=M\n" //get top of stack address
                     + "M=D\n" //store constant to top of stack
                     + "@SP\n" //point to stack pointer
                     + "M=M+1"; //increment stack pointer 
            }
            else if(seg.contains("local"))
            {
                inst = pushToStack("LCL", index, false);
            }
            else if(seg.contains("argument"))
            {
                inst = pushToStack("ARG", index, false);
            }
            else if(seg.contains("this"))
            {
                inst = pushToStack("THIS", index, false);
            }
            else if(seg.contains("that"))
            {
                inst = pushToStack("THAT", index, false);
            }
            else if(seg.contains("temp"))
            {
                inst = pushToStack("R5", index + temp, false);
            }
            else if(seg.contains("pointer") && index == 0)
            {
                inst = pushToStack("THIS", index, true);
            }
            else if(seg.contains("pointer") && index == 1)
            {
                inst = pushToStack("THAT", index, true);
            }
            else //static
            {
                String var = String.valueOf(staticVar+index);
                inst = pushToStack(var, index , true);
            }
        }
        else // pop command
        {
            if(seg.contains("local"))
            {
                inst = popFromStack("LCL", index, false);
            }
            else if(seg.contains("argument"))
            {
                inst = popFromStack("ARG", index, false);
            }
            else if(seg.contains("this"))
            {
                inst = popFromStack("THIS", index, false);
            }
            else if(seg.contains("that"))
            {
                inst = popFromStack("THAT", index, false);
            }
            else if(seg.contains("temp"))
            {
                inst = popFromStack("R5", index + temp, false);
            }
            else if(seg.contains("pointer") && index == 0)
            {
                inst = popFromStack("THIS", index, true);
            }
            else if(seg.contains("pointer") && index == 1)
            {
                inst = popFromStack("THAT", index, true);
            }
            else //static
            {
                String var = String.valueOf(staticVar+index);
                inst = popFromStack(var, index , true);
            }
        }
        writer.println("\n//" + instruction);
        writer.println(inst);
        writer.flush();
    }

    public void writeLabel(String label)
    {
        writer.println("("+label.toUpperCase()+")");
        writer.flush();
    }

    public void writeGoto(String label)
    {
        writer.println("@" + label.toUpperCase() +"\n0;JMP\n");
        writer.flush();
    }
    
    //helper functions-------------------------------------------------------------------------------------
    private String compare(String compareType, int counter)
    {
        String s = "D=M-D\n"
                 + "@TRUE" + "." + counter + "\n"
                 + "M=-1\n"
                 + "D;" + compareType + "\n"
                 //if False---------------------------
                 + "@SP\n"
                 + "AM=M-1\n"
                 + "M=0\n"
                 + "@SP\n"
                 + "M=M+1\n"
                 + "@RETURN" + "." + counter + "\n"
                 + "0;JMP\n"
                 //if True----------------------------
                 + "(TRUE"+ "." + counter + ")\n"
                 + "@SP\n"
                 + "AM=M-1\n"
                 + "M=-1\n"
                 + "@SP\n"
                 + "M=M+1\n"
                 + "(RETURN"+ "." + counter + ")\n";
        return s;
    }
    
    private String twoFromStack() //returns string to get set up arguments for command
                                  //D = 0(SP) & M = 1(SP)
    {
        String s = "";

        s = "@SP\n" //point to Stack pointer
          + "AM=M-1\n" //get top of stack addr & decrement: point to A and update SP
          + "D=M\n" //D reg = first data
          + "@SP\n"
          + "A=M-1\n"; //decrement to set pointer to second data
        return s;
    }

    public String oneFromStack() //returns string to get set up argument for command ; M = 0(SP)
    {
    String s = "";

        s = "@SP\n" //point to Stack pointer
          + "A=M-1\n"; //get top of stack addr & decrement: point to A and update SP
    return s;
    }

    private String pushToStack(String seg, int i, boolean offset)
    {
        String pointStr = "";
        String s = ""; 

        if(offset)
        {
            pointStr = "";
        }
        else
        {
            pointStr = "@" + i + "\n" //get the value index from constant
                    + "A=D+A\n" //add offset to segment address adn point to it
                    + "D=M\n"; //get data from address
        }

        s = "@"+ seg + "\n" //point to proper segment----------------
            + "D=M\n"  //get base address contained in the pointer
            + pointStr
            //push to stack--------------------------------------------
            + "@SP\n" // point to SP
            + "A=M\n" //get top of stack address
            + "M=D\n" //store data to top of stack
            + "@SP\n" //point to stack pointer
            + "M=M+1\n"; //increment stack pointer 
        return s;
    }

    private String popFromStack(String seg, int i, boolean offset)
    {
        String pointStr = "";
        String s = ""; 

        if(offset)
        {
            pointStr = "D=A\n"; //get base address contained in the pointer
        }
        else
        {
            pointStr = "D=M\n" //get base address contained in the pointer
                    + "@" + i + "\n"
                    + "D=D+A\n"; //add offset to base address of segment
        }

        s = "@" + seg + "\n" //get address at segment----------------
          + pointStr
          + "@R13\n" 
          + "M=D\n"   //save address of segment at temp (@R13)

          //pop data from stack--------------------------------------
          + "@SP\n" // point to SP
          + "AM=M-1\n" //get top of stack addr & decrement: point to A and update SP
          + "D=M\n" // pop data to D reg TODO: increment?

          //retrieve address of at segment from R13------------------
          + "@R13\n"
          + "A=M\n" //point to segment address 
          + "M=D\n"; //store data at location
        return s;
    }

    public void writeEnd()
    {
        writer.println("\n//End program\n(END)\n@END\n0;JMP");
    }

    public void close()
    {
        writer.close();
    }
}
