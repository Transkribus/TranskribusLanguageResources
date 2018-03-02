/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.extractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author max
 */
public abstract class IntoSingleFileExtractor extends IntoFileExtractor
{

    @Override
    public void extractTextIntoFiles(String inputFolder, String inputFileName, String outputFolderName, Properties properties)
    {
        String outputFileName = inputFileName.split("\\.")[0] + ".txt";
        File file = new File(outputFolderName);
        file.mkdirs();

        Map<String, String> text = extractText(inputFolder, inputFileName, properties);
        String cleanedText;

        for (Map.Entry<String, String> entry : text.entrySet())
        {
            cleanedText = clean(entry.getValue(), properties);

            if (cleanedText.length() > 0 && entry.getKey().length() > 0)
            {
                if (!entry.getKey().equals("<default>"))
                {
                    outputFileName = entry.getKey().split("\\.")[0] + ".txt";
                    file = new File(outputFolderName + outputFileName);
                    new File(file.getParent()).mkdirs();
                }

                try (PrintWriter pw = new PrintWriter(outputFolderName + outputFileName))
                {
                    pw.write(cleanedText);
                } catch (FileNotFoundException ex)
                {
                    Logger.getLogger(IntoSingleFileExtractor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    protected abstract Map<String, String> extractText(String inputFolder, String inputFileName, Properties properties);

    private String clean(String text, Properties properties)
    {
        if (properties.getProperty("delete_file_newline", "false").equals("true"))
        {
            text = text.replaceAll("\n", " ");
        }
        
        if (properties.getProperty("slash_to_newline", "false").equals("true"))
        {
            text = text.replaceAll("/(?!/)", "\n");
        }

        if (properties.getProperty("keep_abbreviations_in_brackets", "false").equals("true"))
        {
            text = text.replaceAll("\\)", "");
            text = text.replaceAll("\\(", "");
        }

        if (properties.getProperty("delete_between_squared_brackets", "false").equals("true"))
        {
            text = deleteBetween(text, "\\[[^\\[\\]]+\\]");
        }

        if (properties.getProperty("delete_between_round_brackets", "false").equals("true"))
        {
            text = deleteBetween(text, "\\([^\\(\\)]+\\)");
        }

        if (properties.getProperty("delete_between_equal_signs", "false").equals("true"))
        {
            text = deleteBetween(text, "=[^=]+=");
        }
        
        if (properties.getProperty("delete_equal_signs", "false").equals("true"))
        {
            text = text.replaceAll("=", "");
        }
        
        text = text.replaceAll(" +", " ");

        String[] lines = text.split("\n");
        List<String> l = Arrays.asList(lines)
                .stream()
                .map((String t) -> t.trim())
                .filter((String t) -> t.length() > 0)
                .collect(Collectors.toList());
        return String.join("\n", l);
    }

    private String deleteBetween(String text, String regex)
    {
        String before = text;
        while (true)
        {
            text = text.replaceAll(regex, "");
            if (text.equals(before))
            {
                break;
            }
            before = text;
        }

        return text;
    }
}
