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
public class DTATEIExtractor extends XMLExtractor implements IPagewiseTextExtractor {
    public DTATEIExtractor() {
        super();
    }

    public DTATEIExtractor(String pathToConfig) {
        super(pathToConfig);
    }

    public DTATEIExtractor(File configFile) {
        super(configFile);
    }

    @Override
    public List<String> extractTextFromDocumentPagewise(String path) {
        Document document = this.getDocumentFromFile(path);
        List<String> pages = new ArrayList<>();

        NodeList pbs = document.getElementsByTagName("pb");
        String text = this.documentToString(document);
        for ( int i = 0; i < pbs.getLength(); i++ )
            pages.add(this.extractPage(text, pbs.item(i).getAttributes().getNamedItem("facs").getTextContent()));
        return pages;
    }

    @Override
    public String extractTextFromPage(String path, int page) {
        Document document = this.getDocumentFromFile(path);
        NodeList pbs = document.getElementsByTagName("pb");
        return this.extractPage(this.documentToString(document), pbs.item(page).getAttributes().getNamedItem("facs").getTextContent());
    }

    private String extractPage(String document, String facs) {
        StringBuilder page = new StringBuilder();
        Matcher matcher = Pattern.compile("<pb[^>]*facs=\"" + facs + "\"[^>]*/>(.+?)<pb[^>]*/>", Pattern.DOTALL).matcher(document);
        while ( matcher.find() ) {
            page.append(this.stripXML(this.parseAbbreviations(matcher.group(1))));
        }
        return page.toString().trim();
    }

    @Override
    public Dictionary extractAbbreviationsFromPage(String path, int page) {
        throw new UnsupportedOperationException("Not supported yet.");
        /*Document document = this.getDocumentFromFile(path);
        Dictionary dictionary = new Dictionary();
        return dictionary;*/
    }
}
