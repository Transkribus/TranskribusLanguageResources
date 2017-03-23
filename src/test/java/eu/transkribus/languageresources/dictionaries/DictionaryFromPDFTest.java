/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.dictionaries;

import eu.transkribus.languageresources.exceptions.ARPAParseException;
import eu.transkribus.languageresources.extractor.pagexml.PAGEXMLExtractor;
import eu.transkribus.languageresources.extractor.pdf.PDFExtraktor;
import eu.transkribus.languageresources.tokenizer.ConfigTokenizer;
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
public class DictionaryFromPDFTest
{

    private final String pathToFile;
    private final String dictionaryFolder;

    public DictionaryFromPDFTest()
    {
        ClassLoader classLoader = getClass().getClassLoader();
        pathToFile = new File(classLoader.getResource("Brief103BoeckhanVarnhagen.de.pdf").getFile()).getAbsolutePath();
        dictionaryFolder = new File(classLoader.getResource("pdf_test/").getFile()).getAbsolutePath();
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
     public void dictionaryFromPDF() throws ARPAParseException, FileNotFoundException, IOException
     {
        // first, we extract the text from the page xml folder
        PDFExtraktor textExtraktor = new PDFExtraktor();
        String text = textExtraktor.extractTextFromDocument(pathToFile, " ");

        Properties tokenizerProperties = new Properties();

        // we use simple dehyphenation
        tokenizerProperties.setProperty("dehyphenation_signs", "¬");

        // new lines, dots and commas are not treated as types
        // example: word.word -> 'word', '.', 'word'
        tokenizerProperties.setProperty("delimiter_signs", "\n., ");

        // the emptry string means we do not keep the delimiter signs
        tokenizerProperties.setProperty("keep_delimiter_signs", "");

        ConfigTokenizer tokenizer = new ConfigTokenizer(tokenizerProperties);
        List<String> tokenizedText = tokenizer.tokenize(text);

        // the dictionary is created with the tokenized text
        // and is written into a file without frequencies
        Dictionary dictionary = new Dictionary(tokenizedText);
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
