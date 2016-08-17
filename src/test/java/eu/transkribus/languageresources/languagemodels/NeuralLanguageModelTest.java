/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.languagemodels;

import eu.transkribus.languageresources.languagemodels.NeuralLanguageModel;
import de.unileipzig.asv.neuralnetwork.utils.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author max
 */
public class NeuralLanguageModelTest
{
    
    public NeuralLanguageModelTest()
    {
    }
    
    @BeforeClass
    public static void setUpClass()
    {
    }
    
    @AfterClass
    public static void tearDownClass()
    {
    }
    
    @Before
    public void setUp()
    {
    }
    
    @After
    public void tearDown()
    {
    }

    @Test
    public void testGetProbabilityForNextToken()
    {
        NeuralLanguageModel nlm = new NeuralLanguageModel("nn/lm_bozen_layers_6.zip", "nn/lm_bozen_characters_types.txt");
        
        List<String> sequence = new ArrayList<>();
        sequence.add("ä");
        sequence.add("Ö");
        sequence.add("f");
        
        Map<String, Integer> types = Utils.loadTypes("nn/lm_bozen_characters_types.txt");
        Map<String, Double> probabilitiesForNextToken = nlm.getProbabilitiesForNextToken(sequence);
        double[] givenProbabilities = Utils.loadValues("nn/forecasted_layers_6.txt").get("äÖf");
        
        for(String type : types.keySet())
        {
            assertEquals(givenProbabilities[types.get(type)], probabilitiesForNextToken.get(type), 0.00001);
        }
    }
    
}
