package eu.transkribus.languageresources.dictionaries;

import eu.transkribus.languageresources.interfaces.IDictionary;
import eu.transkribus.languageresources.interfaces.IEntry;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 *
 * @author max, jnphilipp
 */
public class Dictionary implements IDictionary {
    private String name;
    private String description;
    private String language;
    private int numberTypes;
    private int numberTokens;
    private Map<Character, Integer> characterTable;
    private LocalDateTime date = LocalDateTime.now();
    private final Map<String, IEntry> entries;

    public Dictionary() {
        this.entries = new LinkedHashMap<>();
        this.characterTable = new LinkedHashMap<>();
    }

    public Dictionary(List<String> tokenizedText) {
        this();

        for ( String token : tokenizedText )
           this.addEntry(token);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getNumberTokens() {
        return this.numberTokens;
    }

    public void setNumberTokens(int numberTokens) {
        this.numberTokens = numberTokens;
    }

    public int getNumberTypes() {
        return this.numberTypes;
    }

    public void setNumberTypes(int numberTypes) {
        this.numberTypes = numberTypes;
    }

    public Map<Character, Integer> getCharacterTable() {
        return this.characterTable;
    }

    public void setCharacterTable(Map<Character, Integer> characterTable) {
        this.characterTable = characterTable;
    }

    public LocalDateTime getDate() {
        return this.date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void addEntry(String key) {
        this.addEntry(key, 1);
    }

    public void addEntry(String key, int frequency) {
        if ( this.entries.containsKey(key) )
            this.entries.get(key).increaseFrequency(frequency);
        else {
            this.entries.put(key, new Entry(key, frequency));
            this.numberTypes++;
        }
        this.numberTokens += frequency;
        this.updateCharacterTable(key, frequency);
    }

    public void addEntry(IEntry entry) {
        if ( this.entries.containsKey(entry.getName()) ) {
            this.addEntry(entry.getName(), entry.getFrequency());
            for ( Map.Entry<String, Integer> value : entry.getValues().entrySet() )
                this.addValue(entry.getName(), value.getKey(), value.getValue());
        }
        else {
            this.entries.put(entry.getName(), entry);
            this.numberTypes++;
            this.numberTokens += entry.getFrequency();
            this.updateCharacterTable(entry.getName(), entry.getFrequency());
            for ( Map.Entry<String, Integer> value : entry.getValues().entrySet() ) {
                this.updateCharacterTable(value.getKey(), value.getValue());
                this.numberTypes++;
                this.numberTokens += value.getValue();
            }
        }
    }

    public void addValue(String key, String name) {
        this.addValue(key, name, 1);
    }

    public void addValue(String key, String name, int frequency) {
        if ( this.entries.containsKey(key) ) {
            if ( !this.entries.get(key).containsKey(name) )
                this.numberTypes += 1;
            this.numberTokens += frequency;
            this.entries.get(key).addValue(name, frequency);
        }
        else {
            Entry e = new Entry(key);
            e.addValue(name, frequency);
            this.entries.put(key, e);
            this.numberTypes += 2;
            this.numberTokens += 1 + frequency;
        }
        this.updateCharacterTable(name, frequency);
    }

    public Collection<IEntry> getEntries() {
        return this.entries.values();
    }

    public boolean containsKey(String key) {
        return this.entries.containsKey(key);
    }

    public IEntry getEntry(String key) throws NoSuchElementException {
        if ( this.entries.containsKey(key) )
            return entries.get(key);

        throw new NoSuchElementException("Could not find entry with given key: " + key);
    }

    public void save(String path) throws IOException, FileNotFoundException {
        DictionaryWriter.writeDictionray(this, path, true, true, true);
    }

    private void updateCharacterTable(String word) {
        this.updateCharacterTable(word, 1);
    }

    private void updateCharacterTable(String word, int frequency) {
        for ( char c : word.toCharArray() )
            this.characterTable.put(c, this.characterTable.containsKey(c) ? this.characterTable.get(c) + frequency : frequency);
    }

    @Override
    public boolean equals(Object obj) {
        if ( obj != null )
            if ( obj instanceof Dictionary )
                if ( this.name.equals(((Dictionary)obj).getName()) && this.description.equals(((Dictionary)obj).getDescription()) && this.language.equals(((Dictionary)obj).getLanguage()) && this.numberTypes == ((Dictionary)obj).getNumberTypes() && this.numberTokens == ((Dictionary)obj).getNumberTokens() && this.characterTable.equals(((Dictionary)obj).getCharacterTable()) && this.date.equals(((Dictionary)obj).getDate()) && this.entries.equals(((Dictionary)obj).getEntries()) )
                    return true;
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + this.name.hashCode();
        hash = 53 * hash + this.entries.hashCode();
        return hash;
    }
}
