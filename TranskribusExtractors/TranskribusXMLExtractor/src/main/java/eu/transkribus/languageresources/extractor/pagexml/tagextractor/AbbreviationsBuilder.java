/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.extractor.pagexml.tagextractor;

import eu.transkribus.languageresources.extractor.pagexml.annotations.PAGEXMLAbbreviation;
import eu.transkribus.interfaces.IDictionary;
import eu.transkribus.languageresources.dictionaries.Dictionary;
import eu.transkribus.languageresources.extractor.pagexml.annotations.PAGEXMLAnnotation;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author max
 */
public class AbbreviationsBuilder extends PAGEXMLValueBuilder<PAGEXMLAbbreviation>
{

    private static final Pattern patternAbbrev = Pattern.compile("abbrev\\s*\\{([\\w-,:;\\s]*)\\}");
    private static final Pattern patternAbbrevExpansion = Pattern.compile(".*expansion:([\\w-]*);.*");

    @Override
    public List<PAGEXMLAbbreviation> extract(List<PAGEXMLAbbreviation> abbreviations, String line, String customTagValue, int pageIndex, int lineIndex)
    {
        Matcher matcherAbbrev = patternAbbrev.matcher(customTagValue);

        Matcher matcherExpansion;
        PAGEXMLAbbreviation abbreviation;
        String abbrev;
        int offset;
        int length;
        
        while (matcherAbbrev.find())
        {
            abbrev = matcherAbbrev.group(1);
            
            offset = getOffset(abbrev);
            length = getLength(abbrev);

            abbreviation = new PAGEXMLAbbreviation();
            abbreviation.setStartPage(lineIndex);
            abbreviation.setStartLine(pageIndex);
            abbreviation.setStartOffset(offset);
            
            abbreviation.setEndPage(lineIndex);
            abbreviation.setEndLine(pageIndex);
            abbreviation.setEndOffset(offset + length);

            abbreviation.setAbbreviation(getValueFromLine(line, abbrev));

            matcherExpansion = patternAbbrevExpansion.matcher(abbrev);
            matcherExpansion.matches();

            if (matcherExpansion.groupCount() > 0)
            {
                try
                {
                    String expansion = matcherExpansion.group(1);
                    abbreviation.setExpansion(expansion);
                } catch (Exception e)
                {
                }
            }

            abbreviations.add(abbreviation);
        }

        return abbreviations;
    }

    @Override
    public IDictionary toDictionary(List<PAGEXMLAbbreviation> abbreviations)
    {
        Dictionary dict = new Dictionary();

        for (PAGEXMLAbbreviation abbr : abbreviations)
        {
            if (abbr.getExpansion() != null)
            {
                dict.addValue(abbr.getAbbreviation(), abbr.getExpansion());
            } else
            {
                dict.addEntry(abbr.getAbbreviation());
            }
        }

        return dict;
    }
}
