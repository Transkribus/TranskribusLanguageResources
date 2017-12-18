package eu.transkribus.languageresources.extractor.xml.tei;

import eu.transkribus.languageresources.extractor.xml.tei.DTATEIExtractor;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class TEI2TxtExtractor extends DTATEIExtractor
{

    @Override
    protected String getTextNodeName()
    {
        return "text";
    }

    @Override
    protected Document getDocumentFromFile(String xml)
    {
        String text;
        Pattern pattern1 = Pattern.compile("(?<tag><pb n=\"(?<n>[0-9]+)\" facs=\"(?<folder>[A-Z0-9]+)\\\\(?<number>[0-9]+).jpg\"/>)");
        Pattern pattern2 = Pattern.compile("(?<tag><pb n=\"(?<n>[0-9]+)\" facs=\"(?<number>[A-Za-z0-9_]+)\"\\s?/>)");
        Pattern pattern3 = Pattern.compile("(?<tag><pb n=\"(?<n>[0-9]+)\" .*>)");

        try (BufferedReader reader = new BufferedReader(new FileReader(xml)))
        {
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            String ls = System.getProperty("line.separator");

            while ((line = reader.readLine()) != null)
            {
                if (line.contains("<pb"))
                {
                    boolean found = false;

                    Matcher m = pattern1.matcher(line);
                    while (m.find())
                    {
                        found = true;
                        String tag = m.group("tag");
                        String folder = m.group("folder");
                        String number = m.group("number");
                        int contains = line.indexOf(tag);
                        line = line.substring(0, contains) + "\n§" + folder + ":" + number + "=\n" + line.substring(contains);
                    }

                    if (!found)
                    {
                        m = pattern2.matcher(line);
                        while (m.find())
                        {
                            found = true;
                            String tag = m.group("tag");
                            String number = m.group("number");
                            int contains = line.indexOf(tag);
                            line = line.substring(0, contains) + "\n§" + number + "=\n" + line.substring(contains);
                        }
                    }

                    if (!found)
                    {
                        m = pattern3.matcher(line);
                        while (m.find())
                        {
                            found = true;
                            String tag = m.group("tag");
                            int n = new Integer(m.group("n"));
                            int contains = line.indexOf(tag);
                            line = line.substring(0, contains) + "\n§" + n + "=\n" + line.substring(contains);
                        }
                    }
                } else
                {
                    line = line.replaceAll("<lb break=\"yes\"/>", "==");
                    line = line.replaceAll("<lb/>", "==");
                    line = line.replaceAll("</l>", "</l>==");
                    line = line.replaceAll("</p>", "</p>==");
                    line = line.replaceAll("<p>", "<p>==");
                    line = line.replaceAll("</head>", "</head>==");
                    line = line.replaceAll("<!-- Bindestrich -->", "");
                    line = line.replaceAll("Page is empty.", "");
                }

                stringBuilder.append(line);
                stringBuilder.append(ls);
            }

            text = stringBuilder.toString();
            text = text.replaceAll("<\\?oxy_comment_start.*?\\?>", "");
            text = text.replaceAll("<\\?oxy_comment_end\\?>", "");

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(false);
            dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(text)));
            return doc;
        } catch (Exception ex)
        {
            Logger.getLogger(TEI2TxtExtractor.class.getName()).log(Level.SEVERE, null, ex);
        } finally
        {
        }

        throw new RuntimeException("Could not read xml file form path: " + xml);
    }

}
