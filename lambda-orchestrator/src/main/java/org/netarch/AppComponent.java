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

import org.apache.felix.scr.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Skeletal ONOS application component.
 */
@Component(immediate = true)
public class AppComponent {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected LambdaOrchestratorService orchestratorService;

    @Activate
    protected void activate() {
        if (orchestratorService != null) {
            log.info("Register Lambda orchestrator service.");
            orchestratorService.activate();
        }
        else {
            log.error("Cannot register Lambda Orchestrator Service.");
        }

    }

    @Deactivate
    protected void deactivate() {
        log.info("Stopped");
        if (orchestratorService != null) {
            orchestratorService.deactivate();
        }
    }
}
