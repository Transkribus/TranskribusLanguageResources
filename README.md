# TranskribusLanguageResources

[![Build Status](http://dbis-halvar.uibk.ac.at/jenkins/buildStatus/icon?job=TranskribusLanguageResources/eu.transkribus:TranskribusXMLExtractor)](http://dbis-halvar.uibk.ac.at/jenkins/job/TranskribusLanguageResources/eu.transkribus:TranskribusXMLExtractor)

## Generic Extractor

The generic extractor is used to extract text from various file types into seperate text files. It is initiated with a base folder, an input and an output folder. It will then check all the files in the input folder, extract files from the found documents and create textfiles in the output folder:

    /base_folder
    /base_folder/data
    /base_folder/data/file1.docx
    /base_folder/data/nested_folder/file2.xml
    /base_folder/data_output
    /base_folder/data_output/file1.txt
    /base_folder/data_output/nested_folder/file2.txt
    
Corresponding java code:

    extractor = new GenericExtractor();
    extractor.extract("src/test/resources", "data", "data_output");


### Settings

The extraction can also be started with a property object to account for specific requirements for the given files.

| Option name | allowed values | description |
| --- | --- | --- |
| keep_abbreviations_in_brackets | true, false (default) | If a document contains abbreviations in the form of `It(em)`, the value `true` would keep that value as it is to show where abbreviations are, `false` would remove the brackets |
| delete_file_newline | true, false (default) | Removes the line breaks created by the file |
| slash_to_newline | true, false (default) | Changes `/` to `\n` |
| delete_between_squared_brackets | true, false (default) | removes everything between squared brackets |
| delete_between_round_brackets | true, false (default) | removes everything between round brackets |
| delete_between_equal_signs | true, false (default) | removes everything between equal signs |
| delete_equal_signs | true, false (default) | removes equal signs |
