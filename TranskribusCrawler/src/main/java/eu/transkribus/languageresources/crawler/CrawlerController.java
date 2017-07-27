/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.crawler;

import com.google.common.io.Files;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author max
 */
public class CrawlerController
{
    public Map<String, String> crawl(String startUrl)
    {
        return crawl(startUrl, 1);
    }

    public Map<String, String> crawl(String startUrl, int numberOfCrawlers)
    {
        CrawlerConfig config = new CrawlerConfig();
        config.setCrawlStorageFolder(Files.createTempDir().getAbsolutePath());
        config.setPolitenessDelay(10);
        config.setIncludeBinaryContentInCrawling(false);
        config.setResumableCrawling(false);
        config.setSeed(startUrl);
        config.setMaxPagesToFetch(10000);

        /*
         * Instantiate the controller for this crawl.
         */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);

        CrawlController controller;
        try
        {
            controller = new CrawlController(config, pageFetcher, robotstxtServer);
        } catch (Exception ex)
        {
            Logger.getLogger(CrawlerController.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex.getMessage());
        }

        /*
         * For each crawl, you need to add some seed urls. These are the first
         * URLs that are fetched and then the crawler starts following links
         * which are found in these pages
         */
        controller.addSeed(startUrl);

        /*
         * Start the crawl. This is a blocking operation, meaning that your code
         * will reach the line after this only when crawling is finished.
         */
        controller.start(Crawler.class, numberOfCrawlers);
//        controller.

        return config.getCrawledText();
    }

//    public static void main(String[] args)
//    {
//        File tmpDir = Files.createTempDir();
//        CrawlerController c = new CrawlerController();
//        Map<String, String> crawl = c.crawl("http://www.berliner-intellektuelle.eu/");
//
////            for(Map.Entry<String, String> e : crawl.entrySet())
////            {
////                System.out.println(e.getKey()+": "+e.getValue());
////            }
//    }
}
