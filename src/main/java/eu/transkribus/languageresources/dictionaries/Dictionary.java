package eu.transkribus.languageresources.dictionaries;

import eu.transkribus.languageresources.interfaces.IDictionary;
import eu.transkribus.languageresources.interfaces.IEntry;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Map;

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
    private Map<Character, Integer> entryCharacterTable;
    private Map<Character, Integer> valueCharacterTable;
    private LocalDateTime creationDate = LocalDateTime.now();
    private final Map<String, IEntry> entries;

    public Dictionary() {
        this.entries = new LinkedHashMap<>();
        this.entryCharacterTable = new LinkedHashMap<>();
        this.valueCharacterTable = new LinkedHashMap<>();
    }

    public Dictionary(List<String> tokenizedText) {
        this();

        for ( String token : tokenizedText )
           this.addEntry(token);
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public int getNumberTokens() {
        return this.numberTokens;
    }

    @Override
    public int getNumberTypes() {
        return this.numberTypes;
    }

    @Override
    public Map<Character, Integer> getEntryCharacterTable() {
        return this.entryCharacterTable;
    }

    @Override
    public Map<Character, Integer> getValueCharacterTable() {
        return this.valueCharacterTable;
    }

    @Override
    public LocalDateTime getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public void addEntry(String key) {
        this.addEntry(key, 1);
    }

    public void addEntry(String key, int frequency) {
        if ( this.entries.containsKey(key) )
            ((Entry)this.entries.get(key)).increaseFrequency(frequency);
        else {
            this.entries.put(key, new Entry(key, frequency));
            this.numberTypes++;
        }
        this.numberTokens += frequency;
        this.updateEntryCharacterTable(key, frequency);
    }

    public void addEntry(IEntry entry) {
        if ( this.entries.containsKey(entry.getKey()) ) {
            this.addEntry(entry.getKey(), entry.getFrequency());
            for ( Map.Entry<String, Integer> value : entry.getValues().entrySet() )
                this.addValue(entry.getKey(), value.getKey(), value.getValue());
        }
        else {
            this.entries.put(entry.getKey(), entry);
            this.numberTypes++;
            this.numberTokens += entry.getFrequency();
            this.updateEntryCharacterTable(entry.getKey(), entry.getFrequency());
            for ( Map.Entry<String, Integer> value : entry.getValues().entrySet() ) {
                this.updateValueCharacterTable(value.getKey(), value.getValue());
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
            if ( !this.entries.get(key).containsValue(name) )
                this.numberTypes += 1;
            this.numberTokens += frequency;
            ((Entry)this.entries.get(key)).increaseFrequency(frequency);
            ((Entry)this.entries.get(key)).addValue(name, frequency);
        }
        else {
            Entry e = new Entry(key, frequency);
            e.addValue(name, frequency);
            this.entries.put(key, e);
            this.numberTypes += 2;
            this.numberTokens += 1 + frequency;
        }
        this.updateValueCharacterTable(name, frequency);
    }

    @Override
    public boolean containsKey(String key) {
        return this.entries.containsKey(key);
    }

    @Override
    public boolean containsValue(String name) {
        for ( IEntry e : this.entries.values() )
            if ( e.containsValue(name) )
                return true;
        return false;
    }

    @Override
    public Collection<IEntry> getEntries() {
        return new LinkedList<IEntry>(this.entries.values());
    }

    @Override
    public IEntry getEntry(String key) throws NoSuchElementException {
        if ( this.entries.containsKey(key) )
            return this.entries.get(key);

        throw new NoSuchElementException("Could not find entry with given key: " + key);
    }

    @Override
    public Collection<IEntry> getEntriesByValue(String name) throws NoSuchElementException {
        Collection<IEntry> entries = new LinkedHashSet<>();
        for ( IEntry e : this.entries.values() )
            if ( e.containsValue(name) )
                entries.add(e);

        if ( entries.isEmpty() )
            throw new NoSuchElementException("Could not find entry with given key: " + name);
        return entries;
    }

    @Override
    public void merge(IDictionary dictionary) {
        for ( IEntry e : dictionary.getEntries() )
            this.addEntry(e);
    }

    public void fromNgrams(Map<Integer, Map<List<String>, Map<String, Double>>> ngrams) {
        Map<List<String>, Map<String, Double>> oneGrams = ngrams.get(1);
        Map<List<String>, Map<String, Double>> twoGrams = ngrams.get(2);

        for ( Map.Entry<List<String>, Map<String, Double>> v : oneGrams.entrySet() )
            this.addEntry(v.getKey().get(0), v.getValue().get("").intValue());

        for ( Map.Entry<List<String>, Map<String, Double>> v : twoGrams.entrySet() )
            for ( Map.Entry<String, Double> w : v.getValue().entrySet() ) {
                this.addValue(v.getKey().get(0), w.getKey(), w.getValue().intValue());
                ((Entry)this.entries.get(w.getKey())).decreaseFrequency(w.getValue().intValue());
                this.numberTokens -= w.getValue().intValue();
                this.updateEntryCharacterTable(w.getKey(), w.getValue().intValue() * -1);
            }

        Iterator it = this.entries.values().iterator();
        while ( it.hasNext() )
            if ( ((IEntry)it.next()).getFrequency() == 0 ) {
                it.remove();
                this.numberTypes--;
            }

        it = this.entryCharacterTable.entrySet().iterator();
        while ( it.hasNext() )
            if ( ((Map.Entry<Character, Integer>)it.next()).getValue() == 0 )
                it.remove();
    }

    public Map<Integer, Map<List<String>, Map<String, Double>>> toNgrams() {
        Map<Integer, Map<List<String>, Map<String, Double>>> ngrams = new LinkedHashMap<>();
        Map<List<String>, Map<String, Double>> oneGrams = new LinkedHashMap<>();
        Map<List<String>, Map<String, Double>> twoGrams = new LinkedHashMap<>();
        for ( IEntry e : this.entries.values() ) {
            int f = e.getFrequency();
            for ( Map.Entry<String, Integer> v : e.getValues().entrySet() ) {
                f -= v.getValue();
                if ( !oneGrams.containsKey(v.getKey()) )
                    oneGrams.put(Arrays.asList(new String[] {v.getKey()}), new LinkedHashMap<String, Double>());
                oneGrams.get(Arrays.asList(new String[] {v.getKey()})).put("", oneGrams.get(Arrays.asList(new String[] {v.getKey()})).containsKey("") ? oneGrams.get(Arrays.asList(new String[] {v.getKey()})).get("") + v.getValue() : (double) v.getValue());
                if ( !twoGrams.containsKey(e.getKey()) )
                    twoGrams.put(Arrays.asList(new String[] {e.getKey()}), new LinkedHashMap<String, Double>());
                twoGrams.get(Arrays.asList(new String[] {e.getKey()})).put(v.getKey(), (double) v.getValue());
            }

            if ( !oneGrams.containsKey(e.getKey()) )
                oneGrams.put(Arrays.asList(new String[] {e.getKey()}), new LinkedHashMap<String, Double>());
            oneGrams.get(Arrays.asList(new String[] {e.getKey()})).put("", oneGrams.get(Arrays.asList(new String[] {e.getKey()})).containsKey("") ? oneGrams.get(Arrays.asList(new String[] {e.getKey()})).get("") + f : (double) f);
        }
        ngrams.put(1, oneGrams);
        ngrams.put(2, twoGrams);
        return ngrams;
    }

    public Map<Integer, Map<List<String>, Map<String, Double>>> entryCharacterTableToNgrams() {
        Map<Integer, Map<List<String>, Map<String, Double>>> ngrams = new LinkedHashMap<>();
        Map<List<String>, Map<String, Double>> oneGrams = new LinkedHashMap<>();
        for ( Map.Entry<Character, Integer> e : this.entryCharacterTable.entrySet() ) {
            Map<String, Double> next = new LinkedHashMap<>();
            next.put("", (double) e.getValue());
            oneGrams.put(Arrays.asList(new String[] {String.valueOf(e.getKey())}), next);
        }
        ngrams.put(1, oneGrams);
        return ngrams;
    }

    public Map<Integer, Map<List<String>, Map<String, Double>>> valueCharacterTableToNgrams() {
        Map<Integer, Map<List<String>, Map<String, Double>>> ngrams = new LinkedHashMap<>();
        Map<List<String>, Map<String, Double>> oneGrams = new LinkedHashMap<>();
        for ( Map.Entry<Character, Integer> e : this.valueCharacterTable.entrySet() ) {
            Map<String, Double> next = new LinkedHashMap<>();
            next.put("", (double) e.getValue());
            oneGrams.put(Arrays.asList(new String[] {String.valueOf(e.getKey())}), next);
        }
        ngrams.put(1, oneGrams);
        return ngrams;
    }

    private void updateEntryCharacterTable(String word) {
        this.updateEntryCharacterTable(word, 1);
    }

    private void updateEntryCharacterTable(String word, int frequency) {
        for ( char c : word.toCharArray() )
            this.entryCharacterTable.put(c, this.entryCharacterTable.containsKey(c) ? this.entryCharacterTable.get(c) + frequency : frequency);
    }

    private void updateValueCharacterTable(String word) {
        this.updateValueCharacterTable(word, 1);
    }

    private void updateValueCharacterTable(String word, int frequency) {
        for ( char c : word.toCharArray() )
            this.valueCharacterTable.put(c, this.valueCharacterTable.containsKey(c) ? this.valueCharacterTable.get(c) + frequency : frequency);
    }

    @Override
    public boolean equals(Object obj) {
        if ( obj != null )
            if ( obj instanceof Dictionary )
                if ( ((this.name != null && this.name.equals(((Dictionary)obj).getName())) || (this.name == null && ((Dictionary)obj).getName() == null)) && ((this.description != null && this.description.equals(((Dictionary)obj).getDescription())) || (this.description == null && ((Dictionary)obj).getDescription() == null)) && ((this.language != null && this.language.equals(((Dictionary)obj).getLanguage())) || (this.language == null && ((Dictionary)obj).getLanguage() == null)) && this.numberTypes == ((Dictionary)obj).getNumberTypes() &&
                    this.numberTokens == ((Dictionary)obj).getNumberTokens() && this.entryCharacterTable.equals(((Dictionary)obj).getEntryCharacterTable()) && this.valueCharacterTable.equals(((Dictionary)obj).getValueCharacterTable()) && this.creationDate.equals(((Dictionary)obj).getCreationDate()) && this.getEntries().equals(((Dictionary)obj).getEntries()) )
                    return true;
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = this.name == null ? hash : 23 * hash + this.name.hashCode();
        hash = this.description == null ? hash : 11 * hash + this.description.hashCode();
        hash = this.language == null ? hash : 2 * hash + this.language.hashCode();
        hash = 53 * hash + this.numberTypes;
        hash = 41 * hash + this.numberTokens;
        hash = 61 * hash + this.entryCharacterTable.hashCode();
        hash = 71 * hash + this.valueCharacterTable.hashCode();
        hash = 97 * hash + this.creationDate.hashCode();
        hash = 43 * hash + this.entries.hashCode();
        return hash;
    }
}
