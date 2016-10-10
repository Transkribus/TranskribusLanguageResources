/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.languagemodels;

import de.unileipzig.asv.neuralnetwork.model.ComplexModel;
import de.unileipzig.asv.neuralnetwork.utils.Utils;
import de.unileipzig.asv.neuralnetwork.values.SingleValue;
import de.unileipzig.asv.neuralnetwork.values.TimeDistributedValues;
import de.unileipzig.asv.neuralnetwork.values.Values;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 *
 * @author max
 */
public class NeuralCorrectionLanguageModel
{

    private final ComplexModel complexModel;
    private final Map<String, Integer> types;
    
    public NeuralCorrectionLanguageModel(String zipFilePath)
    {
        this.complexModel = new ComplexModel(zipFilePath);
        this.types = loadTypes(zipFilePath);
    }
    
    private Map<String, Integer> loadTypes(String zipPath)
    {
        URL url = getClass().getClassLoader().getResource(zipPath);
        
        ZipFile zipFile;
        try
        {
            zipFile = new ZipFile(url.getFile());
        } catch (IOException ex)
        {
            throw new RuntimeException("Given zip file does not contain file types.txt!");
        }
        
        ZipEntry entry = zipFile.getEntry("types.txt");
        
        List<String> lines;
        try
        {
            lines = Utils.loadFileLineByLine(zipFile.getInputStream(entry));
        } catch (IOException ex)
        {
            throw new RuntimeException("Error when loading types.txt!");
        }
        
        return Utils.createTypeMap(lines);
    }
    
    public String correctSequence(String sequence)
    {
        double[][] sequenceArray = Utils.sequenceToMatrix(types, sequence);
        
        TimeDistributedValues inputSequencePadLeft = new TimeDistributedValues();
        TimeDistributedValues inputSequencePadRight = new TimeDistributedValues();
        inputSequencePadLeft.setValues(sequenceArray);
        inputSequencePadRight.setValues(sequenceArray);
        
        SingleValue sequenceLength = new SingleValue();
        sequenceLength.setValues(inputSequencePadLeft.getTimeLength());
        
        HashMap<String, Values> inputValues = new HashMap<>();
        inputValues.put("input_pad_left", inputSequencePadLeft);
        inputValues.put("input_pad_right", inputSequencePadRight);
        inputValues.put("repeat_middle_length", sequenceLength);
        
        Map<String, Values> outputs = complexModel.pass(inputValues);
        
        return Utils.confMatToText(types, ((TimeDistributedValues)outputs.get("out")).getValues());
    }
    
//    public List<Map<String, Double>> correctSequence(List<Map<String, Double>> confMat)
//    {
//        TimeDistributedValues inputSequence = new TimeDistributedValues();
//        
//    }
}
