/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.tokenizer.dehyphenator;

import eu.transkribus.tokenizer.interfaces.IDehyphenator;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author max
 */
public class DehyphenatorComplex implements IDehyphenator
{

    private final List<DehyphenatorSimple> dehyphenators;

    public DehyphenatorComplex(List<DehyphenatorSimple> dehyphenators)
    {
        this.dehyphenators = dehyphenators;
    }

    @Override
    public String process(List<String> lines)
    {
        StringBuilder sb = new StringBuilder();

        for (String line : lines)
        {
            sb.append(process(line));
        }
        
        return sb.toString();
    }

    @Override
    public String process(String[] lines)
    {
        StringBuilder sb = new StringBuilder();

        for (String line : lines)
        {
            sb.append(process(line));
        }
        
        return sb.toString();
    }

    @Override
    public String process(String line)
    {
        for (DehyphenatorSimple dehyphenator : dehyphenators)
        {
            line = dehyphenator.process(line);
        }
        
        return line;
    }
}
