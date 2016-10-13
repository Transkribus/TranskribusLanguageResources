/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.dictionaries;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author max
 */
public class Dictionary
{

    private final Map<String, Entry> entries;

    public Dictionary()
    {
        entries = new HashMap<>();
    }

    public Dictionary(List<String> tokenizedText)
    {
        this();

        for (String token : tokenizedText)
        {
            addEntry(token);
        }
    }

    public void addEntry(String name)
    {
        if (entries.containsKey(name))
        {
            entries.get(name).getKeyEntry().increaseFrequency();
            return;
        }

        entries.put(name, new Entry(name));
    }

    public void addAdditionalValue(String key, String additionalName)
    {
        if (entries.containsKey(key))
        {
            entries.get(key).addAdditionalValue(additionalName);
            return;
        }

        Entry e = new Entry(key);
        e.addAdditionalValue(additionalName);
        entries.put(key, e);
    }

    public Collection<Entry> getEntries()
    {
        return entries.values();
    }

    public boolean containsKeyEntry(String name)
    {
        return entries.containsKey(name);
    }

    public Entry getEntryByKeyName(String name)
    {
        if(entries.containsKey(name))
            return entries.get(name);

        throw new RuntimeException("Could not find entry with given key name: " + name);
    }

    public void addEntry(Entry entry)
    {
        this.entries.put(entry.getKeyEntry().getName(), entry);
    }

    public double outOfVocabulary(Dictionary smallerDictionary)
    {
        int countTotal = entries.size();
        int countFound = 0;

        for (String externalKey : smallerDictionary.entries.keySet())
        {
            if (entries.containsKey(externalKey))
            {
                countFound++;
            }
        }

        return (double) (countTotal - countFound) / (double) countTotal;
    }
}
