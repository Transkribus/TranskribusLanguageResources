/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.dictionaries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;

/**
 *
 * @author max
 */
public class FinishDictCreation
{

    private final String pathToFile;

    public FinishDictCreation()
    {
        ClassLoader classLoader = getClass().getClassLoader();
        pathToFile = new File(classLoader.getResource("klk_fi_1grams_all").getFile()).getAbsolutePath();
    }

    @Test
    public void createDict()
    {
        Dictionary dict = new Dictionary();

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(pathToFile));
            String line;
            while ((line = br.readLine()) != null)
            {
                line = line.trim();
                String[] parts = line.split(" ");
                String type = parts[1].replaceAll("\\P{L}", "");
                int freq = new Integer(parts[0]);

                if (freq > 50 && !type.isEmpty())
                {
                    dict.addEntry(type, freq);
                }
            }

            DictionaryUtils.save("fin.dict", dict);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
