# kuromoji-multiplex

Simple way to tokenize using multiple Kuromoji dictionaries.

## Usage examples

Tokenize using a specific dictionary
```java

   Tokenizer tokenizer = new Tokenizer("unidic");
   List<Token> tokens = tokenizer.tokenize("お寿司がたべたい"))

```


Tokenize the same string with all discovered dictionaries
```java

for (String dictionaryName : Dictionary.getDictionaryNames()) {
  Tokenizer tokenizer = new Tokenizer(dictionaryName);
  for (Token token : tokenizer.tokenize("お寿司がたべたい")) {
     System.out.println(dictionaryName + "\t" + token.getSurface());
     for (String name : token.getFeatureNames()) {
        System.out.println("\t" + name + ": " + token.getFeature(name));
     }
  }
}

```