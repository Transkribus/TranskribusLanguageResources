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
import java.net.URISyntaxException;
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
public class ParallelAbbrevationsDict
{

    private final String pathToAbbreviated;
    private final String pathToExpanded;
    private final String dictionaryFolder;

    public ParallelAbbrevationsDict()
    {
        ClassLoader classLoader = getClass().getClassLoader();
        pathToAbbreviated = new File(classLoader.getResource("itineria/train_input.txt").getFile()).getAbsolutePath();
        pathToExpanded = new File(classLoader.getResource("itineria/train_target.txt").getFile()).getAbsolutePath();
        dictionaryFolder = new File(classLoader.getResource("itineria/").getFile()).getAbsolutePath();
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

     @Test
     public void dictionaryFromParallelAbbreviations() throws ARPAParseException, FileNotFoundException, IOException, URISyntaxException
     {
        IDictionary dictionary = ParallelAbbreviationsParser.createDictionary(pathToAbbreviated, pathToExpanded);
        
        DictionaryUtils.save(dictionaryFolder, dictionary);
        Dictionary dictionary2 = (Dictionary) DictionaryUtils.load(dictionaryFolder);
        assertEquals(dictionary.getEntries(), dictionary2.getEntries());
        assertEquals(dictionary.getEntryCharacterTable(), dictionary2.getEntryCharacterTable());
        assertEquals(dictionary.getValueCharacterTable(), dictionary2.getValueCharacterTable());
        assertEquals(dictionary.getNumberTokens(), dictionary2.getNumberTokens());
        assertEquals(dictionary.getNumberTypes(), dictionary2.getNumberTypes());
        assertEquals(dictionary.getName(), dictionary2.getName());
        assertEquals(dictionary.getDescription(), dictionary2.getDescription());
        assertEquals(dictionary.getLanguage(), dictionary2.getLanguage());
        assertEquals(dictionary.getCreationDate(), dictionary2.getCreationDate());
        assertEquals(dictionary, dictionary2);
     }
}
