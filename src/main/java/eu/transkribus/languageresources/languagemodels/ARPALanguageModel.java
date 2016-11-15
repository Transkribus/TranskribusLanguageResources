package eu.transkribus.languageresources.languagemodels;

import eu.transkribus.languageresources.exceptions.ARPAParseException;
import eu.transkribus.languageresources.exceptions.UnsupportedSequenceException;
import eu.transkribus.languageresources.util.ARPAFileHandler;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jnphilipp
 */
public class ARPALanguageModel implements ILanguageModel {
    private final Map<Integer, Map<List<String>, Map<String, Double>>> ngrams;

    public ARPALanguageModel(String arpaFile) throws ARPAParseException, IOException {
        this.ngrams = ARPAFileHandler.read(arpaFile);
    }

    @Override
    public Map<String, Double> getProbabilitiesForNextToken(List<String> sequence) throws UnsupportedSequenceException {
        if ( !this.ngrams.containsKey(sequence.size() + 1) )
            throw new UnsupportedSequenceException(String.format("Language model does not support sequences with length %s.", sequence.size()));
        if ( !this.ngrams.get(sequence.size() + 1).containsKey(sequence) )
            throw new UnsupportedSequenceException("Language model does not have data for the given sequence.");
        return this.ngrams.get(sequence.size() + 1).get(sequence);
    }
}
