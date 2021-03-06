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
package org.trustedanalytics.platformcontext.cf;

import static org.springframework.context.annotation.ScopedProxyMode.INTERFACES;
import static org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;
import org.trustedanalytics.cloud.auth.OAuth2TokenRetriever;
import org.trustedanalytics.cloud.cc.FeignClient;
import org.trustedanalytics.cloud.cc.api.CcOperations;
import org.trustedanalytics.platformcontext.StashErrorDecoder;
import org.trustedanalytics.platformcontext.data.CFDataProvider;
import org.trustedanalytics.platformcontext.rest.PlatformContextControllerHelpers;


@Configuration
public class CcConfig {

    @Value("${cf.resource:/}")
    private String apiBaseUrl;

    @Autowired
    private OAuth2TokenRetriever tokenRetriever;

    @Bean
    public StashErrorDecoder stashErrorDecoder() {
        return new StashErrorDecoder();
    }

    @Bean
    @Scope(value = SCOPE_REQUEST, proxyMode = INTERFACES)
    protected CcOperations ccClient(@Qualifier("restTemplateWithOAuth2Token") RestTemplate restTemplate) {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final String token = tokenRetriever.getAuthToken(auth);

        return new FeignClient(apiBaseUrl, builder -> builder
                .errorDecoder(stashErrorDecoder())
                .requestInterceptor(template -> template.header("Authorization", "bearer " + token)));
    }

    @Bean
    public CFDataProvider getCFDataProvider(CcOperations ccClient)
    {
        return new CFDataProvider(new PlatformContextControllerHelpers(ccClient));
    }
}
