package me.jamiemansfield.csnea.util.command;

import me.jamiemansfield.csnea.command.CommandArgs;

import java.util.NoSuchElementException;

/**
 * This class is to commands, what {@link java.util.Scanner} is
 * to command line reading.
 */
public class CommandArgsReader {

    private final CommandArgs args;
    private int index = -1;

    /**
     * Creates a new command args reader, from the given command args.
     *
     * @param args The command args
     */
    public CommandArgsReader(final CommandArgs args) {
        this.args = args;
    }

    /**
     * Establishes whether there is a remaining argument to read.
     *
     * @return {@code true} if there is another argument;
     *         {@code false} otherwise
     */
    public boolean hasNext() {
        return this.args.getArgs().size() > this.index + 1;
    }

    /**
     * Gets the next argument, if available.
     *
     * @return The next argument
     * @throws NoSuchElementException Should no available argument be remaining to be read
     */
    public String next() {
        if (!this.hasNext()) throw new NoSuchElementException("There are no arguments left!");
        return this.args.getArgs().get(++this.index);
    }

}
