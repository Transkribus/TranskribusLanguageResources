/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.crawler;

import java.util.Map;
import org.junit.Test;

/**
 *
 * @author max
 */
public class CrawlerTest
{

    @Test
    public void testCrawl()
    {
        CrawlerController c = new CrawlerController();
        Map<String, String> crawledText = c.crawl("berliner-intellektuelle.eu");
    }
}
