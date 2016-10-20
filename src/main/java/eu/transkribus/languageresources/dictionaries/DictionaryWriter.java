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

    public static void writeDictionray(Dictionary dictionary, String path, boolean withFrequencies, boolean withUnicodeRepresentation, boolean withUnicodeName)
    {
        writeDictionray(dictionary, new File(path), withFrequencies, withUnicodeRepresentation, withUnicodeName);
    }

    public static void writeDictionray(Dictionary dictionary, File file, boolean withFrequencies, boolean withUnicodeRepresentation, boolean withUnicodeName)
    {
        try
        {
            PrintWriter printWriter = new PrintWriter(file);
            writeEntries(printWriter, dictionary.getEntries(), withFrequencies, withUnicodeRepresentation, withUnicodeName);
            printWriter.flush();
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(DictionaryWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void writeEntries(PrintWriter printWriter, Collection<Entry> entries, boolean withFrequencies, boolean withUnicodeRepresentation, boolean withUnicodeName)
    {
        for (Entry entry : entries)
        {
            writeSingleEntry(printWriter, entry, withFrequencies, withUnicodeRepresentation, withUnicodeName);
        }
    }

    private static void writeSingleEntry(PrintWriter printWriter, Entry entry, boolean withFrequencies, boolean withUnicodeRepresentation, boolean withUnicodeName)
    {
        writeEntryValue(printWriter, entry.getKeyEntry(), withFrequencies, withUnicodeRepresentation, withUnicodeName);

        for (EntryValue additionalValue : entry.getAdditionalValues().values())
        {
            printWriter.append("\t");
            writeEntryValue(printWriter, additionalValue, withFrequencies, withUnicodeRepresentation, withUnicodeName);
        }

        printWriter.append("\n");
    }

    private static void writeEntryValue(PrintWriter printWriter, EntryValue entryValue, boolean withFrequencies, boolean withUnicodeRepresentation, boolean withUnicodeName)
    {
        printWriter.append(entryValue.getName());

        if (withFrequencies)
        {
            printWriter.append("|");
            printWriter.append(Integer.toString(entryValue.getFrequency()));
        }

        if (withUnicodeRepresentation)
        {
            if (entryValue.getName().length() > 1)
            {
                throw new RuntimeException("Printin of unicode representation is only possible with string length == 1!");
            }

            printWriter.append("|");
            printWriter.append("\\u" + Integer.toHexString(entryValue.getName().charAt(0) | 0x10000).substring(1));
        }
        
        if (withUnicodeName)
        {
            if (entryValue.getName().length() > 1)
            {
                throw new RuntimeException("Printin of unicode name is only possible with string length == 1!");
            }

            printWriter.append("|");
            printWriter.append(Character.getName(entryValue.getName().charAt(0)));
        }
    }
}
