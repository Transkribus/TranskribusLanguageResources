/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.dictionaries;

import eu.transkribus.languageresources.util.SimpleDictFileHandler;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author max
 */
public class FrenchDictCreation
{

    private final String pathToFile;

    public FrenchDictCreation()
    {
        ClassLoader classLoader = getClass().getClassLoader();
        pathToFile = new File(classLoader.getResource("lexique-mots.txt").getFile()).getAbsolutePath();
    }

    @Test
    @Ignore
    public void createDict()
    {
        Dictionary dict = new Dictionary();

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(pathToFile));
            String line;
            while ((line = br.readLine()) != null)
            {
                dict.addEntry(line.trim());
            }

            SimpleDictFileHandler.write(new File("lexique-mots.dict"), dict.getEntries());
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
