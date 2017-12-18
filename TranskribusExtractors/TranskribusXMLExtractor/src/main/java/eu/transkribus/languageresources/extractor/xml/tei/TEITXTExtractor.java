/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.extractor.xml.tei;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author max
 */
public class TEITXTExtractor
{

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
        if (text.startsWith("\n"))
        {
            text = text.substring(1);
        }

        if (text.endsWith("\n"))
        {
            text = text.substring(0, text.length() - 2);
        }

        text = text.replaceAll("\n+", "\n");
        pw.write(text);
        pw.flush();
    }

    public void extractTextIntoFiles(String inputFolder, String inputFileName, String outputFolderName)
    {
        File fileInFile = new File(inputFolder + inputFileName);

        ensureDir(new File(outputFolderName));
        DTATEIExtractor extractor = new TEI2TxtExtractor();
        String extractTextFromDocument = extractor.extractTextFromDocument(fileInFile.getAbsolutePath());

        Matcher m = null;
        String path = null;
        String previousLine = null;
        String folder = null;
        String number = null;
        PrintWriter pw = null;
        Pattern pattern1 = Pattern.compile("§([A-Z0-9]+):([0-9]+)=");
        Pattern pattern2 = Pattern.compile("§([A-Za-z0-9_]+)=");
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
                    path = outputFolderName + folder + "/" + number + ".txt";
                    ensureDir(new File(outputFolderName + folder + "/"));
                    found = true;
                }

                if (!found)
                {
                    m = pattern2.matcher(line);
                    if (m.find())
                    {
                        number = m.group(1);
                        path = outputFolderName + "/" + number + ".txt";
                        found = true;
                    }
                }

                pw = createPrintWriter(pw, textToWrite, path);
                textToWrite = new StringBuilder();
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

    public static void main(String[] args)
    {
        List<String> folders = Arrays.asList("XML-Edited-6-0706_0854",
                "XML-Edited-7-selected-0001_0935",
                "XML-Edited-8-0001_0032",
                "XML-Unedited-7-0567_0721");

        TEITXTExtractor ex = new TEITXTExtractor();
        String resourceFolder = "src/test/resources/";

        for (String folder : folders)
        {
            File[] listOfFiles = new File(resourceFolder + folder).listFiles();

            for (File f : listOfFiles)
            {
                ex.extractTextIntoFiles(resourceFolder+folder+"/", f.getName(), resourceFolder + folder + "_txt");
            }
        }
    }
}
