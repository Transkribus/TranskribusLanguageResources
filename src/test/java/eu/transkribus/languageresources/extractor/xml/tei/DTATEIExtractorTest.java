package eu.transkribus.languageresources.extractor.xml.tei;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
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
public class DTATEIExtractorTest {
    private String acidaliusEpistolaeTEI;
    private String acidaliusEpistolaeTXT;
    private String colliPrincepsTEI;
    private String colliPrincepsTXT;
    private String dieLeidenDesJungenWerthersTEI;
    private String dieLeidenDesJungenWerthersTXT;
    private String dta510291TEI;
    private String dta510291TXT;
    private String properties;

    public DTATEIExtractorTest() throws IOException, FileNotFoundException {
        ClassLoader classLoader = getClass().getClassLoader();
        this.dieLeidenDesJungenWerthersTEI = new File(classLoader.getResource("goethe_werther01_1774.TEI-P5.xml").getFile()).getAbsolutePath();
        this.dta510291TEI = new File(classLoader.getResource("510291.TEI-P5.xml").getFile()).getAbsolutePath();
        this.acidaliusEpistolaeTEI = new File(classLoader.getResource("Acidalius_epistolae.xml").getFile()).getAbsolutePath();
        this.colliPrincepsTEI = new File(classLoader.getResource("Colli_princeps.xml").getFile()).getAbsolutePath();
        this.properties = new File(classLoader.getResource("extractor_config.properties").getFile()).getAbsolutePath();

        this.dieLeidenDesJungenWerthersTXT = this.readFile(classLoader.getResource("goethe_werther01_1774.txt").getFile());
        this.dta510291TXT = this.readFile(classLoader.getResource("510291.txt").getFile());
        this.acidaliusEpistolaeTXT = this.readFile(new File(classLoader.getResource("Acidalius_epistolae.txt").getFile()).getAbsolutePath());
        this.colliPrincepsTXT = this.readFile(new File(classLoader.getResource("Colli_princeps.txt").getFile()).getAbsolutePath());
    }

    @BeforeClass
    public static void setUpClass() {}

    @AfterClass
    public static void tearDownClass() {}

    @Before
    public void setUp() {}

    @After
    public void tearDown() {}

    private String readFile(String path) throws IOException, FileNotFoundException {
        Reader reader = null;
        String s = "";
        try {
            reader = new BufferedReader(new FileReader(new File(path)));
            while ( true ) {
                int c = reader.read();
                if ( c == -1 )
                    break;

                s += (char)c;
            }
        }
        finally {
            if ( reader != null )
                reader.close();
        }
        return s.trim();
    }

    /**
     * Test of extractTextFromDocument method, of class DTATEIExtractor.
     */
    @Test
    public void testExtractTextFromDocument() {
        DTATEIExtractor instance = new DTATEIExtractor(this.properties);

        String result = instance.extractTextFromDocument(this.dieLeidenDesJungenWerthersTEI);
        assertEquals(this.dieLeidenDesJungenWerthersTXT, result);

        result = instance.extractTextFromDocument(this.dta510291TEI);
        assertEquals(this.dta510291TXT, result);

        result = instance.extractTextFromDocument(this.acidaliusEpistolaeTEI);
        assertEquals(this.acidaliusEpistolaeTXT, result);

        result = instance.extractTextFromDocument(this.colliPrincepsTEI);
        assertEquals(this.colliPrincepsTXT, result);
    }

    /**
     * Test of extractTextFromDocumentPagewise method, of class DTATEIExtractor.
     */
    @Test
    public void testExtractTextFromDocumentPagewise() {
        DTATEIExtractor instance = new DTATEIExtractor(this.properties);

        List<String> results = instance.extractTextFromDocumentPagewise(this.dieLeidenDesJungenWerthersTEI);
        assertEquals(results, results);//TODO: test real result

        results = instance.extractTextFromDocumentPagewise(this.dta510291TEI);
        assertEquals(results, results);//TODO: test real result
    }

    /**
     * Test of extractTextFromPage method, of class DTATEIExtractor.
     */
    @Test
    public void testextractTextFromPage() {
        DTATEIExtractor instance = new DTATEIExtractor(this.properties);

        String pageWerther = "am 15. Aug.\nEs iſt doch gewiß, daß in der Welt den Men-\nſchen nichts nothwendig macht als die Liebe.\nJch fuͤhl’s an Lotten, daß ſie mich ungern verloͤh-\nre, und die Kinder haben keine andre Jdee, als\ndaß ich immer morgen wiederkommen wuͤrde.\nHeut war ich hinausgegangen, Lottens Clavier zu\nſtimmen, ich konnte aber nicht-dazu kommen, denn\ndie Kleinen verfolgten mich um ein Maͤhrgen, und\nLotte ſagte denn ſelbſt, ich ſollte ihnen den Willen\nthun. Jch ſchnitt ihnen das Abendbrod, das ſie\nnun faſt ſo gerne von mir als von Lotten annah-\nmen und erzaͤhlte ihnen das Hauptſtuͤckgen von\nder Prinzeßinn, die von Haͤnden bedient wird. Jch\nlerne viel dabey, das verſichr’ ich dich, und ich bin\nerſtaunt, was es auf ſie fuͤr Eindruͤkke macht.\nWeil ich manchmal einen Jnzidenzpunkt erfinden\nmuß, den ich bey’m zweytenmal vergeſſe, ſagen ſie\ngleich, das vorigemal waͤr’s anders geweſt, ſo daß\nich mich jezt uͤbe, ſie unveraͤnderlich in einem ſin-\ngen-";
        String result = instance.extractTextFromPage(this.dieLeidenDesJungenWerthersTEI, 89);
        assertEquals(result, pageWerther);

        String page = "Chriſtliche Leichpredigt.\nben/ Johan. 14. ſehnſtu dich gen Himmel/ ſo iſt der\nHerr der Weg/ ſcheweſtu dich vor Finſterniß/ der\nHerr iſt dein Liecht/ Pſal. 27. begehrſtu Speiß/ derPſal. 27.\nHerr ſelbſt gibt ſich dir zur Speiß vnd Tranck/ Jo-Ioh. 6.\nhan. 6. Cap. Iſt alſo gut dieſen Herrn anzuruf-\nfen/ vnd zu bitten/ dann alle die auff jhn hoffen/ wer-\nden nicht zu ſchanden/ Pſal. 25. Er kan es auch wol lei-Pſal. 25.\nden/ daß wir jhm vnſere Noth klagen: Er heiſts ſelbſt/\ndaß wir es thun ſollen/ Pſalm 50. Pſal. 91.\nPſal. 50.\nPſal. 91. Er iſt nicht wie Nero der ſeiner Vnterthanen Kla-\ngen vnd Supplicationen alle ins Waſſer geworffen.\nEr iſt nicht wie Hannibal/ der in ſeinem Ehren-\nſtand verboten/ daß jhn niemand mehr anſprechen/\nnoch etwas jhme klagen ſolle: Sondern er erhoͤret Ge-Pſal. 65.\nbet/ Pſal. 65. vnſer Hertz iſt gewiß daß ſein Ohr drauff\nmercket/ Pſalm 5.\nPſal. 5. Derowegen/ ſo bleiben wir bey dieſem Herrn/\nvnd begehren keinem andern nachzueilen/ Pſal. 16. vonPſal. 16.\njhm heiſts:\nWer hofft in Gott vnd dem vertrawt/\nDer wird nimmer zu ſchanden/\nDenn wer auff dieſen Felſen bawt/\nOb jhm gleich koͤmpt zu handen\nViel Vnfalls hie/\nHab ich doch nie/\nDen Menſchen ſehen fallen/\nDer ſich verleſt/\nAuff Gottes Troſt/\nEr hilfft ſeinen Gleubigen allen.\nC 2Hinge-";
        result = instance.extractTextFromPage(this.dta510291TEI, 18);
        assertEquals(page, result);
    }
}
