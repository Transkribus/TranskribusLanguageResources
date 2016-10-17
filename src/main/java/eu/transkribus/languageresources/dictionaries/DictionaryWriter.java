/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.dictionaries;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author max
 */
public class DictionaryWriter
{
    public static void writeDictionray(Dictionary dictionary, String path, boolean withFrequencies)
    {
        writeDictionray(dictionary, new File(path), withFrequencies);
    }
    
    public static void writeDictionray(Dictionary dictionary, File file, boolean withFrequencies)
    {
        try
        {
            PrintWriter printWriter = new PrintWriter(file);
            writeEntries(printWriter, dictionary.getEntries(), withFrequencies);
            printWriter.flush();
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(DictionaryWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void writeEntries(PrintWriter printWriter, Collection<Entry> entries, boolean withFrequencies)
    {
        for(Entry entry : entries)
        {
            writeSingleEntry(printWriter, entry, withFrequencies);
        }
    }
    
    private static void writeSingleEntry(PrintWriter printWriter, Entry entry, boolean withFrequencies)
    {
        writeEntryValue(printWriter, entry.getKeyEntry(), withFrequencies);
        
        for(EntryValue additionalValue : entry.getAdditionalValues().values())
        {
            writeEntryValue(printWriter, additionalValue, withFrequencies);
        }
        
        printWriter.append("\n");
    }
    
    private static void writeEntryValue(PrintWriter printWriter, EntryValue entryValue, boolean withFrequencies)
    {
        printWriter.append(entryValue.getName());
        
        if(withFrequencies)
        {
            printWriter.append("|");
            printWriter.append(Integer.toString(entryValue.getFrequency()));
        }
        
        printWriter.append("\t");
    }
}
