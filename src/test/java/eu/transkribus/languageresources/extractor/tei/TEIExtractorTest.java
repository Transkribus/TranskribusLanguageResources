/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.extractor.tei;

import eu.transkribus.languageresources.extractor.tei.TEIExtractor;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
public class TEIExtractorTest
{
    String page1;
    String page2;
    String pathToFile;
            
    public TEIExtractorTest()
    {
        page1 = "in in\nin den gegen Die\nin in , in den\ndes . in\nin ,\nder\nin den . in den in den\nin . die in den\n\nin den in in in den in\nin den in\n" +
                "in ,\nin in ,\nin den\n. in , in den\nin in den in in ,\nin in den\nin den in den\nder der\nin den in\nin den\nder der in den in ,\nin ,\n" +
                "Dr . in\nihren ,\nder der der der\nin werden\nder\n\nin in ,\nin den in ,\nder\nin in in in\n\nDie ,\nseit in den\nin den in den in in ,\n" +
                "in in , seit\nDie in\nder\n,";
        page2 = "";
               
        ClassLoader classLoader = getClass().getClassLoader();
        pathToFile = new File(classLoader.getResource("HTR_Reichsgericht_tei.xml").getFile()).getAbsolutePath();
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
     * Test of extractTextFromDocument method, of class TEIExtractor.
     */
    @Test
    public void testExtractTextFromDocument_String()
    {
        TEIExtractor instance = new TEIExtractor();
        
        String expResult = page1 + "\n" + page2;
        String result = instance.extractTextFromDocument(pathToFile);

        assertEquals(expResult, result);
    }

    /**
     * Test of extractTextFromDocument method, of class TEIExtractor.
     */
    @Test
    public void testExtractTextFromDocument_String_String()
    {
        String splitCharacter = "\n";
        TEIExtractor instance = new TEIExtractor();
        
        String expResult = page1 + "\n" + page2;
        String result = instance.extractTextFromDocument(pathToFile, splitCharacter);

        assertEquals(expResult, result);
    }

    /**
     * Test of extractTextFromDocumentPagewise method, of class TEIExtractor.
     */
    @Test
    public void testExtractTextFromDocumentPagewise()
    {
        TEIExtractor instance = new TEIExtractor();

        List<String> expResult = new ArrayList<>(2);
        expResult.add(page1);
        expResult.add(page2);
        
        List<String> result = instance.extractTextFromDocumentPagewise(pathToFile);
        assertEquals(expResult, result);
    }

    /**
     * Test of extractTextFromPage method, of class TEIExtractor.
     */
    @Test
    public void testExtractTextFromPage()
    {
        TEIExtractor instance = new TEIExtractor();

        String expResult = page1;
        String result = instance.extractTextFromPage(pathToFile, 0);
        assertEquals(expResult, result);
        
        expResult = page2;
        result = instance.extractTextFromPage(pathToFile, 1);
        assertEquals(expResult, result);
    }
    
}
