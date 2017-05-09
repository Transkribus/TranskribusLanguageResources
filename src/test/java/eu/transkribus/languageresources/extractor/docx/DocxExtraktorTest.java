/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.extractor.docx;

import eu.transkribus.languageresources.dictionaries.Dictionary;
import java.io.File;
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
public class DocxExtraktorTest
{
    
    public DocxExtraktorTest()
    {
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
    public void testExtractTextFromDocument()
    {
        ClassLoader classLoader = getClass().getClassLoader();
        String pathToFile = new File(classLoader.getResource("ttexter.docx").getFile()).getAbsolutePath();
        DocxExtraktor instance = new DocxExtraktor();
        String text = instance.extractTextFromDocument(pathToFile);
        
        assertEquals(4197154, text.length());
    }
}
