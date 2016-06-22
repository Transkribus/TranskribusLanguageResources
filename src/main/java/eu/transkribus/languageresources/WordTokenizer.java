/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources;

import eu.transkribus.languageresources.interfaces.ITokenizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author max
 */
public class WordTokenizer implements ITokenizer
{

    @Override
    public List<String> tokenize(String text)
    {
        return Arrays.asList(text.split(" "));
    }
}
