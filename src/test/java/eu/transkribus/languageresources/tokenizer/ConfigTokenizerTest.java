/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.tokenizer;

import java.io.File;
import java.text.Normalizer;
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
public class ConfigTokenizerTest
{
    private final ConfigTokenizer ct_simple;
    private final ConfigTokenizer ct_config;
    
    public ConfigTokenizerTest()
    {
        ct_simple = new ConfigTokenizer();
        
        ClassLoader classLoader = getClass().getClassLoader();
        File configFile = new File(classLoader.getResource("tokenizer_config.properties").getFile());
        
        ct_config = new ConfigTokenizer(configFile);
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
    public void testSimpleTokenization()
    {
        assertEquals(3, ct_simple.tokenize("word1 word2 word3").size());
        assertEquals(4, ct_simple.tokenize("word1 . word2, word3.").size());
        assertEquals(4, ct_simple.tokenize("word1 \n. word2, word3.").size());
        assertEquals(4, ct_simple.tokenize("word1\n \n. word2, word3.").size());
        
        assertEquals(3, ct_config.tokenize("word1 word2 word3").size());
        assertEquals(4, ct_config.tokenize("w1 . w2, w3.").size());
        assertEquals(4, ct_config.tokenize("word1 \n. word2, word3.").size());
        assertEquals(4, ct_config.tokenize("word1\n \n. word2, word3.").size());
    }
    
    @Test
    public void testNormalize()
    {
        assertEquals("hätte", ct_config.normalize("ha\u0308tte", Normalizer.Form.NFC));
        assertEquals("hätte", ct_config.normalize("h\u00E4tte", Normalizer.Form.NFC));
        assertEquals(ct_config.normalize("ha\u0308tte", Normalizer.Form.NFC), ct_config.normalize("h\u00E4tte", Normalizer.Form.NFC));
        
        assertEquals("hätte", ct_config.normalize("ha\u0308tte", Normalizer.Form.NFC));
        assertEquals("hätte", ct_config.normalize("h\u00E4tte", Normalizer.Form.NFC));
        assertEquals(ct_config.normalize("ha\u0308tte", Normalizer.Form.NFC), ct_config.normalize("h\u00E4tte", Normalizer.Form.NFC));
    }
    
    @Test
    public void testDehyphenation()
    {
        assertEquals("test", ct_simple.tokenize("te¬\nst", null, "¬", "\n ", "", false).get(0));
        assertEquals("test", ct_config.tokenize("te¬\nst").get(0));
    }
    
    @Test
    public void testKeepDelimiters()
    {
        assertEquals(2, ct_simple.tokenize("test, test", null, "", "\n., ", "", false).size());
        assertEquals(3, ct_simple.tokenize("test, test", null, "", "\n., ", ",", false).size());
        assertEquals(3, ct_config.tokenize("test, test").size());
    }
    
    @Test
    public void testCharacterWiseTokenization()
    {
        assertEquals(5, ct_simple.tokenize("hello", null, "", "", "", true).size());
        assertEquals(11, ct_simple.tokenize("hello hello", null, "", "", "", true).size());
        assertEquals(10, ct_simple.tokenize("hello hello", null, "", " ", "", true).size());
        assertEquals(10, ct_simple.tokenize("hello. hello!", null, "", ".! ", "", true).size());
    }
    
}
