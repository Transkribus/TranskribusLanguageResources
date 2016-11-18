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

## Tokenizer

The tokenizer can be called with a `properties` file. An example can be found [here](https://github.com/Transkribus/TranskribusLanguageResources/blob/master/src/test/resources/tokenizer_config.properties).

### Rules
* Normalization
* Dehyphanation signs
* Delimiter signs
* Delimiter signs being kept as tokens

### Further explanation:
#### Normalization
The Java normalizer tackles the representation problem of characters like á or ö. These characters can be represented as a single character (á or ö) or as a basic character with additional diacritic. The java normalizer changes the representation to either representation type.
#### Dehypenation signs
When a word at the end of the line is being cut off and continued on the next line, there often is a hyphenation sign. The tokenizer looks for a given set of files, a following \n and a following small letter in the next line. If that expression is found, the split up word is being put together.
#### Delimiter signs
Delemiters are used for splitting tokens. Common signs among others are spaces, newlines and dots.
#### Delimiter signs being kept as tokens
When there is a token like 'is, ', the user may be interested in getting 'is' as a token and the comma as a dedicated token.

## Out of vocabulary rate

The OOV rate can be calculated using a dictionary and a tokenized text. The necessary methods are located in the class `OutOfVocabularyRate` static and are static. There are two ways to have the OOV rate calculated:

### OOV rate by types

When having the OOV rate calculted by types, the number of types in the dictionary and text is counted, the two numbers are divided: `oov_rate = num_types_not_in_dict / (num_types_in_dict + num_types_not_in_dict)`.

Example: There are 100 tokens in a text but only two types, `word1` and `word2`. `word1` appears 99 times and is in the dictionary, `word2` appears only once and is not in the dictionary. The OOV would be calculated as such: `oov_rate = 1 / (1 + 1) = 0.5`

### OOV rate by tokens

When having the OOV rate calculted by tokens, the number of tokens in the dictionary and text is counted, the two numbers are divided: `oov_rate = num_tokens_not_in_dict / (num_tokens_in_dict + num_tokens_not_in_dict)`.

Example: There are 100 tokens in a text but only two types, `word1` and `word2`. `word1` appears 99 times and is in the dictionary, `word2` appears only once and is not in the dictionary. The OOV would be calculated as such: `oov_rate = 1 / (99 + 1) = 0.01`

## Language Models

Two types of language models are supported: ARPA and neuronal networks.

The offered method by the interface `ILanguageModel` looks like this:

    Map<String, Double> getProbabilitiesForNextToken(List<String> sequence) throws UnsupportedSequenceException;

It takes a list of string of which each string represents one token. The returned `Map<String, Double>` contains the probability for each type depending on the given sequence. The two formats both hold a list of known types.


### ARPA

A language model in the ARPA format can be used using the class `ARPALanguageModel`. It is initialized with the path to the file with the ARPA format. The constructor throws an `ARPAParseException` if the file is malformed. When using `getProbabilitiesForNextToken`, the method looks for a sequence length `n` and return the probability of token `n + 1`. It throws an `UnsupportedSequenceException` if the given list of tokens has either a length which was not in the file or if the sequence is unkown.


### Neural Network

A language model in the form of a neural network can be used using the class `NeuralLanguageModel`. It needs to be initialized with a path to the zip file containing the network. When using `getProbabilitiesForNextToken`, the given sequence is mapped onto a matrix with dimensions `(sequence_length, num_types)`, whereas at each timestamp, the index of the given token will be set `1`. It returns a vector with probabilities for the next token. The returned `double[]` is transformed into a map which contains the type as key and its probability as value. If the given sequence contains an unkown token, an `UnsupportedSequenceException` will be thrown.


## Dictionaries

A Dictionary holds a list of entries. Each entry has a key and frequency and a list of associated words with their frequency. The functions to query a dictionary are defined by the interface `IDictionary` and `IEntry.` In addition entries can be added to a dictionary e.g.

    dictionary.addEntry("Die")
    dictionary.addValue("Uni", "Universität")
    dictionary.addEntry("Leipzig")
    dictionary.addEntry("ist")
    dictionary.addEntry("schön")

This results in the entries *Die*, *Uni*, *Leipzig*, *ist*, *schön* and the entry *Uni* has *Universität* as an associated word. All frequencies are 1.

### DictionaryUtils

The class `DictionaryUtils` holds functions to save and load a dictionary. Both required a path to a folder. The save function store a meta data file, three arpa files and two csv. The `entries.arpa` file represents the entries of a dictionary with their frequency as uni- and bigrams. The other two arpa files store the character tables for entries and their value, same as the csv.

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
