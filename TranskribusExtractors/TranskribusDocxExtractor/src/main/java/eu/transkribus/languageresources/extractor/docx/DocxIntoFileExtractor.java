/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.extractor.docx;

import eu.transkribus.languageresources.extractor.IntoSingleFileExtractor;


/**
 *
 * @author max
 */
public class DocxIntoFileExtractor extends IntoSingleFileExtractor
{

    @Override
    protected String extractText(String inputFolder, String inputFileName)
    {
        DocxExtractor ex = new DocxExtractor();
        return ex.extractTextFromDocument(inputFolder + inputFileName);
    }
}
