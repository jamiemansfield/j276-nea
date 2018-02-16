package me.jamiemansfield.csnea.phase.command;

import me.jamiemansfield.csnea.FergusMain;
import me.jamiemansfield.csnea.cli.CommandDispatcher;

/**
 * A class used for registering common commands.
 */
public final class CommonCommands {

    /**
     * Registers the commands to the provided {@link CommandDispatcher}.
     *
     * @param dispatcher The dispatcher to register to
     */
    public static void registerCommands(final CommandDispatcher<?> dispatcher) {
        // The 'exit' command
        dispatcher.register("exit", (caller, args) -> {
            System.out.println("Exiting Fergus' Quiz.");
            System.exit(0);
        });
        // The 'help' command
        dispatcher.register("help", (caller, args) -> {
            FergusMain.get().getCurrentPhase().displayHelp();
        });
    }

    private CommonCommands() {
    }

}
