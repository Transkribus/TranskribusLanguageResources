/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.interfaces;

import eu.transkribus.interfaces.ITokenizer;
import eu.transkribus.languageresources.dictionaries.Dictionary;
import java.util.List;
import java.util.Map;

/**
 *
 * @author max
 */
public interface ILanguageResource
{
    Dictionary getFrequencyDictionary(ITokenizer tokenizer, String text);
}
