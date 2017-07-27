package eu.transkribus.languageresources.dictionaries;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import eu.transkribus.interfaces.IEntry;

/**
 *
 * @author max, jnphilipp
 */
public class Entry implements IEntry {
    private String name;
    private int frequency;
    private Map<String, Integer> values;

    public Entry(String name) {
        this(name, 1);
    }

    public Entry(String name, int frequency) {
        this.name = name;
        this.frequency = frequency;
        this.values = new LinkedHashMap<>();
    }

    @Override
    public String getKey() {
        return this.name;
    }

    @Override
    public int getFrequency() {
        return this.frequency;
    }

    @Override
    public int getFrequency(String name) throws NoSuchElementException {
        if ( this.values.containsKey(name) )
            return this.values.get(name);

        throw new NoSuchElementException("Could not find value with given name: " + name);
    }

    @Override
    public Map<String, Integer> getValues() {
        return this.values;
    }

    public void decreaseFrequency(int decrement) {
        this.frequency -= decrement;
    }

    public void increaseFrequency() {
        this.frequency++;
    }

    public void increaseFrequency(int increment) {
        this.frequency += increment;
    }

    public void addValue(String name) {
        this.values.put(name, this.values.containsKey(name) ? this.values.get(name) + 1 : 0);
    }

    public void addValue(String name, int frequency) {
        this.values.put(name, this.values.containsKey(name) ? this.values.get(name) + frequency : frequency);
    }

    @Override
    public boolean containsValue(String name) {
        return this.values.containsKey(name);
    }

    @Override
    public boolean equals(Object obj) {
        if ( obj != null )
            if ( obj instanceof Entry )
                if ( this.name.equals(((Entry)obj).getKey()) && this.frequency == ((Entry)obj).getFrequency() && this.values.equals(((Entry)obj).getValues()) )
                    return true;
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + this.name.hashCode();
        hash = 11 * hash + this.frequency;
        hash = 53 * hash + this.values.hashCode();
        return hash;
    }
}
