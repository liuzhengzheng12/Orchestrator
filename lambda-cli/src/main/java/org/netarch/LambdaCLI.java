package org.netarch;


import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.service.command.CommandSession;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.onosproject.cli.AbstractShellCommand;

import java.util.List;

@Command(scope="onos", name="lambda", description = "Lambda CLI.")
public class LambdaCLI extends AbstractShellCommand {
    private static final String INSTALL = "install";
    private static final String UNINSTALL = "uninstall";
    private static final String SHOW = "show";


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
                orchestratorService.install(parameter);
                break;
            case UNINSTALL:
                orchestratorService.delete(parameter);
                break;
            case SHOW:
                List<String> policies = orchestratorService.show();
                this.showPolicies(policies);
                break;
            default:
        }
    }


    protected void showPolicies(List<String> policies) {
        // TODO
    }
}
