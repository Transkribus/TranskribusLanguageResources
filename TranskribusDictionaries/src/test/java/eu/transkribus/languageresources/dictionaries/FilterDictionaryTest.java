/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.dictionaries;

import eu.transkribus.interfaces.IDictionary;
import eu.transkribus.interfaces.IEntry;
import eu.transkribus.languageresources.util.SimpleDictFileHandler;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author max
 */
public class FilterDictionaryTest
{

    public FilterDictionaryTest()
    {
    }

    @BeforeClass
    public static void setUpClass()
    {
    }

    @AfterClass
    public static void tearDownClass()
    {
    }

    @Before
    public void setUp()
    {
    }

    @Test
    public void filterTest()
    {
        Dictionary latinDictionary = new Dictionary();
        latinDictionary.addEntry("latinWord1", 10);
        latinDictionary.addEntry("latinWord2", 7);
        latinDictionary.addEntry("latinWord3", 6);
        latinDictionary.addEntry("englishWord1", 2);
        latinDictionary.addEntry("englishWord2", 3);

        Dictionary englishDictionary = new Dictionary();
        englishDictionary.addEntry("latinWord1", 1);
        englishDictionary.addEntry("latinWord2", 2);
        englishDictionary.addEntry("englishWord1", 10);

        IDictionary oddDictionary = DictionaryUtils.getOddTypes(latinDictionary, englishDictionary);
        assertTrue(oddDictionary.containsKey("englishWord1"));
        assertEquals(1, oddDictionary.getNumberTypes());

        IDictionary cleanedLatinDictionary = DictionaryUtils.subtractDictionary(latinDictionary, oddDictionary);
        assertEquals(4, cleanedLatinDictionary.getNumberTypes());

        IDictionary englishInLatinDictionary = DictionaryUtils.subtractDictionary(latinDictionary, cleanedLatinDictionary);
        assertEquals(1, englishInLatinDictionary.getNumberTypes());
    }

    @Test
    public void filterCrawledLatin() throws IOException
    {
        CharsetEncoder asciiEncoder = Charset.forName("US-ASCII").newEncoder();

        ClassLoader classLoader = getClass().getClassLoader();
        String pathEng = new File(classLoader.getResource("stazh/eng.dict").getFile()).getAbsolutePath();
        String pathLatin = new File(classLoader.getResource("stazh/latin.dict").getFile()).getAbsolutePath();
        String pathLatin2 = new File(classLoader.getResource("stazh/latin2.dict").getFile()).getAbsolutePath();
        File pathRemovedEnglish = new File("src/test/resources/stazh/removed_english.dict");
        File pathCleanedLatin = new File("src/test/resources/stazh/cleaned_latin.dict");
        File pathCharsetCleanedLatin = new File("src/test/resources/stazh/charset_cleaned_latin.dict");

        Dictionary englishDictionary = SimpleDictFileHandler.readAsDictionary(pathEng);

        Dictionary latinDictionary = SimpleDictFileHandler.readAsDictionary(pathLatin);
        Dictionary latinDictionary2 = SimpleDictFileHandler.readAsDictionary(pathLatin2);

        latinDictionary.merge(latinDictionary2);

        IDictionary oddDictionary = DictionaryUtils.getOddTypes(latinDictionary, englishDictionary);
        IDictionary cleanedLatinDictionary = DictionaryUtils.subtractDictionary(latinDictionary, oddDictionary);

        SimpleDictFileHandler.write(pathCleanedLatin, cleanedLatinDictionary.getEntries());
        SimpleDictFileHandler.write(pathRemovedEnglish, oddDictionary.getEntries());

        Dictionary charsetCleanedLatinDictionary = new Dictionary();
        for (IEntry entry : latinDictionary.getEntries())
        {
            if (asciiEncoder.canEncode(entry.getKey()))
            {
                charsetCleanedLatinDictionary.addEntry(entry);
            }
        }
        SimpleDictFileHandler.write(pathCharsetCleanedLatin, charsetCleanedLatinDictionary.getEntries());
    }
}
