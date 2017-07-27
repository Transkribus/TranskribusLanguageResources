/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.tokenizer.dehyphenator;

import eu.transkribus.tokenizer.interfaces.IDehyphenator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author gundram
 */
public class DehyphenatorSimple implements IDehyphenator {

    private String suffixes;//= "-:=";
    private String prefixes;// = ":=";
    private boolean skipSuffixes;// = false;
    private boolean skipPrefixes;// = true;
    private boolean nextIsLower;// = true;
    Pattern p;
    private String pattern;

    public DehyphenatorSimple(String suffixes, String prefixes, boolean skipSuffixes, boolean skipPrefixes, boolean nextIsLower) {
        this.suffixes = suffixes;
        this.prefixes = prefixes;
        this.skipSuffixes = skipSuffixes;
        this.skipPrefixes = skipPrefixes;
        this.nextIsLower = nextIsLower;
        init();
    }

    public DehyphenatorSimple() {
        this("-¬", "", false, false, true);
    }

    private void init() {
        String suffix = suffixes == null || suffixes.isEmpty() ? "" : "[" + suffixes + "]" + (skipSuffixes ? "?" : "");
        String prefix = prefixes == null || prefixes.isEmpty() ? "" : "[" + prefixes + "]" + (skipPrefixes ? "?" : "");
        this.pattern = suffix + "\n" + prefix;
        p = Pattern.compile(suffix + "\n" + prefix, Pattern.UNICODE_CASE);
    }

    @Override
    public String process(String[] lines) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            String get = lines[i];
            sb.append(get);
            if (i < lines.length - 1) {
                sb.append('\n');
            }

        }
        return process(sb.toString());
    }

    @Override
    public String process(List<String> lines) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            String get = lines.get(i);
            sb.append(get);
            if (i < lines.size() - 1) {
                sb.append('\n');
            }

        }
        return process(sb.toString());
    }

    @Override
    public String process(String line) {

        while (true) {
            Matcher matcher = p.matcher(line);
            if (!matcher.find()) {
                break;
            }
            int start = matcher.start();
            int end = matcher.end();
            if (!nextIsLower || Character.isLowerCase(line.charAt(end))) {
                line = line.substring(0, start) + line.substring(end);
            } else {
                break;
            }
        }
        return line;
    }

}
