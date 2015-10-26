/*
 *  Copyright 2015 Technische Universität Darmstadt
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  Contributors:
 *      - Uli Fahrer
 *
 */

package jwatson.answer;

import java.util.List;

public class Synonymlist {

    private String partOfSpeech;
    private String value;
    private String lemma;

    /**
     * The set that represents the source (such as wordnet or wikiredirect) of the synonyms.
     */
    private List<Synset> synSet;

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public String getValue() {
        return value;
    }

    public String getLemma() {
        return lemma;
    }

    public List<Synset> getSynset() {
        return synSet;
    }

    @Override
    public String toString() {
        return "Synonymlist{" +
                "partOfSpeech='" + partOfSpeech + '\'' +
                ", value='" + value + '\'' +
                ", lemma='" + lemma + '\'' +
                ", synset=" + synSet +
                '}';
    }
}
