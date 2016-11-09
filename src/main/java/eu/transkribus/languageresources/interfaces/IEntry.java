package eu.transkribus.languageresources.interfaces;

import java.util.Collection;
import java.util.Map;

/**
 *
 * @author jnphilipp
 */
public interface IEntry {
    public Map<String, Integer> getValues();
    public String getKey();
    public int getFrequency();
    public int getFrequency(String name);
    public boolean containsValue(String name);
}
