/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.extractor.docx;

import eu.transkribus.interfaces.languageresources.IPagewiseTextExtractor;
import eu.transkribus.languageresources.dictionaries.Dictionary;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

/**
 *
 * @author max
 */
public class DocxExtractor implements IPagewiseTextExtractor
{

    @Override
    public String extractTextFromDocument(String pathToFile)
    {
        return extractTextFromDocument(pathToFile, "\n");
    }

    @Override
    public String extractTextFromDocument(String pathToFile, String splitCharacter)
    {
        try
        {
            XWPFDocument docx = new XWPFDocument(new FileInputStream(pathToFile));
            XWPFWordExtractor we = new XWPFWordExtractor(docx);
            return we.getText();
        } catch (IOException ex)
        {
            throw new RuntimeException("Could not find docx for given path: "+pathToFile);
        }
    }

    @Override
    public List<String> extractTextFromDocumentPagewise(String pathToFile)
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
