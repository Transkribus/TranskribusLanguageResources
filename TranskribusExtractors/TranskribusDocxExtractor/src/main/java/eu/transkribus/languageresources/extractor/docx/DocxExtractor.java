/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.extractor.docx;

import eu.transkribus.interfaces.languageresources.IPagewiseTextExtractor;
import eu.transkribus.languageresources.dictionaries.Dictionary;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

/**
 *
 * @author max
 */
public class DocxExtractor implements IPagewiseTextExtractor
{
    @Override
    public List<String> extractTextFromDocumentPagewise(String path)
    {
        return extractTextFromDocumentPagewise(path, new Properties());
    }

    @Override
    public Map<String, String> extractTextFromDocument(String path)
    {
        return extractTextFromDocument(path, new Properties());
    }
    
    @Override
    public Map<String, String> extractTextFromDocument(String pathToFile, String splitCharacter)
    {
        return extractTextFromDocument(pathToFile, splitCharacter, new Properties());
    }

    @Override
    public Map<String, String> extractTextFromDocument(String pathToFile, Properties properties)
    {
        return extractTextFromDocument(pathToFile, "\n", properties);
    }

    @Override
    public Map<String, String> extractTextFromDocument(String pathToFile, String splitCharacter, Properties properties)
    {
        try
        {
            XWPFDocument docx = new XWPFDocument(new FileInputStream(pathToFile));
            XWPFWordExtractor we = new XWPFWordExtractor(docx);
            
            Map<String, String> contentPerPage = new HashMap<>();
            contentPerPage.put("<default>", we.getText());
            return contentPerPage;
        } catch (IOException ex)
        {
            throw new RuntimeException("Could not find docx for given path: "+pathToFile);
        }
    }

    @Override
    public List<String> extractTextFromDocumentPagewise(String pathToFile, Properties properties)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String extractTextFromPage(String pathToFile, int page)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Dictionary extractAbbreviationsFromPage(String path, int page)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public String extractTextFromPage(String path, int page, Properties properties)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Dictionary extractAbbreviations(String path)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Dictionary extractPlaceNames(String path)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Dictionary extractPersonNames(String path)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
