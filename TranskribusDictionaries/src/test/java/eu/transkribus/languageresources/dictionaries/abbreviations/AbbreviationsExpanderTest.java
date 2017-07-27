/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.dictionaries.abbreviations;

import eu.transkribus.interfaces.IDictionary;
import eu.transkribus.languageresources.dictionaries.Dictionary;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
public class AbbreviationsExpanderTest
{

    private static IDictionary dict;

    private final String pathToAbbreviated;
    private final String pathToExpanded;
    
    public AbbreviationsExpanderTest()
    {
        ClassLoader classLoader = getClass().getClassLoader();
        pathToAbbreviated = new File(classLoader.getResource("abbr/abbr.txt").getFile()).getAbsolutePath();
        pathToExpanded = new File(classLoader.getResource("abbr/exp.txt").getFile()).getAbsolutePath();
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
    public void setUp() throws Exception
    {
        dict = ParallelAbbreviationsParser.createDictionary(pathToAbbreviated, pathToExpanded);
    }
    
    @After
    public void tearDown()
    {
    }
    
    @Test
    public void testDictFromParallel()
    {
        
    }

    /**
     * Test of expandLineGreedily method, of class AbbreviationsExpander.
     */
    @Test
    public void testExpandLineGreedily()
    {
        List<String> line = Arrays.asList("I m a sh test".split(" "));
        List<String> expResult = Arrays.asList("I am a short test".split(" "));
        
        AbbreviationsExpander expander = new AbbreviationsExpander();
        expander.addDict((Dictionary) dict);
        
        List<String> result = expander.expandLineGreedily(line);
        
        System.out.println("input line: "+line);
        System.out.println("expanded line: "+result);
        
        assertEquals(expResult, result);
    }

    /**
     * Test of getSuggestions method, of class AbbreviationsExpander.
     */
    @Test
    public void testGetSuggestions()
    {
        AbbreviationsExpander expander = new AbbreviationsExpander();
        expander.addDict((Dictionary) dict);
        
        Map<String, Double> suggestions = expander.getSuggestions("I");
        System.out.println("suggestions for 'I': "+suggestions);
        assertEquals(1, suggestions.size());
        assertEquals(1, suggestions.get("I"), 0);
        
        suggestions = expander.getSuggestions("m");
        System.out.println("suggestions for 'm': "+suggestions);
        assertEquals(2, suggestions.size());
        assertEquals(0.5, suggestions.get("m"), 0);
        assertEquals(0.5, suggestions.get("am"), 0);
        
        suggestions = expander.getSuggestions("a");
        System.out.println("suggestions for 'a': "+suggestions);
        assertEquals(1, suggestions.size());
        assertEquals(1, suggestions.get("a"), 0);
        
        suggestions = expander.getSuggestions("sh");
        System.out.println("suggestions for 'sh': "+suggestions);
        assertEquals(1, suggestions.size());
        assertEquals(1, suggestions.get("short"), 0);
        
        suggestions = expander.getSuggestions("test");
        System.out.println("suggestions for 'test': "+suggestions);
        assertEquals(1, suggestions.size());
        assertEquals(1, suggestions.get("test"), 0);
        
        suggestions = expander.getSuggestions("sent");
        System.out.println("suggestions for 'sent': "+suggestions);
        assertEquals(1, suggestions.size());
        assertEquals(1, suggestions.get("sentence"), 0);
        
        suggestions = expander.getSuggestions("num");
        System.out.println("suggestions for 'num': "+suggestions);
        assertEquals(1, suggestions.size());
        assertEquals(1, suggestions.get("number"), 0);
        
        suggestions = expander.getSuggestions("two");
        System.out.println("suggestions for 'two': "+suggestions);
        assertEquals(1, suggestions.size());
        assertEquals(1, suggestions.get("two"), 0);
    }
    
}
