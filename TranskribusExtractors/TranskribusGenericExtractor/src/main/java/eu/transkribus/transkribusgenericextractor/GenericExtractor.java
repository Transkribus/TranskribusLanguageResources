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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 *
 * @author max
 */
public class GenericExtractor
{

    public GenericExtractor()
    {
    }
    
    private String checkPath(String path)
    {
        if (path.charAt(path.length() - 1) != File.pathSeparatorChar)
        {
            path += '/';
        }
        
        return path;
    }

    public void extract(String inputFolder, String inputFileName, String outputFolderName, Properties properties)
    {
        inputFolder = checkPath(inputFolder);
        outputFolderName = checkPath(outputFolderName);

        extract(inputFolder, "", inputFileName, outputFolderName, properties);
    }

    private void extract(String startFolder, String currentSubFolder, String inputFileName, String outputFolderName, Properties properties)
    {
        String currentPath = startFolder + currentSubFolder;
        currentPath = checkPath(currentPath);

        String fileExtension = getFileExtansion(inputFileName);
        IntoSingleFileExtractor ex;

        switch (fileExtension)
        {
            case "folder":
                File folder = new File(currentPath + File.separatorChar + inputFileName);
                File[] files = folder.listFiles();
                for (File file : files)
                {
                    String newSubFolder;
                    if (currentSubFolder.length() == 0)
                    {
                        newSubFolder = inputFileName;
                    } else
                    {
                        newSubFolder = currentSubFolder + File.separatorChar + inputFileName;
                    }

                    extract(startFolder, newSubFolder, file.getName(), outputFolderName, properties);
                }
                
                return;
            case "zip":
                String tmpFolderName = unzip(currentPath, inputFileName);
                extract(startFolder, currentSubFolder, tmpFolderName, outputFolderName, properties);
                new File(currentPath + tmpFolderName).delete();
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
            case "html":
                return;
            default:
                throw new RuntimeException("Unsupported file extension: " + fileExtension);
        }

        ex.extractTextIntoFiles(currentPath, inputFileName, startFolder + outputFolderName, properties);
    }

    public String getFileExtansion(String inputFileName)
    {
        int i = inputFileName.lastIndexOf('.');
        if (i > 0)
        {
            return inputFileName.substring(i + 1);
        }

        return "folder";
    }

    private String unzip(String inputFolder, String inputFileName)
    {
        String tmpFolderName = UUID.randomUUID().toString();
        String zipFilePath = inputFolder + inputFileName;
        String destDir = inputFolder + tmpFolderName;

        byte[] buffer = new byte[1024];

        try
        {
            File folder = new File(destDir);
            if (!folder.exists())
            {
                folder.mkdir();
            }

            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath));
            ZipEntry ze = zis.getNextEntry();

            while (ze != null)
            {
                String fileName = ze.getName();

                if (fileName.contains("."))
                {
                    File newFile = new File(destDir + File.separator + fileName);
                    new File(newFile.getParent()).mkdirs();

                    FileOutputStream fos = new FileOutputStream(newFile);

                    int len;
                    while ((len = zis.read(buffer)) > 0)
                    {
                        fos.write(buffer, 0, len);
                    }

                    fos.close();
                }

                ze = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }

        return tmpFolderName;
    }

    private List<String> getFilesFromZip(String inputFolder, String inputFileName)
    {
        try (ZipFile zipFile = new ZipFile(inputFolder + inputFileName))
        {
            return zipFile
                    .stream()
                    .map(ZipEntry::getName)
                    .filter((String name) -> name.contains("."))
                    .collect(Collectors.toList());
        } catch (Exception ex)
        {
            Logger.getLogger(GenericExtractor.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Could not load zip file with given path: " + inputFolder + inputFileName);
        }
    }
}
