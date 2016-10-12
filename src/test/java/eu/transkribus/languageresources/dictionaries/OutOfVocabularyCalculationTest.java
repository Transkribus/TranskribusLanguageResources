/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.dictionaries;

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
     public void outOfVocabularyTest()
     {
         Dictionary biggerDictionaryBigger = new Dictionary();
         biggerDictionaryBigger.addEntry("word1");
         biggerDictionaryBigger.addEntry("word2");
         biggerDictionaryBigger.addEntry("word3");
         biggerDictionaryBigger.addEntry("word4");
         
         Dictionary smallerDictionary1 = new Dictionary();
         smallerDictionary1.addEntry("word1");
         
         Dictionary smallerDictionary2 = new Dictionary();
         smallerDictionary2.addEntry("word1");
         smallerDictionary2.addEntry("word2");
         
         Dictionary smallerDictionary3 = new Dictionary();
         smallerDictionary3.addEntry("word1");
         smallerDictionary3.addEntry("word2");
         smallerDictionary3.addEntry("word3");
         
         Dictionary smallerDictionary4 = new Dictionary();
         smallerDictionary4.addEntry("word1");
         smallerDictionary4.addEntry("word2");
         smallerDictionary4.addEntry("word3");
         smallerDictionary4.addEntry("word4");
         
         double outOfVocabularyRate1 = biggerDictionaryBigger.outOfVocabulary(smallerDictionary1);
         double outOfVocabularyRate2 = biggerDictionaryBigger.outOfVocabulary(smallerDictionary2);
         double outOfVocabularyRate3 = biggerDictionaryBigger.outOfVocabulary(smallerDictionary3);
         double outOfVocabularyRate4 = biggerDictionaryBigger.outOfVocabulary(smallerDictionary4);
         
         assertEquals(0.75, outOfVocabularyRate1, 0);
         assertEquals(0.5, outOfVocabularyRate2, 0);
         assertEquals(0.25, outOfVocabularyRate3, 0);
         assertEquals(0.0, outOfVocabularyRate4, 0);
     }
}
