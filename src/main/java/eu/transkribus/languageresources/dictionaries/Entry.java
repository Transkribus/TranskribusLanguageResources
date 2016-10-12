/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.dictionaries;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author max
 */
public class Entry
{

    private EntryValue keyEntry;
    private List<EntryValue> additionalValues;
    
    public Entry(EntryValue keyEntry)
    {
        this.keyEntry = keyEntry;
        this.additionalValues = new LinkedList<>();
    }


    public Entry(String name)
    {
        this.keyEntry = new EntryValue(name);
        this.additionalValues = new LinkedList<>();
    }

    public List<EntryValue> getAdditionalValues()
    {
        return additionalValues;
    }

    public EntryValue getKeyEntry()
    {
        return keyEntry;
    }

    public void addAdditionalValue(String name)
    {
        for (EntryValue entry : additionalValues)
        {
            if (entry.getName().equals(name))
            {
                entry.increaseFrequency();
                return;
            }
        }

        EntryValue entry = new EntryValue(name);
        additionalValues.add(entry);
    }
    
    public void addAdditionalValue(EntryValue entryValue)
    {
        for (EntryValue entry : additionalValues)
        {
            if (entry.getName().equals(entryValue.getName()))
            {
                entry.increaseFrequency();
                return;
            }
        }

        additionalValues.add(entryValue);
    }

    public boolean containsAdditionalEntry(String name)
    {
        for (EntryValue e : additionalValues)
        {
            if (e.getName().equals(name))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.keyEntry);
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
        final Entry other = (Entry) obj;
        if (!Objects.equals(this.keyEntry, other.keyEntry))
        {
            return false;
        }
        return true;
    }
}
