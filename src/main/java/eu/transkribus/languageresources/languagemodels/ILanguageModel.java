/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.languagemodels;

import java.util.List;
import java.util.Map;

/**
 *
 * @author max
 */
public interface ILanguageModel
{
    public Map<String, Double> getProbabilitiesForNextToken(List<String> sequence);
}
