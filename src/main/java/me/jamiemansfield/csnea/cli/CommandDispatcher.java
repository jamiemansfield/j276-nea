package me.jamiemansfield.csnea.cli;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * An object used for dispatching commands, implementing
 * {@link Command} as an easy means of implementing sub-commands.
 *
 * @param <C> The type of command used with this dispatcher.
 */
public class CommandDispatcher<C> implements Command<C> {

    /**
     * A fake command to use when no command of the given name is
     * inputted to the console.
     */
    private final Command<C> COMMAND_NOT_EXIST =
            (caller, args) -> System.out.println("Invalid command!");

    private final Map<String, Command<C>> commands = new HashMap<>();

    /**
     * Registers the given name to the dispatcher.
     *
     * @param name The name of the command
     * @param command The command
     * @return {@code this} for chaining
     */
    public CommandDispatcher<C> register(final String name, final Command<C> command) {
        this.commands.put(name, command);
        return this;
    }

    /**
     * Executes the command, with the caller, and the command line.
     *
     * @param caller The command caller
     * @param commandLine The raw console input
     */
    public void execute(final C caller, final String commandLine) {
        // Split command line into arguments
        final String[] args = commandLine.split(" ");

        // Execute the command
        this.execute(caller, new CommandArgs(args));
    }

    @Override
    public void execute(final C caller, final CommandArgs args) {
        // Get the command name
        final String commandName = args.getRawArgs()[0];

        // Get the command
        final Command<C> command = this.commands.getOrDefault(commandName, COMMAND_NOT_EXIST);

        // Create the arguments array, without the command name.
        final String[] newArgs =
                Arrays.copyOfRange(args.getRawArgs(), 1, args.getRawArgs().length);

        // Execute the command
        command.execute(caller, new CommandArgs(newArgs));
    }

}
