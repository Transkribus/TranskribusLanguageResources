/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.interfaces;

import java.util.List;
import java.util.Map;

/**
 *
 * @author max
 */
public interface ILanguageResource
{
    Map<String, Integer> getFrequencyDictionary(ITokenizer tokenizer, String text);
    
    Map<List<String>, Integer> getNGramFrequencyDictionary(ITokenizer tokenizer, String text, int ngramSize);
}
