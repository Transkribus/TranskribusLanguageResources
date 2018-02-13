/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.dictionaries;

import eu.transkribus.interfaces.IDictionary;
import eu.transkribus.languageresources.exceptions.ARPAParseException;
import eu.transkribus.languageresources.util.ARPAFileHandler;
import eu.transkribus.languageresources.util.SimpleDictFileHandler;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author max, jnphilipp
 */
public class DictionaryTest
{

    public DictionaryTest()
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

    /**
     * Test of addEntry method, of class Dictionary.
     */
    @Test
    public void testBasic()
    {
        Dictionary dictionary = new Dictionary();
        dictionary.addEntry("abk");
        dictionary.addValue("abk", "abkürzung");

        assertEquals(1, dictionary.getEntries().size());
        assertEquals(2, dictionary.getNumberTypes());
        assertEquals(2, dictionary.getNumberTokens());
        assertTrue(dictionary.containsKey("abk"));
        assertFalse(dictionary.containsKey("abkürzung"));
        assertEquals(1, dictionary.getEntry("abk").getValues().size());
        assertTrue(dictionary.getEntry("abk").containsValue("abkürzung"));
        assertEquals(1, (int) dictionary.getEntry("abk").getValues().get("abkürzung"));

        dictionary.addValue("abk", "abkürzung", 2);

        assertEquals(1, dictionary.getEntries().size());
        assertEquals(2, dictionary.getNumberTypes());
        assertEquals(4, dictionary.getNumberTokens());
        assertTrue(dictionary.containsKey("abk"));
        assertFalse(dictionary.containsKey("abkürzung"));
        assertEquals(1, dictionary.getEntry("abk").getValues().size());
        assertTrue(dictionary.getEntry("abk").containsValue("abkürzung"));
        assertEquals(3, (int) dictionary.getEntry("abk").getValues().get("abkürzung"));

        dictionary.addValue("abk", "Abkürzung");

        assertEquals(1, dictionary.getEntries().size());
        assertEquals(3, dictionary.getNumberTypes());
        assertEquals(5, dictionary.getNumberTokens());
        assertTrue(dictionary.containsKey("abk"));
        assertFalse(dictionary.containsKey("abkürzung"));
        assertEquals(2, dictionary.getEntry("abk").getValues().size());
        assertTrue(dictionary.getEntry("abk").containsValue("abkürzung"));
        assertTrue(dictionary.getEntry("abk").containsValue("Abkürzung"));
        assertEquals(3, (int) dictionary.getEntry("abk").getValues().get("abkürzung"));
        assertEquals(1, (int) dictionary.getEntry("abk").getValues().get("Abkürzung"));
    }

    @Test
    public void testWriteReadWithFrequencies() throws ARPAParseException, FileNotFoundException, IOException {
        Dictionary dictionary1 = new Dictionary();
        dictionary1.setName("TestDictionary");
        dictionary1.setDescription("Lorem ipsum dolrem sid amet...");
        dictionary1.addEntry("abk");
        dictionary1.addEntry("abk", 2);
        dictionary1.addValue("abk", "abkürzung");
        dictionary1.addEntry("Das");
        dictionary1.addEntry("Auto");
        dictionary1.addEntry("Auto");
        dictionary1.addValue("Auto", "Au");
        dictionary1.addEntry("ist");
        dictionary1.addEntry("grün");
        dictionary1.addEntry("rot", 5);
        dictionary1.addValue("z.B.", "zum_Beispiel", 3);

        ClassLoader classLoader = getClass().getClassLoader();
        String dictionaryPath = new File(classLoader.getResource(".").getFile()).getAbsolutePath() + "/test-dictionary";

        DictionaryUtils.save(dictionaryPath, dictionary1);
        assertTrue(new File(dictionaryPath).exists());
        assertTrue(new File(dictionaryPath).isDirectory());
        assertTrue(new File(dictionaryPath + "/metadata.properties").exists());
        assertTrue(new File(dictionaryPath + "/entries.arpa").exists());
        assertTrue(new File(dictionaryPath + "/entries.dict").exists());
        assertTrue(new File(dictionaryPath + "/entry-character-table.arpa").exists());
        assertTrue(new File(dictionaryPath + "/entry-character-table.csv").exists());
        assertTrue(new File(dictionaryPath + "/value-character-table.arpa").exists());
        assertTrue(new File(dictionaryPath + "/value-character-table.csv").exists());
        assertTrue(new File(dictionaryPath + "/entriesWords.arpa").exists());
        assertTrue(new File(dictionaryPath + "/entriesWords.dict").exists());
        assertTrue(new File(dictionaryPath + "/entriesPunctuationMarks.arpa").exists());
        assertTrue(new File(dictionaryPath + "/entriesPunctuationMarks.dict").exists());

        String entriesArpa = "\\data\\\nngram 1=10\nngram 2=3\n\n\\1-grams:\n5.00000000\trot\n3.00000000\tabk\n3.00000000\tzum_Beispiel\n2.00000000\tAuto\n1.00000000\tabkürzung\n1.00000000\tDas\n1.00000000\tAu\n1.00000000\tist\n1.00000000\tgrün\n0.00000000\tz.B.\n\n\\2-grams:\n3.00000000\tz.B. zum_Beispiel\n1.00000000\tabk abkürzung\n1.00000000\tAuto Au\n\n\\end\\\n";
        assertEquals(entriesArpa, readFile(dictionaryPath + "/entries.arpa"));

        IDictionary dictionary2 = DictionaryUtils.load(dictionaryPath);
        assertTrue(dictionary1.getEntries().containsAll(dictionary2.getEntries()));
        assertTrue(dictionary2.getEntries().containsAll(dictionary1.getEntries()));
        assertEquals(dictionary1.getEntryCharacterTable(), dictionary2.getEntryCharacterTable());
        assertEquals(dictionary1.getValueCharacterTable(), dictionary2.getValueCharacterTable());
        assertEquals(dictionary1.getNumberTokens(), dictionary2.getNumberTokens());
        assertEquals(dictionary1.getNumberTypes(), dictionary2.getNumberTypes());
        assertEquals(dictionary1.getName(), dictionary2.getName());
        assertEquals(dictionary1.getDescription(), dictionary2.getDescription());
        assertEquals(dictionary1.getLanguage(), dictionary2.getLanguage());
        assertEquals(dictionary1.getCreationDate(), dictionary2.getCreationDate());
        assertEquals(dictionary1, dictionary2);
    }

    @Test
    public void testWriteReadWordAndPunctuationFiles() throws ARPAParseException, FileNotFoundException, IOException {
        Dictionary dictionary3 = new Dictionary();
        dictionary3.setName("TestDictionary3");
        dictionary3.addEntry("Wörter");
        dictionary3.addEntry("Hallo");
        dictionary3.addValue("Hallo","greeting");
        dictionary3.addEntry("!");
        dictionary3.addValue("!", "Ausrufungszeichen");
        dictionary3.addEntry(".");
        dictionary3.addEntry("High8");
        dictionary3.addEntry("Los!");

        ClassLoader classLoader = getClass().getClassLoader();
        String dictionaryPath = new File(classLoader.getResource(".").getFile()).getAbsolutePath() + "/test-dictionary3";

        DictionaryUtils.save(dictionaryPath, dictionary3);
        assertTrue(new File(dictionaryPath).exists());
        assertTrue(new File(dictionaryPath).isDirectory());

        Dictionary dictionary4 = new Dictionary();
        ((Dictionary)dictionary4).fromNgrams(ARPAFileHandler.read(new File(dictionaryPath + "/entriesWords.arpa")));

        assertEquals(dictionary4.getEntries().size(), 3);
        assertTrue(dictionary4.containsKey("Wörter"));
        assertTrue(dictionary4.containsKey("Hallo"));
        assertTrue(dictionary4.containsKey("High8"));
        assertEquals(dictionary4.getEntry("Wörter").getValues().size(), 0);
        assertEquals(dictionary4.getEntry("Hallo").getValues().size(), 1);
        assertEquals(dictionary4.getEntry("High8").getValues().size(), 0);
        assertTrue(dictionary4.getEntry("Hallo").containsValue("greeting"));


        Map<String, Integer> entriesWord = SimpleDictFileHandler.read(new File(dictionaryPath + "/entriesWords.dict"));
        assertTrue(entriesWord.containsKey("Wörter"));
        assertTrue(entriesWord.containsKey("Hallo"));


        dictionary4 = new Dictionary();
        ((Dictionary)dictionary4).fromNgrams(ARPAFileHandler.read(new File(dictionaryPath + "/entriesPunctuationMarks.arpa")));

        assertEquals(dictionary4.getEntries().size(), 2);
        assertTrue(dictionary4.containsKey("!"));
        assertTrue(dictionary4.containsKey("."));
        assertEquals(dictionary4.getEntry("!").getValues().size(), 0);
        assertEquals(dictionary4.getEntry(".").getValues().size(), 0);


        entriesWord = SimpleDictFileHandler.read(new File(dictionaryPath + "/entriesPunctuationMarks.dict"));
        assertTrue(entriesWord.containsKey("!"));
        assertTrue(entriesWord.containsKey("."));
    }

    @Test
    public void testToJSON() throws FileNotFoundException, IOException {
        Dictionary dictionary = new Dictionary();
        dictionary.setName("Test");
        dictionary.setLanguage("de-DE");
        dictionary.addEntry("Goethe");
        dictionary.addEntry("hat");
        dictionary.addEntry("die");
        dictionary.addEntry("Leiden");
        dictionary.addEntry("des");
        dictionary.addEntry("jungen");
        dictionary.addEntry("Werthers");
        dictionary.addEntry("in");
        dictionary.addEntry("Wetzlar");
        dictionary.addEntry("geschrieben");
        dictionary.addEntry("Goethe");
        dictionary.addEntry("hat");
        dictionary.addEntry("aber");
        dictionary.addEntry("nicht");
        dictionary.addEntry("Dr.");
        dictionary.addEntry("Faustus");
        dictionary.addEntry("geschrieben");
        dictionary.addEntry("sondern");
        dictionary.addEntry("das");
        dictionary.addEntry("war");
        dictionary.addEntry("Thomas");
        dictionary.addEntry("Mann");

        Dictionary abbreviationsDictionary = new Dictionary();
        abbreviationsDictionary.addValue("Dr.", "Doktor");

        Dictionary personsDictionary = new Dictionary();
        personsDictionary.addEntry("Goethe");
        personsDictionary.addEntry("Werthers");
        personsDictionary.addEntry("Faustus");
        personsDictionary.addEntry("Thomas");
        personsDictionary.addEntry("Mann");

        Dictionary placeNamesDictionary = new Dictionary();
        placeNamesDictionary.addEntry("Wetzlar");

        Dictionary organizationsDictionary = new Dictionary();

        String result = DictionaryUtils.toJSON(dictionary, abbreviationsDictionary, personsDictionary, placeNamesDictionary, organizationsDictionary).toString();
        assertEquals("{\"name\":\"Test\",\"description\":null,\"language\":\"de-DE\",\"number_types\":\"19\",\"number_tokens\":\"22\",\"creation_date\":\"" + dictionary.getCreationDate() + "\",\"entries\":[{\"Goethe\":{\"frequency\":2,\"tags\":{\"Abbreviation\":{\"Total\":0,\"expantions\":{}},\"Person\":1,\"PlaceName\":0,\"Organization\":0}}},{\"hat\":{\"frequency\":2,\"tags\":{\"Abbreviation\":{\"Total\":0,\"expantions\":{}},\"Person\":0,\"PlaceName\":0,\"Organization\":0}}},{\"die\":{\"frequency\":1,\"tags\":{\"Abbreviation\":{\"Total\":0,\"expantions\":{}},\"Person\":0,\"PlaceName\":0,\"Organization\":0}}},{\"Leiden\":{\"frequency\":1,\"tags\":{\"Abbreviation\":{\"Total\":0,\"expantions\":{}},\"Person\":0,\"PlaceName\":0,\"Organization\":0}}},{\"des\":{\"frequency\":1,\"tags\":{\"Abbreviation\":{\"Total\":0,\"expantions\":{}},\"Person\":0,\"PlaceName\":0,\"Organization\":0}}},{\"jungen\":{\"frequency\":1,\"tags\":{\"Abbreviation\":{\"Total\":0,\"expantions\":{}},\"Person\":0,\"PlaceName\":0,\"Organization\":0}}},{\"Werthers\":{\"frequency\":1,\"tags\":{\"Abbreviation\":{\"Total\":0,\"expantions\":{}},\"Person\":1,\"PlaceName\":0,\"Organization\":0}}},{\"in\":{\"frequency\":1,\"tags\":{\"Abbreviation\":{\"Total\":0,\"expantions\":{}},\"Person\":0,\"PlaceName\":0,\"Organization\":0}}},{\"Wetzlar\":{\"frequency\":1,\"tags\":{\"Abbreviation\":{\"Total\":0,\"expantions\":{}},\"Person\":0,\"PlaceName\":1,\"Organization\":0}}},{\"geschrieben\":{\"frequency\":2,\"tags\":{\"Abbreviation\":{\"Total\":0,\"expantions\":{}},\"Person\":0,\"PlaceName\":0,\"Organization\":0}}},{\"aber\":{\"frequency\":1,\"tags\":{\"Abbreviation\":{\"Total\":0,\"expantions\":{}},\"Person\":0,\"PlaceName\":0,\"Organization\":0}}},{\"nicht\":{\"frequency\":1,\"tags\":{\"Abbreviation\":{\"Total\":0,\"expantions\":{}},\"Person\":0,\"PlaceName\":0,\"Organization\":0}}},{\"Dr.\":{\"frequency\":1,\"tags\":{\"Abbreviation\":{\"Total\":1,\"expantions\":{\"Doktor\":1}},\"Person\":0,\"PlaceName\":0,\"Organization\":0}}},{\"Faustus\":{\"frequency\":1,\"tags\":{\"Abbreviation\":{\"Total\":0,\"expantions\":{}},\"Person\":1,\"PlaceName\":0,\"Organization\":0}}},{\"sondern\":{\"frequency\":1,\"tags\":{\"Abbreviation\":{\"Total\":0,\"expantions\":{}},\"Person\":0,\"PlaceName\":0,\"Organization\":0}}},{\"das\":{\"frequency\":1,\"tags\":{\"Abbreviation\":{\"Total\":0,\"expantions\":{}},\"Person\":0,\"PlaceName\":0,\"Organization\":0}}},{\"war\":{\"frequency\":1,\"tags\":{\"Abbreviation\":{\"Total\":0,\"expantions\":{}},\"Person\":0,\"PlaceName\":0,\"Organization\":0}}},{\"Thomas\":{\"frequency\":1,\"tags\":{\"Abbreviation\":{\"Total\":0,\"expantions\":{}},\"Person\":1,\"PlaceName\":0,\"Organization\":0}}},{\"Mann\":{\"frequency\":1,\"tags\":{\"Abbreviation\":{\"Total\":0,\"expantions\":{}},\"Person\":1,\"PlaceName\":0,\"Organization\":0}}}]}", result);

        ClassLoader classLoader = getClass().getClassLoader();
        String dictionaryPath = new File(classLoader.getResource(".").getFile()).getAbsolutePath() + "/dictionary.json";

        DictionaryUtils.saveAsJSON(dictionaryPath, dictionary, abbreviationsDictionary, personsDictionary, placeNamesDictionary, organizationsDictionary);
        assertTrue(new File(dictionaryPath).exists());
    }

    private String readFile(String path) throws IOException, FileNotFoundException {
        StringBuilder s = new StringBuilder();
        try ( Reader reader = new BufferedReader(new FileReader(new File(path))) ) {
            String line;
            while ( (line = ((BufferedReader) reader).readLine()) != null ) {
                s.append(line);
                s.append(System.lineSeparator());
            }
        }
        return s.toString();
    }
}
