/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.crawler;

import eu.transkribus.interfaces.IDictionary;
import eu.transkribus.languageresources.dictionaries.Dictionary;
import eu.transkribus.languageresources.util.SimpleDictFileHandler;
import eu.transkribus.tokenizer.TokenizerConfig;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author max
 */
public class StaZhCrawlTest
{
    
    public StaZhCrawlTest()
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

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
     @Test
     @Ignore
     public void hello() throws IOException
     {
        CrawlerController c = new CrawlerController();
//        Map<String, String> crawledText = c.crawl("http://linguaeterna.com/medlat/");
        Map<String, String> crawledText = c.crawl("http://www.perseus.tufts.edu/hopper/", 10);

        TokenizerConfig tokenizer = new TokenizerConfig();

        IDictionary dictionary = new Dictionary();

        for (Map.Entry<String, String> e : crawledText.entrySet())
        {
            for (String token : tokenizer.tokenize(e.getValue()))
            {
                ((Dictionary)dictionary).addEntry(token);
            }
        }
        
        SimpleDictFileHandler.write(new File("latin2.dict"), dictionary.getEntries());
     }
}
