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


import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.onosproject.cli.AbstractShellCommand;

import java.util.List;

@Command(scope="onos", name="lambda", description = "Lambda CLI.")
public class LambdaCLI extends AbstractShellCommand {
    private static final String INSTALL = "install";
    private static final String UNINSTALL = "uninstall";
    private static final String SHOW = "show";
    private static final String LOAD_CONF = "load-config";

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected LambdaOrchestratorService orchestratorService;


    @Argument(index = 0, name = "Operations", description = "Operations: show, uninstall, update",
            required = true, multiValued = false)
    private String operation;

    @Argument(index = 1, name = "Parameters", description = "Policy parameters.",
            required = true, multiValued = false)
    private String parameter;


    @Override
    protected void execute() {
        switch(operation) {
            case INSTALL:
                try {
                    orchestratorService.install(parameter);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case UNINSTALL:
                orchestratorService.delete(parameter);
                break;
            case SHOW:
                List<String> policies = orchestratorService.show();
                this.showPolicies(policies);
                break;
            case LOAD_CONF:
                break;
            default:
        }
    }


    protected void showPolicies(List<String> policies) {
        // TODO
    }
}
