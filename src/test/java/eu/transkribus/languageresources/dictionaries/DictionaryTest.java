/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.dictionaries;

import java.io.File;
import java.io.IOException;
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
        dictionary.addAdditionalValue("abk", "abkürzung");
        
        assertEquals(1, dictionary.getEntries().size());
        assertEquals(true, dictionary.containsKeyEntry("abk"));
        assertEquals(false, dictionary.containsKeyEntry("abkürzung"));
        assertEquals(1, dictionary.getEntryByKeyName("abk").getAdditionalValues().size());
        assertEquals("abkürzung", dictionary.getEntryByKeyName("abk").getAdditionalValues().get("abkürzung").getName());
        assertEquals(1, dictionary.getEntryByKeyName("abk").getAdditionalValues().get("abkürzung").getFrequency());
        
        dictionary.addAdditionalValue("abk", "abkürzung");
        
        assertEquals(1, dictionary.getEntries().size());
        assertEquals(true, dictionary.containsKeyEntry("abk"));
        assertEquals(false, dictionary.containsKeyEntry("abkürzung"));
        assertEquals(1, dictionary.getEntryByKeyName("abk").getAdditionalValues().size());
        assertEquals("abkürzung", dictionary.getEntryByKeyName("abk").getAdditionalValues().get("abkürzung").getName());
        assertEquals(2, dictionary.getEntryByKeyName("abk").getAdditionalValues().get("abkürzung").getFrequency());
        
        dictionary.addAdditionalValue("abk", "Abkürzung");
        
        assertEquals(1, dictionary.getEntries().size());
        assertEquals(true, dictionary.containsKeyEntry("abk"));
        assertEquals(false, dictionary.containsKeyEntry("abkürzung"));
        assertEquals(2, dictionary.getEntryByKeyName("abk").getAdditionalValues().size());
        assertEquals("abkürzung", dictionary.getEntryByKeyName("abk").getAdditionalValues().get("abkürzung").getName());
        assertEquals("Abkürzung", dictionary.getEntryByKeyName("abk").getAdditionalValues().get("Abkürzung").getName());
        assertEquals(2, dictionary.getEntryByKeyName("abk").getAdditionalValues().get("abkürzung").getFrequency());
        assertEquals(1, dictionary.getEntryByKeyName("abk").getAdditionalValues().get("Abkürzung").getFrequency());
    }
    
    @Test
    public void testWriteReadWithFrequencies()
    {
        try
        {
            File tmpFile = File.createTempFile("dict_freq", "txt");
            tmpFile.deleteOnExit();
            
            Dictionary dictionary1 = new Dictionary();
            dictionary1.addEntry("abk");
            dictionary1.addAdditionalValue("abk", "abkürzung");
            
            DictionaryWriter.writeDictionray(dictionary1, tmpFile, true);
            
            Dictionary dictionary2 = DictionaryReader.readDictionary(tmpFile);
            
            assertEquals(1, dictionary2.getEntries().size());
            assertEquals(true, dictionary2.containsKeyEntry("abk"));
            assertEquals(false, dictionary2.containsKeyEntry("abkürzung"));
            assertEquals(1, dictionary2.getEntryByKeyName("abk").getAdditionalValues().size());
            assertEquals("abkürzung", dictionary2.getEntryByKeyName("abk").getAdditionalValues().get("abkürzung").getName());
            assertEquals(1, dictionary2.getEntryByKeyName("abk").getAdditionalValues().get("abkürzung").getFrequency());
        } catch (IOException ex)
        {
            Logger.getLogger(DictionaryTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void testWriteReadWithoutFrequencies()
    {
        try
        {
            File tmpFile = File.createTempFile("dict_no_freq", "txt");
            tmpFile.deleteOnExit();
            
            Dictionary dictionary1 = new Dictionary();
            dictionary1.addEntry("abk");
            dictionary1.addAdditionalValue("abk", "abkürzung");
            
            DictionaryWriter.writeDictionray(dictionary1, tmpFile, false);
            
            Dictionary dictionary2 = DictionaryReader.readDictionary(tmpFile);
            
            assertEquals(1, dictionary2.getEntries().size());
            assertEquals(true, dictionary2.containsKeyEntry("abk"));
            assertEquals(false, dictionary2.containsKeyEntry("abkürzung"));
            assertEquals(1, dictionary2.getEntryByKeyName("abk").getAdditionalValues().size());
            assertEquals("abkürzung", dictionary2.getEntryByKeyName("abk").getAdditionalValues().get("abkürzung").getName());
            assertEquals(1, dictionary2.getEntryByKeyName("abk").getAdditionalValues().get("abkürzung").getFrequency());
        } catch (IOException ex)
        {
            Logger.getLogger(DictionaryTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
