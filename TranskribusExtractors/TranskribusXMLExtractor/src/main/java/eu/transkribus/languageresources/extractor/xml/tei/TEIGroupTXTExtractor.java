/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.extractor.xml.tei;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author max
 */
public class TEIGroupTXTExtractor
{

    private final String inputFolderName;
    private final String outputFolderName;
    private final String resourceFolderName;

    public TEIGroupTXTExtractor(String resourceFolderName, String inputFolderName, String outputFolderName)
    {
        this.resourceFolderName = resourceFolderName;
        this.inputFolderName = inputFolderName;
        this.outputFolderName = outputFolderName;
    }

    public void extractIntoTXT()
    {
        String folderOut = resourceFolderName + outputFolderName;
        TEITXTExtractor extractor;

        for (File fileIn : getXMLFiles())
        {
            System.out.println("Reading file "+fileIn);
//            extractor = new TEITXTExtractor(fileIn.getAbsolutePath(), folderOut);
//            extractor.extract();
        }
    }

    private List<File> getXMLFiles()
    {
        File folder = new File(resourceFolderName + inputFolderName);
        File[] listOfFiles = folder.listFiles();
        List<File> files = new LinkedList<>();

        for (File file : listOfFiles)
        {
            if (file.isFile() && file.getName().endsWith("xml"))
            {
                files.add(file);
            }
        }

        return files;
    }

    private void ensureDir(File folder)
    {
        if (!folder.exists())
        {
            try
            {
                folder.mkdir();
            } catch (SecurityException se)
            {
                se.printStackTrace();
            }
        }
    }

    private PrintWriter createPrintWriter(PrintWriter pw, StringBuilder textToWrite, String path)
    {
        if (pw != null)
        {
            write(pw, textToWrite.toString());
        }

        try
        {
            return new PrintWriter(path);
        } catch (FileNotFoundException ex)
        {
            throw new RuntimeException("Could not create PrintWriter for given path: " + path);
        }
    }
    
    private void write(PrintWriter pw, String text)
    {
        if(text.startsWith("\n"))
            text = text.substring(1);
        
        if(text.endsWith("\n"))
            text = text.substring(0, text.length()-2);
        
        text = text.replaceAll("\n+", "\n");
        pw.write(text);
        pw.flush();
    }

    private void writeIntoFile(File fileIn, File folderOut)
    {
        ensureDir(folderOut);
        DTATEIExtractor extractor = new TEI2TxtExtractor();
        String extractTextFromDocument = extractor.extractTextFromDocument(fileIn.getAbsolutePath());

        Matcher m = null;
        String path = null;
        String previousLine = null;
        String folder = null;
        String number = null;
        PrintWriter pw = null;
        Pattern pattern1 = Pattern.compile("§([A-Z0-9]+):([0-9]+)=");
        Pattern pattern2 = Pattern.compile("§([A-Z0-9]+)=");
        StringBuilder textToWrite = new StringBuilder();

        for (String line : extractTextFromDocument.split("\n"))
        {
            line = line.trim();
            if (line.startsWith("§"))
            {
                boolean found = false;
                m = pattern1.matcher(line);
                if (m.find())
                {
                    folder = m.group(1);
                    number = m.group(2);
                    path = resourceFolderName + outputFolderName + folder + "/" + number + ".txt";
                    found = true;
                }

                if (!found)
                {
                    m = pattern2.matcher(line);
                    if (m.find())
                    {
                        number = m.group(1);
                        path = resourceFolderName + outputFolderName + number + ".txt";
                        found = true;
                    }
                }

                pw = createPrintWriter(pw, textToWrite, path);
                continue;
            }

            if (line.length() > 0 && !line.equals("\n"))
            {
                line = line.replaceAll("==", "\n");
                line = line.replaceAll("\n+", "\n");
                if (previousLine != null && previousLine.endsWith("\n") && line.startsWith("\n"))
                {
                    line = line.substring(1);
                }

                textToWrite.append(line);
                previousLine = line;
            }
        }

        write(pw, textToWrite.toString());
    }
}
