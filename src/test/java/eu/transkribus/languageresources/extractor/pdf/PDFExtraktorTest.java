/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.extractor.pdf;

import eu.transkribus.languageresources.extractor.pdf.PDFExtraktor;
import eu.transkribus.languageresources.*;
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
public class PDFExtraktorTest
{
    String page1;
    String page2;
    String pathToFile;
            
    public PDFExtraktorTest()
    {
        page1 = "in in\nin den gegen Die\nin in , in den\ndes . in\ni ,\ner\nin den . in den in den\nin . die in den\nin den in in in den in\n" +
                "i  de  i\n ,\nin in ,\ni  den\n. in , in den\nin in den in in ,\ni  in de\nin den in den\nder der\nin den in\ni  de\n" +
                "der der in den in ,\ni ,\nDr . in\nih en ,\nder der der der\nin werden\nr\nin in ,\ni  de  in ,\nd r\nin in in in\n" +
                "Die ,\nseit in den\nin den in den in in ,\ni  i  , seit\nDie in\nr,\n";
        page2 = "\n";
        
        ClassLoader classLoader = getClass().getClassLoader();
        pathToFile = new File(classLoader.getResource("HTR_Reichsgericht.pdf").getFile()).getAbsolutePath();
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
     * Test of extractTextFromDocument method, of class PDFExtraktor.
     */
    @Test
    public void testExtractTextFromDocument_String()
    {
        
        PDFExtraktor instance = new PDFExtraktor();
        
        String expResult = page1 + "\n" + page2;
        String result = instance.extractTextFromDocument(pathToFile);
        
        assertEquals(expResult, result);
    }

    /**
     * Test of extractTextFromDocument method, of class PDFExtraktor.
     */
    @Test
    public void testExtractTextFromDocument_String_String()
    {
        String splitCharacter = "\n";

        PDFExtraktor instance = new PDFExtraktor();
        
        String expResult = page1 + splitCharacter + page2;
        String result = instance.extractTextFromDocument(pathToFile, splitCharacter);
        
        assertEquals(expResult, result);
    }

    /**
     * Test of extractTextFromDocumentPagewise method, of class PDFExtraktor.
     */
    @Test
    public void testExtractTextFromDocumentPagewise()
    {
        PDFExtraktor instance = new PDFExtraktor();
        
        List<String> result = instance.extractTextFromDocumentPagewise(pathToFile);
        List<String> expResult = new ArrayList<>();
        expResult.add(page1);
        expResult.add(page2);
        
        assertEquals(expResult, result);
    }

    /**
     * Test of extractTextFromPage method, of class PDFExtraktor.
     */
    @Test
    public void testExtractTextFromPage()
    {
        PDFExtraktor instance = new PDFExtraktor();
        
        int page = 0;
        String expResult = page1;
        String result = instance.extractTextFromPage(pathToFile, page);
        
        assertEquals(expResult, result);
        
        page = 1;
        expResult = page2;
        result = instance.extractTextFromPage(pathToFile, page);
        assertEquals(expResult, result);
    }
    
}
