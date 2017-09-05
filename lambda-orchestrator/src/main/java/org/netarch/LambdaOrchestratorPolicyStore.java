/*
 * Copyright 2017-present Network Architecture Laboratory, Tsinghua University
 *
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
 */
package org.netarch;

import org.onosproject.store.serializers.KryoNamespaces;
import org.onosproject.store.service.ConsistentMap;
import org.onosproject.store.service.Serializer;
import org.onosproject.store.service.StorageService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LambdaOrchestratorPolicyStore {

    private List<LambdaOrchestratorPolicy> policyStore;
    private ConsistentMap<LambdaFlowIdentifier, LambdaOrchestratorPolicy> consistentPolicyMap;
    private Map<LambdaFlowIdentifier, LambdaOrchestratorPolicy> policyMap;

    /**
     * Create Lambda Orchestration Policy Store
     * @param storageService if not null, policyMap is created as consistent map,
     *                       or it will be created as ordinary map.
     */
    public LambdaOrchestratorPolicyStore(StorageService storageService) {
        policyStore = new ArrayList<>();
        if (storageService == null) {
            consistentPolicyMap = null;
            policyMap = new HashMap<>();
        } else {
            consistentPolicyMap = storageService.<LambdaFlowIdentifier, LambdaOrchestratorPolicy>consistentMapBuilder()
                    .withSerializer(Serializer.using(KryoNamespaces.API))
                    .withName("Lambda-Policy-Store")
                    .build();
            policyMap = consistentPolicyMap.asJavaMap();
        }
    }

    /**
     *
     * @param policy
     * @return
     */
    public boolean addPolicy(LambdaOrchestratorPolicy policy) {
        if(policyMap.containsKey(policy.getFlowIdentifier())) {
            return  false;
        }

        policyStore.add(policy);
        policyMap.put(policy.getFlowIdentifier(), policy);

        return true;
    }

    /**
     *
     * @param policy
     * @return
     */
    public boolean updatePolicy(LambdaOrchestratorPolicy policy) {
        if(!policyMap.containsKey(policy.getFlowIdentifier())) {
            return  false;
        }
        LambdaOrchestratorPolicy originalPolicy = policyMap.get(policy.getFlowIdentifier());
        policyStore.remove(originalPolicy);
        policyStore.add(policy);

        policyMap.put(policy.getFlowIdentifier(), policy);

        return true;
    }

    /**
     *
     * @param identifier
     * @return
     */
    public LambdaOrchestratorPolicy getPolicy(LambdaFlowIdentifier identifier) {
        return policyMap.get(identifier);
    }

    /**
     *
     * @param identifier
     * @return
     */
    public LambdaOrchestratorPolicy deletePolicy(LambdaFlowIdentifier identifier) {
        if(!policyMap.containsKey(identifier)) {
            return null;
        }

        LambdaOrchestratorPolicy policy = policyMap.remove(identifier);

        policyStore.remove(policy);

        return policy;
    }

    public List<LambdaOrchestratorPolicy> getPolicyStore() {
        return policyStore;
    }
}
