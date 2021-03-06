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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * A token delegate that works across all kuromoji dictionaries
 */
public class Token {

    private final static String DEFAULT_FIELD_VALUE = "*";

    /**
     * the dictionary this token is from/for
     */
    private Dictionary dictionary = null;

    /**
     * the underlying token implementation
     */
    private TokenBase token;

    public Token(Dictionary dictionary, TokenBase sourceToken) {
        this.dictionary = dictionary;
        this.token = sourceToken;
    }

    /**
     * delegate for surface form
     */
    public String getSurface() {
        return token.getSurface();
    }

    /**
     * Return the position of this token in the source text
     *
     * @return
     */
    public int getPosition() {
        return token.getPosition();
    }

    /**
     * Get the featuers of this token
     *
     * @return
     */
    public String[] getAllFeaturesArray() {
        return token.getAllFeaturesArray();
    }

    public String getAllFeatures() {
        return token.getAllFeatures();
    }

    /**
     * Get the feature by feature name
     *
     * @param featureName
     * @return
     */
    public String getFeature(String featureName) {
        Method method = dictionary.getFields().get(featureName);
        if (method != null) {
            try {
                return String.valueOf(method.invoke(token));
            } catch (Exception e) {
                // ignored
            }
        }
        return DEFAULT_FIELD_VALUE;
    }

    public List<String> getFeatureNames() {
        return new ArrayList<String>(dictionary.getFields().keySet());
    }

    /**
     * is it from the dictionary ?
     */
    public boolean isKnown() {
        return token.isKnown();
    }


    /**
     * is it from a user dictionary
     */
    public boolean isUser() {
        return token.isUser();
    }


    @Override
    public String toString() {
        return "Token{" +
            "surfaceForm='" + getSurface() + '\'' +
            ", position=" + getPosition() +
            ", dictionary=" + dictionary.getName() +
            '}';
    }

}
