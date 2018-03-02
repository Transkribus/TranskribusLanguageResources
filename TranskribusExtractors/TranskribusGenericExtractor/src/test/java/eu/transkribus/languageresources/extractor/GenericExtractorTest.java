/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.extractor;

import eu.transkribus.transkribusgenericextractor.GenericExtractor;
import java.util.Properties;
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
    public void testZIP()
    {
        Properties properties = new Properties();
        properties.setProperty("keep_abbreviations_in_brackets", "false");
        properties.setProperty("slash_to_newline", "true");
        properties.setProperty("delete_file_newline", "true");
        properties.setProperty("delete_between_round_brackets", "true");
        properties.setProperty("delete_between_squared_brackets", "true");
        properties.setProperty("delete_equal_signs", "true");
        properties.setProperty("delete_between_equal_signs", "false");
        properties.setProperty("double_slash_to_new_page", "true");
        properties.setProperty("filename_from_filetag", "true");
        
        this.extractor.extract("src/test/resources", "cceh", "cceh_1", properties);
        
        properties.setProperty("delete_between_round_brackets", "false");
        properties.setProperty("keep_abbreviations_in_brackets", "true");
        this.extractor.extract("src/test/resources", "cceh", "cceh_2", properties);
    }
}
