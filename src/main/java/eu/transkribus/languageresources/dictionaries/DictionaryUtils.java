package eu.transkribus.languageresources.dictionaries;

import eu.transkribus.languageresources.exceptions.ARPAParseException;
import eu.transkribus.languageresources.interfaces.IDictionary;
import eu.transkribus.languageresources.interfaces.IEntry;
import eu.transkribus.languageresources.util.ARPAFileHandler;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
        ARPAFileHandler.write(new File(path.getAbsolutePath() + "/entry-character-table.arpa"), ((Dictionary)dictionary).entryCharacterTableToNgrams());
        ARPAFileHandler.write(new File(path.getAbsolutePath() + "/value-character-table.arpa"), ((Dictionary)dictionary).valueCharacterTableToNgrams());
        // write character tables as csv
    }

    private static void readMetadataFile(File file, IDictionary dictionary) throws FileNotFoundException, IOException {
        Properties properties = new Properties();
        Reader reader = new BufferedReader(new FileReader(file));
        properties.load(reader);
        reader.close();

        ((Dictionary)dictionary).setName(properties.getProperty("name"));
        ((Dictionary)dictionary).setDescription(properties.getProperty("description"));
        ((Dictionary)dictionary).setLanguage(properties.getProperty("language", "").isEmpty() ? null : properties.getProperty("language", ""));
        ((Dictionary)dictionary).setCreationDate(LocalDateTime.parse(properties.getProperty("creation_date")));
    }

    private static void writeMetadataFile(File file, IDictionary dictionary) throws FileNotFoundException, IOException {
        Properties properties = new Properties();
        properties.setProperty("name", dictionary.getName() == null ? "" : dictionary.getName());
        properties.setProperty("description", dictionary.getDescription() == null ? "" : dictionary.getDescription());
        properties.setProperty("language", dictionary.getLanguage() == null ? "" : dictionary.getLanguage());
        properties.setProperty("number_types", Integer.toString(dictionary.getNumberTypes()));
        properties.setProperty("number_tokens", Integer.toString(dictionary.getNumberTokens()));
        properties.setProperty("creation_date", dictionary.getCreationDate().toString());

        Writer writer = new BufferedWriter(new FileWriter(file));
        properties.store(writer, null);
        writer.close();
    }
}
