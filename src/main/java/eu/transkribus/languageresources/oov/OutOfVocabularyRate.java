/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.oov;

import eu.transkribus.languageresources.interfaces.IDictionary;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author max
 */
public class OutOfVocabularyRate
{

    public static double byTypes(IDictionary dictionary, List<String> tokenizedText, boolean key)
    {
        Set<String> typesInDict = new HashSet<>();
        Set<String> typesNotInDict = new HashSet<>();

        for (String token : tokenizedText)
        {
            if ( key ) {
                if (dictionary.containsKey(token))
                    typesInDict.add(token);
                else
                    typesNotInDict.add(token);
            }
            else {
                if (dictionary.containsValue(token))
                    typesInDict.add(token);
                else
                    typesNotInDict.add(token);
            }
        }

        int numTypesInDict = typesInDict.size();
        int numTypesNotInDict = typesNotInDict.size();
        int totalTypesSeen = numTypesInDict + numTypesNotInDict;

        return (double) numTypesNotInDict / (double) totalTypesSeen;
    }

    public static double byTokens(IDictionary dictionary, List<String> tokenizedText, boolean key)
    {
        int numTokensSeen = 0;
        int numTokensNotInDict = 0;

        for (String token : tokenizedText)
        {
            numTokensSeen += 1;

            if ( key )
                if (!dictionary.containsKey(token))
                    numTokensNotInDict += 1;
            else
                if (!dictionary.containsValue(token))
                    numTokensNotInDict += 1;
        }

        return (double) numTokensNotInDict / (double) numTokensSeen;
    }
}
