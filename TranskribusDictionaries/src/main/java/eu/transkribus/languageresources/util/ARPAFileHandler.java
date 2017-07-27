package eu.transkribus.languageresources.util;

import eu.transkribus.languageresources.exceptions.ARPAParseException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author jnphilipp
 */
public class ARPAFileHandler {
    public static Map<Integer, Map<List<String>, Map<String, Double>>> read(String file) throws ARPAParseException, IOException {
        return read(new File(file));
    }

    public static Map<Integer, Map<List<String>, Map<String, Double>>> read(File file) throws ARPAParseException, FileNotFoundException, IOException {
        Map<Integer, Integer> ngramTypes = new LinkedHashMap<>();
        Map<Integer, Map<List<String>, Map<String, Double>>> ngrams = new LinkedHashMap<>();

        int state = -1;
        int ngramType = -1;
        Reader reader = new BufferedReader(new FileReader(file));
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
                        ngramTypes.put(Integer.valueOf(a[0]), Integer.valueOf(a[1]));
                    }
                    break;
                case 1:
                    if ( line.matches("\\\\\\d+-grams:") ) {
                        ngramType = checkNgramType(ngramTypes, line);
                        state = 2;
                    }
                    break;
                case 2:
                    if ( line.isEmpty() || line.equals("\\end\\") )
                        state = 1;
                    else if ( line.matches("\\\\\\d+-grams:") )
                        ngramType = checkNgramType(ngramTypes, line);
                    else {
                        if ( !ngrams.containsKey(ngramType) )
                            ngrams.put(ngramType, new LinkedHashMap<>());
                        String[] a = line.split("\t");
                        List<String> sequence = new ArrayList<>();
                        String next = "";
                        if ( line.contains(" ") ) {
                            sequence.addAll(Arrays.asList(a[1].split(" ")));
                            next = sequence.get(sequence.size() - 1);
                            sequence.remove(sequence.size() - 1);
                        }
                        else
                            sequence.add(a[1]);

                        if ( !ngrams.get(ngramType).containsKey(sequence) )
                            ngrams.get(ngramType).put(sequence, new LinkedHashMap<>());
                        if ( ngrams.get(ngramType).size() <= ngramTypes.get(ngramType) )
                            ngrams.get(ngramType).get(sequence).put(next, Double.valueOf(a[0]));
                        else
                            throw new ARPAParseException(String.format("Found more %s-ngrams than defined.", ngramType));
                    }
            }
        }
        reader.close();

        if ( !ngramTypes.keySet().equals(ngrams.keySet()) )
            throw new ARPAParseException("N-gram definitions do not match the listed N-grams.");
        return ngrams;
    }

    private static int checkNgramType(Map<Integer, Integer> ngramTypes, String line) throws ARPAParseException {
        int ngramType = Integer.valueOf(line.replaceAll("\\\\", "").replaceAll("-grams:", ""));
        if ( !ngramTypes.containsKey(ngramType) )
            throw new ARPAParseException(String.format("%s-ngram type is not defined, but found %s-ngrams.", ngramType, ngramType));
        return ngramType;
    }

    public static void write(String file, Map<Integer, Map<List<String>, Map<String, Double>>> ngrams) throws FileNotFoundException, IOException {
        write(new File(file), ngrams);
    }

    public static void write(File file, Map<Integer, Map<List<String>, Map<String, Double>>> ngrams) throws FileNotFoundException, IOException {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.ROOT);
		numberFormat.setMinimumFractionDigits(8);
		numberFormat.setMaximumFractionDigits(8);
        numberFormat.setGroupingUsed(false);

        try ( PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file))) ) {
            writer.println("\\data\\");
            for ( Map.Entry<Integer, Map<List<String>, Map<String, Double>>> ngram : ngrams.entrySet() )
                if ( ngram.getValue().size() > 0 )
                    writer.println(String.format("ngram %d=%d", ngram.getKey(), ngram.getValue().size()));
            writer.println();
            for ( Map.Entry<Integer, Map<List<String>, Map<String, Double>>> ngram : ngrams.entrySet() ) {
                if ( ngram.getValue().size() > 0 ) {
                    writer.println(String.format("\\%d-grams:", ngram.getKey()));

                    List<String> list = new LinkedList<>();
                    for ( Map.Entry<List<String>, Map<String, Double>> words : ngram.getValue().entrySet() ) {
                        if ( String.join("", words.getKey()).matches(".*\\p{Space}.*") )
                            throw new RuntimeException(String.format("The String \"%s\" contains unsupported characters.", String.join("", words.getKey())));
                        for ( Map.Entry<String, Double> word : words.getValue().entrySet() ) {
                            if ( word.getKey().matches(".*\\p{Space}.*") )
                                throw new RuntimeException(String.format("The String \"%s\" contains unsupported characters.", String.join("", words.getKey())));
                            list.add(String.format("%s\t%s %s", numberFormat.format(word.getValue()), String.join(" ", words.getKey()), word.getKey()).trim());
                        }
                    }

                    list.sort(Comparator.comparing((String s) -> Double.valueOf(s.substring(0, s.indexOf("\t")))).reversed());
                    for ( String s : list )
                        writer.println(s.trim());
                }
                writer.println();
            }
            writer.println("\\end\\");
        }
    }
}
