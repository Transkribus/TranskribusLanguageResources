/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.oov;

import eu.transkribus.languageresources.dictionaries.Dictionary;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author max
 */
public class OutOfVocabularyRate
{

    public static double byTypes(Dictionary dictionary, List<String> tokenizedText)
    {
        Set<String> typesInDict = new HashSet<>();
        Set<String> typesNotInDict = new HashSet<>();

        for (String token : tokenizedText)
        {
            if (dictionary.containsKeyEntry(token))
            {
                typesInDict.add(token);
            } else
            {
                typesNotInDict.add(token);
            }
        }

        int numTypesInDict = typesInDict.size();
        int numTypesNotInDict = typesNotInDict.size();
        int totalTypesSeen = numTypesInDict + numTypesNotInDict;

        return (double) numTypesNotInDict / (double) totalTypesSeen;
    }

    public static double byTokens(Dictionary dictionary, List<String> tokenizedText)
    {
        int numTokensSeen = 0;
        int numTokensNotInDict = 0;

        for (String token : tokenizedText)
        {
            numTokensSeen += 1;
            
            if (!dictionary.containsKeyEntry(token))
            {
                numTokensNotInDict += 1;
            }
        }

        return (double) numTokensNotInDict / (double) numTokensSeen;
    }
}
