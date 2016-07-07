/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.extractor;

import eu.transkribus.languageresources.interfaces.ITextExtractor;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    private String extractTextFromPage(Document document, int page)
    {
        String pageId = getPageNames(document).get(page);
        return extractTextFromPage(document, pageId);
    }

    private String extractTextFromPage(Document document, String pageId)
    {
        List<String> pageZoneNames = getPageParagraphNames(document, pageId);
        return getTextFromLines(document, pageZoneNames);
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

    private String getTextFromLines(Document document, List<String> lineNames)
    {
        NodeList nList = document.getElementsByTagName("l");
        StringBuilder content = new StringBuilder();

        String zoneId;
        for (int i = 0; i < nList.getLength(); i++)
        {
            zoneId = nList.item(i).getAttributes().getNamedItem("facs").getNodeValue().substring(1);
            if (lineNames.contains(zoneId))
            {
                content.append(nList.item(i).getTextContent());

                if (i + 1 < nList.getLength())
                {
                    content.append("\n");
                }
            }
        }

        return content.toString();
    }
}
