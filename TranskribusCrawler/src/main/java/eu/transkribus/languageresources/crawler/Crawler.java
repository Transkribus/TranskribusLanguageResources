/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.crawler;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import java.util.Set;
import java.util.regex.Pattern;

/**
 *
 * @author max
 */
public class Crawler extends WebCrawler
{

    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg|jpeg|png|mp3|zip|gz))$");

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url)
    {
        String startUrl = ((CrawlerConfig)getMyController().getConfig()).getSeed();
        String href = url.getURL().toLowerCase();
        return !FILTERS.matcher(href).matches()
                && href.contains(startUrl);
    }

    @Override
    public void visit(Page page)
    {
        String url = page.getWebURL().getURL();
        System.out.println("URL: " + url);

        if (page.getParseData() instanceof HtmlParseData)
        {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String text = htmlParseData.getText();
            String html = htmlParseData.getHtml();
            Set<WebURL> links = htmlParseData.getOutgoingUrls();

            System.out.println("Text length: " + text.length());
            System.out.println("Html length: " + html.length());
            System.out.println("Number of outgoing links: " + links.size());
            
            ((CrawlerConfig)getMyController().getConfig()).addCrawledText(url, text);
        }
    }
}
