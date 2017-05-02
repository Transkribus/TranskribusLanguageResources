/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.extractor.pagexml.annotations;

/**
 *
 * @author max
 */
public class PAGEXMLAbbreviation extends PAGEXMLAnnotation
{
    private String abbreviation;
    private String expansion;

    public String getAbbreviation()
    {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation)
    {
        this.abbreviation = abbreviation;
    }
    
    public void setExpansion(String expansion)
    {
        this.expansion = expansion;
    }

    public String getExpansion()
    {
        return expansion;
    }
    
    public int getLengthDiff()
    {
        return expansion.length() - abbreviation.length();
    }
}
