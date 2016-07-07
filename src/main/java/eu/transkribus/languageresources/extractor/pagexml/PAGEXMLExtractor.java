/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.extractor;

import eu.transkribus.languageresources.*;
import eu.transkribus.languageresources.interfaces.ITextExtractor;
import eu.transkribus.languageresources.util.PAGEFileComparator;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author max
 */
public class PAGEXMLExtractor implements ITextExtractor
{

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
            
            if(fileIndex + 1 < files.size())
                content.append(splitCharacter);
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
            Node node;
            int numNodes = nList.getLength();

            for (int nodeIndex = 0; nodeIndex < numNodes; nodeIndex++)
            {
                node = nList.item(nodeIndex);
                content.append(node.getTextContent());

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

}
