package eu.transkribus.languageresources.interfaces;

import java.util.List;

/**
 *
 * @author jnphilipp
 */
public interface IPagewiseTextExtractor extends ITextExtractor {   
    List<String> extractTextFromDocumentPagewise(String path);
    
    String extractTextFromPage(String path, int page);
}
