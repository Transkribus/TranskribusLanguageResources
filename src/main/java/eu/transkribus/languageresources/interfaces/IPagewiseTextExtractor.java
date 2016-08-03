package eu.transkribus.languageresources.interfaces;

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
    
    Map<String, Set<String>> extractAbbreviationsFromPage(String path, int page);
}
