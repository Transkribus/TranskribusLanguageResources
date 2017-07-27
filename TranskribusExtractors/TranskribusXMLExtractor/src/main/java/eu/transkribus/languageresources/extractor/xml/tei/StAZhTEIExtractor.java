package eu.transkribus.languageresources.extractor.xml.tei;

import eu.transkribus.languageresources.dictionaries.Dictionary;
import eu.transkribus.languageresources.extractor.pagexml.annotations.PAGEXMLAbbreviation;
import eu.transkribus.languageresources.extractor.xml.XMLExtractor;
import eu.transkribus.interfaces.languageresources.IPagewiseTextExtractor;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 *
 * @author jnphilipp
 */
public class StAZhTEIExtractor extends DTATEIExtractor implements IPagewiseTextExtractor {
    public StAZhTEIExtractor() {
        super();
    }

    public StAZhTEIExtractor(String pathToConfig) {
        super(pathToConfig);
    }

    public StAZhTEIExtractor(File configFile) {
        super(configFile);
    }

    @Override
    public List<String> extractTextFromDocumentPagewise(String path) {
        Document document = this.getDocumentFromFile(path);
        List<String> pages = new ArrayList<>();

        NodeList pbs = document.getElementsByTagName("p");
        for ( int i = 0; i < pbs.getLength(); i++ )
            pages.add(pbs.item(i).getTextContent());
        return pages;
    }
}
