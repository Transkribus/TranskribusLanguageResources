/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.extractor.pagexml;

import eu.transkribus.interfaces.IDictionary;
import eu.transkribus.languageresources.extractor.pagexml.PAGEXMLExtractor;
import eu.transkribus.languageresources.extractor.pagexml.annotations.PAGEXMLAbbreviation;
import eu.transkribus.languageresources.extractor.pagexml.annotations.PAGEXMLAnnotation;
import eu.transkribus.languageresources.extractor.pagexml.annotations.PAGEXMLIndex;
import java.io.File;
import java.util.List;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author max
 */
public class PAGEXMLNERTest
{

    private final PAGEXMLExtractor extractor;
    private final String pageTest1;

    public PAGEXMLNERTest()
    {
        extractor = new PAGEXMLExtractor();
        ClassLoader classLoader = getClass().getClassLoader();
        pageTest1 = new File("src/test/resources/PXML_NER_TEST_1").getAbsolutePath();
//        pageTest1 = new File(classLoader.getResource("PXML_NER_TEST_1").getFile()).getAbsolutePath();
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
    @Ignore
    public void testSmallNER()
    {
        List<PAGEXMLAbbreviation> abbreviationsList = extractor.extractAbbreviationsAsList(pageTest1);
        List<PAGEXMLAnnotation> placesList = extractor.extractPlaceNamesAsList(pageTest1);
        List<PAGEXMLAnnotation> personsList = extractor.extractPersonNamesAsList(pageTest1);
        List<PAGEXMLAnnotation> organizationsList = extractor.extractOrganizationsAsList(pageTest1);

        IDictionary abbreviations = extractor.extractAbbreviations(pageTest1);
        IDictionary places = extractor.extractPlaceNames(pageTest1);
        IDictionary persons = extractor.extractPersonNames(pageTest1);
        IDictionary organizations = extractor.extractOrganizations(pageTest1);

        PAGEXMLIndex index = extractor.createIndex(pageTest1);

        Set<PAGEXMLAnnotation> allPlaceAnnotations = index.getAllPossibleAnnotations(places);
        Set<PAGEXMLAnnotation> newPlaceAnnotations = index.getNewAnnotations(allPlaceAnnotations, placesList);

        Set<PAGEXMLAnnotation> allPersonAnnotations = index.getAllPossibleAnnotations(persons);
        Set<PAGEXMLAnnotation> newPersonAnnotations = index.getNewAnnotations(allPersonAnnotations, personsList);
        
        Set<PAGEXMLAnnotation> allOrganizationsAnnotations = index.getAllPossibleAnnotations(organizations);
        Set<PAGEXMLAnnotation> newOrganizationsAnnotations = index.getNewAnnotations(allOrganizationsAnnotations, organizationsList);

        // abbreviations
        // number of unique abbreviations
        assertEquals(0, abbreviations.getNumberTypes());
        // number of all abbreviations
        assertEquals(0, abbreviations.getNumberTokens());
        assertEquals(0, abbreviationsList.size());

        // places
        assertTrue(places.containsKey("PlaceName"));
        assertTrue(places.containsKey("Place Name"));
        // number of unique places
        assertEquals(2, places.getNumberTypes());
        // number of all place annotations
        assertEquals(2, places.getNumberTokens());
        assertEquals(2, placesList.size());
        // number of new place annotations
        assertEquals(4, allPlaceAnnotations.size());
        assertEquals(2, newPlaceAnnotations.size());

        //persons
        // number of unique persons
        assertEquals(1, persons.getNumberTypes());
        // number of all person annotations
        assertEquals(1, persons.getNumberTokens());
        assertEquals(1, personsList.size());
        // number of new person annotations
        assertEquals(2, allPersonAnnotations.size());
        assertEquals(1, newPersonAnnotations.size());
        
        // organizations
        // number of unique organizations
        assertEquals(1, organizations.getNumberTypes());
        // number of all organizations annotations
        assertEquals(1, organizations.getNumberTokens());
        assertEquals(1, organizationsList.size());
        // number of new organizations annotations
        assertEquals(0, newOrganizationsAnnotations.size());
    }

    @Test
    @Ignore
    public void testBigNER()
    {
        ClassLoader classLoader = getClass().getClassLoader();
        String pageFolderBig = new File(classLoader.getResource("HS_115").getFile()).getAbsolutePath();
        
        List<PAGEXMLAbbreviation> abbreviationsList = extractor.extractAbbreviationsAsList(pageFolderBig);
        List<PAGEXMLAnnotation> placesList = extractor.extractPlaceNamesAsList(pageFolderBig);
        List<PAGEXMLAnnotation> personsList = extractor.extractPersonNamesAsList(pageFolderBig);
        List<PAGEXMLAnnotation> organizationsList = extractor.extractOrganizationsAsList(pageFolderBig);

        IDictionary abbreviations = extractor.extractAbbreviations(pageFolderBig);
        IDictionary places = extractor.extractPlaceNames(pageFolderBig);
        IDictionary persons = extractor.extractPersonNames(pageFolderBig);
        IDictionary organizations = extractor.extractOrganizations(pageFolderBig);

        PAGEXMLIndex index = extractor.createIndex(pageFolderBig);

        Set<PAGEXMLAnnotation> allPlaceAnnotations = index.getAllPossibleAnnotations(places);
        Set<PAGEXMLAnnotation> newPlaceAnnotations = index.getNewAnnotations(allPlaceAnnotations, placesList);

        Set<PAGEXMLAnnotation> allPersonAnnotations = index.getAllPossibleAnnotations(persons);
        Set<PAGEXMLAnnotation> newPersonAnnotations = index.getNewAnnotations(allPersonAnnotations, personsList);
        
        Set<PAGEXMLAnnotation> allOrganizationsAnnotations = index.getAllPossibleAnnotations(organizations);
        Set<PAGEXMLAnnotation> newOrganizationsAnnotations = index.getNewAnnotations(allOrganizationsAnnotations, organizationsList);

        // abbreviations
        // number of unique abbreviations
        assertEquals(2713, abbreviations.getNumberTypes());
        // number of all abbreviations
        assertEquals(7591, abbreviations.getNumberTokens());
//        assertEquals(7591, abbreviationsList.size());

        // places
        // number of unique places
        assertEquals(337, places.getNumberTypes());
        // number of all place annotations
        assertEquals(620, places.getNumberTokens());
        assertEquals(620, placesList.size());
        // number of new place annotations
        assertEquals(187, newPlaceAnnotations.size());

        // persons
        // number of unique persons
        assertEquals(1271, persons.getNumberTypes());
        // number of all person annotations
        assertEquals(1771, persons.getNumberTokens());
        assertEquals(1771, personsList.size());
        // number of new person annotations
        assertEquals(2394, newPersonAnnotations.size());
        
        // organizations
        // number of unique organizations
        assertEquals(593, organizations.getNumberTypes());
        // number of all organizations annotations
        assertEquals(1060, organizations.getNumberTokens());
        assertEquals(1060, organizationsList.size());
        // number of new organizations annotations
        assertEquals(1055, newOrganizationsAnnotations.size());
    }
}
