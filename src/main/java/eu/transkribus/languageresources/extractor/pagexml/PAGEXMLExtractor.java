/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.extractor.pagexml;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eu.transkribus.interfaces.IDictionary;
import eu.transkribus.languageresources.extractor.pagexml.annotations.PAGEXMLAbbreviation;
import eu.transkribus.languageresources.extractor.pagexml.annotations.PAGEXMLAnnotation;
import eu.transkribus.languageresources.extractor.pagexml.annotations.PAGEXMLIndex;
import eu.transkribus.languageresources.extractor.pagexml.tagextractor.AbbreviationsBuilder;
import eu.transkribus.languageresources.extractor.pagexml.tagextractor.PAGEXMLValueBuilder;
import eu.transkribus.languageresources.extractor.pagexml.tagextractor.SimpleBuilder;
import eu.transkribus.languageresources.extractor.pagexml.tagextractor.TokenBuilder;
import eu.transkribus.languageresources.extractor.xml.XMLExtractor;
import eu.transkribus.languageresources.interfaces.IPagewiseTextExtractor;
import eu.transkribus.languageresources.util.PAGEFileComparator;

/**
 *
 * @author max
 */
public class PAGEXMLExtractor extends XMLExtractor implements IPagewiseTextExtractor
{

    // Patterns do take time to compile, thus make them
    // static for performance reasons.
    private static Pattern patternAbbrev = Pattern.compile("abbrev\\s*\\{([\\w-,:;\\s]*)\\}");
    private static Pattern patternAbbrevExpansion = Pattern.compile(".*expansion:([\\w-]*);.*");
    private static Pattern patternPlace = Pattern.compile("place\\s*\\{([\\w-,:;\\s]*)\\}");
    private static Pattern patternOffset = Pattern.compile(".*offset:([0-9]*);.*");
    private static Pattern patternLength = Pattern.compile(".*length:([0-9]*);.*");

    public PAGEXMLExtractor()
    {
        super();
    }

    public PAGEXMLExtractor(String pathToConfig)
    {
        super(new File(pathToConfig));
    }

    public PAGEXMLExtractor(File configFile)
    {
        super(configFile);
    }

    @Override
    public String extractTextFromDocument(String path)
    {
        return extractTextFromDocument(path, "\n");
    }

    @Override
    public String extractTextFromDocument(String path, String splitCharacter)
    {
        List<File> files = getFileList(path);

        return extractTextFromFileList(files, splitCharacter);
    }

    public String extractTextFromFileList(List<File> files, String splitCharacter)
    {
        StringBuilder content = new StringBuilder();

        for (int fileIndex = 0; fileIndex < files.size(); fileIndex++)
        {
            content.append(getTextFromFile(files.get(fileIndex)));

            if (fileIndex + 1 < files.size())
            {
                content.append(splitCharacter);
            }
        }

        return content.toString();
    }

    @Override
    public List<String> extractTextFromDocumentPagewise(String path)
    {
        List<String> content = new ArrayList<>();
        List<File> files = getFileList(path);

        for (File f : files)
        {
            content.add(getTextFromFile(f));
        }

        return content;
    }

    @Override
    public String extractTextFromPage(String path, int page)
    {
        List<File> files = getFileList(path);
        return getTextFromFile(files.get(page));
    }

    private List<File> getFileList(String path)
    {
        if (!path.endsWith("/page"))
        {
            path += "/page";
        }

        File folder = new File(path);
        List<String> fileNames = new ArrayList<>();
        for (File f : folder.listFiles((File pathname) -> pathname.getName().endsWith(".xml")))
        {
            fileNames.add(f.getName());
        }

        Collections.sort(fileNames, new PAGEFileComparator());
        List<File> files = new ArrayList<>();
        for (String fileName : fileNames)
        {
            files.add(new File(path + "/" + fileName));
        }

//        files = files.subList(0, 3);
        return files;
    }

    private NodeList getNodesFromXml(Document doc)
    {
        try
        {
            return doc.getElementsByTagName("Unicode");

        } catch (Exception ex)
        {
            System.out.println(ex);
            throw new RuntimeException("Could not load nodes from file!");
        }
    }
    
    private NodeList getNodesFromFile(File f)
    {
        try
        {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(f);

            return getNodesFromXml(doc);

        } catch (Exception ex)
        {
            System.out.println(ex);
            throw new RuntimeException("Could not load nodes from file!");
        }
    }

    protected String getTextFromXml(Document d) 
    {
        NodeList unicodeNodes = getNodesFromXml(d);
        return getTextFromNodeList(unicodeNodes);
    }
    
    protected String getTextFromFile(File f)
    {
        NodeList unicodeNodes = getNodesFromFile(f);
        return getTextFromNodeList(unicodeNodes);
    }
    
    private String getTextFromNodeList(NodeList unicodeNodes)
    {
    	StringBuilder content = new StringBuilder();
        Node unicodeNode;

        int numNodes = unicodeNodes.getLength();

        for (int nodeIndex = 0; nodeIndex < numNodes; nodeIndex++)
        {
            unicodeNode = unicodeNodes.item(nodeIndex);
            content.append(getTextFromNode(unicodeNode));

            if (nodeIndex + 1 < numNodes)
            {
                content.append("\n");
            }
        }

        return content.toString();
    }

    public String getTextFromNode(Node unicodeNode)
    {
        String textContent = unicodeNode.getTextContent();
        Node textLineNode = unicodeNode.getParentNode().getParentNode();
        Node customNode = textLineNode.getAttributes().getNamedItem("custom");
        String customTagValue = null;
        if (customNode != null)
        {
            customTagValue = textLineNode.getAttributes().getNamedItem("custom").getTextContent();
        }
        return getTextFromNode(textContent, customTagValue);
    }

    public String getTextFromNode(String textContent, String customTagValue)
    {
        String abbreviationMode = properties.getProperty("abbreviation_expansion_mode", "keep");
        return getTextFromNode(textContent, customTagValue, abbreviationMode);
    }

    public String getTextFromNode(String textContent, String customTagValue, String abbreviationExpansionMode)
    {
        if (!abbreviationExpansionMode.equals("keep") && !abbreviationExpansionMode.equals("expand"))
        {
            throw new IllegalArgumentException("Unkown mode, abbreviationExpansionMode has to be 'keep' or 'expand'");
        }

        if (abbreviationExpansionMode.equals("expand"))
        {
            textContent = expandAbbreviations(textContent, customTagValue);
        }

        return textContent;
    }

    public String expandAbbreviations(String textContent, String customTagValue)
    {
        return expandAbbreviations(textContent, customTagValue, 0, 0);
    }

    public String expandAbbreviations(String textContent, String customTagValue, int pageIndex, int lineIndex)
    {
        List<PAGEXMLAbbreviation> abbreviations = this.<PAGEXMLAbbreviation>extractValueFromLine(new LinkedList<>(), textContent, customTagValue, new AbbreviationsBuilder(), pageIndex, lineIndex);

        String partLeft;
        String partRight;
        int startLeft;
        int expansionDiffSum = 0;

        for (PAGEXMLAbbreviation abbreviation : abbreviations)
        {
            if (abbreviation.getExpansion() != null)
            {
                startLeft = abbreviation.getStartOffset() + expansionDiffSum;
                partLeft = textContent.substring(0, startLeft);
                partRight = textContent.substring(startLeft + abbreviation.getLength(), textContent.length());

                textContent = partLeft + abbreviation.getExpansion() + partRight;
                expansionDiffSum += abbreviation.getLengthDiff();
            }
        }

        return textContent;
    }

    protected <V extends PAGEXMLAnnotation> List<V> extractValueFromLine(List<V> list, String line, String customTagValue, PAGEXMLValueBuilder builder, int pageIndex, int lineIndex)
    {
        return builder.extract(list, line, customTagValue, pageIndex, lineIndex);
    }

    @Override
    public IDictionary extractAbbreviations(String path)
    {
        return extractValuesAsDict(path, new AbbreviationsBuilder());
    }

    @Override
    public IDictionary extractPlaceNames(String path)
    {
        return extractValuesAsDict(path, new SimpleBuilder("place"));
    }

    @Override
    public IDictionary extractPersonNames(String path)
    {
        return extractValuesAsDict(path, new SimpleBuilder("person"));
    }
    
    public IDictionary extractOrganizations(String path)
    {
        return extractValuesAsDict(path, new SimpleBuilder("organization"));
    }
    
    public List<PAGEXMLAbbreviation> extractAbbreviationsAsList(String path)
    {
        return extractValues(path, new AbbreviationsBuilder());
    }

    public List<PAGEXMLAnnotation> extractPlaceNamesAsList(String path)
    {
        return extractValues(path, new SimpleBuilder("place"));
    }

    public List<PAGEXMLAnnotation> extractPersonNamesAsList(String path)
    {
        return extractValues(path, new SimpleBuilder("person"));
    }
    
    public List<PAGEXMLAnnotation> extractOrganizationsAsList(String path)
    {
        return extractValues(path, new SimpleBuilder("organization"));
    }

    private <A extends PAGEXMLAnnotation> IDictionary extractValuesAsDict(String path, PAGEXMLValueBuilder builder)
    {
        return builder.toDictionary(extractValues(path, builder));
    }

    private <A extends PAGEXMLAnnotation> List<A> extractValues(String path, PAGEXMLValueBuilder builder)
    {
        List<A> values = new LinkedList<>();
        List<File> files = getFileList(path);

        for (int fileIndex = 0; fileIndex < files.size(); fileIndex++)
        {
            values = extractValuesFromPage(values, files.get(fileIndex), builder, fileIndex);
        }

        return values;
    }

    @Override
    public IDictionary extractAbbreviationsFromPage(String path, int page)
    {
        AbbreviationsBuilder builder = new AbbreviationsBuilder();
        List<PAGEXMLAbbreviation> abbrevations = this.<PAGEXMLAbbreviation>extractValuesFromPage(new LinkedList<>(), path, page, builder);
        return builder.toDictionary(abbrevations);
    }

    private <A extends PAGEXMLAnnotation> List<A> extractValuesFromPage(List<A> list, String path, int page, PAGEXMLValueBuilder builder)
    {
        File file = getFileList(path).get(page);
        return extractValuesFromPage(list, file, builder, page);
    }

    private <A extends PAGEXMLAnnotation> List<A> extractValuesFromPage(List<A> list, File file, PAGEXMLValueBuilder builder, int pageIndex)
    {
        NodeList unicodeNodes = getNodesFromFile(file);

        for (int i = 0; i < unicodeNodes.getLength(); i++)
        {
            if (unicodeNodes.item(i).getParentNode().getParentNode().getNodeName().equals("TextLine"))
            {
                list = extractValuesFromLine(list, unicodeNodes.item(i), builder, pageIndex, i);
            }
        }

        return list;
    }

    private String getCustomTagValue(Node unicodeNode)
    {
        Node textLineNode = unicodeNode.getParentNode().getParentNode();
        return textLineNode.getAttributes().getNamedItem("custom").getTextContent();
    }

    private <A extends PAGEXMLAnnotation> List<A> extractValuesFromLine(List<A> list, Node unicodeNode, PAGEXMLValueBuilder builder, int pageIndex, int lineIndex)
    {
        String textContent = unicodeNode.getTextContent();
        String customTagValue = getCustomTagValue(unicodeNode);
        return extractValueFromLine(list, textContent, customTagValue, builder, pageIndex, lineIndex);
    }

    public PAGEXMLIndex createIndex(String path)
    {
        PAGEXMLIndex index = new PAGEXMLIndex();
        index.addTokens(extractValues(path, new TokenBuilder()));
        return index;
    }
}
