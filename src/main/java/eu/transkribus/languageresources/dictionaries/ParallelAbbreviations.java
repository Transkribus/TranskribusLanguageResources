/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.dictionaries;

import eu.transkribus.interfaces.IDictionary;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author max
 */
public class ParallelAbbreviations
{

    private final String fileAbbreviated;
    private final String fileExpanded;

    public ParallelAbbreviations(String fileAbbreviated, String fileExpanded)
    {
        this.fileAbbreviated = fileAbbreviated;
        this.fileExpanded = fileExpanded;
    }

    private List<String> getLinesFromFile(String pathToFile) throws FileNotFoundException, IOException
    {
        List<String> lines = new ArrayList<>();

        File file = new File(pathToFile);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line;
        while ((line = br.readLine()) != null)
        {
            lines.add(line);
        }

        return lines;
    }

    public IDictionary createDictionary() throws IOException, URISyntaxException
    {
        List<String> linesAbbreviated = getLinesFromFile(fileAbbreviated);
        List<String> linesExpanded = getLinesFromFile(fileExpanded);

        if (linesAbbreviated.size() != linesExpanded.size())
        {
            throw new RuntimeException("The two given files differ in length!");
        }

        Dictionary dict = new Dictionary();
        for (int i = 0; i < linesAbbreviated.size(); i++)
        {
            compareLines(dict, linesAbbreviated.get(i), linesExpanded.get(i));
        }

        return dict;
    }

    private void compareLines(Dictionary dict, String lineAbbreviated, String lineExpanded)
    {
        String[] tokensAbbreviated = lineAbbreviated.split(" ");
        String[] tokensExpanded = lineExpanded.split(" ");

        if (tokensAbbreviated.length == tokensExpanded.length)
        {
            for (int i = 0; i < tokensAbbreviated.length; i++)
            {
                if (!tokensAbbreviated[i].equals(tokensExpanded[i]))
                {
                    dict.addValue(tokensAbbreviated[i], tokensExpanded[i], 1);
                }
            }
        }
    }
}
