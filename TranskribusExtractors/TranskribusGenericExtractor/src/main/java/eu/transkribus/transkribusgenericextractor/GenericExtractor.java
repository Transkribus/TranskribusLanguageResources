/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.transkribusgenericextractor;

import eu.transkribus.languageresources.extractor.IntoSingleFileExtractor;

import eu.transkribus.languageresources.extractor.docx.DocxIntoFileExtractor;
import eu.transkribus.languageresources.extractor.pdf.PDFIntoFileExtractor;
import eu.transkribus.languageresources.extractor.xml.XMLExtractor;
import eu.transkribus.languageresources.extractor.xml.tei.TEITXTExtractor;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 *
 * @author max
 */
public class GenericExtractor
{

    public GenericExtractor()
    {
    }

    public void extract(String inputFolder, String inputFileName, String outputFolderName)
    {
        if(inputFolder.charAt(inputFolder.length()-1) != '/')
            inputFolder += '/';
        
        String fileExtension = getFileExtansion(inputFileName);
        IntoSingleFileExtractor ex;

        switch (fileExtension)
        {
            case "zip":
                List<String> zipEntryFiles = getFilesFromZip(inputFolder, inputFileName);
                for (String zipEntry : zipEntryFiles)
                {
                    extract(inputFolder, zipEntry, outputFolderName);
                }
                return;
            case "docx":
                ex = new DocxIntoFileExtractor();
                break;
            case "pdf":
                ex = new PDFIntoFileExtractor();
                break;
            case "xml":
                ex = new XMLExtractor();
                break;
            default:
                throw new RuntimeException("Unsupported file extension: " + fileExtension);
        }

        ex.extractTextIntoFiles(inputFolder, inputFileName, outputFolderName);
    }

    public String getFileExtansion(String inputFileName)
    {
        int i = inputFileName.lastIndexOf('.');
        if (i > 0)
        {
            return inputFileName.substring(i + 1);
        }

        throw new RuntimeException("Chould not get extension of file name: " + inputFileName);
    }

    private List<String> getFilesFromZip(String inputFolder, String inputFileName)
    {
        try (ZipFile zipFile = new ZipFile(inputFolder + inputFileName))
        {
            return zipFile.stream().map(ZipEntry::getName).collect(Collectors.toList());
        } catch (Exception ex)
        {
            Logger.getLogger(GenericExtractor.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Could not load zip file with given path: " + inputFolder + inputFileName);
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
}
