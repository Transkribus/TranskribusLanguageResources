/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.extractor.xml.tei;

import eu.transkribus.interfaces.IDictionary;
import eu.transkribus.languageresources.dictionaries.Dictionary;
import eu.transkribus.languageresources.extractor.xml.XMLExtractor;
import eu.transkribus.interfaces.languageresources.IPagewiseTextExtractor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author max, jnphilipp
 */
public class HTRTEIExtractor extends XMLExtractor implements IPagewiseTextExtractor
{

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
                content.append(this.parseAbbreviations(nList.item(i), abbreviationExpansionMode));

                if (i + 1 < nList.getLength())
                {
                    content.append("\n");
                }
            }
        }

        return content.toString();
    }

    @Override
    public IDictionary extractAbbreviations(String path)
    {
        IDictionary abbreviationsDictionary = new Dictionary();

        Document document = getDocumentFromFile(path);
        NodeList nodeList = document.getElementsByTagName("l");

        Node lNode;
        NodeList children;
        Node child;

        String abbreviation;
        String expansion;

        for (int i = 0; i < nodeList.getLength(); i++)
        {
            lNode = nodeList.item(i);
            children = lNode.getChildNodes();

            for (int j = 0; j < children.getLength(); j++)
            {
                child = children.item(j);
                if (child.getNodeName().equalsIgnoreCase("choice"))
                {
                    abbreviation = ((Element) child).getElementsByTagName("abbr").item(0).getTextContent();
                    expansion = ((Element) child).getElementsByTagName("expan").item(0).getTextContent();

                    ((Dictionary)abbreviationsDictionary).addEntry(abbreviation);
                    if (expansion != null && !expansion.equals(""))
                        ((Dictionary)abbreviationsDictionary).addValue(abbreviation, expansion);
                }
            }
        }

        return abbreviationsDictionary;
    }

    @Override
    public Dictionary extractAbbreviationsFromPage(String path, int page)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
