 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.extractor.pdf;

import eu.transkribus.languageresources.dictionaries.Dictionary;
import eu.transkribus.interfaces.languageresources.IPagewiseTextExtractor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

/**
 *
 * @author max
 */
public class PDFExtraktor implements IPagewiseTextExtractor
{

    @Override
    public String extractTextFromDocument(String pathToFile)
    {
        return extractTextFromDocument(pathToFile, "\n");
    }

    @Override
    public String extractTextFromDocument(String pathToFile, String splitCharacter)
    {
        List<String> pageWiseText = extractTextFromDocumentPagewise(pathToFile);
        StringBuilder sb = new StringBuilder();

        for (int pageId = 0; pageId < pageWiseText.size(); pageId++)
        {
            sb.append(pageWiseText.get(pageId));

            if (pageId + 1 < pageWiseText.size())
            {
                sb.append(splitCharacter);
            }
        }

        return sb.toString();
    }

    @Override
    public List<String> extractTextFromDocumentPagewise(String pathToFile)
    {
        List<String> pageWiseText = new LinkedList<>();

        COSDocument cosDoc = null;
        PDDocument pdDoc = null;
        try
        {
            PDFParser parser = new PDFParser(new FileInputStream(new File(pathToFile)));
            parser.parse();
            cosDoc = parser.getDocument();
            pdDoc = new PDDocument(cosDoc);

            for (int pageId = 0; pageId < pdDoc.getNumberOfPages(); pageId++)
            {
                pageWiseText.add(extractTextFromPage(pdDoc, pageId));
            }
        } catch (IOException ex)
        {
            Logger.getLogger(PDFExtraktor.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            if ( pdDoc != null )
                try {
                    pdDoc.close();
                }
                catch ( IOException ex ) {
                    Logger.getLogger(PDFExtraktor.class.getName()).log(Level.SEVERE, null, ex);
                }
            if ( cosDoc != null )
                try {
                    cosDoc.close();
                }
                catch ( IOException ex ) {
                    Logger.getLogger(PDFExtraktor.class.getName()).log(Level.SEVERE, null, ex);
                }
        }

        return pageWiseText;
    }

    @Override
    public String extractTextFromPage(String pathToFile, int page)
    {
        StringBuilder sb = new StringBuilder();

        COSDocument cosDoc = null;
        PDDocument pdDoc = null;
        try
        {
            PDFParser parser = new PDFParser(new FileInputStream(new File(pathToFile)));
            parser.parse();
            cosDoc = parser.getDocument();
            pdDoc = new PDDocument(cosDoc);
            sb.append(extractTextFromPage(pdDoc, page));
        } catch (IOException ex)
        {
            Logger.getLogger(PDFExtraktor.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            if ( pdDoc != null )
                try {
                    pdDoc.close();
                }
                catch ( IOException ex ) {
                    Logger.getLogger(PDFExtraktor.class.getName()).log(Level.SEVERE, null, ex);
                }
            if ( cosDoc != null )
                try {
                    cosDoc.close();
                }
                catch ( IOException ex ) {
                    Logger.getLogger(PDFExtraktor.class.getName()).log(Level.SEVERE, null, ex);
                }
        }

        return sb.toString();
    }

    private String extractTextFromPage(PDDocument pdDoc, int page)
    {
        try
        {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            pdDoc.getNumberOfPages();
            pdfStripper.setStartPage(page+1);
            pdfStripper.setEndPage(page+1);

            return pdfStripper.getText(pdDoc);
        } catch (IOException ex)
        {
            Logger.getLogger(PDFExtraktor.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex.getMessage());
        }
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
