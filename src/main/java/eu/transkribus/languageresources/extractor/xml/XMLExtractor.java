package eu.transkribus.languageresources.extractor.xml;

import eu.transkribus.languageresources.interfaces.ITextExtractor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author jnphilipp
 */
public class XMLExtractor implements ITextExtractor {
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

        return content.toString();
    }

    protected Document getDocumentFromFile(String path) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            return dBuilder.parse(new File(path));
        }
        catch (Exception ex) {
            Logger.getLogger(XMLExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }

        throw new RuntimeException("XML file could not be found for given path: " + path);
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

    @Override
    public Map<String, Set<String>> extractAbbreviations(String path)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
