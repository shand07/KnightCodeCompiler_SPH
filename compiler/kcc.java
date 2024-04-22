/**
 * 
 * @author Sean Hand
 * @version 1.1
 * Assignment 5
 * CS322 - Compiler Construction
 * Spring 2024
 */
package compiler;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;


import lexparse.*;
public class kcc
{
    public static void main(String[] args)
    {

        //length test
        if(args.length !=2) 
        {
            System.out.println("Our program will use two arguments, a knightcode file to compile and an output program");
            System.exit(1);
        }
        
        CharStream input;
        KnightCodeLexer lexer;
        CommonTokenStream tokens;
        KnightCodeParser parser;
        
        //file creation try block
        try
        {
            input = CharStreams.fromFileName(args[0]);  //this is the input
            lexer = new KnightCodeLexer(input); //generates lexer
            tokens = new CommonTokenStream(lexer); //makes the tokens
            parser = new KnightCodeParser(tokens); //generates parser
            ParseTree tree = parser.file();  //parser starting location
            
            kccTable table = new kccTable();
            
            String genName = "Gen" + args[1].substring(args[1].length()-1);
            String outputName = args[1].substring(args[1].indexOf("/")+1);
            File output = new File("output/" + genName + ".java");
            String asm = "";
            String generate = "";
            
            //try block for the FileOutputStream
            try {
                FileOutputStream outStream = new FileOutputStream(output);
                //required asm code at the beginning and end of each file being generated
                asm = "package output;\nimport org.objectweb.asm.*;\nimport java.io.File;\nimport java.io.FileOutputStream;\nimport java.io.IOException;\n";
                asm += "public class " + genName + " {\npublic static void main(String[] args) {\n";
                asm += "System.out.println(\"Creating Bytecode...\");\nClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);\n";
                asm += "cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC,\"output/" + outputName + "\", null, \"java/lang/Object\",null);\n";
                asm += "MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC+Opcodes.ACC_STATIC, \"main\", \"([Ljava/lang/String;)V\", null, null);\n";
                asm += "mv.visitCode();\n";
                outStream.write(asm.getBytes());
                asm = "";

                kccVisitor visit = new kccVisitor(table);
               
                generate += visit.visit(tree);
                outStream.write(generate.getBytes());
                
                asm += "\nmv.visitInsn(Opcodes.RETURN);\n";
                asm += "mv.visitMaxs(0,0);\n";
                asm += "mv.visitEnd();\n";
                asm += "cw.visitEnd();\n\n";
                asm += "byte[] b = cw.toByteArray();\ntry{";
                asm += "File out = new File(\"output/" +outputName+".class\");\n";
                asm += "FileOutputStream byteOut = new FileOutputStream(out);\n";
                asm += "byteOut.write(b);\n";
                asm += "byteOut.close();\n}catch(IOException e){System.out.println(e.getMessage());}\nSystem.out.println(\"Finished!\");\n}\n}";
                outStream.write(asm.getBytes());
                outStream.close();
            } catch(FileNotFoundException e) {System.out.println(e.getMessage());}
            System.out.println("Creating File...");
            Runtime.getRuntime().exec("javac output/" + genName + ".java");
            Runtime.getRuntime().exec("java output/" + genName);
            System.out.println("Finished!");
            
        }
        catch(IOException e){System.out.println(e.getMessage());}
        
    }//end main
}//end kcc