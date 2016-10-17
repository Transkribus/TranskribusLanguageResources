/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.dictionaries;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author max
 */
public class Entry
{

    private EntryValue keyEntry;
    private Map<String, EntryValue> additionalValues;

    public Entry(EntryValue keyEntry)
    {
        this.keyEntry = keyEntry;
        this.additionalValues = new HashMap<>();
    }

    public Entry(String name)
    {
        this.keyEntry = new EntryValue(name);
        this.additionalValues = new HashMap<>();
    }

    public Map<String, EntryValue> getAdditionalValues()
    {
        return additionalValues;
    }

    public EntryValue getKeyEntry()
    {
        return keyEntry;
    }

    public void addAdditionalValue(String name)
    {
        if (additionalValues.containsKey(name))
        {
            additionalValues.get(name).increaseFrequency();
        } else
        {
            EntryValue entry = new EntryValue(name);
            additionalValues.put(name, entry);
        }
    }

    public void addAdditionalValue(EntryValue entryValue)
    {
        if (additionalValues.containsKey(entryValue.getName()))
        {
            additionalValues.get(entryValue.getName()).increaseFrequency();
        } else
        {
            additionalValues.put(entryValue.getName(), entryValue);
        }
    }

    public boolean containsAdditionalEntry(String name)
    {
        return additionalValues.containsKey(name);
    }
}
