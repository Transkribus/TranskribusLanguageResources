/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.dictionaries;

import de.unileipzig.asv.neuralnetwork.utils.Utils;
import eu.transkribus.interfaces.IDictionary;
import eu.transkribus.languageresources.tokenizer.ConfigTokenizer;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author max
 */
public class DictionaryTest
{

    public DictionaryTest()
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

    @After
    public void tearDown()
    {
    }

    /**
     * Test of addEntry method, of class Dictionary.
     */
    @Test
    public void testBasic()
    {
        Dictionary dictionary = new Dictionary();
        dictionary.addEntry("abk");
        dictionary.addValue("abk", "abkürzung");

        assertEquals(1, dictionary.getEntries().size());
        assertTrue(dictionary.containsKey("abk"));
        assertFalse(dictionary.containsKey("abkürzung"));
        assertEquals(1, dictionary.getEntry("abk").getValues().size());
        assertTrue(dictionary.getEntry("abk").containsValue("abkürzung"));
        assertEquals(1, (int) dictionary.getEntry("abk").getValues().get("abkürzung"));

        dictionary.addValue("abk", "abkürzung");

        assertEquals(1, dictionary.getEntries().size());
        assertTrue(dictionary.containsKey("abk"));
        assertFalse(dictionary.containsKey("abkürzung"));
        assertEquals(1, dictionary.getEntry("abk").getValues().size());
        assertTrue(dictionary.getEntry("abk").containsValue("abkürzung"));
        assertEquals(2, (int) dictionary.getEntry("abk").getValues().get("abkürzung"));

        dictionary.addValue("abk", "Abkürzung");

        assertEquals(1, dictionary.getEntries().size());
        assertTrue(dictionary.containsKey("abk"));
        assertFalse(dictionary.containsKey("abkürzung"));
        assertEquals(2, dictionary.getEntry("abk").getValues().size());
        assertTrue(dictionary.getEntry("abk").containsValue("abkürzung"));
        assertTrue(dictionary.getEntry("abk").containsValue("Abkürzung"));
        assertEquals(2, (int) dictionary.getEntry("abk").getValues().get("abkürzung"));
        assertEquals(1, (int) dictionary.getEntry("abk").getValues().get("Abkürzung"));
    }

    @Test
    public void testWriteReadWithFrequencies()
    {
        /*try
        {
            File tmpFile = File.createTempFile("dict_freq", "txt");
            tmpFile.deleteOnExit();

            Dictionary dictionary1 = new Dictionary();
            dictionary1.addEntry("abk");
            dictionary1.addValue("abk", "abkürzung");

            DictionaryWriter.writeDictionray(dictionary1, tmpFile, true, false, false);

            Dictionary dictionary2 = DictionaryReader.readDictionary(tmpFile);
            assertEquals(dictionary1, dictionary2);
        } catch (IOException ex)
        {
            Logger.getLogger(DictionaryTest.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }

    /*@Test
    public void testWriteReadWithoutFrequencies()
    {
        try
        {
            File tmpFile = File.createTempFile("dict_no_freq", "txt");
            tmpFile.deleteOnExit();

            Dictionary dictionary1 = new Dictionary();
            dictionary1.addEntry("abk");
            dictionary1.addValue("abk", "abkürzung");

            DictionaryWriter.writeDictionray(dictionary1, tmpFile, false, false, false);

            Dictionary dictionary2 = DictionaryReader.readDictionary(tmpFile);

            assertEquals(1, dictionary2.getEntries().size());
            assertEquals(true, dictionary2.containsKey("abk"));
            assertEquals(false, dictionary2.containsKey("abkürzung"));
            assertEquals(1, dictionary2.getEntry("abk").getValues().size());
            assertEquals("abkürzung", dictionary2.getEntry("abk").getValues().get("abkürzung").getName());
            assertEquals(1, dictionary2.getEntry("abk").getValues().get("abkürzung").getFrequency());
        } catch (IOException ex)
        {
            Logger.getLogger(DictionaryTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void createCharacterDictionary()
    {
        try
        {
            File tmpFile = File.createTempFile("dict_character_freq", "txt");
            tmpFile.deleteOnExit();

            String text = "Hello! test, test, test...";

            Properties tokenizerProperties = new Properties();
            tokenizerProperties.setProperty("delimiter_signs", "!., ");
            tokenizerProperties.setProperty("tokenize_character_wise", "true");
            ConfigTokenizer tokenizer = new ConfigTokenizer(tokenizerProperties);

            List<String> tokenizedText = tokenizer.tokenize(text);

            Dictionary characterFrequencyDictionary = new Dictionary(tokenizedText);
            DictionaryWriter.writeDictionray(characterFrequencyDictionary, tmpFile, false, false, false);

            Dictionary readDictionary = DictionaryReader.readDictionary(tmpFile);

            assertEquals(6, readDictionary.getEntries().size());
        } catch (IOException ex)
        {
            Logger.getLogger(DictionaryTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/

    @Test
    public void createExtendedCharacterDictionary()
    {
        try
        {
            File tmpFile = File.createTempFile("dict_character_freq", "txt");
            tmpFile.deleteOnExit();

            String text = "Hi!";

            Properties tokenizerProperties = new Properties();
            tokenizerProperties.setProperty("delimiter_signs", "!., ");
            tokenizerProperties.setProperty("tokenize_character_wise", "true");
            ConfigTokenizer tokenizer = new ConfigTokenizer(tokenizerProperties);

            List<String> tokenizedText = tokenizer.tokenize(text);

            Dictionary characterFrequencyDictionary = new Dictionary(tokenizedText);
            /*DictionaryWriter.writeDictionray(characterFrequencyDictionary, tmpFile, false, true, true);

            String[] loadFileLineByLine = Utils.loadFileLineByLine(tmpFile);
            String expected = "[H|\\u0048|LATIN CAPITAL LETTER H, i|\\u0069|LATIN SMALL LETTER I]";
            assertEquals(expected, Arrays.toString(loadFileLineByLine));*/

        } catch (IOException ex)
        {
            Logger.getLogger(DictionaryTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
