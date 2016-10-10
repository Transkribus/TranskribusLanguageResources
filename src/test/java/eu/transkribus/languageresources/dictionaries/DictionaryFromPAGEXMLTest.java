/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.dictionaries;

import eu.transkribus.languageresources.extractor.pagexml.PAGEXMLExtractor;
import eu.transkribus.languageresources.tokenizer.ConfigTokenizer;
import java.io.File;
import java.util.List;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author max
 */
public class DictionaryFromPAGEXMLTest
{

    private final String pathToFile;
    
    public DictionaryFromPAGEXMLTest()
    {
        ClassLoader classLoader = getClass().getClassLoader();
        pathToFile = new File(classLoader.getResource("page/").getFile()).getAbsolutePath();
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
     public void dictionaryFromPAGEXML()
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
         
         ConfigTokenizer tokenizer = new ConfigTokenizer(tokenizerProperties);
         List<String> tokenizedText = tokenizer.tokenize(text);

         // the dictionary is created with the tokenized text
         // and is written into a file without frequencies
         Dictionary dictionary = new Dictionary(tokenizedText);
         DictionaryWriter.writeDictionray(dictionary, "dictionary.txt", false);
     }
}
