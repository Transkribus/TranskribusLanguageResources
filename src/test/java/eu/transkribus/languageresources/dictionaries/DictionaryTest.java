/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.dictionaries;

import eu.transkribus.interfaces.IDictionary;
import eu.transkribus.languageresources.exceptions.ARPAParseException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author max, jnphilipp
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
    public void testWriteReadWithFrequencies() throws ARPAParseException, FileNotFoundException, IOException {
        Dictionary dictionary1 = new Dictionary();
        dictionary1.setName("TestDictionary");
        dictionary1.setDescription("Lorem ipsum dolrem sid amet...");
        dictionary1.addEntry("abk");
        dictionary1.addEntry("abk");
        dictionary1.addValue("abk", "abkürzung");
        dictionary1.addEntry("Das");
        dictionary1.addEntry("Auto");
        dictionary1.addValue("Auto", "Au");
        dictionary1.addEntry("ist");
        dictionary1.addEntry("grün");

        ClassLoader classLoader = getClass().getClassLoader();
        String dictionaryPath = new File(classLoader.getResource(".").getFile()).getAbsolutePath() + "/test-dictionary";

        DictionaryUtils.save(dictionaryPath, dictionary1);
        assertTrue(new File(dictionaryPath).exists());
        assertTrue(new File(dictionaryPath).isDirectory());
        assertTrue(new File(dictionaryPath + "/metadata.properties").exists());
        assertTrue(new File(dictionaryPath + "/entries.arpa").exists());
        assertTrue(new File(dictionaryPath + "/entries.dict").exists());
        assertTrue(new File(dictionaryPath + "/entry-character-table.arpa").exists());
        assertTrue(new File(dictionaryPath + "/entry-character-table.csv").exists());
        assertTrue(new File(dictionaryPath + "/value-character-table.arpa").exists());
        assertTrue(new File(dictionaryPath + "/value-character-table.csv").exists());

        IDictionary dictionary2 = DictionaryUtils.load(dictionaryPath);
        assertEquals(dictionary1.getEntries(), dictionary2.getEntries());
        assertEquals(dictionary1.getEntryCharacterTable(), dictionary2.getEntryCharacterTable());
        assertEquals(dictionary1.getValueCharacterTable(), dictionary2.getValueCharacterTable());
        assertEquals(dictionary1.getNumberTokens(), dictionary2.getNumberTokens());
        assertEquals(dictionary1.getNumberTypes(), dictionary2.getNumberTypes());
        assertEquals(dictionary1.getName(), dictionary2.getName());
        assertEquals(dictionary1.getDescription(), dictionary2.getDescription());
        assertEquals(dictionary1.getLanguage(), dictionary2.getLanguage());
        assertEquals(dictionary1.getCreationDate(), dictionary2.getCreationDate());
        assertEquals(dictionary1, dictionary2);
    }
}
