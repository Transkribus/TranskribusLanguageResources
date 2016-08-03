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

## Building
Here is a short guide with steps that need to be performed
to build your project.

### Requirements
- Java >= version 7
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
