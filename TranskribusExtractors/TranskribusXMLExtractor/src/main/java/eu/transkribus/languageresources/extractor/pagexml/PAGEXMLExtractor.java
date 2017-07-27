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
import eu.transkribus.interfaces.languageresources.IPagewiseTextExtractor;
import eu.transkribus.languageresources.util.PAGEFileComparator;
import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.math3.util.Pair;

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

    private List<Node> getNodesFromXml(Document doc)
    {
        try
        {
            NodeList nodeList = doc.getElementsByTagName("Unicode");
            List<Node> unicodeNodes = new LinkedList<>();

            for (int i = 0; i < nodeList.getLength(); i++)
            {
                if (nodeList.item(i).getParentNode().getParentNode().getNodeName().equals("TextLine"))
                {
                    unicodeNodes.add(nodeList.item(i));
                }
            }

            return unicodeNodes;

        } catch (Exception ex)
        {
            System.out.println(ex);
            throw new RuntimeException("Could not load nodes from file!");
        }
    }

    private List<Node> getNodesFromFile(File f)
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
        List<Node> unicodeNodes = getNodesFromXml(d);
        return getTextFromNodeList(unicodeNodes);
    }

    protected String getTextFromFile(File f)
    {
        List<Node> unicodeNodes = getNodesFromFile(f);
        return getTextFromNodeList(unicodeNodes);
    }

    private String getTextFromNodeList(List<Node> unicodeNodes)
    {
        StringBuilder content = new StringBuilder();
        Node unicodeNode;

        int numNodes = unicodeNodes.size();

        for (int nodeIndex = 0; nodeIndex < numNodes; nodeIndex++)
        {
            unicodeNode = unicodeNodes.get(nodeIndex);
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
        List<Node> unicodeNodes = getNodesFromFile(file);

        for (int i = 0; i < unicodeNodes.size(); i++)
        {
            if (unicodeNodes.get(i).getParentNode().getParentNode().getNodeName().equals("TextLine"))
            {
                list = extractValuesFromLine(list, unicodeNodes.get(i), builder, pageIndex, i);
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

    public List<Pair<String, String>> extractTextFromDocumentPairwise(String path1, String path2)
    {
        List<File> fileList1 = getFileList(path1);
        List<File> fileList2 = getFileList(path2);

        if (fileList1.size() != fileList2.size())
        {
            return createWholeDocumentPair(fileList1, fileList2);
        }

        return createPageWisePairs(fileList1, fileList2);
    }
    
    public List<Pair<String, String>> extractTextFromFilePairwise(String path1, String path2)
    {
        List<File> fileList1 = Arrays.asList(new File[]{new File(path1)});
        List<File> fileList2 = Arrays.asList(new File[]{new File(path2)});

        if (fileList1.size() != fileList2.size())
        {
            return createWholeDocumentPair(fileList1, fileList2);
        }

        return createPageWisePairs(fileList1, fileList2);
    }

    private List<Pair<String, String>> createWholeDocumentPair(List<File> fileList1, List<File> fileList2)
    {
        String text1 = extractTextFromFileList(fileList1, "\n");
        String text2 = extractTextFromFileList(fileList2, "\n");
        return Arrays.asList(new Pair(text1, text2));
    }

    private List<Pair<String, String>> createPageWisePairs(List<File> fileList1, List<File> fileList2)
    {
        List<Pair<String, String>> pagePairs = new LinkedList<>();

        for (int fileIndex = 0; fileIndex < fileList1.size(); fileIndex++)
        {
            List<Node> nodeList1 = getNodesFromFile(fileList1.get(fileIndex));
            List<Node> nodeList2 = getNodesFromFile(fileList2.get(fileIndex));

            pagePairs.addAll(getPagePairs(nodeList1, nodeList2));
        }

        return pagePairs;
    }

    private List<Pair<String, String>> getPagePairs(List<Node> nodeList1, List<Node> nodeList2)
    {
        if(allAreAlligned(nodeList1, nodeList2))
        {
            List<Pair<String, String>> linePairs = new LinkedList<>();
            
            for(int nodeIndex = 0; nodeIndex < nodeList1.size(); nodeIndex++)
            {
                String text1 = getTextFromNode(nodeList1.get(nodeIndex));
                String text2 = getTextFromNode(nodeList2.get(nodeIndex));
                linePairs.add(new Pair(text1, text2));
            }
            
            return linePairs;
        }else
        {
            String text1 = getTextFromNodeList(nodeList1);
            String text2 = getTextFromNodeList(nodeList2);
            return Arrays.asList(new Pair(text1, text2));
        }
    }
    
    private boolean allAreAlligned(List<Node> nodeList1, List<Node> nodeList2)
    {
        if(nodeList1.size() != nodeList2.size())
            return false;
        
        for(int nodeIndex = 0; nodeIndex < nodeList1.size(); nodeIndex++)
        {
            if(!equalsBaseline(nodeList1.get(nodeIndex), nodeList2.get(nodeIndex)))
                return false;
        }
        
        return true;
    }

    private boolean equalsBaseline(Node node1, Node node2)
    {
        if (node1 == node2)
        {
            return true;
        }
        
        String points1 = node1.getAttributes().getNamedItem("points").getTextContent();
        String points2 = node2.getAttributes().getNamedItem("points").getTextContent();
        return points1.equals(points2);
    }
}
