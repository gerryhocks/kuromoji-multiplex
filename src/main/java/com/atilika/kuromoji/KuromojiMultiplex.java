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

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by gerry on 12/09/15.
 */
public class KuromojiMultiplex {

    public static void main(String args[]) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String selectedDictionary = args.length > 0 ? args[0] : null;

        try {
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                for (String dictionaryName : Dictionary.getDictionaryNames()) {
                    if ((selectedDictionary == null) || dictionaryName.equalsIgnoreCase(selectedDictionary)) {
                        Tokenizer tokenizer = new Tokenizer(dictionaryName);
                        for (Token token : tokenizer.tokenize(line)) {
                            System.out.println(dictionaryName + "\t" + token.getSurface() + "\t" + token.getAllFeatures());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
