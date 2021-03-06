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
package org.trustedanalytics.platformcontext;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.context.annotation.Scope;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.trustedanalytics.cloud.auth.OAuth2TokenRetriever;
import org.trustedanalytics.platformcontext.data.DataProvider;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST;

@Configuration
public class ApplicationConfiguration {
    
    @Bean
    public DataProvider getDataProvider()
    {
        return new DataProvider();
    }

    @Bean
    @Scope(value = SCOPE_REQUEST, proxyMode= TARGET_CLASS)
    protected RestTemplate restTemplateWithOAuth2Token() {
        /*Default SimpleClientHttpRequestFactory caused random "Unexpected end of file" errors while createing
        requests to Clound Controller*/
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        return(new RestTemplate(factory));
    }

    @Bean
    protected OAuth2TokenRetriever tokenRetriever() {
        return new OAuth2TokenRetriever();
    }
}
