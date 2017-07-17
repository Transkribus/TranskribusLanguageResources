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
import eu.transkribus.languageresources.util.SimpleDictFileHandler;
import eu.transkribus.tokenizer.TokenizerConfig;
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

    private final String pathToInputFile1;
    private final String pathToInputFile2;
    private final String pathToInputFile3;
    private final String pathToInputFile4;
    private final String pathToInputFile5;
    private File outputFile1;
    private File outputFile2;
    private File outputFile3;

    public DictionaryFromDocxTest()
    {
        ClassLoader classLoader = getClass().getClassLoader();
        pathToInputFile1 = new File(classLoader.getResource("ttexter.docx").getFile()).getAbsolutePath();
        pathToInputFile2 = new File(classLoader.getResource("Weingartner_Tagebuecher_1.docx").getFile()).getAbsolutePath();
        pathToInputFile3 = new File(classLoader.getResource("deutsch.dict").getFile()).getAbsolutePath();
        
        pathToInputFile4 = new File(classLoader.getResource("Hendschel/Hendschel.docx").getFile()).getAbsolutePath();
        pathToInputFile5 = new File(classLoader.getResource("Hendschel/deutsch.dict").getFile()).getAbsolutePath();
        
        try
        {
            outputFile1 = File.createTempFile("ttexter", "dict");
            outputFile2 = File.createTempFile("deutsch2", "dict");
            outputFile3 = new File("Hendschel_und_deutsch.dict");
//            outputFile3 = new File("Hendschel/Hendschel_und_deutsch.dict", "dict");
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
    public void dictionaryFromDocx() throws IOException
    {
        // first, we extract the text from the docx file
        DocxExtraktor instance = new DocxExtraktor();
        String text = instance.extractTextFromDocument(pathToInputFile1);

        Properties tokenizerProperties = new Properties();

        // we use simple dehyphenation
//        tokenizerProperties.setProperty("dehyphenation_signs", "¬");

        // new lines, dots and commas are not treated as types
        // example: word.word -> 'word', '.', 'word'
//        tokenizerProperties.setProperty("delimiter_signs", "\n.,„“ ");

        // the emptry string means we do not keep the delimiter signs
//        tokenizerProperties.setProperty("keep_delimiter_signs", "");

        TokenizerConfig tokenizer = new TokenizerConfig();
        List<String> tokenizedText = tokenizer.tokenize(text);

        // the dictionary is created with the tokenized text
        Dictionary dictionary = new Dictionary(tokenizedText);
        SimpleDictFileHandler.write(outputFile1, dictionary.getEntries());
    }
    
    @Test
    public void testExtractTextFromDocumentAndMerge() throws IOException
    {
        // first, we extract the text from the docx file
        DocxExtraktor instance = new DocxExtraktor();
        String text = instance.extractTextFromDocument(pathToInputFile2);

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
        Dictionary dictionary = new Dictionary(tokenizedText);
        Dictionary otherDictionary = SimpleDictFileHandler.readAsDictionary(pathToInputFile3);
        dictionary.merge(otherDictionary);
        
        SimpleDictFileHandler.write(outputFile2, dictionary.getEntries());
    }
    
    @Test
    public void mergeDictsTest() throws IOException
    {
        // first, we extract the text from the docx file
        DocxExtraktor instance = new DocxExtraktor();
        String text = instance.extractTextFromDocument(pathToInputFile4);

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
        Dictionary dictionary = new Dictionary(tokenizedText);
        Dictionary otherDictionary = SimpleDictFileHandler.readAsDictionary(pathToInputFile5);
        dictionary.merge(otherDictionary);
        
        SimpleDictFileHandler.write(outputFile3, dictionary.getEntries());
    }
}
