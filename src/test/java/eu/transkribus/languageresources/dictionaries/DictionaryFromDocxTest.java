/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.dictionaries;

import com.google.common.io.Files;
import eu.transkribus.interfaces.IDictionary;
import eu.transkribus.languageresources.exceptions.ARPAParseException;
import eu.transkribus.languageresources.extractor.docx.DocxExtraktor;
import eu.transkribus.languageresources.tokenizer.ConfigTokenizer;
import eu.transkribus.languageresources.util.SimpleDictFileHandler;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author max
 */
public class DictionaryFromDocxTest
{

    private final String dictionaryFolder;
    private final String pathToInputFile;
    private File outputFile;

    public DictionaryFromDocxTest()
    {
        dictionaryFolder = Files.createTempDir().getAbsolutePath();

        ClassLoader classLoader = getClass().getClassLoader();
        pathToInputFile = new File(classLoader.getResource("ttexter.docx").getFile()).getAbsolutePath();
        
        try
        {
            outputFile = File.createTempFile("ttexter", "dict");
        } catch (IOException ex)
        {
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
    public void dictionaryFromDocx() throws ARPAParseException, FileNotFoundException, IOException
    {
        // first, we extract the text from the page xml folder

        DocxExtraktor instance = new DocxExtraktor();
        String text = instance.extractTextFromDocument(pathToInputFile);

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
        SimpleDictFileHandler.write(outputFile, dictionary.getEntries());
        Map<String, Integer> dictionary2 = SimpleDictFileHandler.read(outputFile);
        
        assertEquals(52929, dictionary2.size());
    }
}
