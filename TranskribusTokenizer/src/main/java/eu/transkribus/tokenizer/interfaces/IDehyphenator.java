/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.tokenizer.interfaces;

import java.util.List;

/**
 * This class should detect hyphenations between line and result with a cleaned
 * string, without line breaks
 *
 * @author gundram
 */
public interface IDehyphenator {

    /**
     * delete hyphenations between lines
     *
     * @param lines
     * @return
     */
    public String process(List<String> lines);

    /**
     * delete hyphenations between lines
     *
     * @param lines
     * @return
     */
    public String process(String[] lines);

    /**
     * delete hyphenations between lines.<br>
     * assume that line breaks are coded by an newline '\n'
     *
     * @param line
     * @return
     */
    public String process(String line);

}
