/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.tokenizer;

import eu.transkribus.interfaces.ITokenizer;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.text.Normalizer;
import java.util.ArrayList;

/**
 * The ConfigTokenizer is used to tokenized strings. The main idea is that is being used with a configuration file. If different use cases call for different types of tokenization, the same tokenizer with different configuration files can be used.
 *
 * Rules are: - Normalization - Dehyphanation signs - Delimiter signs - Delimiter signs being kept as tokens
 *
 * Further explanation:
 *
 * - Normalization: The Java normalizer tackles the representation problem of characters like á or ö. These characters can be represented as a single character (á or ö) or as a basic character with additional diacritic. The java normalizer changes the representation to either representation type.
 *
 * - Dehypenation signs When a word at the end of the line is being cut off and continued on the next line, there often is a hyphenation sign. The tokenizer looks for a given set of files, a following \n and a following small letter in the next line. If that expression is found, the split up word is being put together.
 *
 * - Delimiter signs Delemiter are used for splitting tokens. Common signs among others are spaces, newlines and dots.
 *
 * - Delimiter signs being kept as tokens When there is a token like 'is, ', the user may be interested in getting 'is' as a token and the comma as a dedicated token.
 *
 *
 * @author max
 */
public class ConfigTokenizer implements ITokenizer
{

    public enum NormalizerOption
    {

        None,
        NFC,
        NFD
    }

    private final Properties properties;

    public ConfigTokenizer()
    {
        properties = new Properties();
    }

    public ConfigTokenizer(Properties properties)
    {
        this.properties = properties;
    }

    public ConfigTokenizer(String pathToConfig)
    {
        this(new File(pathToConfig));
    }

    public ConfigTokenizer(File configFile)
    {
        properties = new Properties();
        try
        {
            properties.load(new FileInputStream(configFile));
        } catch (IOException ex)
        {
            Logger.getLogger(ConfigTokenizer.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Could not load given property file with path: " + configFile.getAbsolutePath());
        }
    }

    public List<String> tokenize(String text)
    {
        String normalizerString = properties.getProperty("normalizer", null);
        Normalizer.Form normalizer = null;
        if (normalizerString != null && !normalizerString.equals("None"))
        {
            switch (normalizerString)
            {
                case "NFC":
                    normalizer = Normalizer.Form.NFC;
                    break;
                case "NFD":
                    normalizer = Normalizer.Form.NFD;
                    break;
                case "NFKC":
                    normalizer = Normalizer.Form.NFKC;
                    break;
                case "NFKD":
                    normalizer = Normalizer.Form.NFKD;
                    break;
            }
        }

        String dehyphenationSigns = properties.getProperty("dehyphenation_signs", "");
        String delimiterSigns = properties.getProperty("delimiter_signs", "\n ");
        String keepDelimiterSigns = properties.getProperty("keep_delimiter_signs", "");
        boolean tokenizeCharacterWise = properties.getProperty("tokenize_character_wise", "false").equals("true");

        return tokenize(text, normalizer, dehyphenationSigns, delimiterSigns, keepDelimiterSigns, tokenizeCharacterWise);
    }

    public String normalize(String text, Normalizer.Form normalizer)
    {
        return Normalizer.normalize(text, normalizer);
    }

    public List<String> tokenize(
            String text,
            Normalizer.Form normalizer,
            String dehyphenizationSignsString,
            String delimiterSignsString,
            String keepDelimiterSignsString,
            boolean tokenizeCharacterWise)
    {
        if (normalizer != null)
        {
            text = normalize(text, normalizer);
        }

        List<Character> dehyphenizationSigns = createSignListFromString(dehyphenizationSignsString);
        List<Character> delimiterSigns = createSignListFromString(delimiterSignsString);
        List<Character> keepDelimiterSigns = createSignListFromString(keepDelimiterSignsString);
        List<String> tokenizedText = new ArrayList<>();

        int lenText = text.length();
        char charCurrent;
        char charNext;
        char charNext2;

        StringBuilder nextToken = new StringBuilder();
        boolean dehyphenize = false;

        for (int index = 0; index < lenText; index++)
        {
            charCurrent = text.charAt(index);

            if (tokenizeCharacterWise)
            {
                if (!delimiterSigns.contains(charCurrent))
                {
                    tokenizedText.add(Character.toString(charCurrent));
                }
            } else
            {
                if (index + 2 < lenText && dehyphenizationSigns.contains(charCurrent))
                {

                    charNext = text.charAt(index + 1);
                    if (delimiterSigns.contains(charNext))
                    {

                        charNext2 = text.charAt(index + 2);
                        if (isLowercase(charNext2))
                        {
                            dehyphenize = true;
                            continue;
                        }
                    }
                }

                if (!delimiterSigns.contains(charCurrent))
                {
                    dehyphenize = false;
                }

                if (!dehyphenize)
                {
                    if (delimiterSigns.contains(charCurrent))
                    {
                        if (nextToken.toString().length() > 0)
                        {
                            tokenizedText.add(nextToken.toString());
                            nextToken = new StringBuilder();
                        }

                        if (keepDelimiterSigns.contains(charCurrent))
                        {
                            tokenizedText.add(Character.toString(charCurrent));
                        }
                    } else
                    {
                        nextToken.append(charCurrent);
                    }
                }
            }
        }

        if (nextToken.toString().length() > 0)
        {
            tokenizedText.add(nextToken.toString());
        }

        return tokenizedText;
    }

    private List<Character> createSignListFromString(String signString)
    {
        int numSigns = signString.length();
        List<Character> signList = new ArrayList<>(numSigns);

        for (int index = 0; index < numSigns; index++)
        {
            signList.add(signString.charAt(index));
        }

        return signList;
    }

    private boolean isLowercase(char c)
    {
        return Character.toLowerCase(c) == c;
    }
}
