package eu.transkribus.languageresources.interfaces;

import java.util.Collection;
import java.util.Map;

/**
 *
 * @author jnphilipp
 */
public interface IEntry {
    public Map<String, Integer> getValues();
    public String getName();
    public int getFrequency();
    public void increaseFrequency();
    public void increaseFrequency(int increment);
    public void addValue(String name);
    public void addValue(String name, int frequency);
    public boolean containsKey(String name);
}
