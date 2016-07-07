/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.extractor.tei;

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
public class TEIAbbreviationsTest
{

    private final TEIExtractor extractor_simple;
    private final TEIExtractor extractor_config;

    public TEIAbbreviationsTest()
    {
        extractor_simple = new TEIExtractor();
        
        ClassLoader classLoader = getClass().getClassLoader();
        File configFile = new File(classLoader.getResource("abbreviations.properties").getFile());
        
        extractor_config = new TEIExtractor(configFile);
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
    public void testExpandAbbreviation()
    {
        String textOriginal = "hy deselue sal by hem houden om in cas van noot, <choice><expan>en1</expan><abbr>en</abbr></choice> anders niet,";
        String textExpanded = "hy deselue sal by hem houden om in cas van noot, en1 anders niet,";
        String textKept = "hy deselue sal by hem houden om in cas van noot, en anders niet,";
        
        String result = extractor_simple.parseAbbreviations(textOriginal);
        assertEquals(textKept, result);
        result = extractor_simple.parseAbbreviations(textOriginal, "keep");
        assertEquals(textKept, result);
        result = extractor_simple.parseAbbreviations(textOriginal, "expand");
        assertEquals(textExpanded, result);
        
        result = extractor_config.parseAbbreviations(textOriginal);
        assertEquals(textKept, result);
        result = extractor_config.parseAbbreviations(textOriginal, "keep");
        assertEquals(textKept, result);
        result = extractor_config.parseAbbreviations(textOriginal, "expand");
        assertEquals(textExpanded, result);
    }
    
    @Test
    public void testDoubleExpandAbbreviation()
    {
        String textOriginal = "<choice><expan>en1</expan><abbr>en</abbr></choice> hy deselue sal by hem houden om in cas van noot, <choice><expan>en2</expan><abbr>en</abbr></choice> anders niet,";
        String textExpanded = "en1 hy deselue sal by hem houden om in cas van noot, en2 anders niet,";
        String textKept = "en hy deselue sal by hem houden om in cas van noot, en anders niet,";
        
        String result = extractor_simple.parseAbbreviations(textOriginal);
        assertEquals(textKept, result);
        result = extractor_simple.parseAbbreviations(textOriginal, "keep");
        assertEquals(textKept, result);
        result = extractor_simple.parseAbbreviations(textOriginal, "expand");
        assertEquals(textExpanded, result);
        
        result = extractor_config.parseAbbreviations(textOriginal);
        assertEquals(textKept, result);
        result = extractor_config.parseAbbreviations(textOriginal, "keep");
        assertEquals(textKept, result);
        result = extractor_config.parseAbbreviations(textOriginal, "expand");
        assertEquals(textExpanded, result);
    }
    
    @Test
    public void testmissingExpansion()
    {
        String textOriginal = "hy deselue sal by hem houden om in cas van noot, <choice></expan><abbr>en</abbr></choice> anders niet,";
        String textExpanded = "hy deselue sal by hem houden om in cas van noot, en anders niet,";
        String textKept = "hy deselue sal by hem houden om in cas van noot, en anders niet,";
        
        String result = extractor_simple.parseAbbreviations(textOriginal);
        assertEquals(textKept, result);
        result = extractor_simple.parseAbbreviations(textOriginal, "keep");
        assertEquals(textKept, result);
        result = extractor_simple.parseAbbreviations(textOriginal, "expand");
        assertEquals(textExpanded, result);
        
        result = extractor_config.parseAbbreviations(textOriginal);
        assertEquals(textKept, result);
        result = extractor_config.parseAbbreviations(textOriginal, "keep");
        assertEquals(textKept, result);
        result = extractor_config.parseAbbreviations(textOriginal, "expand");
        assertEquals(textExpanded, result);
    }
}
