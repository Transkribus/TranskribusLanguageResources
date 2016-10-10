/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.languagemodels;

import de.unileipzig.asv.neuralnetwork.model.ComplexModel;
import de.unileipzig.asv.neuralnetwork.values.SingleValue;
import de.unileipzig.asv.neuralnetwork.values.TimeDistributedValues;
import de.unileipzig.asv.neuralnetwork.values.Values;
import java.util.HashMap;
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
public class NeuralCorrectionLanguageModelTest
{
    
    public NeuralCorrectionLanguageModelTest()
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

    /**
     * Test of correctSequence method, of class NeuralCorrectionLanguageModel.
     */
    @Test
    public void testCorrectSequence()
    {
        NeuralCorrectionLanguageModel lm = new NeuralCorrectionLanguageModel("nn/lm_bozen_characters_correction.zip");
        
        correct(lm, "meistre");
        correct(lm, "dre");
        correct(lm, "dne");
        correct(lm, "dei");
        correct(lm, "uaf");
    }
    
    private void correct(NeuralCorrectionLanguageModel lm, String sequence)
    {
        System.out.println(sequence+": "+lm.correctSequence(sequence));
    }
}
