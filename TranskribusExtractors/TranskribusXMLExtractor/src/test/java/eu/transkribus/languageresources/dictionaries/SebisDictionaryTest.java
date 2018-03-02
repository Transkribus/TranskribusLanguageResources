package eu.transkribus.languageresources.dictionaries;

import eu.transkribus.languageresources.dictionaries.Dictionary;
import eu.transkribus.languageresources.dictionaries.DictionaryUtils;
import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import eu.transkribus.languageresources.exceptions.ARPAParseException;
import eu.transkribus.languageresources.extractor.pagexml.PAGEXMLExtractor;
import eu.transkribus.tokenizer.TokenizerConfig;

public class SebisDictionaryTest {
	
	public static void arpaToDict(String arpaFile) throws Exception {
		
		
		File f = new File(arpaFile);
		
		File fOut = new File(f.getAbsolutePath()+".dict");
		
		boolean start=false;
		try(BufferedReader br = new BufferedReader(new FileReader(f));
				BufferedWriter bw = new BufferedWriter(new FileWriter(fOut))
				
				) {
		    for(String line; (line = br.readLine()) != null; ) {
		    	if (line.equals("\\1-grams:")) {
		    		start = true;
		    	}
		    	if (line.equals("\\end\\")) {
		    		start = false;
		    	}
		    
		    	if (start) {
		    		String[] s = line.split("\t");
		    		if (s.length == 2) {
		    			bw.write(s[1]+","+(int)Double.parseDouble(s[0])+"\n");
		    		}
		    		
		    	}
		    }
		    // line is not visible here.
		}
		
		
		
	}

	// @Test
	public static void dictionaryFromPAGEXML(String exportedDocFolder)
			throws ARPAParseException, FileNotFoundException, IOException {
		String pathFoFile = exportedDocFolder;
		String dictionaryFolder = exportedDocFolder + "/dictionary";
		
		// first, we extract the text from the page xml folder
		PAGEXMLExtractor textExtraktor = new PAGEXMLExtractor();
		String text = textExtraktor.extractTextFromDocument(pathFoFile, " ").get("<default>");

		Properties tokenizerProperties = new Properties();

		// we use simple dehyphenation
		tokenizerProperties.setProperty("dehyphenation_signs", "¬");

		// new lines, dots and commas are not treated as types
		// example: word.word -> 'word', '.', 'word'
		tokenizerProperties.setProperty("delimiter_signs", "\n., ");

		// the emptry string means we do not keep the delimiter signs
		tokenizerProperties.setProperty("keep_delimiter_signs", "");

		TokenizerConfig tokenizer = new TokenizerConfig(tokenizerProperties);
		List<String> tokenizedText = tokenizer.tokenize(text);

		// the dictionary is created with the tokenized text
		// and is written into a file without frequencies
		Dictionary dictionary = new Dictionary(tokenizedText);
		DictionaryUtils.save(dictionaryFolder, dictionary);
		Dictionary dictionary2 = (Dictionary) DictionaryUtils.load(dictionaryFolder);
		assertEquals(dictionary.getEntries(), dictionary2.getEntries());
		assertEquals(dictionary.getEntryCharacterTable(), dictionary2.getEntryCharacterTable());
		assertEquals(dictionary.getValueCharacterTable(), dictionary2.getValueCharacterTable());
		assertEquals(dictionary.getNumberTokens(), dictionary2.getNumberTokens());
		assertEquals(dictionary.getNumberTypes(), dictionary2.getNumberTypes());
		assertEquals(dictionary.getName(), dictionary2.getName());
		assertEquals(dictionary.getDescription(), dictionary2.getDescription());
		assertEquals(dictionary.getLanguage(), dictionary2.getLanguage());
		assertEquals(dictionary.getCreationDate(), dictionary2.getCreationDate());
		assertEquals(dictionary, dictionary2);
	}

	public static void main(String[] args) throws Exception {
		dictionaryFromPAGEXML("/home/sebastian/tmp/TRAIN_CITlab_Ambraser_Heldenbuch/TRAIN_CITlab_Ambraser_Heldenbuch/");
		
//		arpaToDict("/home/sebastian/tmp/TRAIN_CITlab_Ambraser_Heldenbuch/TRAIN_CITlab_Ambraser_Heldenbuch/dictionary/entries.arpa");
		
//		System.out.println("loading dictionary...");
//		
//		long t = System.currentTimeMillis();
//		IDictionary d = DictionaryUtils.load(new File("/home/sebastian/tmp/TRAIN_CITlab_Ambraser_Heldenbuch/TRAIN_CITlab_Ambraser_Heldenbuch/"));
//		System.out.println("loaded dict, t = "+(System.currentTimeMillis()-t));
//		t = System.currentTimeMillis();
//		
//		DictionaryUtils.save("/home/sebastian/tmp/", d);
		
	}

}
