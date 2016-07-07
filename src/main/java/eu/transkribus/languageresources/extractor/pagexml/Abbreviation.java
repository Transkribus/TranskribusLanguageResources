/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.extractor.pagexml;

/**
 *
 * @author max
 */
public class Abbreviation
{
    private String abbreviation;
    private String expansion;
    private int length;
    private int offset;

    public void setAbbreviationFromLine(String line)
    {
        this.abbreviation = line.substring(offset, offset+length);
    }

    public void setLength(String length)
    {
        this.length = Integer.parseInt(length);
    }

    public int getLength()
    {
        return length;
    }

    public void setOffset(String offset)
    {
        this.offset = Integer.parseInt(offset);
    }

    public int getOffset()
    {
        return offset;
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
