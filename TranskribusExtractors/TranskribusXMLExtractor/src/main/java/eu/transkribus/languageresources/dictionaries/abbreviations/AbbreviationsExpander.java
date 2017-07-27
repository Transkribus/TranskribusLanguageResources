/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.dictionaries.abbreviations;

import eu.transkribus.interfaces.IDictionary;
import eu.transkribus.languageresources.dictionaries.Dictionary;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author max
 */
public class AbbreviationsExpander
{

    private final Dictionary dict;

    public AbbreviationsExpander()
    {
        this.dict = new Dictionary();
    }

    public AbbreviationsExpander(IDictionary dict)
    {
        this.dict = (Dictionary) dict;
    }

    public void addDict(Dictionary additionalDict)
    {
        this.dict.merge(additionalDict);
    }

    public List<String> expandLineGreedily(List<String> line)
    {
        List<String> expanedLine = new ArrayList<>(line.size());

        for (String token : line)
        {
            if (this.dict.containsKey(token))
            {
                expanedLine.add(this.dict.getMostFrequentValue(token));
            } else
            {
                expanedLine.add(token);
            }
        }
        
        return expanedLine;
    }
    
    private int getFrequencySumForToken(String token)
    {
        return this.dict
                .getEntry(token)
                .getValues()
                .values()
                .stream()
                .mapToInt(i -> i)
                .sum();
    }
    
    public Map<String, Double> getSuggestions(String token)
    {
        Map<String, Double> suggestionMap = new HashMap<>();
        int sum = getFrequencySumForToken(token);
        
        for(Map.Entry<String, Integer> entry : this.dict.getEntry(token).getValues().entrySet())
        {
            suggestionMap.put(entry.getKey(), entry.getValue() / (double)sum);
        }
        
        return suggestionMap;
    }
}
