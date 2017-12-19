/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.extractor;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author max
 */
public abstract class IntoSingleFileExtractor extends IntoFileExtractor
{

    @Override
    public void extractTextIntoFiles(String inputFolder, String inputFileName, String outputFolderName)
    {
        String text = extractText(inputFolder, inputFileName);
        try(PrintWriter pw = new PrintWriter(outputFolderName + inputFileName))
        {
            pw.write(text);
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(IntoSingleFileExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected abstract String extractText(String inputFolder, String inputFileName);
}
