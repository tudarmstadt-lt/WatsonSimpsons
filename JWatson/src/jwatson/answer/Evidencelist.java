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


public class Evidencelist {

    /**
     * A decimal percentage that represents the confidence that Watson has in this evidence.
     * Higher values represent higher confidences.
     */
    private String value;

    /**
     * The evidence passage text.
     */
    private String text;

    /** */
    private String id;

    /**
     * The title of the document for the evidence passage.
     */
    private String title;

    /**
     * The URL to a document passage for an answer.
     */
    private String document;

    /**
     * The copyright holder for the document that contains the evidence passage.
     * If there is no copyright information, the element is empty.
     */
    private String copyright;

    /**
     * A URL that points to the license that describes the terms of use of the document
     * that contains the evidence passage. If there is no information about terms of use,
     * the element is empty.
     */
    private String termsOfUse;

    /**
     * The container for the metadata from the document that contains the evidence passage.
     * This element is returned only when the IBM Watson processing pipeline is configured to support metadata.
     */
    private Metadatamap metadatamap;

    public double getValue() {
        return Double.parseDouble(value);
    }

    public String getText() {
        return text;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDocument() {
        return document;
    }

    public String getCopyright() {
        return copyright;
    }

    public String getTermsOfUse() {
        return termsOfUse;
    }

    public Metadatamap getMetadatamap() {
        return metadatamap;
    }

    @Override
    public String toString() {
        return "Evidencelist{" +
                "value='" + value + '\'' +
                ", text='" + text + '\'' +
                ", id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", document='" + document + '\'' +
                ", copyright='" + copyright + '\'' +
                ", termsOfUse='" + termsOfUse + '\'' +
                ", metadatamap=" + metadatamap +
                '}';
    }
}
