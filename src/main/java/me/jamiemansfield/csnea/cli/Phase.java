package me.jamiemansfield.csnea.cli;

import me.jamiemansfield.csnea.command.CommandDispatcher;

/**
 * An interface used to describe a phase, a phase being a specific state
 * that Fergus' Quiz is currently within.
 *
 * Each phase has its own {@link CommandDispatcher}.
 *
 * @param <C> The type of the command caller
 */
public interface Phase<C> {

    /**
     * Displays the phases help screen to the phases'
     * own command caller.
     */
    void displayHelp();

    /**
     * This method is called upon the entry to the phase,
     * either as the first phase of the program, or having exited
     * another phase.
     *
     * <em>Typically calls {@link #displayHelp()}.</em>
     */
    void enter();

    /**
     * This method is called upon the exit of the phase,
     * either the program is closing, or it has exited to
     * proceed to another phase.
     */
    void exit();

    /**
     * Gets the command caller for this instance of the phase.
     *
     * @return The command caller
     */
    C getCaller();

    /**
     * Gets the command dispatcher responsible for this phase.
     *
     * @return The command dispatcher
     */
    CommandDispatcher<C> getDispatcher();

    /**
     * Executes the given command input.
     *
     * @param input The command input
     */
    default void execute(final String input) {
        this.getDispatcher().execute(this.getCaller(), input);
    }

}
