/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.dictionaries;

import com.google.common.io.Files;
import eu.transkribus.languageresources.exceptions.ARPAParseException;
import eu.transkribus.languageresources.extractor.pagexml.PAGEXMLExtractor;
import eu.transkribus.languageresources.extractor.pdf.PDFExtraktor;
import eu.transkribus.languageresources.extractor.xml.tei.DTATEIExtractor;
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

/**
 *
 * @author max
 */
public class DictionaryFromTEITest
{

    private final String dictionaryFolder;
    private final String pathToFile;

    public DictionaryFromTEITest()
    {
        ClassLoader classLoader = getClass().getClassLoader();
        pathToFile = new File(classLoader.getResource("nn_msgermqu2345_1827.TEI-P5.xml").getFile()).getAbsolutePath();
        dictionaryFolder = Files.createTempDir().getAbsolutePath();
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
    public void dictionaryFromTEI() throws ARPAParseException, FileNotFoundException, IOException
    {
        // first, we extract the text from the page xml folder
        DTATEIExtractor textExtraktor = new DTATEIExtractor();
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
        assertEquals(dictionary.getEntries().size(), dictionary2.getEntries().size());
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
    public void abbreviationsFromTEI()
    {
        DTATEIExtractor textExtraktor = new DTATEIExtractor();
        Dictionary dict = (Dictionary) textExtraktor.extractAbbreviations(pathToFile);
        
        assertEquals(26, dict.getEntries().size());
    }
    
    @Test
    public void personsFromTEI()
    {
        DTATEIExtractor textExtraktor = new DTATEIExtractor();
        Dictionary dict = (Dictionary) textExtraktor.extractPersonNames(pathToFile);
        
        assertEquals(79, dict.getEntries().size());
    }
    
    @Test
    public void placesFromTEI()
    {
        DTATEIExtractor textExtraktor = new DTATEIExtractor();
        Dictionary dict = (Dictionary) textExtraktor.extractPlaceNames(pathToFile);
        
        assertEquals(0, dict.getEntries().size());
    }
}
