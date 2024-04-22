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
    private int myInt;
    private String myString;

    /**
     * constructor for integer argument
     * @param value this value is an integer
     */
    public variable(int value) 
    {
        myInt = value;
        myString = null;
    }//end int constructor

    /**
     * comstructor for string argument
     * @param str this value is a string
     */
    public variable(String str) 
    {
        myString = str;
    }//end String constructor

    /**
     * determines if our value is a string or an integer
     * @return returns true if string, false if not
     */
    public boolean isString() 
    {
        if(myString != null)
            return true;
        else
            return false;
    }//end isString

    /**
     * getter for string
     * @return our string
     */
    public String getString()//string getter
    {
        return myString;
    }//end getString

    /**
     * getter for int
     * @return our int
     */
    public int getInt()//int getter
    {
        return myInt;
    }//end getInt

    public void clear()//resets our value
    {
        myInt = 0;
        myString = "";
    }//end clear

    public void setString(String str)//string setter
    {
        myString = str;
    }//end setString

    public void setInt(int num)//int setter 
    {
        this.myInt = num;
    }//end setInt
}//end Variable
