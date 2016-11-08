package eu.transkribus.languageresources.extractor.xml;

import eu.transkribus.languageresources.interfaces.IDictionary;
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
 * @author jnphilipp
 */
public class XMLExtractorTest {
    private final String page1_keep = "Lorem ipsumLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor i reprehenderit i voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt i culpa qui officî deserunt mollit anim id est laborû.";
    private final String page1_expand = "Lorem ipsumLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
    private final String page2_keep = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis auŧ irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sü in culpa qui officia deserunt mollit anim id est laborum.";
    private final String page2_expand = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
    private String page1Path;
    private String page2Path;
    private String properties;

    public XMLExtractorTest() {
        ClassLoader classLoader = getClass().getClassLoader();
        this.page1Path = new File(classLoader.getResource("page1.xml").getFile()).getAbsolutePath();
        this.page2Path = new File(classLoader.getResource("page2.xml").getFile()).getAbsolutePath();
        this.properties = new File(classLoader.getResource("extractor_config.properties").getFile()).getAbsolutePath();
    }

    @BeforeClass
    public static void setUpClass() {}

    @AfterClass
    public static void tearDownClass() {}

    @Before
    public void setUp() {}

    @After
    public void tearDown() {}

    /**
     * Test of extractTextFromDocument method, of class XMLExtractor.
     */
    @Test
    public void testExtractTextFromDocument() {
        XMLExtractor instance = new XMLExtractor(this.properties);

        String result = instance.extractTextFromDocument(this.page1Path);
        assertEquals(this.page1_expand, result);

        result = instance.extractTextFromDocument(this.page2Path);
        assertEquals(this.page2_expand, result);

        instance.getProperties().put("abbreviation_expansion_mode", "keep");
        result = instance.extractTextFromDocument(this.page1Path);
        assertEquals(this.page1_keep, result);

        result = instance.extractTextFromDocument(this.page2Path);
        assertEquals(this.page2_keep, result);
    }


    /**
     * Test of parseAbbreviations method, of class XMLExtractor.
     */
    @Test
    public void testParseAbbreviations() {
        XMLExtractor instance = new XMLExtractor(this.properties);

        String page1 = "Lorem ipsumLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor <choice><abbr>i</abbr><expan>in</expan></choice> reprehenderit <choice><abbr>i</abbr><expan>in</expan></choice> voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt <choice><abbr>i</abbr><expan>in</expan></choice> culpa qui <abbr expand=\"officia\">officî</abbr> deserunt mollit anim id est <abbr expand=\"laborum\">laborû</abbr>.";
        String page2 = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis au<choice><abbr>ŧ</abbr><expan>te</expan></choice> irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, s<choice><abbr>ü</abbr><expan>unt</expan></choice> in culpa qui officia deserunt mollit anim id est laborum.";
        String result = instance.parseAbbreviations(page1);
        assertEquals(this.page1_expand, result);

        result = instance.parseAbbreviations(page2);
        assertEquals(this.page2_expand, result);

        instance.getProperties().put("abbreviation_expansion_mode", "keep");
        result = instance.parseAbbreviations(page1);
        assertEquals(this.page1_keep, result);

        result = instance.parseAbbreviations(page2);
        assertEquals(this.page2_keep, result);
    }


    /**
     * Test of parseAbbreviations method, of class XMLExtractor.
     */
    @Test
    public void testExtractAbbreviations() {
        XMLExtractor instance = new XMLExtractor(this.properties);
        IDictionary dictionary = instance.extractAbbreviations(this.page1Path);
        assertEquals(3, dictionary.getEntries().size());
        assertTrue(dictionary.containsKey("i"));
        assertTrue(dictionary.getEntry("i").containsKey("in"));
        assertTrue(dictionary.containsKey("officî"));
        assertTrue(dictionary.getEntry("officî").containsKey("officia"));
        assertTrue(dictionary.containsKey("laborû"));
        assertTrue(dictionary.getEntry("laborû").containsKey("laborum"));
    }
}
