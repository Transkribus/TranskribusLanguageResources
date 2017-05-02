/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.extractor.pagexml.annotations;

import eu.transkribus.interfaces.IDictionary;
import eu.transkribus.interfaces.IEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author max
 */
public class PAGEXMLIndex
{

    private final List<PAGEXMLToken> tokens;
    private final Map<String, List<Integer>> tokenIndex;

    public PAGEXMLIndex()
    {
        tokens = new ArrayList<>();
        tokenIndex = new HashMap<>();
    }

    public void addTokens(List<PAGEXMLToken> tokens)
    {
        for (PAGEXMLToken token : tokens)
        {
            addToken(token);
        }
    }

    public void addToken(PAGEXMLToken token)
    {
        tokens.add(token);

        if (!tokenIndex.containsKey(token.getValue()))
        {
            tokenIndex.put(token.getValue(), new LinkedList<>());
        }
        tokenIndex.get(token.getValue()).add(tokens.size() - 1);
    }

    public Set<PAGEXMLAnnotation> getAllPossibleAnnotations(IDictionary dictionary)
    {
        Set<PAGEXMLAnnotation> allAnnotations = new HashSet<>();

        for (IEntry entry : dictionary.getEntries())
        {
            allAnnotations.addAll(getAllPossibleAnnotations(entry.getKey()));
        }

        return allAnnotations;
    }

    public Set<PAGEXMLAnnotation> getNewAnnotations(IDictionary dictionary, Set<PAGEXMLAnnotation> givenAnnotations)
    {
        Set<PAGEXMLAnnotation> allAnnotations = getAllPossibleAnnotations(dictionary);
        return getNewAnnotations(allAnnotations, givenAnnotations);
    }

    public Set<PAGEXMLAnnotation> getNewAnnotations(Set<PAGEXMLAnnotation> allAnnotations, Collection<PAGEXMLAnnotation> givenAnnotations)
    {
        Set<PAGEXMLAnnotation> newAnnotations = new HashSet<>();
        for (PAGEXMLAnnotation all : allAnnotations)
        {
            if (!givenAnnotations.contains(all))
            {
                newAnnotations.add(all);
            }
        }
        return newAnnotations;
    }

    private Set<PAGEXMLAnnotation> getAllPossibleAnnotations(String entry)
    {
        List<String> annotationTokens = Arrays.asList(entry.split(" "));
        Set<PAGEXMLAnnotation> allAnnotationsForEntry = new HashSet<>();
        List<PAGEXMLToken> sublist;

        if (tokenIndex.containsKey(annotationTokens.get(0)))
        {
            for (Integer index : tokenIndex.get(annotationTokens.get(0)))
            {
                sublist = tokens.subList(index, index + annotationTokens.size());
                if (checkEquality(annotationTokens, sublist))
                {
                    allAnnotationsForEntry.add(createAnnotation(sublist));
                }
            }
        }

        return allAnnotationsForEntry;
    }

    private boolean checkEquality(List<String> givenTokens, List<PAGEXMLToken> tokensToCheck)
    {
        for (int i = 0; i < givenTokens.size(); i++)
        {
            if (!givenTokens.get(i).equals(tokensToCheck.get(i).getValue()))
            {
                return false;
            }
        }

        return true;
    }

    private PAGEXMLAnnotation createAnnotation(List<PAGEXMLToken> sublist)
    {
        PAGEXMLAnnotation annotation = new PAGEXMLAnnotation();
        PAGEXMLToken firstToken = sublist.get(0);
        PAGEXMLToken lastToken = sublist.get(sublist.size() - 1);

        annotation.setStartPage(firstToken.getStartPage());
        annotation.setStartLine(firstToken.getStartLine());
        annotation.setStartOffset(firstToken.getStartOffset());

        annotation.setEndPage(lastToken.getEndPage());
        annotation.setEndLine(lastToken.getEndLine());
        annotation.setEndOffset(lastToken.getEndOffset());

        List<String> asString = sublist.stream().map(PAGEXMLToken::getValue).collect(Collectors.toList());

        for (PAGEXMLToken token : sublist)
        {
            annotation.addValue(token.getValue());
        }
        return annotation;
    }
}
