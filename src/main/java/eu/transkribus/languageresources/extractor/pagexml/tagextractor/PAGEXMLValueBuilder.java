/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.extractor.pagexml.tagextractor;

import eu.transkribus.interfaces.IDictionary;
import eu.transkribus.languageresources.extractor.pagexml.annotations.PAGEXMLAnnotation;
import eu.transkribus.languageresources.extractor.pagexml.annotations.PAGEXMLToken;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.opencv.imgproc.Imgproc.line;
import static org.opencv.imgproc.Imgproc.line;
import static org.opencv.imgproc.Imgproc.line;
import static org.opencv.imgproc.Imgproc.line;

/**
 *
 * @author max
 * @param <A>
 */
public abstract class PAGEXMLValueBuilder<A extends PAGEXMLAnnotation>
{

    private static final Pattern patternAbbrevOffset = Pattern.compile(".*offset:([0-9]*);.*");
    private static final Pattern patternAbbrevLength = Pattern.compile(".*length:([0-9]*);.*");
    private static final Pattern patternContinued = Pattern.compile(".*continued:true;.*");
    
    private int getValue(String customTag, Pattern pattern)
    {
        Matcher matcherOffset = pattern.matcher(customTag);
        matcherOffset.matches();
        return new Integer(matcherOffset.group(1));
    }

    protected int getOffset(String customTag)
    {
        return getValue(customTag, patternAbbrevOffset);
    }

    protected int getLength(String customTag)
    {
        return getValue(customTag, patternAbbrevLength);
    }

    protected boolean isBeingContinued(String customTag)
    {
        return customTag.contains("continued:true;");
    }

    protected String getValueFromLine(String contentLine, String extractedCustomTag)
    {
        int offset = getOffset(extractedCustomTag);
        int length = getLength(extractedCustomTag);
        return contentLine.substring(offset, offset + length);
    }

    public abstract List<A> extract(List<A> list, String line, String customTagValue, int pageIndex, int lineIndex);

    public abstract IDictionary toDictionary(List<A> values);
}
