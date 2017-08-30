package org.netarch;


import org.apache.felix.service.command.CommandSession;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.onosproject.cli.AbstractShellCommand;

@Command(scope="onos", name="lambda", description = "Lambda CLI.")
public class LambdaCLI extends AbstractShellCommand {
    private static final String INSTALL = "install";
    private static final String UNINSTALL = "uninstall";
    private static final String SHOW = "show";

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
                break;
            case UNINSTALL:
                break;
            case SHOW:
                break;
            default:
        }
    }
}
