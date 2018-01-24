package eu.transkribus.languageresources.dictionaries;

import eu.transkribus.interfaces.IDictionary;
import eu.transkribus.interfaces.IEntry;
import eu.transkribus.languageresources.exceptions.ARPAParseException;
import eu.transkribus.languageresources.util.ARPAFileHandler;
import eu.transkribus.languageresources.util.SimpleDictFileHandler;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Properties;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author jnphilipp
 */
public class DictionaryUtils {
    public static IDictionary load(String path) throws ARPAParseException, FileNotFoundException, IOException {
        return load(new File(path));
    }

    public static IDictionary load(File path) throws ARPAParseException, FileNotFoundException, IOException {
        if ( path.exists() && !path.isDirectory() )
            throw new IOException("Given path \"" + path + "\" is not a directory.");

        IDictionary dictionary = new Dictionary();
        readMetadataFile(new File(path.getAbsolutePath() + "/metadata.properties"), dictionary);
        ((Dictionary)dictionary).fromNgrams(ARPAFileHandler.read(new File(path.getAbsolutePath() + "/entries.arpa")));

        return dictionary;
    }

    public static void save(String path, IDictionary dictionary) throws FileNotFoundException, IOException {
        save(new File(path), dictionary);
    }

    public static void save(File path, IDictionary dictionary) throws FileNotFoundException, IOException {
        if ( path.exists() && !path.isDirectory() )
            throw new IOException("Given path \"" + path + "\" is not a directory.");

        if ( !path.exists() )
            path.mkdirs();

        writeMetadataFile(new File(path.getAbsolutePath() + "/metadata.properties"), dictionary);
        ARPAFileHandler.write(new File(path.getAbsolutePath() + "/entries.arpa"), ((Dictionary)dictionary).toNgrams());
        SimpleDictFileHandler.write(new File(path.getAbsolutePath() + "/entries.dict"), ((Dictionary)dictionary).getEntries());
        ARPAFileHandler.write(new File(path.getAbsolutePath() + "/entry-character-table.arpa"), ((Dictionary)dictionary).entryCharacterTableToNgrams());
        writeCharacterTable(new File(path.getAbsolutePath() + "/entry-character-table.csv"), ((Dictionary)dictionary).getEntryCharacterTable());
        ARPAFileHandler.write(new File(path.getAbsolutePath() + "/value-character-table.arpa"), ((Dictionary)dictionary).valueCharacterTableToNgrams());
        writeCharacterTable(new File(path.getAbsolutePath() + "/value-character-table.csv"), ((Dictionary)dictionary).getValueCharacterTable());

        ARPAFileHandler.write(new File(path.getAbsolutePath() + "/entriesWords.arpa"), ((Dictionary)dictionary).toNgrams((String type) ->
        {
            return type.matches("[\\p{L}\\p{N}\\p{Z}\\p{M}]+");
        }));
        SimpleDictFileHandler.write(new File(path.getAbsolutePath() + "/entriesWords.dict"), ((Dictionary)dictionary).getEntries((String type) ->
        {
            return type.matches("[\\p{L}\\p{N}\\p{Z}\\p{M}]+");
        }));
        ARPAFileHandler.write(new File(path.getAbsolutePath() + "/entriesPunctuationMarks.arpa"), ((Dictionary)dictionary).toNgrams((String type) ->
        {
            return type.matches("\\p{P}+");
        }));
        SimpleDictFileHandler.write(new File(path.getAbsolutePath() + "/entriesPunctuationMarks.dict"), ((Dictionary)dictionary).getEntries((String type) ->
        {
            return type.matches("\\p{P}+");
        }));
    }

    public static void saveAsJSON(String file, IDictionary dictionary, IDictionary abbreviations, IDictionary persons, IDictionary placeNames, IDictionary organizations)  throws FileNotFoundException, IOException {
        saveAsJSON(new File(file), dictionary, abbreviations, persons, placeNames, organizations);
    }

    public static void saveAsJSON(File file, IDictionary dictionary, IDictionary abbreviations, IDictionary persons, IDictionary placeNames, IDictionary organizations)  throws FileNotFoundException, IOException {
        Writer writer = new BufferedWriter(new FileWriter(file));
        toJSON(dictionary, abbreviations, persons, placeNames, organizations).writeJSONString(writer);
        writer.close();
    }

    private static void readMetadataFile(File file, IDictionary dictionary) throws FileNotFoundException, IOException {
        Properties properties = new Properties();
        Reader reader = new BufferedReader(new FileReader(file));
        properties.load(reader);
        reader.close();

        ((Dictionary)dictionary).setName(properties.getProperty("name", "").isEmpty() ? null : properties.getProperty("name", ""));
        ((Dictionary)dictionary).setDescription(properties.getProperty("description", "").isEmpty() ? null : properties.getProperty("description", ""));
        ((Dictionary)dictionary).setLanguage(properties.getProperty("language", "").isEmpty() ? null : properties.getProperty("language", ""));
        ((Dictionary)dictionary).setCreationDate(LocalDateTime.parse(properties.getProperty("creation_date")));
    }

    private static void writeMetadataFile(File file, IDictionary dictionary) throws FileNotFoundException, IOException {
        Properties properties = new Properties();
        properties.setProperty("name", dictionary.getName() == null ? "" : dictionary.getName());
        properties.setProperty("description", dictionary.getDescription() == null ? "" : dictionary.getDescription());
        properties.setProperty("language", dictionary.getLanguage() == null ? "" : dictionary.getLanguage());
        properties.setProperty("number_types", Integer.toString(dictionary.getNumberTypes()));
        properties.setProperty("number_tokens", Long.toString(dictionary.getNumberTokens()));
        properties.setProperty("creation_date", dictionary.getCreationDate().toString());

        Writer writer = new BufferedWriter(new FileWriter(file));
        properties.store(writer, null);
        writer.close();
    }

    private static void writeCharacterTable(File file, Map<Character, Integer> characterTable) throws FileNotFoundException, IOException {
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
        writer.println("\"char\",\"unicode\",\"unicode_name\",\"frequency\"");
        for ( Map.Entry<Character, Integer> v : characterTable.entrySet() )
            writer.println(String.format("\"%s\",\"\\u%s\",\"%s\",\"%d\"", v.getKey(), Integer.toHexString(v.getKey() | 0x10000), Character.getName(v.getKey()), v.getValue()));
        writer.close();
    }

    /**
     * Create a dictionary from another dictionary. The new dictionary cointains only types
     * that seem not to belong in the given dictionary. As reference another dictionary
     * will be used. An examplary use case would be if the givenDictionary should
     * contain only latin types, any non-latin type would be considered odd. The new
     * dictionary will cointain types that are more likely to appear in the comparing
     * dictionary than in the given one.
     * @param givenDictionary dictionary to find odd tokens in
     * @param comparingDictionary
     * @param entry
     * @return
     */
    public static IDictionary getOddTypes(IDictionary givenDictionary, IDictionary comparingDictionary) {
        Dictionary newDictionary = new Dictionary();

        for(IEntry entry : givenDictionary.getEntries())
        {
            if(entryIsOdd(givenDictionary, comparingDictionary, entry))
                newDictionary.addEntry(entry);
        }

        return newDictionary;
    }

    private static boolean entryIsOdd(IDictionary givenDictionary, IDictionary comparingDictionary, IEntry entry) {
        if(!comparingDictionary.containsKey(entry.getKey()))
           return false;

        long numTokensGivenDictionary = givenDictionary.getNumberTokens();
        long numTokensComparingDictionary = comparingDictionary.getNumberTokens();

        double probabilityInGiven = entry.getFrequency() / (double)numTokensGivenDictionary;
        double probabilityInComparing = comparingDictionary.getEntry(entry.getKey()).getFrequency() / (double)numTokensComparingDictionary;

        return probabilityInComparing >= probabilityInGiven;
    }

    public static IDictionary subtractDictionary(IDictionary givenDictionary, IDictionary dictionaryToSubtract) {
        Dictionary newDictionary = new Dictionary();

        for(IEntry entry : givenDictionary.getEntries())
        {
            if(!dictionaryToSubtract.containsKey(entry.getKey()))
                newDictionary.addEntry(entry);
        }

        return newDictionary;
    }

    public static JSONObject toJSON(IDictionary dictionary, IDictionary abbreviations, IDictionary persons, IDictionary placeNames, IDictionary organizations) {
        JSONObject obj = new JSONObject();

        obj.put("name", dictionary.getName());
        obj.put("description", dictionary.getDescription());
        obj.put("language", dictionary.getLanguage());
        obj.put("number_types", Integer.toString(dictionary.getNumberTypes()));
        obj.put("number_tokens", Long.toString(dictionary.getNumberTokens()));
        obj.put("creation_date", dictionary.getCreationDate().toString());

        JSONArray entries = new JSONArray();
        for ( IEntry entry : dictionary.getEntries() ) {
            JSONObject value = new JSONObject();
            value.put("frequency", entry.getFrequency());

            JSONObject tags = new JSONObject();
            if ( abbreviations.containsKey(entry.getKey()) )
                tags.put("Abbreviation", abbreviations.getEntry(entry.getKey()).getFrequency());
            if ( persons.containsKey(entry.getKey()) )
                tags.put("Person", persons.getEntry(entry.getKey()).getFrequency());
            if ( placeNames.containsKey(entry.getKey()) )
                tags.put("PlaceName", placeNames.getEntry(entry.getKey()).getFrequency());
            if ( organizations.containsKey(entry.getKey()) )
                tags.put("Organization", organizations.getEntry(entry.getKey()).getFrequency());
            value.put("tags", tags);

            JSONObject e = new JSONObject();
            e.put(entry.getKey(), value);
            entries.add(e);
        }

        obj.put("entries", entries);
        return obj;
    }
}
