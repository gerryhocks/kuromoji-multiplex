# kuromoji-multiplex
Simple way to tokenize using multiple Kuromoji dictionaries

Usage examples


``` java

   Tokenizer tokenizer = new Tokenizer("unidic");
   List<Token> tokens = tokenizer.tokenize("お寿司がたべたい"))

```

``` java

for (String dictionaryName : Dictionary.getDictionaryNames()) {
   if ((selectedDictionary == null) || dictionaryName.equalsIgnoreCase(selectedDictionary)) {
      Tokenizer tokenizer = new Tokenizer(dictionaryName);
      for (Token token : tokenizer.tokenize("お寿司がたべたい")) {
        System.out.println(dictionaryName + "\t" + token.getSurface() + "\t" + token.getAllFeatures());
      }
   }
}

```