/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.extractor.pagexml.annotations;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author max
 */
public class PAGEXMLToken extends PAGEXMLAnnotation
{
    public void setFirstValue(String value)
    {
        values.clear();
        values.add(value);
    }
    
    public void setSecondValue(String value)
    {
        values.add(value);
    }
    
    public String getValue()
    {
        return String.join("", values);
    }
}
