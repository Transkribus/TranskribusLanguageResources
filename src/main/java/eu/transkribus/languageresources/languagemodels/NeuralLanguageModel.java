/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.languagemodels;

import de.unileipzig.asv.neuralnetwork.model.Model;
import de.unileipzig.asv.neuralnetwork.utils.Utils;
import de.unileipzig.asv.neuralnetwork.values.StaticValues;
import de.unileipzig.asv.neuralnetwork.values.TimeDistributedValues;
import eu.transkribus.languageresources.exceptions.UnsupportedSequenceException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author max
 */
public class NeuralLanguageModel implements ILanguageModel
{
    private final Map<String, Integer> types;
    private final Model model;
    
    public NeuralLanguageModel(String pathToModel, String pathToTypesFile)
    {
        this.types = Utils.loadTypes(pathToTypesFile);
        this.model = new Model(pathToModel, types.size(), types.size());
    }
    
    private double[][] sequenceToMatrix(Map<String, Integer> types, List<String> sequence) throws UnsupportedSequenceException
    {
        double[][] input = new double[sequence.size()][types.size()];
        String token;
        
        for (int i = 0; i < sequence.size(); i++)
        {
            token = sequence.get(i);
            
            if(!types.containsKey(token))
            {
                throw new UnsupportedSequenceException("Sequence contains unknown token: "+token);
            }
            
            input[i][types.get(token)] = 1;
        }
        return input;
    }
    
    @Override
    public Map<String, Double> getProbabilitiesForNextToken(List<String> sequence) throws UnsupportedSequenceException
    {
        TimeDistributedValues values = new TimeDistributedValues();
        values.setValues(Utils.sequenceToMatrix(types, sequence));
        StaticValues result = (StaticValues) model.pass(values, types.size());
        
        Map<String, Double> confMat = new HashMap();
        for(Map.Entry<String, Integer> entry : types.entrySet())
        {
            confMat.put(entry.getKey(), result.getValues()[entry.getValue()]);
        }
        
        return confMat;
    }
}
