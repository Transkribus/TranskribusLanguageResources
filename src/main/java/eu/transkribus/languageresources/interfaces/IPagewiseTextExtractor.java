package eu.transkribus.languageresources.interfaces;

import eu.transkribus.languageresources.dictionaries.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author jnphilipp
 */
public interface IPagewiseTextExtractor extends ITextExtractor
{

    List<String> extractTextFromDocumentPagewise(String path);

    String extractTextFromPage(String path, int page);
    
    Dictionary extractAbbreviationsFromPage(String path, int page);
}
