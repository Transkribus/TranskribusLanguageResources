/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.extractor.tei;

import eu.transkribus.languageresources.interfaces.ITextExtractor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author max
 */
public class TEIExtractor implements ITextExtractor
{
    // Patterns do take time to compile, thus make them
    // static for performance reasons.
    private static Pattern patternChoice = Pattern.compile("<choice>(.*?)</choice>");
    private static Pattern patternExpandFull = Pattern.compile(".*<expan>([\\w]+)</expan>.*");
    private static Pattern patternExpandEmpty = Pattern.compile("[<abbr>\\w.</abbr>]?</expan>[<abbr>\\w.</abbr>]?");
    private static Pattern patternAbbr = Pattern.compile(".*<abbr>([\\w]+)</abbr>.*");

    private final Properties properties;

    public TEIExtractor()
    {
        properties = new Properties();
    }

    public TEIExtractor(String pathToConfig)
    {
        this(new File(pathToConfig));
    }

    public TEIExtractor(File configFile)
    {
        properties = new Properties();
        try
        {
            properties.load(new FileInputStream(configFile));
        } catch (IOException ex)
        {
            Logger.getLogger(TEIExtractor.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Could not load given property file with path: " + configFile.getAbsolutePath());
        }
    }

    @Override
    public String extractTextFromDocument(String path)
    {
        return extractTextFromDocument(path, "\n");
    }

    @Override
    public String extractTextFromDocument(String path, String splitCharacter)
    {
        Document document = getDocumentFromFile(path);
        StringBuilder content = new StringBuilder();

        List<String> pageNames = getPageNames(document);
        for (int i = 0; i < pageNames.size(); i++)
        {
            content.append(extractTextFromPage(document, pageNames.get(i)));

            if (i + 1 < pageNames.size())
            {
                content.append(splitCharacter);
            }
        }

        return content.toString();
    }

    @Override
    public List<String> extractTextFromDocumentPagewise(String path)
    {
        Document document = getDocumentFromFile(path);
        List<String> content = new ArrayList<>();
        List<String> pageNames = getPageNames(document);
        for (String pageName : pageNames)
        {
            content.add(extractTextFromPage(document, pageName));
        }

        return content;
    }

    @Override
    public String extractTextFromPage(String path, int page)
    {
        Document document = getDocumentFromFile(path);
        return extractTextFromPage(document, page);
    }

    private Document getDocumentFromFile(String path)
    {
        try
        {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            return dBuilder.parse(new File(path));
        } catch (Exception ex)
        {
            Logger.getLogger(TEIExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }

        throw new RuntimeException("TEI.xml file could not be found for given path: " + path);
    }

    private String extractTextFromPage(Document document, String pageId)
    {
        return extractTextFromPage(document, pageId, properties.getProperty("abbreviation_expansion_mode", "keep"));
    }

    private String extractTextFromPage(Document document, int page)
    {
        return extractTextFromPage(document, page, properties.getProperty("abbreviation_expansion_mode", "keep"));
    }

    private String extractTextFromPage(Document document, int page, String abbreviationExpansionMode)
    {
        String pageId = getPageNames(document).get(page);
        return extractTextFromPage(document, pageId, abbreviationExpansionMode);
    }

    private String extractTextFromPage(Document document, String pageId, String abbreviationExpansionMode)
    {
        List<String> pageZoneNames = getPageParagraphNames(document, pageId);
        return getTextFromLines(document, pageZoneNames, abbreviationExpansionMode);
    }

    private List<String> getPageNames(Document document)
    {
        NodeList nList = document.getElementsByTagName("facsimile");

        List<String> pageIDs = new ArrayList<>(nList.getLength());
        for (int i = 0; i < nList.getLength(); i++)
        {
            pageIDs.add(nList.item(i).getAttributes().getNamedItem("xml:id").getTextContent());
        }

        Collections.sort(pageIDs);
        return pageIDs;
    }

    private Node getFacsimilieNode(Document document, String pageID)
    {
        NodeList nList = document.getElementsByTagName("facsimile");
        for (int i = 0; i < nList.getLength(); i++)
        {
            if (nList.item(i).getAttributes().getNamedItem("xml:id").getTextContent().equals(pageID))
            {
                return nList.item(i);
            }
        }

        throw new RuntimeException("Node with id-attribute " + pageID + " not found.");
    }

    private NodeList getZoneNodes(Document document, Node facsimileNode)
    {
        NodeList nList = facsimileNode.getChildNodes();
        for (int i = 0; i < nList.getLength(); i++)
        {
            if (nList.item(i).getNodeName().equals("surface"))
            {
                Node surfaceNode = nList.item(i);
                return surfaceNode.getChildNodes();
            }
        }

        throw new RuntimeException("Given facsimilie node has no child node with name surface.");
    }

    private List<String> getPageParagraphNames(Document document, String pageID)
    {
        Node facsimileNode = getFacsimilieNode(document, pageID);
        NodeList zoneNodes = getZoneNodes(document, facsimileNode);
        int numZoneNodes = zoneNodes.getLength();

        NodeList lineNodes;
        Node zoneNode;
        NamedNodeMap attributes;

        List<String> lineNames = new ArrayList<>();
        for (int i = 0; i < numZoneNodes; i++)
        {
            zoneNode = zoneNodes.item(i);
            if (zoneNode.getNodeName().equals("zone"))
            {
                attributes = zoneNode.getAttributes();

                if (attributes != null && attributes.getNamedItem("xml:id") != null)
                {
                    lineNames.add(attributes.getNamedItem("xml:id").getTextContent());
                }

                lineNodes = zoneNode.getChildNodes();
                for (int j = 0; j < lineNodes.getLength(); j++)
                {
                    attributes = lineNodes.item(j).getAttributes();

                    if (attributes != null)
                    {
                        lineNames.add(attributes.getNamedItem("xml:id").getTextContent());
                    }
                }
            }
        }

        return lineNames;
    }

    private String getTextFromLines(Document document, List<String> lineNames, String abbreviationExpansionMode)
    {
        NodeList nList = document.getElementsByTagName("l");
        StringBuilder content = new StringBuilder();

        String zoneId;
        String textContent;

        for (int i = 0; i < nList.getLength(); i++)
        {
            zoneId = nList.item(i).getAttributes().getNamedItem("facs").getNodeValue().substring(1);
            if (lineNames.contains(zoneId))
            {
                textContent = nList.item(i).getTextContent();
                textContent = parseAbbreviations(textContent, abbreviationExpansionMode);
                textContent = stripXML(textContent);

                content.append(textContent);

                if (i + 1 < nList.getLength())
                {
                    content.append("\n");
                }
            }
        }

        return content.toString();
    }

    public String parseAbbreviations(String textContent)
    {
        return parseAbbreviations(textContent, properties.getProperty("abbreviation_expansion_mode", "keep"));
    }

    public String parseAbbreviations(String textContent, String abbreviationExpansionMode)
    {
        if (!abbreviationExpansionMode.equals("keep") && !abbreviationExpansionMode.equals("expand"))
        {
            throw new IllegalArgumentException("Unkown mode, abbreviationExpansionMode has to be 'keep' or 'expand'");
        }

        Matcher matcherChoice = patternChoice.matcher(textContent);
        Matcher matcherExpandFull;
        Matcher matcherAbbr;
        String choiceContent;
        String replaceWith;
        boolean expandFull;

        while(matcherChoice.find())
        {
            choiceContent = matcherChoice.group();

            matcherExpandFull = patternExpandFull.matcher(choiceContent);
            expandFull = matcherExpandFull.matches();

            if(abbreviationExpansionMode.equals("keep") || !expandFull)
            {
                matcherAbbr = patternAbbr.matcher(choiceContent);
                matcherAbbr.matches();
                replaceWith = matcherAbbr.group(1);
            }else{
                replaceWith = matcherExpandFull.group(1);
            }

            textContent = textContent.replaceAll(choiceContent, replaceWith);
        }

        return textContent;
    }

    private String stripXML(String textContent)
    {
        return textContent.replaceAll("<.*?>", "");
    }
}
