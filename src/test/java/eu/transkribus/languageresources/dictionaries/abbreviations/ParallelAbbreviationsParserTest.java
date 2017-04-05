/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.dictionaries.abbreviations;

import eu.transkribus.languageresources.dictionaries.abbreviations.ParallelAbbreviationsParser;
import eu.transkribus.interfaces.IDictionary;
import eu.transkribus.languageresources.dictionaries.DictionaryUtils;
import java.io.File;
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
public class ParallelAbbreviationsParserTest
{

    private final String pathToAbbreviated;
    private final String pathToExpanded;
    
    private final String itineria_pathToAbbreviated;
    private final String itineria_pathToExpanded;

    public ParallelAbbreviationsParserTest()
    {
        ClassLoader classLoader = getClass().getClassLoader();
        pathToAbbreviated = new File(classLoader.getResource("abbr/abbr.txt").getFile()).getAbsolutePath();
        pathToExpanded = new File(classLoader.getResource("abbr/exp.txt").getFile()).getAbsolutePath();
        
        itineria_pathToAbbreviated = new File(classLoader.getResource("itineria/train_input.txt").getFile()).getAbsolutePath();
        itineria_pathToExpanded = new File(classLoader.getResource("itineria/train_target.txt").getFile()).getAbsolutePath();
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
     * Test of createDictionary method, of class ParallelAbbreviationsParser.
     */
    @Test
    public void testCreateDictionary() throws Exception
    {
        IDictionary dict = ParallelAbbreviationsParser.createDictionary(pathToAbbreviated, pathToExpanded);
        
        assertEquals(8, dict.getEntries().size());
        
        assertEquals(2, dict.getEntry("I").getFrequency());
        assertEquals(1, dict.getEntry("I").getValues().size());
        assertEquals(2, (int)dict.getEntry("I").getValues().get("I"));
        
        assertEquals(2, dict.getEntry("m").getFrequency());
        assertEquals(2, dict.getEntry("m").getValues().size());
        assertEquals(1, (int)dict.getEntry("m").getValues().get("m"));
        assertEquals(1, (int)dict.getEntry("m").getValues().get("am"));
        
        assertEquals(1, dict.getEntry("a").getFrequency());
        assertEquals(1, dict.getEntry("a").getValues().size());
        assertEquals(1, (int)dict.getEntry("a").getValues().get("a"));
        
        assertEquals(1, dict.getEntry("sh").getFrequency());
        assertEquals(1, dict.getEntry("sh").getValues().size());
        assertEquals(1, (int)dict.getEntry("sh").getValues().get("short"));
        
        assertEquals(1, dict.getEntry("test").getFrequency());
        assertEquals(1, dict.getEntry("test").getValues().size());
        assertEquals(1, (int)dict.getEntry("test").getValues().get("test"));
        
        assertEquals(1, dict.getEntry("sent").getFrequency());
        assertEquals(1, dict.getEntry("sent").getValues().size());
        assertEquals(1, (int)dict.getEntry("sent").getValues().get("sentence"));
        
        assertEquals(1, dict.getEntry("num").getFrequency());
        assertEquals(1, dict.getEntry("num").getValues().size());
        assertEquals(1, (int)dict.getEntry("num").getValues().get("number"));
        
        assertEquals(1, dict.getEntry("two").getFrequency());
        assertEquals(1, dict.getEntry("two").getValues().size());
        assertEquals(1, (int)dict.getEntry("two").getValues().get("two"));
    }
    
    @Test
    public void testCreateItineriaDictionary() throws Exception
    {
        IDictionary dict = ParallelAbbreviationsParser.createDictionary(itineria_pathToAbbreviated, itineria_pathToExpanded, true);
        DictionaryUtils.save("itineria/abbr_dict", dict);
    }
}
