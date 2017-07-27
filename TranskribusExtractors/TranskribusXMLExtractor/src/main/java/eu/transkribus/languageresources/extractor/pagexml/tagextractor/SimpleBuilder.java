/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.extractor.pagexml.tagextractor;

import eu.transkribus.interfaces.IDictionary;
import eu.transkribus.languageresources.dictionaries.Dictionary;
import eu.transkribus.languageresources.extractor.pagexml.annotations.PAGEXMLAnnotation;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author max
 */
public class SimpleBuilder extends PAGEXMLValueBuilder<PAGEXMLAnnotation>
{

    private final Pattern pattern;
    
    public SimpleBuilder(String tagName)
    {
        pattern = Pattern.compile(tagName+"\\s*\\{([\\w-,:;\\s]*)\\}");
    }

    @Override
    public List<PAGEXMLAnnotation> extract(List<PAGEXMLAnnotation> values, String line, String customTagValue, int pageIndex, int lineIndex)
    {
        Matcher matcherAbbrev = pattern.matcher(customTagValue);

        String value;
        String customTagEntry;
        int offset;
        PAGEXMLAnnotation annotation;

        while (matcherAbbrev.find())
        {
            customTagEntry = matcherAbbrev.group(1);
            offset = getOffset(customTagEntry);

            value = getValueFromLine(line, customTagEntry);
            value = cutHyphenation(value);

            annotation = new PAGEXMLAnnotation();
            annotation.setStartPage(pageIndex);
            annotation.setStartLine(lineIndex);
            annotation.setStartOffset(offset);

            annotation.setEndPage(pageIndex);
            annotation.setEndLine(lineIndex);
            annotation.setEndOffset(value.length() + offset);

            for (String token : value.split(" "))
            {
                annotation.addValue(token);
            }

            if (customTagEntry.contains("continued:true") && offset == 0)
            {
                annotation = values.get(values.size() - 1);
                annotation.setEndPage(pageIndex);
                annotation.setEndLine(lineIndex);
                annotation.setEndOffset(value.length());

                for (String token : value.split(" "))
                {
                    annotation.addValue(token);
                }
            } else
            {
                values.add(annotation);
            }
        }

        return values;
    }

    private String cutHyphenation(String value)
    {
        if ("¬".equals(value.charAt(value.length() - 1)))
        {
            value = value.substring(0, value.length() - 2);
        }

        return value;
    }

    @Override
    public IDictionary toDictionary(List<PAGEXMLAnnotation> values)
    {
        Dictionary dict = new Dictionary();

        for (PAGEXMLAnnotation value : values)
        {
            dict.addEntry(String.join(" ", value.getValues()));
        }

        return dict;
    }
}
