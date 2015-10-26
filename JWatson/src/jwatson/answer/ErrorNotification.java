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


public class ErrorNotification {

    /**
     * The container for message information about a recoverable error.
     */
    private String error;

    /**
     * A string that describes the error.
     */
    private String text;


    public String getError() {
        return error;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "ErrorNotification{" +
                "error='" + error + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
