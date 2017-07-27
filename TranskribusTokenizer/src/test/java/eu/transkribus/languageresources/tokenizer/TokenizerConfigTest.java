/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.tokenizer;

import eu.transkribus.tokenizer.TokenizerConfig;
import eu.transkribus.interfaces.ITokenizer;
import eu.transkribus.tokenizer.TokenizerCategorizer;
import eu.transkribus.tokenizer.categorizer.CategorizerWordDft;
import eu.transkribus.tokenizer.dehyphenator.DehyphenatorSimple;
import java.io.File;
import java.net.URL;
import java.text.Normalizer;
import java.util.LinkedList;
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
public class TokenizerConfigTest {
    
    private static class TestCase {

        public String input;
        public int target;

        public TestCase(String input, int target) {
            this.input = input;
            this.target = target;
        }
    }

    private final TokenizerConfig ct_simple;
    private final TokenizerConfig ct_config;
    private final ITokenizer ct_categorizer;
    private static final List<TestCase> cases;

    static {
        cases = new LinkedList<>();
        cases.add(new TestCase("\n this is \n with\n linebreaks\n ", 4));
        cases.add(new TestCase("word1 word2 word3", 6));
        cases.add(new TestCase("word1. word2, word3.", 9));
        cases.add(new TestCase("word1 . word2, word3.", 9));
        cases.add(new TestCase("word1\n. word2, word3.", 9));
        cases.add(new TestCase("word1\n\n. word2, word3.", 9));
        cases.add(new TestCase("it's wrong", 4));
        cases.add(new TestCase("its wrong", 2));
        cases.add(new TestCase("30 examples, just some...", 8));
        cases.add(new TestCase("90.", 2));
        cases.add(new TestCase("90th", 2));
    }

    public TokenizerConfigTest() {
        this.ct_simple = new TokenizerConfig();
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL resource = classLoader.getResource("tokenizer_config.properties");
        File configFile = new File(resource.getFile());
        ct_config = new TokenizerConfig(configFile);
        ct_categorizer = new TokenizerCategorizer(new CategorizerWordDft());
    }

    @Test
    public void TestTokenizerConfig() {
        testTokenizer(ct_simple);
    }

    @Test
    public void TestTokenizerConfig2() {
        testTokenizer(ct_config);
    }

    @Test
    public void TestTokenizerCategorizer() {
        testTokenizer(ct_categorizer);
    }

    private void testTokenizer(ITokenizer tokenizer) {
        for (TestCase aCase : cases) {
            String message = "tokenizer of class '" + tokenizer.getClass().getName() + "' works unexpected for string '" + aCase.input.replace("\n", "\\n") + "'";
            assertEquals(message, aCase.target, tokenizer.tokenize(aCase.input).size());
        }
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testNormalize() {
        assertEquals("hätte", ct_config.normalize("ha\u0308tte", Normalizer.Form.NFC));
        assertEquals("hätte", ct_config.normalize("h\u00E4tte", Normalizer.Form.NFC));
        assertEquals(ct_config.normalize("ha\u0308tte", Normalizer.Form.NFC), ct_config.normalize("h\u00E4tte", Normalizer.Form.NFC));

        assertEquals("hätte", ct_config.normalize("ha\u0308tte", Normalizer.Form.NFC));
        assertEquals("hätte", ct_config.normalize("h\u00E4tte", Normalizer.Form.NFC));
        assertEquals(ct_config.normalize("ha\u0308tte", Normalizer.Form.NFC), ct_config.normalize("h\u00E4tte", Normalizer.Form.NFC));
    }

    @Test
    public void testKeepDelimiters() {
        assertEquals(2, ct_simple.tokenize("test, test", null, "", "\n., ", "", false, false, false, true).size());
        assertEquals(3, ct_simple.tokenize("test, test", null, "", "\n., ", ",", false, true, false, true).size());
        assertEquals(3, ct_config.tokenize("test, test").size());
    }

    @Test
    public void testCharacterWiseTokenization() {
        assertEquals(5, ct_simple.tokenize("hello", null, "", "", "", true, true, true, true).size());
        assertEquals(11, ct_simple.tokenize("hello hello", null, "", "", "", true, true, true, true).size());
        assertEquals(11, ct_simple.tokenize("hello hello", null, "", " ", "", true, true, true, true).size());
        assertEquals(13, ct_simple.tokenize("hello. hello!", null, "", ".! ", "", true, true, true, true).size());
    }
}
