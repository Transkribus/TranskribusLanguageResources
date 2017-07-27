/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.crawler;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author max
 */
public class CrawlerConfig extends CrawlConfig
{
    private String seed;
    private final Map<String, String> crawledText;
    
    public CrawlerConfig()
    {
        crawledText = new HashMap<>();
    }

    public String getSeed()
    {
        return seed;
    }

    public void setSeed(String seed)
    {
        this.seed = seed;
    }
    
    public void addCrawledText(String url, String text)
    {
        crawledText.put(url, text);
    }

    public Map<String, String> getCrawledText()
    {
        return crawledText;
    }
}
