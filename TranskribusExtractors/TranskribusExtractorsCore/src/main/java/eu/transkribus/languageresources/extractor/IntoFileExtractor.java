/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.extractor;

/**
 *
 * @author max
 */
public abstract class IntoFileExtractor
{
    public abstract void extractTextIntoFiles(String inputFolder, String inputFileName, String outputFolderName);
}
