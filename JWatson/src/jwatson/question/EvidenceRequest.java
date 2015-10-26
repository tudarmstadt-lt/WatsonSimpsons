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

package jwatson.question;

public class EvidenceRequest {
    /**
     * An integer in the range 1 – 10 that defines the number of supporting passages
     * to return for each answer. Fewer passages might be returned for answers that
     * do not have good passage support. The default value is 3 passages.
     */
    private int items;

    /**
     * Whether to return evidence profiles for each possible answer.
     */
    private ProfileValue profile;

    public EvidenceRequest(int items, ProfileValue profile) {
        this.items = items;
        this.profile = profile;
    }

    @Override
    public String toString() {
        return "EvidenceRequest{" +
                "items=" + items +
                ", profile='" + profile + '\'' +
                '}';
    }
}
