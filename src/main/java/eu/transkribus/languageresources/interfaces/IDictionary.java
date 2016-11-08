package eu.transkribus.languageresources.interfaces;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 *
 * @author jnphilipp
 */
public interface IDictionary {
    /* meta */
    public String getName();
    public void setName(String name);
    public String getDescription();
    public void setDescription(String description);
    public String getLanguage();
    public void setLanguage(String language);
    public int getNumberTokens();
    public void setNumberTokens(int numberTokens);
    public int getNumberTypes();
    public void setNumberTypes(int numberTypes);
    public Map<Character, Integer> getCharacterTable();
    public void setCharacterTable(Map<Character, Integer> characterTable);
    public LocalDateTime getDate();
    public void setDate(LocalDateTime date);

    /* entires */
    public void addEntry(String key);
    public void addEntry(String key, int frequency);
    public void addEntry(IEntry entry);
    public void addValue(String key, String name);
    public void addValue(String key, String name, int frequency);
    public Collection<IEntry> getEntries();
    public boolean containsKey(String key);
    public IEntry getEntry(String key) throws NoSuchElementException;
    public void save(String path) throws IOException, FileNotFoundException;
}
