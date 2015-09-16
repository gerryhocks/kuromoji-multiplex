/**
 * Copyright © 2010-2015 Atilika Inc. and contributors (see CONTRIBUTORS.md)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the
 * License is distributed with this work in the LICENSE.md file.  You may
 * also obtain a copy of the License from
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.atilika.kuromoji;

import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An abstraction of a kuromoji dictionary
 */
public class Dictionary {

    private static final String ATILIKA_CLASSES = "com.atilika.kuromoji";

    public static class InvalidDictionaryException extends Exception {
        public InvalidDictionaryException(String message) {
            super(message);
        }
    }

    /**
     * a registry of all known dictionaries
     */
    private static Map<String, Dictionary> registry = new HashMap<String, Dictionary>();
    /**
     * the default dictionary (essentially the first found implementation)
     */
    private static Dictionary defaultDictionary = null;

    /**
     * the dictionary name
     */
    private String name;
    /**
     * the tokenizer used by the dictionary
     */
    private Class<? extends TokenizerBase> tokenizer;
    /**
     * a map of feilds/feature names to their implementation's field id
     */
    private Map<String, Integer> fields;

    private static void initialize() {
        // search for implementations and register them
        if (registry.isEmpty()) {
            Reflections reflections = new Reflections(ATILIKA_CLASSES);
            Set<Class<? extends TokenizerBase>> tokenizers = reflections.getSubTypesOf(TokenizerBase.class);
            for (Class<? extends TokenizerBase> tokenizer : tokenizers) {
                String name = tokenizer.getName().startsWith(ATILIKA_CLASSES) ? tokenizer.getName().substring(ATILIKA_CLASSES.length() + 1) : tokenizer.getName();
                if (name.endsWith(".Tokenizer")) {
                    name = name.substring(0, name.length() - 10);
                }
                register(name, tokenizer);
            }
        }
    }

    /**
     * Load a dictionary
     *
     * @param dictionaryName
     * @return
     * @throws InvalidDictionaryException
     */
    public static Dictionary getDictionary(String dictionaryName) throws InvalidDictionaryException {
        initialize();

        Dictionary dictionary;
        if (dictionaryName == null) {
            dictionary = defaultDictionary;
        } else {
            dictionary = registry.get(dictionaryName);
        }

        if (dictionary == null) {
            throw new InvalidDictionaryException(defaultDictionary == null ?
                "Cannot find dictionary - no dictionaries loaded"
                : "Cannot find dictionary '" + dictionaryName + "'");
        }

        return dictionary;
    }


    /**
     * Register a tokenizer implementation
     *
     * @param name
     * @param tokenizer
     */
    public static void register(String name, Class<? extends TokenizerBase> tokenizer) {
        Dictionary dictionary = new Dictionary();
        dictionary.name = name;
        dictionary.tokenizer = tokenizer;
        registry.put(name, dictionary);
        if (defaultDictionary == null) {
            defaultDictionary = dictionary;
        }
        dictionary.fields = extractDictionaryFields(tokenizer);
    }

    /**
     * Try to determine the tokenizer's feature/field names and their field ids
     *
     * @param tokenizer
     * @return
     */
    private static Map<String, Integer> extractDictionaryFields(Class<? extends TokenizerBase> tokenizer) {
        Map<String, Integer> fields = new HashMap<String, Integer>();

        // scrape the field declarations
        String dictionaryFieldClassname = tokenizer.getName().replaceAll("Tokenizer", "compile.DictionaryEntry");

        String[] dummyDictionaryFields = new String[128];
        for (int i = 0; i < dummyDictionaryFields.length; i++) {
            dummyDictionaryFields[i] = "0";
        }
        try {
            Class dictFields = Class.forName(dictionaryFieldClassname);
            Constructor c = dictFields.getConstructors()[0];
            Object instance = c.newInstance(new Object[]{dummyDictionaryFields});
            for (Field field : dictFields.getFields()) {
                fields.put(field.getName(), field.getInt(instance));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fields;
    }

    /**
     * Return a list of known dictionaries
     *
     * @return
     */
    public static List<String> getDictionaryNames() {
        initialize();
        return new ArrayList<String>(registry.keySet());
    }

    /**
     * Return the name of the dictionary
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Return a map of known fields to their field id
     *
     * @return
     */
    public Map<String, Integer> getFields() {
        return fields;
    }

    /**
     * Return an instance of a tokenizer for this dictionary
     *
     * @return
     */
    public TokenizerBase getTokenizer() {
        try {
            return (TokenizerBase) (tokenizer.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
