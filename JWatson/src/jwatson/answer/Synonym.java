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

/**
 * A synonym that belongs to a synSet. The synonym is represented by its value and whether it is used.
 * All synonyms are returned in lowercase text.
 *
 * @author Uli Fahrer
 */
public class Synonym {

    private boolean isChosen;
    private String value;
    private int weight;

    public Synonym() {
    }

    public boolean isChosen() {
        return isChosen;
    }

    public String getValue() {
        return value;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "Synonym{" +
                "isChosen=" + isChosen +
                ", value='" + value + '\'' +
                ", weight=" + weight +
                '}';
    }
}
