/**
 * Our visitor used by kcc.java
 * @author Sean Hand
 * @version 2.5
 * Assignment 5
 * CS322 - Compiler Construction
 * Spring 2024
 */
package compiler;
import org.antlr.v4.runtime.tree.ParseTree;

import lexparse.KnightCodeBaseVisitor;
import lexparse.KnightCodeParser;
import lexparse.KnightCodeParser.*;

public class kccVisitor extends KnightCodeBaseVisitor<String>
{
    private kccTable tableOne;//Variable Table
    private kccTable tableTwo;//Index Table
   
    private int asmIndex;//index for bytecode storage

    /**
     * passing of symbol table
     * @param @table passed table
     */
    public kccVisitor(kccTable table) 
    {
        tableOne = table;
        asmIndex = 1;
        tableTwo = new kccTable();
    }//end Constructor
 

    /**
     * 
     * Traversing of parse tree
     */
    @Override
    public String visitFile(KnightCodeParser.FileContext ctx) 
    { 
        System.out.println("Traveling the tree..");
        visitDeclare((DeclareContext) ctx.getChild(2));
        return visitBody((BodyContext) ctx.getChild(3));
    }//end visitFile

    /**
     * 
     * puts identifers and variable types in symbol table
     */
    @Override
    public String visitDeclare(KnightCodeParser.DeclareContext ctx) 
    {
        System.out.println("visiting DECLARE");
        if(ctx.getChildCount() >=2) {
            for(int i = 1; i<ctx.getChildCount(); i++) {
                ParseTree tempNode = ctx.getChild(i);
                if(tempNode.getChild(0).getText().compareTo("INTEGER") == 0) 
                {
                    variable temp = new variable(0);
                    String idName = visitIdentifier((IdentifierContext) tempNode.getChild(1));
                    tableOne.addEntry(idName, temp);
                }
                else 
                {
                    variable temp = new variable("\"\"");
                    String idname = visitIdentifier((IdentifierContext) tempNode.getChild(1));
                    tableOne.addEntry(idname, temp);
                }
            }
        }
     
        return "";
    }//end visitDeclare

    /**
     * 
     * travels the tree and returns asm code
     */
    @Override 
    public String visitBody(KnightCodeParser.BodyContext ctx) 
    { 
        System.out.println("Visiting the Body...");
        
        String code = "";
        //visits children of the body
        for(int i = 1; i<ctx.getChildCount()-1; i++) 
        {
            ParseTree tempNode = ctx.getChild(i);//the stat child
            code += visitStat((StatContext) tempNode);
        }
        System.out.println("Finished!");
        return code; 
    }//end visitBody

    /**
     * 
     * vists statements
     */
    @Override 
    public String visitStat(KnightCodeParser.StatContext ctx) { 
        System.out.println("Visiting STAT...");
        String code = "";
        ParseTree childNode = ctx.getChild(0);
        String childContent = childNode.getText();
        
        //Looks for SET
        if(childContent.substring(0, 3).equals("SET")) {
            System.out.println("Set");
            code += visitSetvar((SetvarContext) childNode);
        }
        //Looks for PRINT
        else if(childContent.substring(0,5).equals("PRINT")) {
            System.out.println("Print");
            code += visitPrint((PrintContext) childNode);
        }
        //Looks for READ
        else if(childContent.substring(0,4).equals("READ")) 
        {
            System.out.println("Read");
            code += visitRead((ReadContext) childNode);
        }
        //Looks for IF
        else if(childContent.substring(0,2).equals("IF")) 
        {
            System.out.println("If");
            code += visitDecision((DecisionContext) childNode);
        }
         //Looks for WHILE
         else if(childContent.substring(0,5).equals("WHILE")) 
         {
            System.out.println("While");
            code += visitLoop((LoopContext) childNode);
        }
        //Looks for MULTIPLICATION
        else if(childContent.indexOf('*') != -1) 
        {
            System.out.println("Multiplication");
            code += visitMultiplication((MultiplicationContext) childNode);
        }
        //Looks for DIVISION
        else if(childContent.indexOf('/') != -1) 
        {
            System.out.println("Divison");
            code += visitDivision((DivisionContext) childNode);
        }
        //Looks for SUBTRACTION
        else if(childContent.indexOf('+') != -1) 
        {
            System.out.println("Subtraction");
            code += visitAddition((AdditionContext) childNode);
        }
        //Looks for ADDITION
        else if(childContent.indexOf('-') != -1) 
        {
            System.out.println("Addition");
            code += visitSubtraction((SubtractionContext) childNode);
        }
        //Looks for COMPARE
        else if(childContent.indexOf('>') != -1 || childContent.indexOf('<') != -1 || childContent.indexOf('=') != -1 || childContent.indexOf("<>") != -1) 
        {
            System.out.println("Comparison");
            code += visitComp((CompContext) childNode);
        }
        return code;
    }//end visitStat

    /**
     * 
     * sets value of varible and/or index
     */
    @Override 
    public String visitSetvar(KnightCodeParser.SetvarContext ctx) 
    { 
        System.out.println("Visiting SET");
        String code = "";
        String id = ctx.getChild(1).getText();
       
        String exprTxt = ctx.getChild(3).getText();
        //looks for set variable/index
        if(tableOne.getValue(id) != null && ctx.getChild(3).getChildCount() == 1) 
        {
            variable temp = tableOne.getValue(id);
            if(!temp.isString()) 
            {
                temp.setInt((Integer.parseInt(ctx.getChild(3).getChild(0).getText())));
                tableOne.addEntry(id, temp);
            }
            else 
            {
                temp.setString(exprTxt);
                tableOne.addEntry(id, temp);
            }
        }
        //looks for keywords and calls correct method
        else if(exprTxt.indexOf('*') != -1) 
        {
            System.out.println("Multiplication");
            code += visitMultiplication((MultiplicationContext)ctx.getChild(3));
            if(tableOne.getValue(id) != null) 
            {
                tableTwo.addEntry(id, new variable(asmIndex-1));
                tableOne.remove(id);
            }
            else 
                tableTwo.getValue(id).setInt(asmIndex-1);
        }
        
        else if(exprTxt.indexOf('/') != -1) 
        {
            System.out.println("Division");
            code += visitDivision((DivisionContext) ctx.getChild(3));
            if(tableOne.getValue(id) != null) 
            {
                tableTwo.addEntry(id, new variable(asmIndex-1));
                tableOne.remove(id);
            }
            else 
                tableTwo.getValue(id).setInt(asmIndex-1);
        }
        else if(exprTxt.indexOf('+') != -1) 
        {
            System.out.println("Addition");
            code += visitAddition((AdditionContext) ctx.getChild(3));
            if(tableOne.getValue(id) != null) 
            {
                tableTwo.addEntry(id, new variable(asmIndex-1));
                tableOne.remove(id);
            }
            else 
                tableTwo.getValue(id).setInt(asmIndex-1);
        }
        else if(exprTxt.indexOf('-') != -1) {
            System.out.println("Subtraction");
            code += visitSubtraction((SubtractionContext) ctx.getChild(3));
            if(tableOne.getValue(id) != null) {
                tableTwo.addEntry(id, new variable(asmIndex-1));
                tableOne.remove(id);
            }
            else 
                tableTwo.getValue(id).setInt(asmIndex-1);
        }
        return code; 
    }//end visitSetvar

    /**
     * 
     * returning of asm code for printing our value
     */
    @Override 
    public String visitPrint(KnightCodeParser.PrintContext ctx) 
    {

        System.out.println("Visiting PRINT");
       
        String content = ctx.getChild(1).getText();
   
        String code = "";
        //Looking for saved variable
        if(tableTwo.getValue(content) != null) 
        {
            
            variable temp = tableTwo.getValue(content);
            //Looking for string or int
            if(temp.isString()) 
            {
                code += "mv.visitFieldInsn(Opcodes.GETSTATIC, \"java/lang/System\", \"out\", \"Ljava/io/PrintStream;\");\n";
                code += "mv.visitVarInsn(Opcodes.ALOAD,"+temp.getInt()+");\n";
                code += "mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, \"java/io/PrintStream\", \"println\", \"(Ljava/lang/String;)V\", false);\n";
            }
            else 
            {
                code += "mv.visitFieldInsn(Opcodes.GETSTATIC, \"java/lang/System\", \"out\", \"Ljava/io/PrintStream;\");\n";
                code += "mv.visitVarInsn(Opcodes.ILOAD,"+temp.getInt()+");\n";
                code += "mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, \"java/io/PrintStream\", \"println\", \"(I)V\", false);\n";
            }
        }
       
        //Prints value
        else 
        {
           
            code += "mv.visitFieldInsn(Opcodes.GETSTATIC, \"java/lang/System\", \"out\", \"Ljava/io/PrintStream;\");\n";
            code += "mv.visitLdcInsn((String)"+content+");\n";
            code += "mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, \"java/io/PrintStream\", \"println\", \"(Ljava/lang/String;)V\", false);\n";
        }
             
        return code;
    }//end visitPrint

    /**
     * 
     * reads method, returns asm, saves index
     */
    @Override 
    public String visitRead(KnightCodeParser.ReadContext ctx) 
    { 
        System.out.println("Visiting READ");
        
        String code = "";
        int scanIndex = asmIndex;
        asmIndex++;
        //asm
        code += "mv.visitTypeInsn(Opcodes.NEW, \"java/util/Scanner\");\n";
        code += "mv.visitInsn(Opcodes.DUP);\n";
        code += "mv.visitFieldInsn(Opcodes.GETSTATIC, \"java/lang/System\", \"in\", \"Ljava/io/InputStream;\");\n";
        code += "mv.visitMethodInsn(Opcodes.INVOKESPECIAL, \"java/util/Scanner\", \"<init>\", \"(Ljava/io/InputStream;)V\", false);\n";
        code += "mv.visitVarInsn(Opcodes.ASTORE,"+scanIndex+");\n";      
        String idName = ctx.getChild(1).getText();
        //Looking for ID value on nextline
        if(tableOne.getValue(idName) != null) 
        {
            code += "mv.visitVarInsn(Opcodes.ALOAD,"+scanIndex+");\n";
            code += "mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, \"java/util/Scanner\", \"nextLine\", \"()Ljava/lang/String;\", false);\n";
            code += "mv.visitVarInsn(Opcodes.ASTORE,"+asmIndex+");\n";
            asmIndex++;
            variable temp = tableOne.remove(idName);
          
            //Saves string value to index
            if(temp.isString()) 
            {
                temp.setInt(asmIndex-1);
                tableTwo.addEntry(idName, temp);
            }
            //Saves int value to inx
            else 
            {
                code += "mv.visitVarInsn(Opcodes.ALOAD,"+(asmIndex-1)+");\n";
                code += "mv.visitMethodInsn(Opcodes.INVOKESTATIC, \"java/lang/Integer\", \"parseInt\", \"(Ljava/lang/String;)I\", false);\n";
                code += "mv.visitVarInsn(Opcodes.ISTORE,"+asmIndex+");\n";
                temp.setInt(asmIndex);
                tableTwo.addEntry(idName, temp);
                asmIndex++;
            }
        }
        return code; 
    }//end visitRead

    /**
     * 
     * returns asm for if then else 
     */
    @Override 
    public String visitDecision(KnightCodeParser.DecisionContext ctx) { 
        System.out.println("Visiting IF");
        System.out.println(ctx.getText());
        String code = "";
        int one=0;
        int two=0;
        int oneIndex=0;
        int twoIndex=0;
        String stringOne = ctx.getChild(1).getText();
        String stringTwo = ctx.getChild(3).getText();
        
        //Variable one testing
        if(tableTwo.getValue(stringOne) != null) 
        {
            oneIndex = tableTwo.getValue(stringOne).getInt();
            code += "mv.visitVarInsn(Opcodes.ILOAD,"+oneIndex+");\n";
        }
        else 
        {
            try 
            {
                one = Integer.parseInt(stringOne);
                code += "mv.visitIntInsn(Opcodes.BIPUSH,"+one+");\n";
            } catch(NumberFormatException e) {}
        }
        //variable two testing
        if(tableTwo.getValue(stringTwo) != null) 
        {
            twoIndex = tableTwo.getValue(stringTwo).getInt();
            code += "mv.visitVarInsn(Opcodes.ILOAD,"+twoIndex+");\n";
        }
        //Int parsing
        else 
        {
            try 
            {
                two = Integer.parseInt(stringTwo);
                code += "mv.visitIntInsn(Opcodes.BIPUSH,"+two+");\n";
            } catch(NumberFormatException e) {}
        }
       
        return code += visitComp((CompContext) ctx.getChild(2)); 
    }//end visitDecision

    /**
     * 
     * asm while loop code
     */
    @Override 
    public String visitLoop(KnightCodeParser.LoopContext ctx) 
    { 
        System.out.println("Visiting WHILE");
        String code = "";
        
        int one=-1;
        int two=-1;
        int oneIndex=0;
        int twoIndex=0;
        String stringOne = ctx.getChild(1).getText();
        String stringTwo = ctx.getChild(3).getText();
     
        code += "Label loop = new Label();\nLabel endLoop = new Label();\n";
        code += "mv.visitLabel(loop);\n";
        //variable one testing
        if(tableTwo.getValue(stringOne) != null) 
        {
            oneIndex = tableTwo.getValue(stringOne).getInt();
            code += "mv.visitVarInsn(Opcodes.ILOAD,"+oneIndex+");\n";
        }
        else 
        {
            try 
            {
                one = Integer.parseInt(stringOne);
                code += "mv.visitIntInsn(Opcodes.BIPUSH,"+one+");\n";
            } catch(NumberFormatException e) {}
        }
        //variable two testing
        if(tableTwo.getValue(stringTwo) != null) 
        {
            twoIndex = tableTwo.getValue(stringTwo).getInt();
            code += "mv.visitVarInsn(Opcodes.ILOAD,"+twoIndex+");\n";
        }
        //Int parsing
        else 
        {
            try 
            {
                two = Integer.parseInt(stringTwo);
                if(two!=0)
                    code += "mv.visitIntInsn(Opcodes.BIPUSH,"+two+");\n";
            } catch(NumberFormatException e) {}
        }
       
        code += visitComp((CompContext)ctx.getChild(2));
        return code;
    }//endvisitLoop

    /**
     * 
     * visitor for multiplication
     */
    @Override 
    public String visitMultiplication(KnightCodeParser.MultiplicationContext ctx) 
    { 
        System.out.println("Visiting MULT");
        String code = "";
      
        int one=0;
        int two=0;
        String stringOne = ctx.getChild(0).getText();
        String stringTwo = ctx.getChild(2).getText();
        //variable one testing
        if(tableOne.getValue(stringOne) != null) 
        {
            one = tableOne.remove(stringOne).getInt();
        }
        //variable two testing
        if(tableOne.getValue(stringTwo) != null) 
        {
            two = tableOne.remove(stringTwo).getInt();
        }
        //int parsing
        else 
        {
            try 
            {
                one = Integer.parseInt(stringOne);
            } catch(NumberFormatException e) {}
            try 
            {
                two = Integer.parseInt(stringTwo);
        
            } catch(NumberFormatException e) {}
        }
        code += "mv.visitIntInsn(Opcodes.BIPUSH,"+one+");\n";
        code += "mv.visitVarInsn(Opcodes.ISTORE,"+asmIndex+");\n";
        tableTwo.addEntry(stringOne, new variable(asmIndex));
        asmIndex++;
        code += "mv.visitIntInsn(Opcodes.BIPUSH,"+two+");\n";
        code += "mv.visitVarInsn(Opcodes.ISTORE,"+asmIndex+");\n";
        tableTwo.addEntry(stringTwo, new variable(asmIndex));
        asmIndex++;
        code += "mv.visitVarInsn(Opcodes.ILOAD,"+(asmIndex-2)+");\n";
        code += "mv.visitVarInsn(Opcodes.ILOAD,"+(asmIndex-1)+");\n";
        code += "mv.visitInsn(Opcodes.IMUL);\n";
        code += "mv.visitVarInsn(Opcodes.ISTORE,"+asmIndex+");\n";
        asmIndex++;
        return code;
    }//visitMutlitplication

    /**
     * 
     * visitor for divison
     */
    @Override 
    public String visitDivision(KnightCodeParser.DivisionContext ctx) 
    { 
        System.out.println("Visiting DIV");
        String code = "";
        int one=0;
        int two=0;
        String stringOne = ctx.getChild(0).getText();
        String termB = ctx.getChild(2).getText();
        //variable one testing
        if(tableOne.getValue(stringOne) != null) 
        {
            one = tableOne.remove(stringOne).getInt();
        }
        //variable two testing
        if(tableOne.getValue(termB) != null) 
        {
            two = tableOne.remove(termB).getInt();
        }
        //Int parsing
        else 
        {
            try 
            {
                one = Integer.parseInt(stringOne);
            } catch(NumberFormatException e) {}
            try 
            {
                two = Integer.parseInt(termB);
        
            } catch(NumberFormatException e) {}
        }
        code += "mv.visitIntInsn(Opcodes.BIPUSH,"+one+");\n";
        code += "mv.visitVarInsn(Opcodes.ISTORE,"+asmIndex+");\n";
        tableTwo.addEntry(stringOne, new variable(asmIndex));
        asmIndex++;
        code += "mv.visitIntInsn(Opcodes.BIPUSH,"+two+");\n";
        code += "mv.visitVarInsn(Opcodes.ISTORE,"+asmIndex+");\n";
        tableTwo.addEntry(termB, new variable(asmIndex));
        asmIndex++;
        code += "mv.visitVarInsn(Opcodes.ILOAD,"+(asmIndex-2)+");\n";
        code += "mv.visitVarInsn(Opcodes.ILOAD,"+(asmIndex-1)+");\n";
        code += "mv.visitInsn(Opcodes.IDIV);\n";
        code += "mv.visitVarInsn(Opcodes.ISTORE,"+asmIndex+");\n";
        asmIndex++;
        return code;
    }//end visitDivision

    /**
     * 
     * visitor for addition
     */
    @Override 
    public String visitAddition(KnightCodeParser.AdditionContext ctx) 
    {
        System.out.println("Visiting ADD");
        String code = "";
        int one=0;
        int two=0;
        String stringOne = ctx.getChild(0).getText();
        String stringTwo = ctx.getChild(2).getText();
        //variable one testing
        if(tableOne.getValue(stringOne) != null) 
        {
            one = tableOne.remove(stringOne).getInt();
        }
        //variable two testing
        if(tableOne.getValue(stringTwo) != null) 
        {
            two = tableOne.remove(stringTwo).getInt();
        }
        //Int parse
        else 
        {
            try 
            {
                one = Integer.parseInt(stringOne);
            } catch(NumberFormatException e) {}
            try 
            {
                two = Integer.parseInt(stringTwo);
        
            } catch(NumberFormatException e) {}
        }
        code += "mv.visitIntInsn(Opcodes.BIPUSH,"+one+");\n";
        code += "mv.visitVarInsn(Opcodes.ISTORE,"+asmIndex+");\n";
        tableTwo.addEntry(stringOne, new variable(asmIndex));
        asmIndex++;
        code += "mv.visitIntInsn(Opcodes.BIPUSH,"+two+");\n";
        code += "mv.visitVarInsn(Opcodes.ISTORE,"+asmIndex+");\n";
        tableTwo.addEntry(stringTwo, new variable(asmIndex));
        asmIndex++;
        code += "mv.visitVarInsn(Opcodes.ILOAD,"+(asmIndex-2)+");\n";
        code += "mv.visitVarInsn(Opcodes.ILOAD,"+(asmIndex-1)+");\n";
        code += "mv.visitInsn(Opcodes.IADD);\n";
        code += "mv.visitVarInsn(Opcodes.ISTORE,"+asmIndex+");\n";
        asmIndex++;
        return code; 
    }//visitAddition

    /**
     * 
     * visitor for subtraction
     */
    @Override 
    public String visitSubtraction(KnightCodeParser.SubtractionContext ctx) 
    { 
        System.out.println("Visiting SUB");
        String code = "";
        int one=-1;
        int two=-1;
        int oneIndex=0;
        int twoIndex=0;
        String stringOne = ctx.getChild(0).getText();
        String stringTwo = ctx.getChild(2).getText();
        //variable one testing
        if(tableOne.getValue(stringOne) != null) 
        {
            one = tableOne.remove(stringOne).getInt();
            code += "mv.visitIntInsn(Opcodes.BIPUSH,"+one+");\n";
            code += "mv.visitVarInsn(Opcodes.ISTORE,"+asmIndex+");\n";
            tableTwo.addEntry(stringOne, new variable(asmIndex));
            asmIndex++;
            code += "mv.visitVarInsn(Opcodes.ILOAD,"+(asmIndex-1)+");\n";
        }
        else if(tableTwo.getValue(stringOne) != null) 
        {
            oneIndex = tableTwo.getValue(stringOne).getInt();
            if(stringTwo.equals("1")) 
            {
                code += "mv.visitIincInsn("+oneIndex+", -1);\n";
                return code;
            }
            else
                code += "mv.visitVarInsn(Opcodes.ILOAD,"+oneIndex+");\n";
        }
        else 
        {
            try 
            {
                one = Integer.parseInt(stringOne);
                code += "mv.visitIntInsn(Opcodes.BIPUSH,"+one+");\n";
                code += "mv.visitVarInsn(Opcodes.ISTORE,"+asmIndex+");\n";
                
                asmIndex++;
                code += "mv.visitVarInsn(Opcodes.ILOAD,"+(asmIndex-1)+");\n";
            } catch(NumberFormatException e) {}
        }
        //variable two testing
        if(tableOne.getValue(stringTwo) != null) 
        {
            two = tableOne.remove(stringTwo).getInt();
            code += "mv.visitIntInsn(Opcodes.BIPUSH,"+two+");\n";
            code += "mv.visitVarInsn(Opcodes.ISTORE,"+asmIndex+");\n";
            tableTwo.addEntry(stringTwo, new variable(asmIndex));
            asmIndex++;
            code += "mv.visitVarInsn(Opcodes.ILOAD,"+(asmIndex-1)+");\n";
        }
        else if(tableTwo.getValue(stringTwo) != null) 
        {
            twoIndex = tableTwo.getValue(stringTwo).getInt();
            code += "mv.visitVarInsn(Opcodes.ILOAD,"+twoIndex+");\n";
        }
        //Int parsing
        else 
        {
            try 
            {
                two = Integer.parseInt(stringTwo);
                    code += "mv.visitIntInsn(Opcodes.BIPUSH,"+two+");\n";
                    code += "mv.visitVarInsn(Opcodes.ISTORE,"+asmIndex+");\n";
               
                    asmIndex++;
                    code += "mv.visitVarInsn(Opcodes.ILOAD,"+(asmIndex-1)+");\n";
            } catch(NumberFormatException e) {}
        }
       
        code += "mv.visitInsn(Opcodes.ISUB);\n";
        code += "mv.visitVarInsn(Opcodes.ISTORE,"+asmIndex+");\n";
        asmIndex++;
        return code; 
    }//end visitSubtraction

     /**
     * 
     * visitor comparison
     */
    public String visitComp(KnightCodeParser.CompContext ctx) 
    { 
        System.out.println("Visiting context");
       
        String code = "";
        String comp = ctx.getText();
        String label = "";
        if(ctx.getParent().getChild(0).getText().equals("IF")) 
        {
            label = "endIf";
        }
        else
            label = "endLoop";
        String childName = ctx.getParent().getChild(5).getText();
        if(ctx.getParent().getText().contains("ELSE")) 
        {
            code += "Label elseblock = new Label();\nLabel endIf = new Label();\n";
            if(ctx.getParent().getChild(1).getText().equals("0") || ctx.getParent().getChild(3).getText().equals("0") ){
                if(comp.equals(">")) 
                {
                    code += "mv.visitJumpInsn(Opcodes.IFLE, elseblock);\n";
                }
                else if(comp.equals("<")) 
                {
                    code += "mv.visitJumpInsn(Opcodes.IFGE, elseblock);\n";
                }
                else if(comp.equals("=")) 
                {
                    code += "mv.visitJumpInsn(Opcodes.IFNE, elseblock);\n";
                }
                else 
                {
                    code += "mv.visitJumpInsn(Opcodes.IFEQ, elseblock);\n";
                }
            }
            else 
            {
                if(comp.equals(">")) 
                {
                    code += "mv.visitJumpInsn(Opcodes.IF_ICMPLE, elseblock);\n";
                }
                else if(comp.equals("<")) 
                {
                    code += "mv.visitJumpInsn(Opcodes.IF_ICMPGE, elseblock);\n";
                }
                else if(comp.equals("=")) 
                {
                    code += "mv.visitJumpInsn(Opcodes.IF_ICMPNE, elseblock);\n";
                }
                else {
                    code += "mv.visitJumpInsn(Opcodes.IF_ICMPEQ, elseblock);\n";
                }
            }
            //IF
            int i=5;
            while(!childName.equals("ELSE")) 
            {
                code += visitStat((StatContext)ctx.getParent().getChild(i));
                i++;
                childName = ctx.getParent().getChild(i).getText();
            }
            //ELSE
            code += "mv.visitLabel(elseblock);\n";
            for(int j=i+1; j<ctx.getParent().getChildCount()-1; j++) 
            {
                code += visitStat((StatContext)ctx.getParent().getChild(j));
            }
            code += "mv.visitLabel(endIf);\n";
        }
        else if(ctx.getParent().getChild(1).getText().equals("0") || ctx.getParent().getChild(3).getText().equals("0") ){
            if(comp.equals(">")) 
            {
                code += "mv.visitJumpInsn(Opcodes.IFLE,"+label+");\n";
            }
            else if(comp.equals("<")) 
            {
                code += "mv.visitJumpInsn(Opcodes.IFGE,"+label+");\n";
            }
            else if(comp.equals("=")) 
            {
                code += "mv.visitJumpInsn(Opcodes.IFNE,"+label+");\n";
            }
            else 
            {
                code += "mv.visitJumpInsn(Opcodes.IFEQ,"+label+");\n";
            }    
            for(int i=5; i<ctx.getParent().getChildCount()-1; i++) 
            {
                code += visitStat((StatContext) ctx.getParent().getChild(i));
            }
            if(label.equals("endLoop"));
                code += "mv.visitJumpInsn(Opcodes.GOTO, loop);\n";
            code += "mv.visitLabel("+label+");\n";
        }
        else 
        {
            if(comp.equals(">")) 
            {
                code += "mv.visitJumpInsn(Opcodes.IF_ICMPLE, "+label+");\n";
            }
            else if(comp.equals("<")) 
            {
                code += "mv.visitJumpInsn(Opcodes.IF_ICMPGE,"+label+");\n";
            }
            else if(comp.equals("=")) 
            {
                code += "mv.visitJumpInsn(Opcodes.IF_ICMPNE, "+label+");\n";
            }
            else 
            {
                code += "mv.visitJumpInsn(Opcodes.IF_ICMPEQ,"+label+");\n";
            }
            for(int i=5; i<ctx.getParent().getChildCount()-1; i++) 
            {
                code += visitStat((StatContext) ctx.getParent().getChild(i));
            }
            if(label.equals("endLoop"));
                code += "mv.visitJumpInsn(Opcodes.GOTO, loop);\n";
            code += "mv.visitLabel("+label+");\n";
        }
        return code;
    }//visitComp

    /**
     * 
     * visitor for identifier
     */
    @Override 
    public String visitIdentifier(KnightCodeParser.IdentifierContext ctx) 
    { 
        return ctx.ID().getText();
    }

}//end kccVisitor