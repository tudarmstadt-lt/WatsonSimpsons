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

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import controllers.IWatsonService;
import controllers.WatsonService;
import models.User;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.F.Promise;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import play.mvc.Results;

public class Global extends GlobalSettings {

    private Injector injector;

    @Override
    public void onStart(Application application) {
        Logger.info("Application has started");

        if (User.findByUsername("guest") == null) {
            User.createGuestUser();
        }

        injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(IWatsonService.class).to(WatsonService.class);
            }
        });
    }

    @Override
    public <T> T getControllerInstance(Class<T> aClass) throws Exception {
        return injector.getInstance(aClass);
    }

    @Override
    public Promise<Result> onHandlerNotFound(RequestHeader request) {
        return Promise.<Result>pure(Results.notFound(views.html.notFoundPage.render(request.uri())));
    }
}
