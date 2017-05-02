/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.crawler;

import eu.transkribus.interfaces.IDictionary;
import eu.transkribus.languageresources.dictionaries.Dictionary;
import eu.transkribus.languageresources.tokenizer.ConfigTokenizer;
import java.util.Map;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author max
 */
public class CrawlerTest
{

    @Test
    @Ignore
    public void testCrawl()
    {
        CrawlerController c = new CrawlerController();
        Map<String, String> crawledText = c.crawl("berliner-intellektuelle.eu");
    }

    @Test
    @Ignore
    public void testCreateDictionaryFromURL()
    {
        CrawlerController c = new CrawlerController();
        Map<String, String> crawledText = c.crawl("berliner-intellektuelle.eu");

        ConfigTokenizer tokenizer = new ConfigTokenizer();

        IDictionary dictionary = new Dictionary();

        for (Map.Entry<String, String> e : crawledText.entrySet())
        {
            for (String token : tokenizer.tokenize(e.getValue()))
            {
                ((Dictionary)dictionary).addEntry(token);
            }
        }
    }
}
