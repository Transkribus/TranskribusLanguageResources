/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.dictionaries;

import eu.transkribus.languageresources.oov.OutOfVocabularyRate;
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
public class OutOfVocabularyCalculationTest
{

    public OutOfVocabularyCalculationTest()
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
    public void outOfVocabularyByTypeTest()
    {
        List<String> tokenizedText = new ArrayList<>(5);
        tokenizedText.add("word1");
        tokenizedText.add("word2");
        tokenizedText.add("word3");
        tokenizedText.add("word4");
        tokenizedText.add("word4");

        Dictionary smallDictionary1 = new Dictionary();
        smallDictionary1.addEntry("word1");

        Dictionary smallDictionary2 = new Dictionary();
        smallDictionary2.addEntry("word1");
        smallDictionary2.addEntry("word2");

        Dictionary smallDictionary3 = new Dictionary();
        smallDictionary3.addEntry("word1");
        smallDictionary3.addEntry("word2");
        smallDictionary3.addEntry("word3");

        Dictionary smallDictionary4 = new Dictionary();
        smallDictionary4.addEntry("word1");
        smallDictionary4.addEntry("word2");
        smallDictionary4.addEntry("word3");
        smallDictionary4.addEntry("word4");

        double outOfVocabularyRate1 = OutOfVocabularyRate.byTypes(smallDictionary1, tokenizedText, true);
        double outOfVocabularyRate2 = OutOfVocabularyRate.byTypes(smallDictionary2, tokenizedText, true);
        double outOfVocabularyRate3 = OutOfVocabularyRate.byTypes(smallDictionary3, tokenizedText, true);
        double outOfVocabularyRate4 = OutOfVocabularyRate.byTypes(smallDictionary4, tokenizedText, true);

        assertEquals(0.75, outOfVocabularyRate1, 0);
        assertEquals(0.5, outOfVocabularyRate2, 0);
        assertEquals(0.25, outOfVocabularyRate3, 0);
        assertEquals(0.0, outOfVocabularyRate4, 0);
    }

    @Test
     public void outOfVocabularyByTokenTest()
     {
         List<String> tokenizedText = new ArrayList<>(5);
         tokenizedText.add("word1");
         tokenizedText.add("word2");
         tokenizedText.add("word3");
         tokenizedText.add("word4");
         tokenizedText.add("word4");

         Dictionary smallDictionary1 = new Dictionary();
         smallDictionary1.addEntry("word1");

         Dictionary smallDictionary2 = new Dictionary();
         smallDictionary2.addEntry("word1");
         smallDictionary2.addEntry("word2");

         Dictionary smallDictionary3 = new Dictionary();
         smallDictionary3.addEntry("word1");
         smallDictionary3.addEntry("word2");
         smallDictionary3.addEntry("word3");

         Dictionary smallDictionary4 = new Dictionary();
         smallDictionary4.addEntry("word1");
         smallDictionary4.addEntry("word2");
         smallDictionary4.addEntry("word3");
         smallDictionary4.addEntry("word4");

         double outOfVocabularyRate1 = OutOfVocabularyRate.byTokens(smallDictionary1, tokenizedText, true);
         double outOfVocabularyRate2 = OutOfVocabularyRate.byTokens(smallDictionary2, tokenizedText, true);
         double outOfVocabularyRate3 = OutOfVocabularyRate.byTokens(smallDictionary3, tokenizedText, true);
         double outOfVocabularyRate4 = OutOfVocabularyRate.byTokens(smallDictionary4, tokenizedText, true);

         assertEquals(0.8, outOfVocabularyRate1, 0);
         assertEquals(0.6, outOfVocabularyRate2, 0);
         assertEquals(0.4, outOfVocabularyRate3, 0);
         assertEquals(0.0, outOfVocabularyRate4, 0);
     }
}
