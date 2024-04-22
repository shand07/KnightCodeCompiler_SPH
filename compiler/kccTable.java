package compiler;
/**
 * Our symbol table for kcc.java using a hashmap
 * @author Sean Hand
 * @version 1.1
 * Assignment 5
 * CS322 - Compiler Construction
 * Spring 2024
 */
import java.util.HashMap;
public class kccTable 
{
    private HashMap<String, variable> symbolTable;

    /**
     * Prefered constructor for initialization
     */
    public kccTable() 
    {
        symbolTable = new HashMap<String, variable>();
    }//end constructor

    /**
     * used for adding a new entry
     * @param key used to store variable name
     * @param value stored int or string
     */
    public void addEntry(String key, variable value) 
    {
        symbolTable.put(key, value);
    }//end addEntry

    /**
     * looks through hashmap for a key
     * @param key variable name stored in hashmap
     * @return int or string value for respective key
     */
    public variable getValue(String key) 
    {
        return symbolTable.get(key);
    }//end getValue

    public variable remove(String key) 
    {
        return symbolTable.remove(key);
    }//end remove

    /**
     * lets us remove key pair from hashmap
     * @param key removed variable name
     * @param value removed variable value
     */
    public void removePair(String key, variable value) 
    {
        symbolTable.remove(key, value);
    }//end removePair

    /**
     * clears hashmap
     */
    public void clearTable() 
    {
        symbolTable.clear();
    }//end ClearTable

    /**
     * prints hashmap key pairs
     */
    public void print() 
    {
        System.out.println("Our table contains: ");
        for(HashMap.Entry<String, variable> e : symbolTable.entrySet()) 
        {
            String key = e.getKey();
            variable val = e.getValue();
            if(val.isString())
            {
                System.out.println(key + "\t" + val.getString());
            }
            else 
            {
                System.out.println(key + "\t" + val.getInt());
            }
        }
    }//end print
}//end SymbolTable