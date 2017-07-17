/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.dictionaries;

import eu.transkribus.interfaces.languageresources.IPagewiseTextExtractor;
import eu.transkribus.languageresources.extractor.xml.tei.DTATEIExtractor;
import eu.transkribus.languageresources.util.SimpleDictFileHandler;
import eu.transkribus.tokenizer.TokenizerConfig;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import org.apache.commons.io.FilenameUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 *
 * @author max
 */
public class DictionariesForStaZH
{

    public DictionariesForStaZH()
    {
    }

    @BeforeClass
    public static void setUpClass()
    {
    }

    @AfterClass
    public static void tearDownClass()
    {
    }

    @Before
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {
    }

    private void listf(String directoryName, List<File> files)
    {
        File directory = new File(directoryName);

        // get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList)
        {
            if (file.isFile() && FilenameUtils.getExtension(file.getAbsolutePath()).equals("xml"))
            {
                files.add(file);
            } else if (file.isDirectory())
            {
                listf(file.getAbsolutePath(), files);
            }
        }
    }

    private List<File> getAllXMLFiles(String directoryName)
    {
        List<File> files = new LinkedList<>();
        listf(directoryName, files);
        return files;
    }

    @Test
    public void hello() throws IOException
    {
        List<File> files = getAllXMLFiles("/home/max/Downloads/TKR_RRB_Transkripte/");
//        files = files.subList(0, 10000);
        
        StAZhTEIExtractor textExtraktor = new StAZhTEIExtractor();

        Dictionary dictionary = new Dictionary(new LinkedList<>());
        StringJoiner sj = new StringJoiner(" ");
        TokenizerConfig tokenizer = new TokenizerConfig();
        
        for (int i = 0; i < files.size(); i++)
        {
            System.out.println("Extracting "+(i+1)+"/"+files.size());
                
            File f = files.get(i);
            String extractedText = textExtraktor.extractTextFromDocumentPagewise(f.getAbsolutePath()).stream().collect(Collectors.joining(" "));
            List<String> tokenizedText = tokenizer.tokenize(extractedText);
            Dictionary tmp = new Dictionary(tokenizedText);
            dictionary.merge(tmp);
        }

        SimpleDictFileHandler.write(new File("stazh.dict"), dictionary.getEntries());
    }

    private static class StAZhTEIExtractor extends DTATEIExtractor implements IPagewiseTextExtractor
    {

        public StAZhTEIExtractor()
        {
            super();
        }

        public StAZhTEIExtractor(String pathToConfig)
        {
            super(pathToConfig);
        }

        public StAZhTEIExtractor(File configFile)
        {
            super(configFile);
        }

        @Override
        public List<String> extractTextFromDocumentPagewise(String path)
        {
            Document document = this.getDocumentFromFile(path);
            List<String> pages = new ArrayList<>();

            NodeList pbs = document.getElementsByTagName("p");
            for (int i = 0; i < pbs.getLength(); i++)
            {
                pages.add(pbs.item(i).getTextContent());
            }
            return pages;
        }
    }
}
