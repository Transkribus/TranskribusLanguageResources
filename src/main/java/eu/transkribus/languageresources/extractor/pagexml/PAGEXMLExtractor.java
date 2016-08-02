/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.extractor.pagexml;

import eu.transkribus.languageresources.*;
import eu.transkribus.languageresources.interfaces.IPagewiseTextExtractor;
import eu.transkribus.languageresources.util.PAGEFileComparator;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.lang3.tuple.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author max
 */
public class PAGEXMLExtractor implements IPagewiseTextExtractor
{

    // Patterns do take time to compile, thus make them
    // static for performance reasons.
    private static Pattern patternAbbrev = Pattern.compile("abbrev\\s*\\{([\\w-,:;\\s]*)\\}");
    private static Pattern patternAbbrevOffset = Pattern.compile(".*offset:([0-9]*);.*");
    private static Pattern patternAbbrevLength = Pattern.compile(".*length:([0-9]*);.*");
    private static Pattern patternAbbrevExpansion = Pattern.compile(".*expansion:([\\w-]*);.*");

    private final Properties properties;

    public PAGEXMLExtractor()
    {
        properties = new Properties();
    }

    public PAGEXMLExtractor(String pathToConfig)
    {
        this(new File(pathToConfig));
    }

    public PAGEXMLExtractor(File configFile)
    {
        properties = new Properties();
        try
        {
            properties.load(new FileInputStream(configFile));
        } catch (IOException ex)
        {
            Logger.getLogger(PAGEXMLExtractor.class.getName()).log(Level.SEVERE, null, ex);
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
        List<String> fileNames = listOfFiles
                .stream()
                .map(((File f) -> f.getName()))
                .collect(Collectors.toList());

        Comparator c = new PAGEFileComparator();
        Collections.sort(fileNames, c);

        List<File> orderedFileList = new ArrayList<>(listOfFiles.size());
        for (String fileName : fileNames)
        {
            orderedFileList.add(new File(path + "/" + fileName));
        }

        return orderedFileList;
    }

    private String getTextFromFile(File f)
    {
        StringBuilder content = new StringBuilder();

        try
        {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(f);

            NodeList nList = doc.getElementsByTagName("Unicode");
            Node unicodeNode;
            int numNodes = nList.getLength();

            for (int nodeIndex = 0; nodeIndex < numNodes; nodeIndex++)
            {
                unicodeNode = nList.item(nodeIndex);
                content.append(getTextFromNode(unicodeNode));

                if (nodeIndex + 1 < numNodes)
                {
                    content.append("\n");
                }
            }

        } catch (Exception ex)
        {
            Logger.getLogger(PAGEXMLExtractor.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }

        return content.toString();
    }

    public String getTextFromNode(Node unicodeNode)
    {
        String textContent = unicodeNode.getTextContent();
        Node textLineNode = unicodeNode.getParentNode().getParentNode();
        String customTagValue = textLineNode.getAttributes().getNamedItem("custom").getTextContent();
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
        List<Abbreviation> abbriviations = extractAbbrevations(textContent, customTagValue);

        String partLeft;
        String partRight;
        int startLeft;
        int expansionDiffSum = 0;

        for (Abbreviation abbriviation : abbriviations)
        {
            startLeft = abbriviation.getOffset() + expansionDiffSum;
            partLeft = textContent.substring(0, startLeft);
            partRight = textContent.substring(startLeft + abbriviation.getLength(), textContent.length());

            textContent = partLeft + abbriviation.getExpansion() + partRight;
            expansionDiffSum += abbriviation.getLengthDiff();
        }

        return textContent;
    }

    private List<Abbreviation> extractAbbrevations(String line, String customTagValue)
    {
        List<Abbreviation> abbriviations = new LinkedList<>();

        Matcher matcherAbbrev = patternAbbrev.matcher(customTagValue);

        Matcher matcherExpansion;
        Matcher matcherOffset;
        Matcher matcherLength;
        Abbreviation abbriviation;
        String abbrev;
        while (matcherAbbrev.find())
        {
            abbriviation = new Abbreviation();
            abbrev = matcherAbbrev.group(1);

            matcherExpansion = patternAbbrevExpansion.matcher(abbrev);
            matcherExpansion.matches();

            if (matcherExpansion.groupCount() > 0)
            {
                try
                {
                    String expansion = matcherExpansion.group(1);
                    abbriviation.setExpansion(expansion);

                    matcherOffset = patternAbbrevOffset.matcher(abbrev);
                    matcherOffset.matches();
                    abbriviation.setOffset(matcherOffset.group(1));

                    matcherLength = patternAbbrevLength.matcher(abbrev);
                    matcherLength.matches();
                    abbriviation.setLength(matcherLength.group(1));

                    abbriviation.setAbbreviationFromLine(line);

                    abbriviations.add(abbriviation);
                } catch (IllegalStateException e)
                {
                    // If no expension tag is being found, groupCount is still 1
                }
            }
        }

        return abbriviations;
    }
}
