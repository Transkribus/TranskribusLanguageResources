/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.extractor.xml;

import eu.transkribus.languageresources.interfaces.IDictionary;
import java.io.File;
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
public class XMLTagExtractorTest
{

    private final String path;

    public XMLTagExtractorTest()
    {
        ClassLoader classLoader = getClass().getClassLoader();
        path = new File(classLoader.getResource("Brief05vonBuchanBeausobre.xml").getFile()).getAbsolutePath();
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
    public void persNameTest()
    {
        File f = new File(path);
        boolean isFile = f.isFile();
        XMLExtractor extractor = new XMLExtractor();

        IDictionary extractedPlaceNames = extractor.extractPlaceNames(path);
        assertEquals(1, extractedPlaceNames.getEntries().size());
        assertEquals(2, extractedPlaceNames.getEntry("Berlin").getFrequency());

        IDictionary extractedPersonNames = extractor.extractPersonNames(path);
        assertEquals(3, extractedPersonNames.getEntries().size());
        assertEquals(2, extractedPersonNames.getEntry("Grecourt").getFrequency());
    }
}
