/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.extractor.pagexml;

import eu.transkribus.languageresources.dictionaries.Dictionary;
import eu.transkribus.languageresources.extractor.xml.XMLExtractor;
import eu.transkribus.languageresources.interfaces.IPagewiseTextExtractor;
import eu.transkribus.languageresources.util.PAGEFileComparator;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author max
 */
public class PAGEXMLExtractor extends XMLExtractor implements IPagewiseTextExtractor
{

    // Patterns do take time to compile, thus make them
    // static for performance reasons.
    private static Pattern patternAbbrev = Pattern.compile("abbrev\\s*\\{([\\w-,:;\\s]*)\\}");
    private static Pattern patternAbbrevOffset = Pattern.compile(".*offset:([0-9]*);.*");
    private static Pattern patternAbbrevLength = Pattern.compile(".*length:([0-9]*);.*");
    private static Pattern patternAbbrevExpansion = Pattern.compile(".*expansion:([\\w-]*);.*");
    
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
        StringBuilder content = new StringBuilder();
        List<File> files = getFileList(path);

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
        File[] files = folder.listFiles();
        List<File> listOfFiles = Arrays.asList(folder.listFiles());
        List<String> fileNames = new ArrayList<>(listOfFiles.size());
        for(File f : listOfFiles)
        {
            fileNames.add(f.getName());
        }

        Comparator c = new PAGEFileComparator();
        Collections.sort(fileNames, c);

        List<File> orderedFileList = new ArrayList<>(listOfFiles.size());
        for (String fileName : fileNames)
        {
            orderedFileList.add(new File(path + "/" + fileName));
        }

        return orderedFileList;
    }

    private NodeList getNodesFromFile(File f)
    {
        try
        {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(f);

            return doc.getElementsByTagName("Unicode");

        } catch (Exception ex)
        {
            throw new RuntimeException("Could not load nodes from file!");
        }
    }

    private String getTextFromFile(File f)
    {
        StringBuilder content = new StringBuilder();

        NodeList unicodeNodes = getNodesFromFile(f);
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
        if(customNode != null) {
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
        List<PAGEXMLAbbreviation> abbreviations = extractAbbrevationsFromLine(textContent, customTagValue);

        String partLeft;
        String partRight;
        int startLeft;
        int expansionDiffSum = 0;

        for (PAGEXMLAbbreviation abbreviation : abbreviations)
        {
            startLeft = abbreviation.getOffset() + expansionDiffSum;
            partLeft = textContent.substring(0, startLeft);
            partRight = textContent.substring(startLeft + abbreviation.getLength(), textContent.length());

            textContent = partLeft + abbreviation.getExpansion() + partRight;
            expansionDiffSum += abbreviation.getLengthDiff();
        }

        return textContent;
    }

    private List<PAGEXMLAbbreviation> extractAbbrevationsFromLine(String line, String customTagValue)
    {
        List<PAGEXMLAbbreviation> abbreviations = new LinkedList<>();

        Matcher matcherAbbrev = patternAbbrev.matcher(customTagValue);

        Matcher matcherExpansion;
        Matcher matcherOffset;
        Matcher matcherLength;
        PAGEXMLAbbreviation abbreviation;
        String abbrev;
        while (matcherAbbrev.find())
        {
            abbreviation = new PAGEXMLAbbreviation();
            abbrev = matcherAbbrev.group(1);

            matcherExpansion = patternAbbrevExpansion.matcher(abbrev);
            matcherExpansion.matches();

            if (matcherExpansion.groupCount() > 0)
            {
                try
                {
                    String expansion = matcherExpansion.group(1);
                    abbreviation.setExpansion(expansion);

                    matcherOffset = patternAbbrevOffset.matcher(abbrev);
                    matcherOffset.matches();
                    abbreviation.setOffset(matcherOffset.group(1));

                    matcherLength = patternAbbrevLength.matcher(abbrev);
                    matcherLength.matches();
                    abbreviation.setLength(matcherLength.group(1));

                    abbreviation.setAbbreviationFromLine(line);

                    abbreviations.add(abbreviation);
                } catch (IllegalStateException e)
                {
                    // If no expension tag is being found, groupCount is still 1
                }
            }
        }

        return abbreviations;
    }

    public Dictionary extractAbbrevations(String line, String customTagValue)
    {
        return listToDictionary(extractAbbrevationsFromLine(line, customTagValue));
    }

    @Override
    public Dictionary extractAbbreviations(String path)
    {
        List<PAGEXMLAbbreviation> abbreviations = new LinkedList<>();
        List<File> files = getFileList(path);

        for (int fileIndex = 0; fileIndex < files.size(); fileIndex++)
        {
            abbreviations.addAll(PAGEXMLExtractor.this.extractAbbreviationsFromPage(files.get(fileIndex)));
        }

        return listToDictionary(abbreviations);
    }

    private Dictionary listToDictionary(List<PAGEXMLAbbreviation> list)
    {
        Dictionary dictionary = new Dictionary();

        for (PAGEXMLAbbreviation abbr : list)
        {
            dictionary.addEntry(abbr.getAbbreviation());
            
            if (abbr.getExpansion() != null)
            {
                dictionary.addAdditionalValue(abbr.getAbbreviation(), abbr.getExpansion());
            }
        }

        return dictionary;
    }

    @Override
    public Dictionary extractAbbreviationsFromPage(String path, int page)
    {
        List<File> files = getFileList(path);
        List<PAGEXMLAbbreviation> abbreviations = PAGEXMLExtractor.this.extractAbbreviationsFromPage(files.get(page));
        return listToDictionary(abbreviations);
    }

    private List<PAGEXMLAbbreviation> extractAbbreviationsFromPage(File f)
    {
        List<PAGEXMLAbbreviation> abbreviations = new LinkedList<>();
        NodeList unicodeNodes = getNodesFromFile(f);

        for (int i = 0; i < unicodeNodes.getLength(); i++)
        {
            abbreviations.addAll(extractAbbreviationsFromPage(unicodeNodes.item(i)));
        }

        return abbreviations;
    }

    private List<PAGEXMLAbbreviation> extractAbbreviationsFromPage(Node unicodeNode)
    {
        String textContent = unicodeNode.getTextContent();
        Node textLineNode = unicodeNode.getParentNode().getParentNode();
        String customTagValue = textLineNode.getAttributes().getNamedItem("custom").getTextContent();
        return extractAbbrevationsFromLine(textContent, customTagValue);
    }

}
