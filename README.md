# TranskribusLanguageResources

The ConfigTokenizer is used to tokenized strings. The main idea is that is being used with a configuration file. If different use cases call for different types of tokenization, the same tokenizer with different configuration files can be used.

[![Build Status](http://dbis-halvar.uibk.ac.at/jenkins/buildStatus/icon?job=TranskribusLanguageResources)](http://dbis-halvar.uibk.ac.at/jenkins/job/TranskribusLanguageResources)

## Extractors

### Text
The extractors take a file and extract contained text. Text can be extracted document- or page-wise. Three formats are accepted:
* PDF
* PAGE.xml
* TEI

The extractors can be called with a `properties` file. An example can be found [here](https://github.com/Transkribus/TranskribusLanguageResources/blob/master/src/test/resources/extractor_config.properties).

### Abbreviations

Abbreviations can be extracted if the given format contains annotated abbrevitions like TEI or PAGE.XML. The abbreviations are returned in the format `Map<String, Set<String>>`, whereas every abbreviations is stored together with known expansions.

## Out of vocabulary rate

The OOV rate can be calculated using a dictionary and a tokenized text. The necessary methods are located in the class `OutOfVocabularyRate` static and are static. There are two ways to have the OOV rate calculated:

### OOV rate by types

When having the OOV rate calculted by types, the number of types in the dictionary and text is counted, the two numbers are divided: `oov_rate = num_types_not_in_dict / (num_types_in_dict + num_types_not_in_dict)`.

Example: There are 100 tokens in a text but only two types, `word1` and `word2`. `word1` appears 99 times and is in the dictionary, `word2` appears only once and is not in the dictionary. The OOV would be calculated as such: `oov_rate = 1 / (1 + 1) = 0.5`

### OOV rate by tokens

When having the OOV rate calculted by tokens, the number of tokens in the dictionary and text is counted, the two numbers are divided: `oov_rate = num_tokens_not_in_dict / (num_tokens_in_dict + num_tokens_not_in_dict)`.

Example: There are 100 tokens in a text but only two types, `word1` and `word2`. `word1` appears 99 times and is in the dictionary, `word2` appears only once and is not in the dictionary. The OOV would be calculated as such: `oov_rate = 1 / (99 + 1) = 0.01`

## Dictionaries

A Dictionary holds a list of entries. Each entry has a key and frequency and a list of associated words with their frequency. The functions to query a dictionary are defined by the interface `IDictionary` and `IEntry.` In addition entries can be added to a dictionary e.g.

    dictionary.addEntry("Die")
    dictionary.addValue("Uni", "Universität")
    dictionary.addEntry("Leipzig")
    dictionary.addEntry("ist")
    dictionary.addEntry("schön")

This results in the entries *Die*, *Uni*, *Leipzig*, *ist*, *schön* and the entry *Uni* has *Universität* as an associated word. All frequencies are 1.

### DictionaryUtils

The class `DictionaryUtils` holds functions to save and load a dictionary. Both required a path to a folder. The save function stores:
 * meta data file (`metadata.properties`)
 * three arpa files (`entries.arpa`, `entry-character-table.arpa`, `value-character-table.arpa`)
 * two csv (`entry-character-table.csv`, `value-character-table.csv`)

The `entries.arpa` file represents the entries of a dictionary with their frequency as uni- and bigrams. The other two arpa files store the character tables for entries and their value, same as the csv.

To load a dictionary two files are necessary, the meta data file and the `entries.arpa`.


## Building
Here is a short guide with steps that need to be performed
to build your project.

### Requirements
- Java >= version 8
- Maven
- All further dependencies are gathered via Maven

### Build Steps
```
git clone https://github.com/Transkribus/TranskribusLanguageResources
cd TranskribusLanguageResources
mvn install
```

### Links
- https://transkribus.eu/TranskribusLanguageResources/apidocs/index.html
