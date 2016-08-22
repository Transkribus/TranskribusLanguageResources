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
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 *
 * @author max
 */
public class NeuralLanguageModel implements ILanguageModel
{

    private final Map<String, Integer> types;
    private final Model model;

    public NeuralLanguageModel(String zipPath)
    {
        try
        {
            this.types = loadTypes(zipPath);
            this.model = new Model(zipPath, types.size(), types.size());
        } catch (IOException ex)
        {
            throw new RuntimeException("Could find zip archive with given path: " + zipPath);
        }
    }

    private Map<String, Integer> loadTypes(String zipPath) throws IOException
    {
        URL url = getClass().getClassLoader().getResource(zipPath);
        ZipFile zipFile = new ZipFile(url.getFile());
        ZipEntry entry = zipFile.getEntry("types.txt");
        List<String> lines = Utils.loadFileLineByLine(zipFile.getInputStream(entry));
        return Utils.createTypeMap(lines);
    }

    public Map<String, Integer> getTypes()
    {
        return types;
    }

    @Override
    public Map<String, Double> getProbabilitiesForNextToken(List<String> sequence) throws UnsupportedSequenceException
    {
        TimeDistributedValues values = new TimeDistributedValues();
        values.setValues(Utils.sequenceToMatrix(types, sequence));
        StaticValues result = (StaticValues) model.pass(values, types.size());

        Map<String, Double> confMat = new HashMap();
        for (Map.Entry<String, Integer> entry : types.entrySet())
        {
            confMat.put(entry.getKey(), result.getValues()[entry.getValue()]);
        }

        return confMat;
    }
}
