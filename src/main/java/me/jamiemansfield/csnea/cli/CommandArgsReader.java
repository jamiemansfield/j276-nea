package me.jamiemansfield.csnea.cli;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is to commands, what {@link java.util.Scanner} is
 * to command line reading.
 */
public class CommandArgsReader {

    private final List<String> args;
    private int index = -1;

    public CommandArgsReader(final List<String> args) {
        // Create a copy of the arguments, so they cannot change
        // while they are being read.
        this.args = new ArrayList<>();
        this.args.addAll(args);
    }

    /**
     * Establishes whether there is another argument to read.
     *
     * @return {@code true} if there is another argument;
     *         {@code false} otherwise
     */
    public boolean hasNext() {
        return this.args.size() > (this.index + 1);
    }

    /**
     * Gets the next argument.
     *
     * @return The next arg
     */
    public String next() {
        if (!this.hasNext()) throw new IndexOutOfBoundsException("There are no arguments left!");
        return this.args.get(++this.index);
    }

}
