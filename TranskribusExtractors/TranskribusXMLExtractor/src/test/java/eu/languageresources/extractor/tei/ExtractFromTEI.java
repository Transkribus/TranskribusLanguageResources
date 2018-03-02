/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.languageresources.extractor.tei;

import eu.transkribus.languageresources.extractor.xml.tei.DTATEIExtractor;
import eu.transkribus.languageresources.extractor.xml.tei.TEIGroupTXTExtractor;
import eu.transkribus.languageresources.extractor.xml.tei.TEITXTExtractor;
import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.junit.Test;

/**
 *
 * @author max
 */
public class ExtractFromTEI
{

    public ExtractFromTEI()
    {
    }

    @Test
    public void extractFolder() throws Exception
    {
        File file = new File("src/test/resources/sks-rabbe-transcriptions");
        String[] directories = file.list((File current, String name) -> new File(current, name).getName().endsWith("xml"));

        TEITXTExtractor extractor = new TEITXTExtractor();
        String outputFolder;

        for (String inputFile : directories)
        {
//            outputFolder = inputFile +"/";
//            inputFile = "src/test/resources/" + inputFile;
            outputFolder = "src/test/resources/sks-rabbe-transcriptions_txt/";
            extractor.extractTextIntoFiles("src/test/resources/sks-rabbe-transcriptions/", inputFile, outputFolder);
        }
    }

    private void extractFolder(String folderName)
    {
        String resourceFolder = "src/test/resources/";
        String inputFolder = folderName + "/";
        String outputFolder = "src/test/resources/text/" + folderName + "/";

        TEIGroupTXTExtractor extractor = new TEIGroupTXTExtractor(resourceFolder, inputFolder, outputFolder);
        extractor.extractIntoTXT();
    }
}
