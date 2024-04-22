package compiler;
/**
 * Used for storing a integer or a string
 * @author Sean Hand
 * @version 1.3
 * Assignment 5
 * CS322 - Compiler Construction
 * Spring 2024
 */
public class variable 
{
    private int num;
    private String word;

    /**
     * constructor for integer argument
     * @param value this value is an integer
     */
    public variable(int value) 
    {
        num = value;
        word = null;
    }//end int constructor

    /**
     * comstructor for string argument
     * @param str this value is a string
     */
    public variable(String str) 
    {
        word = str;
    }//end String constructor

    /**
     * determines if our value is a string or an integer
     * @return returns true if string, false if not
     */
    public boolean isString() 
    {
        if(word != null)
            return true;
        else
            return false;
    }//end isString

    /**
     * getter for string
     * @return our string
     */
    public String getString() 
    {
        return word;
    }//end getString

    /**
     * getter for int
     * @return our int
     */
    public int getInt() 
    {
        return num;
    }//end getInt

    public void clear()//resets our value
    {
        num = 0;
        word = "";
    }//end clear

    public void setString(String str) 
    {
        word = str;
    }//end setString

    public void setInt(int num) 
    {
        this.num = num;
    }//end setInt
}//end Variable