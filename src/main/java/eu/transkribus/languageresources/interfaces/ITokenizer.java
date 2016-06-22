/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.interfaces;

import java.util.List;

/**
 *
 * @author max
 */
public interface ITokenizer
{
    List<String> tokenize(String text);
}
