/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.tokenizer.categorizer;

import eu.transkribus.tokenizer.interfaces.ICategorizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * categorizes characters by their general category specified by the unicode
 * properties. Therefore, the main category is taken ("L" for letters, "N" for
 * numbers, "S" for symbols,...). Delimiters are all codepoints with category
 * "Z". All codepoints are isolated tokens EXCEPT codepoints with general
 * categories "L" and "N".
 *
 * @author gundram
 */
public class CategorizerWordMergeGroups implements ICategorizer {

    public static Logger LOG = Logger.getLogger(CategorizerWordMergeGroups.class.getName());

    @Override
    public String getCategory(char c) {
        if (c == '\n' || c == '\t') {
            return "Z";
        }
        if (c == 173) {
            return "P";
        }
        String cat = CategoryUtils.getCategoryGeneral(c);
        switch (cat) {
            case "M":
                return "L";
            case "S":
                return "P";
            case "C":
                switch (CategoryUtils.getCategory(c)) {
                    case "Cs":
                        LOG.log(Level.WARNING, "cannot handle surrogates - character ''{0}'' with decimal-value {1}. Assume to be a letter.", new Object[]{c, (int) c});
//                        throw new RuntimeException("cannot handle surrogates - character '" + c + "' with decimal-value " + ((int) c) + ".");
                    case "Co":
                        return "L";
                    case "Cc":
                        throw new RuntimeException("cannot handle control characters '" + c + "' with decimal-value " + ((int) c) + ".");
                    case "Cf":
                        throw new RuntimeException("cannot handle format characters '" + c + "' with decimal-value " + ((int) c) + ".");
                    default:
                        throw new RuntimeException("cannot handle characters '" + c + "' with decimal-value " + ((int) c) + ".");

                }
            default:
                return cat;
        }
    }

    @Override
    public boolean isDelimiter(char c) {
        return "Z".equals(getCategory(c));
    }

    @Override
    public boolean isIsolated(char c) {
        switch (getCategory(c)) {
            case "L":
            case "N":
                return false;
            default:
                return true;
        }
    }

}
