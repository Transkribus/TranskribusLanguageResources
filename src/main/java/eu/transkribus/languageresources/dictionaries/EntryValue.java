/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.dictionaries;

/**
 *
 * @author max
 */
public class EntryValue
{
    private String name;
    private int frequency;
    
    public EntryValue(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getFrequency()
    {
        return frequency;
    }

    public void increaseFrequency()
    {
        this.frequency++;
    }

    public void setFrequency(int frequency)
    {
        this.frequency = frequency;
    }
}
