/*
 * Copyright 2015 Technische Universitaet Darmstadt
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *         - Uli Fahrer
 */

package controllers;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.net.MalformedURLException;

import jwatson.JWatson;

public class WatsonService implements IWatsonService {

    private JWatson instance;

    public WatsonService() throws MalformedURLException {
        Config conf = ConfigFactory.load();
        String username = conf.getString("watson.username");
        String password = conf.getString("watson.password");
        String url = conf.getString("watson.url");

        instance = new JWatson(username, password, url);
    }

    @Override
    public JWatson getInstance() {
        return instance;
    }
}
