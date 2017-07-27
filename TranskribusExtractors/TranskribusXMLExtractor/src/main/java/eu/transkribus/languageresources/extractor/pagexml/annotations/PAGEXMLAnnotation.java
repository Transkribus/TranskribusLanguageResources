/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.extractor.pagexml.annotations;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author max
 */
public class PAGEXMLAnnotation
{
    private int startPage;
    private int startLine;
    private int startOffset;
    
    private int endPage;
    private int endLine;
    private int endOffset;
    
    protected final List<String> values;
    
    public PAGEXMLAnnotation()
    {
        values = new LinkedList<>();
    }
    
    public void addValue(String value)
    {
        values.add(value);
    }

    public List<String> getValues()
    {
        return values;
    }

    public int getStartPage()
    {
        return startPage;
    }

    public void setStartPage(int startPage)
    {
        this.startPage = startPage;
    }

    public int getStartLine()
    {
        return startLine;
    }

    public void setStartLine(int startLine)
    {
        this.startLine = startLine;
    }

    public int getStartOffset()
    {
        return startOffset;
    }

    public void setStartOffset(int startOffset)
    {
        this.startOffset = startOffset;
    }

    public int getEndPage()
    {
        return endPage;
    }

    public void setEndPage(int endPage)
    {
        this.endPage = endPage;
    }

    public int getEndLine()
    {
        return endLine;
    }

    public void setEndLine(int endLine)
    {
        this.endLine = endLine;
    }

    public int getEndOffset()
    {
        return endOffset;
    }

    public void setEndOffset(int endOffset)
    {
        this.endOffset = endOffset;
    }
    
    public int getLength()
    {
        return endOffset - startOffset;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 17 * hash + this.startPage;
        hash = 17 * hash + this.startLine;
        hash = 17 * hash + this.startOffset;
        hash = 17 * hash + this.endPage;
        hash = 17 * hash + this.endLine;
        hash = 17 * hash + this.endOffset;
        hash = 17 * hash + Objects.hashCode(this.values);
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final PAGEXMLAnnotation other = (PAGEXMLAnnotation) obj;
        if (this.startPage != other.startPage)
        {
            return false;
        }
        if (this.startLine != other.startLine)
        {
            return false;
        }
        if (this.startOffset != other.startOffset)
        {
            return false;
        }
        if (this.endPage != other.endPage)
        {
            return false;
        }
        if (this.endLine != other.endLine)
        {
            return false;
        }
        if (this.endOffset != other.endOffset)
        {
            return false;
        }
        if (!Objects.equals(String.join(" ", this.values), String.join(" ", other.values)))
        {
            return false;
        }
        return true;
    }
}
