/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources;

import eu.transkribus.languageresources.interfaces.ILanguageResource;
import eu.transkribus.languageresources.interfaces.ITokenizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author max
 */
public class LanguageResource implements ILanguageResource
{

    @Override
    public Map<String, Integer> getFrequencyDictionary(ITokenizer tokenizer, String text)
    {
        List<String> tokenizedText = tokenizer.tokenize(text);
        Map<String, Integer> frequencies = new HashMap<>();
        
        for(String token : tokenizedText)
        {
            if(!frequencies.containsKey(token))
                frequencies.put(token, 0);
            
            frequencies.put(token, frequencies.get(token)+1);
        }
        
        return frequencies;
    }

    @Override
    public Map<List<String>, Integer> getNGramFrequencyDictionary(ITokenizer tokenizer, String text, int ngramSize)
    {
        List<String> tokenizedText = tokenizer.tokenize(text);
        Map<List<String>, Integer> frequencies = new HashMap<>();
        List<String> tempToken;
        
        for(int i = 0; i < tokenizedText.size()-ngramSize; i++)
        {
            tempToken = new ArrayList<>(ngramSize);
            for(int j = 0; j < ngramSize; j++)
            {
                tempToken.add(tokenizedText.get(i+j));
            }
            
            if(!frequencies.containsKey(tempToken))
                frequencies.put(tempToken, 0);
            
            frequencies.put(tempToken, frequencies.get(tempToken)+1);
        }
        
        return frequencies;
    }
    
}
