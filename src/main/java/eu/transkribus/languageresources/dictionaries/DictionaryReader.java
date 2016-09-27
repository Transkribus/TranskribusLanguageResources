/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.dictionaries;

import de.unileipzig.asv.neuralnetwork.utils.Utils;
import java.io.File;

/**
 *
 * @author max
 */
public class DictionaryReader
{
    public static Dictionary readDictionary(String path)
    {
        return readDictionary(new File(path));
    }

    public static Dictionary readDictionary(File file)
    {

        String[] lines = Utils.loadFileLineByLine(file);
        Dictionary dictionary = new Dictionary();
        
        for(String line : lines)
        {
            parseLine(dictionary, line);
        }
        
        return dictionary;
    }

    private static void parseLine(Dictionary dictionary, String line)
    {
        String[] parts = line.split("\t");
        
        Entry entry = new Entry(getEntryValue(parts[0]));
        dictionary.addEntry(entry);
        
        if(parts.length > 1)
        {
            for(int i = 1; i < parts.length; i++)
            {
                entry.addAdditionalValue(getEntryValue(parts[i]));
            }
        }
    }
    
    private static EntryValue getEntryValue(String part)
    {
        String[] parts = part.split("\\|");
        String name = parts[0];
        
        EntryValue v = new EntryValue(name);
        
        if(parts.length == 2)
        {
            v.setFrequency(new Integer(parts[1]));
        }
        
        return v;
    }
}
