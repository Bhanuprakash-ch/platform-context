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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trustedanalytics.cloud.cc.api.CcOperations;
import org.trustedanalytics.platformcontext.model.Service;
import org.trustedanalytics.platformcontext.model.ServiceInstance;
import org.trustedanalytics.platformcontext.model.ServiceKey;
import rx.Observable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlatformContextControllerHelpers {

    private final CcOperations ccClient;

    public PlatformContextControllerHelpers(CcOperations ccClient) {
        this.ccClient = ccClient;
    }

    public void mergeServiceKeys(Collection<ServiceInstance> instances,
                                 Observable<ServiceKey> serviceKeys) {
        Map<UUID, List<ServiceKey>> serviceKeysIndex = createServiceKeysIndex(serviceKeys);

        instances.stream().forEach(i -> i.setServiceKeys(
                Optional.ofNullable(serviceKeysIndex.get(i.getGuid()))
                        .orElse(new ArrayList<>())
        ));
    }

    public void mergeInstances(Collection<Service> services, Collection<ServiceInstance> instances) {
        Map<UUID, List<ServiceInstance>> instancesIndex = createInstancesIndex(instances);
        services.stream().forEach(b -> b.setInstances(
                Optional.ofNullable(instancesIndex.get(b.getGuid()))
                        .orElse(new ArrayList<>())
        ));
    }

    public List<Service> getServices(UUID orgId) {
        return ccClient.getOrganizationServices(orgId)
                .map(Service::from)
                .toList().toBlocking().single();
    }

    public List<ServiceInstance> getServiceInstances(UUID spaceId) {
        return ccClient.getSpaceSummary(spaceId).getServiceInstances().stream()
                .map(ServiceInstance::from)
                .filter(si -> si.getServicePlan() != null)
                .collect(Collectors.toList());
    }

    public Observable<ServiceKey> getServiceKeys() {
        return ccClient.getServiceKeys().map(ServiceKey::from);
    }

    public List<Service> filterServicesByTag(List<Service> services, String tag) {
        return services.stream()
                .filter(s -> Optional.ofNullable((ArrayList<String>) s.getTags()).orElse(new ArrayList<String >()).contains(tag))
                .collect(Collectors.toList());
    }

    private Map<UUID, List<ServiceInstance>> createInstancesIndex(Collection<ServiceInstance> instances) {
        return instances.stream()
                .collect(Collectors.groupingBy(i -> i.getServicePlan().getService().getGuid()));
    }

    private Map<UUID, List<ServiceKey>> createServiceKeysIndex(Observable<ServiceKey> serviceKeys) {
        return serviceKeys.toList().toBlocking().single()
                .stream()
                .collect(Collectors.groupingBy(ServiceKey::getService_instance_guid));
    }
}
