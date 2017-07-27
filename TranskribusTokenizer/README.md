# Tokenizer

The tokenizer can be called with a `properties` file. An example can be found [here](https://github.com/Transkribus/TranskribusTokenizer/blob/master/src/test/resources/tokenizer_config.properties).

## Rules
* Normalization
* Dehyphanation signs
* Delimiter signs
* Delimiter signs being kept as tokens

## Further explanation:
### Normalization
The Java normalizer tackles the representation problem of characters like á or ö. These characters can be represented as a single character (á or ö) or as a basic character with additional diacritic. The java normalizer changes the representation to either representation type.
### Dehypenation signs
When a word at the end of the line is being cut off and continued on the next line, there often is a hyphenation sign. The tokenizer looks for a given set of files, a following \n and a following small letter in the next line. If that expression is found, the split up word is being put together.
### Delimiter signs
Delemiters are used for splitting tokens. Common signs among others are spaces, newlines and dots.
### Delimiter signs being kept as tokens
When there is a token like 'is, ', the user may be interested in getting 'is' as a token and the comma as a dedicated token.
