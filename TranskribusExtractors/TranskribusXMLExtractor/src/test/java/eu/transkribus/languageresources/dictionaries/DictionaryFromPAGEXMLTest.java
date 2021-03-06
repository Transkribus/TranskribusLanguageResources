/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.dictionaries;

import eu.transkribus.languageresources.exceptions.ARPAParseException;
import eu.transkribus.languageresources.extractor.pagexml.PAGEXMLExtractor;
import eu.transkribus.tokenizer.TokenizerConfig;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import org.junit.After;
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
public class DictionaryFromPAGEXMLTest
{

    private final String pathToFile;
    private final File dictionaryFolder;

    public DictionaryFromPAGEXMLTest()
    {
        ClassLoader classLoader = getClass().getClassLoader();
        pathToFile = new File(classLoader.getResource("page/").getFile()).getAbsolutePath();

        try
        {
            dictionaryFolder = File.createTempFile("temp_folder", "");
            dictionaryFolder.delete();
            dictionaryFolder.mkdir();
        } catch (IOException ex)
        {
            throw new RuntimeException("Could not create temp folder.");
        }
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
//     @Ignore
    public void dictionaryFromPAGEXML() throws ARPAParseException, FileNotFoundException, IOException
    {
        // first, we extract the text from the page xml folder
        PAGEXMLExtractor textExtraktor = new PAGEXMLExtractor();
        String text = textExtraktor.extractTextFromDocument(pathToFile, " ");

        Properties tokenizerProperties = new Properties();

        // we use simple dehyphenation
        tokenizerProperties.setProperty("dehyphenation_signs", "¬");

        // new lines, dots and commas are not treated as types
        // example: word.word -> 'word', '.', 'word'
        tokenizerProperties.setProperty("delimiter_signs", "\n., ");

        // the emptry string means we do not keep the delimiter signs
        tokenizerProperties.setProperty("keep_delimiter_signs", "");

        TokenizerConfig tokenizer = new TokenizerConfig(tokenizerProperties);
        List<String> tokenizedText = tokenizer.tokenize(text);

        // the dictionary is created with the tokenized text
        // and is written into a file without frequencies
        Dictionary dictionary = new Dictionary(tokenizedText);
        DictionaryUtils.save(dictionaryFolder, dictionary);
        Dictionary dictionary2 = (Dictionary) DictionaryUtils.load(dictionaryFolder);
        assertTrue(dictionary.getEntries().containsAll(dictionary2.getEntries()));
        assertTrue(dictionary2.getEntries().containsAll(dictionary.getEntries()));
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

    @Test
    public void testToJSON() {
        PAGEXMLExtractor textExtraktor = new PAGEXMLExtractor();
        String text = textExtraktor.extractTextFromDocument(pathToFile, " ");

        Properties tokenizerProperties = new Properties();
        tokenizerProperties.setProperty("dehyphenation_signs", "¬");
        tokenizerProperties.setProperty("delimiter_signs", "\n., ");
        tokenizerProperties.setProperty("keep_delimiter_signs", "");

        TokenizerConfig tokenizer = new TokenizerConfig(tokenizerProperties);
        List<String> tokenizedText = tokenizer.tokenize(text);
        Dictionary dictionary = new Dictionary(tokenizedText);

        Dictionary personsDictionary = (Dictionary) textExtraktor.extractPersonNames(pathToFile);
        Dictionary abbreviationsDictionary = (Dictionary) textExtraktor.extractAbbreviations(pathToFile);
        Dictionary placeNamesDictionary = (Dictionary) textExtraktor.extractPlaceNames(pathToFile);
        Dictionary organizationsDictionary = (Dictionary) textExtraktor.extractOrganizations(pathToFile);
        String result = DictionaryUtils.toJSON(dictionary, abbreviationsDictionary, personsDictionary, placeNamesDictionary, organizationsDictionary).toString();
        assertEquals("{\"name\":null,\"description\":null,\"language\":null,\"number_types\":\"13\",\"number_tokens\":\"123\",\"creation_date\":\"" + dictionary.getCreationDate() + "\",\"entries\":[{\"in\":{\"frequency\":58,\"tags\":{\"Abbreviation\":{\"Total\":0,\"expansions\":{}},\"Person\":0,\"PlaceName\":0,\"Organization\":0}}},{\"den\":{\"frequency\":22,\"tags\":{\"Abbreviation\":{\"Total\":0,\"expansions\":{}},\"Person\":0,\"PlaceName\":0,\"Organization\":0}}},{\"gegen\":{\"frequency\":1,\"tags\":{\"Abbreviation\":{\"Total\":0,\"expansions\":{}},\"Person\":0,\"PlaceName\":0,\"Organization\":0}}},{\"Die\":{\"frequency\":3,\"tags\":{\"Abbreviation\":{\"Total\":0,\"expansions\":{}},\"Person\":0,\"PlaceName\":0,\"Organization\":0}}},{\",\":{\"frequency\":15,\"tags\":{\"Abbreviation\":{\"Total\":0,\"expansions\":{}},\"Person\":0,\"PlaceName\":0,\"Organization\":0}}},{\"des\":{\"frequency\":1,\"tags\":{\"Abbreviation\":{\"Total\":0,\"expansions\":{}},\"Person\":0,\"PlaceName\":0,\"Organization\":0}}},{\".\":{\"frequency\":5,\"tags\":{\"Abbreviation\":{\"Total\":0,\"expansions\":{}},\"Person\":0,\"PlaceName\":0,\"Organization\":0}}},{\"der\":{\"frequency\":12,\"tags\":{\"Abbreviation\":{\"Total\":0,\"expansions\":{}},\"Person\":0,\"PlaceName\":0,\"Organization\":0}}},{\"die\":{\"frequency\":1,\"tags\":{\"Abbreviation\":{\"Total\":0,\"expansions\":{}},\"Person\":0,\"PlaceName\":0,\"Organization\":0}}},{\"Dr\":{\"frequency\":1,\"tags\":{\"Abbreviation\":{\"Total\":0,\"expansions\":{}},\"Person\":0,\"PlaceName\":0,\"Organization\":0}}},{\"ihren\":{\"frequency\":1,\"tags\":{\"Abbreviation\":{\"Total\":0,\"expansions\":{}},\"Person\":0,\"PlaceName\":0,\"Organization\":0}}},{\"werden\":{\"frequency\":1,\"tags\":{\"Abbreviation\":{\"Total\":0,\"expansions\":{}},\"Person\":0,\"PlaceName\":0,\"Organization\":0}}},{\"seit\":{\"frequency\":2,\"tags\":{\"Abbreviation\":{\"Total\":0,\"expansions\":{}},\"Person\":0,\"PlaceName\":0,\"Organization\":0}}}]}", result);

    }
}
