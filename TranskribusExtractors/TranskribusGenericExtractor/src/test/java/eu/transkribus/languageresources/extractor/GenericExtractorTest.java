/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.extractor;

import eu.transkribus.transkribusgenericextractor.GenericExtractor;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author max
 */
public class GenericExtractorTest
{

    private final GenericExtractor extractor;

    public GenericExtractorTest()
    {
        this.extractor = new GenericExtractor();
    }

    @Test
    @Ignore
    public void testZIP()
    {
        this.extractor.extract("src/test/resources", "Pestarchiv.zip", "Pestarchiv");
    }
}
