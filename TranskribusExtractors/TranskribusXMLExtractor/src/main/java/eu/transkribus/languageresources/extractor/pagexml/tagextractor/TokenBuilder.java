/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.extractor.pagexml.tagextractor;

import eu.transkribus.interfaces.IDictionary;
import eu.transkribus.languageresources.dictionaries.Dictionary;
import eu.transkribus.languageresources.extractor.pagexml.annotations.PAGEXMLToken;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author max
 */
public class TokenBuilder extends PAGEXMLValueBuilder<PAGEXMLToken>
{

    private static final Pattern patternContinue = Pattern.compile("\\{offset:0+; length:[0-9]+; continued:true;\\}");

    @Override
    public List<PAGEXMLToken> extract(List<PAGEXMLToken> list, String line, String customTagValue, int pageIndex, int lineIndex)
    {
        boolean firstTokenIsBeingContinued = firstTokenIsBeingContinued(customTagValue);
        PAGEXMLToken token;
        String[] tokens = line.split(" ");
        int summedOffset = 0;

        for (int i = 0; i < tokens.length; i++)
        {
            if(firstTokenIsBeingContinued)
            {
                token = list.get(list.size()-1);
                token.setSecondValue(tokens[i]);
                
                token.setEndPage(pageIndex);
                token.setEndLine(lineIndex);
                token.setEndOffset(tokens[i].length());
                summedOffset += tokens[i].length() + 1;
            }else{
                token = new PAGEXMLToken();
                token.setFirstValue(tokens[i]);
                
                token.setStartPage(pageIndex);
                token.setStartLine(lineIndex);
                token.setStartOffset(summedOffset);
                
                token.setEndPage(pageIndex);
                token.setEndLine(lineIndex);
                token.setEndOffset(summedOffset + tokens[i].length());
                list.add(token);
                summedOffset += tokens[i].length() + 1;
            }
        }
        
        return list;
    }

    private boolean firstTokenIsBeingContinued(String customTagValue)
    {
        Matcher matcher = patternContinue.matcher(customTagValue);
        return matcher.matches();
    }

    @Override
    public IDictionary toDictionary(List<PAGEXMLToken> values)
    {
        Dictionary dict = new Dictionary();
        for (PAGEXMLToken token : values)
        {
            dict.addEntry(token.getValue());
        }
        return dict;
    }

}
