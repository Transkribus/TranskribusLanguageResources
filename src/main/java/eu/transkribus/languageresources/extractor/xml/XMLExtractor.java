package eu.transkribus.languageresources.extractor.xml;

import eu.transkribus.languageresources.dictionaries.Dictionary;
import eu.transkribus.languageresources.interfaces.IDictionary;
import eu.transkribus.languageresources.interfaces.ITextExtractor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author jnphilipp
 */
public class XMLExtractor implements ITextExtractor {
    private static final Pattern PATTERN_CHOICE = Pattern.compile("<choice>(.*?)</choice>");
    private static final Pattern PATTERN_ABBR = Pattern.compile("<abbr>(.+)</abbr>");
    private static final Pattern PATTERN_ABBR_ATTR = Pattern.compile("<abbr expand=\"([^\"]+)\">(.+?)</abbr>");
    private static final Pattern PATTERN_EXPAN = Pattern.compile("<expan>(.+)</expan>");

    protected final Properties properties;

    public XMLExtractor() {
        this.properties = new Properties();
    }

    public XMLExtractor(String pathToConfig) {
        this(new File(pathToConfig));
    }

    public XMLExtractor(File configFile) {
        this.properties = new Properties();
        try {
            this.properties.load(new FileInputStream(configFile));
        }
        catch (IOException ex) {
            Logger.getLogger(XMLExtractor.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Could not load given property file with path: " + configFile.getAbsolutePath());
        }
    }

    public Properties getProperties() {
        return this.properties;
    }

    @Override
    public String extractTextFromDocument(String path) {
        return this.extractTextFromDocument(path, "\n");
    }

    @Override
    public String extractTextFromDocument(String path, String splitCharacter) {
        Document document = this.getDocumentFromFile(path);
        StringBuilder content = new StringBuilder();

        NodeList nList = document.getElementsByTagName("text");
        if ( nList.getLength() > 0 ) {
            for ( int i = 0; i < nList.getLength(); i++ ) {
                content.append(this.parseAbbreviations(nList.item(i), this.properties.getProperty("abbreviation_expansion_mode", "keep")));

                if ( i + 1 < nList.getLength() ) {
                    content.append(splitCharacter);
                }
            }
        }
        else
            content.append(this.parseAbbreviations(document.getDocumentElement(), this.properties.getProperty("abbreviation_expansion_mode", "keep")));

        return content.toString().trim();
    }

    protected Document getDocumentFromFile(String path) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            return dBuilder.parse(new File(path));
        }
        catch (ParserConfigurationException ex)
        {
            ex.printStackTrace();
            throw new RuntimeException();
        }
        catch(SAXException ex)
        {
            ex.printStackTrace();
            throw new RuntimeException("XML file could not be parsed, it is probably malformatted.");
        }
        catch(IOException ex)
        {
            throw new RuntimeException("XML file could not be found for given path: " + path);
        }
    }

    public String parseAbbreviations(Node node) {
        return this.parseAbbreviations(node, this.properties.getProperty("abbreviation_expansion_mode", "keep"));
    }

    public String parseAbbreviations(Node node, String abbreviationExpansionMode) {
        if ( !abbreviationExpansionMode.equals("keep") && !abbreviationExpansionMode.equals("expand") ) {
            throw new IllegalArgumentException("Unkown mode, abbreviationExpansionMode has to be 'keep' or 'expand'");
        }

        if ( node.getNodeName().equalsIgnoreCase("choice") ) {
            NodeList children = node.getChildNodes();
            String abbr = "", expan = "";
            for ( int i = 0; i < children.getLength(); i++ )
                if ( children.item(i).getNodeName().equalsIgnoreCase("abbr") )
                    abbr = children.item(i).getTextContent();
                else if ( children.item(i).getNodeName().equalsIgnoreCase("expan") )
                    expan = children.item(i).getTextContent();

            if ( abbreviationExpansionMode.equals("keep") || expan.isEmpty() )
                return abbr;
            else
                return expan;
        }
        else if ( node.getNodeName().equalsIgnoreCase("abbr") ) {
            String abbr = node.getTextContent();
            String expan = node.getAttributes().getNamedItem("expan") == null ? "" : node.getAttributes().getNamedItem("expan").getTextContent();
            if ( abbreviationExpansionMode.equals("keep") || expan.isEmpty() )
                return abbr;
            else
                return expan;
        }
        else if ( node.hasChildNodes() ) {
            NodeList children = node.getChildNodes();
            StringBuilder content = new StringBuilder();
            for ( int i = 0; i < children.getLength(); i++ )
                content.append(this.parseAbbreviations(children.item(i), abbreviationExpansionMode));
            return content.toString();
        }
        else
            return node.getTextContent();
    }

    public String parseAbbreviations(String text) {
        return this.parseAbbreviations(text, this.properties.getProperty("abbreviation_expansion_mode", "keep"));
    }

    public String parseAbbreviations(String text, String abbreviationExpansionMode) {
        if ( !abbreviationExpansionMode.equals("keep") && !abbreviationExpansionMode.equals("expand") ) {
            throw new IllegalArgumentException("Unkown mode, abbreviationExpansionMode has to be 'keep' or 'expand'");
        }

        Matcher matcher = PATTERN_CHOICE.matcher(text);
        while ( matcher.find() ) {
            String abbr = "", expan = "";
            Matcher abbr_matcher = PATTERN_ABBR.matcher(matcher.group(1));
            if ( abbr_matcher.find() )
                abbr = abbr_matcher.group(1);
            Matcher expan_matcher = PATTERN_EXPAN.matcher(matcher.group(1));
            if ( expan_matcher.find() )
                expan = expan_matcher.group(1);
            text = text.replaceAll(matcher.group(), abbreviationExpansionMode.equals("keep") || expan.isEmpty() ? abbr : expan);
        }
        matcher = PATTERN_ABBR_ATTR.matcher(text);
        while ( matcher.find() ) {
            String abbr = matcher.group(2);
            String expan = matcher.group(1);
            text = text.replaceAll(matcher.group(), abbreviationExpansionMode.equals("keep") || expan.isEmpty() ? abbr : expan);
        }
        return text;
    }

    public String stripXML(String text) {
        return text.replaceAll("<[^>]*>", "");
    }

    @Override
    public IDictionary extractAbbreviations(String path) {
        Document document = this.getDocumentFromFile(path);
        IDictionary dictionary = new Dictionary();

        NodeList nodes = document.getElementsByTagName("choice");
        for  ( int i = 0; i < nodes.getLength(); i++ ) {
            NodeList children = nodes.item(i).getChildNodes();
            String abbr = "", expan = "";
            for ( int j = 0; j < children.getLength(); j++ )
                if ( children.item(j).getNodeName().equalsIgnoreCase("abbr") )
                    abbr = children.item(j).getTextContent();
                else if ( children.item(j).getNodeName().equalsIgnoreCase("expan") )
                    expan = children.item(j).getTextContent();
            ((Dictionary)dictionary).addEntry(abbr);
            if ( !expan.isEmpty() )
                ((Dictionary)dictionary).addValue(abbr, expan);
        }
        nodes = document.getElementsByTagName("abbr");
        for  ( int i = 0; i < nodes.getLength(); i++ ) {
            if (nodes.item(i).getParentNode().getNodeName().equalsIgnoreCase("choice") )
                continue;
            ((Dictionary)dictionary).addEntry(nodes.item(i).getTextContent());
            String expan = nodes.item(i).getAttributes().getNamedItem("expan") == null ? "" : nodes.item(i).getAttributes().getNamedItem("expan").getTextContent();
            if ( !expan.isEmpty() )
                ((Dictionary)dictionary).addValue(nodes.item(i).getTextContent(), expan);
        }

        return dictionary;
    }

    protected String documentToString(Document document) {
        try {
            DOMSource domSource = new DOMSource(document);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            transformer.transform(domSource, result);
            return writer.toString();
        }
        catch ( TransformerException e ) {
            return null;
        }
    }

    private boolean underTextNode(Node n)
    {
        if(n.getNodeName().equals("text"))
            return true;

        if(n.getParentNode() == null)
            return false;

        return underTextNode(n.getParentNode());
    }

    private Dictionary extractTag(String path, String tagName)
    {
        Dictionary dictionary = new Dictionary();

        Document document = getDocumentFromFile(path);
        NodeList foundNodes = document.getElementsByTagName(tagName);

        for(int i = 0, len = foundNodes.getLength(); i < len; i++)
        {
            if(underTextNode(foundNodes.item(i)))
            {
                dictionary.addEntry(foundNodes.item(i).getTextContent());
            }
        }

        return dictionary;
    }

    @Override
    public Dictionary extractPlaceNames(String path)
    {
        return extractTag(path, "placeName");
    }

    @Override
    public Dictionary extractPersonNames(String path)
    {
        return extractTag(path, "persName");
    }
}
