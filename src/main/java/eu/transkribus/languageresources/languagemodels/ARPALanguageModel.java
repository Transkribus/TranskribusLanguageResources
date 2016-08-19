package eu.transkribus.languageresources.languagemodels;

import eu.transkribus.languageresources.exceptions.ARPAParseException;
import eu.transkribus.languageresources.exceptions.UnsupportedSequenceException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jnphilipp
 */
public class ARPALanguageModel implements ILanguageModel {
    private final Map<Integer, Integer> ngramTypes;
    private final Map<Integer, Map<List<String>, Map<String, Double>>> ngrams;

    public ARPALanguageModel(String arpaFile) throws ARPAParseException, IOException {
        this.ngrams = new LinkedHashMap<>();
        this.ngramTypes = new LinkedHashMap<>();
        this.load(arpaFile);
    }

    private void load(String file) throws ARPAParseException, IOException {
        int state = -1;
        int ngramType = -1;
        Reader reader = new BufferedReader(new FileReader(new File(file)));
        while ( true ) {
            String line = ((BufferedReader) reader).readLine();
            if ( line == null )
                break;
            line = line.trim();
            switch ( state ) {
                case -1:
                    if ( line.equals("\\data\\") )
                        state = 0;
                    break;
                case 0:
                    if ( line.isEmpty() )
                        state = 1;
                    else {
                        line = line.replace("ngram ", "");
                        String[] a = line.split("=");
                        this.ngramTypes.put(Integer.valueOf(a[0]), Integer.valueOf(a[1]));
                    }
                    break;
                case 1:
                    if ( line.matches("\\\\\\d+-grams:") ) {
                        ngramType = Integer.valueOf(line.replaceAll("\\\\", "").replaceAll("-grams:", ""));
                        state = 2;
                    }
                    break;
                case 2:
                    if ( line.isEmpty() || line.equals("\\end\\") )
                        state = 1;
                    else if ( line.matches("\\\\\\d+-grams:") )
                        ngramType = Integer.valueOf(line.replaceAll("\\\\", "").replaceAll("-grams:", ""));
                    else {
                        if ( !this.ngrams.containsKey(ngramType) )
                            this.ngrams.put(ngramType, new LinkedHashMap<List<String>, Map<String, Double>>());
                        String[] a = line.split("\t");
                        List<String> sequence = new ArrayList<>();
                        sequence.addAll(Arrays.asList(a[1].split(" ")));
                        String next = sequence.get(sequence.size() - 1);
                        sequence.remove(sequence.size() - 1);

                        if ( !this.ngrams.get(ngramType).containsKey(sequence) )
                            this.ngrams.get(ngramType).put(sequence, new LinkedHashMap<String, Double>());
                        if ( this.ngrams.get(ngramType).size() <= this.ngramTypes.get(ngramType) )
                            this.ngrams.get(ngramType).get(sequence).put(next, Double.valueOf(a[0]));
                        else
                            throw new ARPAParseException(String.format("Found more %s-ngrams than defined.", ngramType));
                    }
            }
        }
        reader.close();

        if ( !this.ngramTypes.keySet().equals(this.ngrams.keySet()) )
            throw new ARPAParseException("N-gram definitions do not match the listed N-grams.");
    }

    @Override
    public Map<String, Double> getProbabilitiesForNextToken(List<String> sequence) throws UnsupportedSequenceException {
        if ( !this.ngramTypes.containsKey(sequence.size() + 1) )
            throw new UnsupportedSequenceException(String.format("Language model does not support sequences with length %s.", sequence.size()));
        if ( !this.ngrams.get(sequence.size() + 1).containsKey(sequence) )
            throw new UnsupportedSequenceException("Language model does not have data for the given sequence.");
        return this.ngrams.get(sequence.size() + 1).get(sequence);
    }
}
