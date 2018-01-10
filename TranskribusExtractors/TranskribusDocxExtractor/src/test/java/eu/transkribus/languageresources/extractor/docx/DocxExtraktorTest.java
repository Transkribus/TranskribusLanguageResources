/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.extractor.docx;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author max
 */
public class DocxExtraktorTest
{
    
    public DocxExtraktorTest()
    {
    }

//    @Test
//    public void testExtractTextFromDocument()
//    {
//        ClassLoader classLoader = getClass().getClassLoader();
//        String pathToFile = new File(classLoader.getResource("ttexter.docx").getFile()).getAbsolutePath();
//        DocxExtractor instance = new DocxExtractor();
//        String text = instance.extractTextFromDocument(pathToFile);
//        
//        assertEquals(4197154, text.length());
//    }
    
    private void write(String text, String filePath) throws FileNotFoundException
    {
        PrintWriter pw = new PrintWriter(filePath);
        pw.write(text);
        pw.flush();
    }
    
    @Test
    public void testExtract() throws FileNotFoundException
    {
        File folder = new File("src/test/resources/Pestarchiv/");
        File[] listOfFiles = folder.listFiles();
        DocxExtractor e = new DocxExtractor();
        
        for(File f : listOfFiles)
        {
            String text = e.extractTextFromDocument(f.getAbsolutePath());
            String name = f.getName();
            name = name.split("\\.")[0];
            
            write(text, "src/test/resources/Pestarchiv_txt/"+name+".txt");
        }
    }
}
