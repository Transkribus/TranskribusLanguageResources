/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.extractor.pdf;

import eu.transkribus.languageresources.extractor.IntoSingleFileExtractor;


/**
 *
 * @author max
 */
public class PDFIntoFileExtractor extends IntoSingleFileExtractor
{

    @Override
    protected String extractText(String inputFolder, String inputFileName)
    {
        PDFExtractor ex = new PDFExtractor();
        return ex.extractTextFromDocument(inputFolder + inputFileName);
    }
}
