/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.dictionaries;

import java.util.Objects;

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
        this.frequency = 1;
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

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final EntryValue other = (EntryValue) obj;
        if (!Objects.equals(this.name, other.name))
        {
            return false;
        }
        return true;
    }
}
