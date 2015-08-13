/**
 * Copyright (c) 2015 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.trustedanalytics.platformcontext.rest;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.trustedanalytics.platformcontext.data.PlatformContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.trustedanalytics.platformcontext.data.DataProvider;


@RestController
public class PlatformContextController {
    public static final String GET_PLATFORM_CONTEXT_URL = "/rest/platform_context";

    private final DataProvider dataProvider;

    @Autowired
    public PlatformContextController(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    @RequestMapping(value = GET_PLATFORM_CONTEXT_URL, method = GET, produces = APPLICATION_JSON_VALUE)
    public PlatformContext getPlatformContext() {
        return dataProvider.getData();
    }
}
