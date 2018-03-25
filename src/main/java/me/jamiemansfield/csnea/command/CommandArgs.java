package me.jamiemansfield.csnea.command;

import java.util.*;

/**
 * A container used to hold the command arguments, allowing
 * for flags and parameters to be used.
 */
public final class CommandArgs {

    /**
     * Assembles the given flags, and arguments into a single raw
     * arguments array.
     *
     * @param flags The command flags
     * @param args  The command args
     * @return      The raw arguments
     */
    public static String[] assemble(final Map<String, String> flags,
                                    final String[] args) {
        final List<String> rawArgs = new ArrayList<>();

        // Add all the flags
        flags.forEach((flag, value) -> {
            rawArgs.add("-" + value);
            rawArgs.add(value);
        });
        // Add all the args
        rawArgs.addAll(Arrays.asList(args));

        return rawArgs.toArray(new String[rawArgs.size()]);
    }

    private final String[] rawArgs;
    private final List<String> args = new ArrayList<>();
    private final Map<String, String> flags = new HashMap<>();

    /**
     * Creates a command arguments container, from the raw
     * arguments as provided from the command dispatcher.
     *
     * @param rawArgs The raw command arguments
     */
    public CommandArgs(final String[] rawArgs) {
        this.rawArgs = rawArgs;

        // If this is not null, a flag is being read
        String flag = null;
        for (final String arg : rawArgs) {
            // Reading a flag name
            if (arg.startsWith("-")) {
                // The previous flag had no value
                if (flag != null) {
                    this.flags.put(flag, "");
                    flag = null;
                }

                // The value is also given (through use of =)
                if (arg.contains("=")) {
                    final String[] split = flag.split("=");
                    this.flags.put(split[0].substring(1), split[1]);
                }
                // Start the new flag
                else {
                    flag = arg.substring(1); // The flag name doesn't include the hyphen
                }
            }
            // Reading a normal argument, or a flag value
            else {
                // Its a flag value
                if (flag != null) {
                    this.flags.put(flag, arg);
                    flag = null;
                }
                // Its a normal argument
                else {
                    this.args.add(arg);
                }
            }
        }
    }

    /**
     * Creates the command arguments from the flags, and the arguments.
     *
     * @param flags The command flags
     * @param args  The command args
     */
    public CommandArgs(final Map<String, String> flags, final String[] args) {
        this.rawArgs = assemble(flags, args);
        this.flags.putAll(flags);
        this.args.addAll(Arrays.asList(args));
    }

    /**
     * Gets the raw arguments provided to the command args.
     *
     * @return The raw arguments
     */
    public String[] getRawArgs() {
        return this.rawArgs;
    }

    /**
     * Gets the value of the given flag.
     *
     * @param flag The flag to get the value of
     * @return The flag value
     */
    public String getFlag(final String flag) {
        return this.getFlag(flag, "");
    }

    /**
     * Gets the value of the given flag, if not present returning
     * the default value provided.
     *
     * @param flag The flag to get the value of
     * @param defaultValue The value to return, should one not exist
     * @return The flag value
     */
    public String getFlag(final String flag, final String defaultValue) {
        return this.flags.getOrDefault(flag, defaultValue);
    }

    /**
     * Establishes whether the arguments contains a flag of the
     * provided name.
     *
     * @param flag The flag name
     * @return {@code true} if the flag is present;
     *         {@code false} otherwise
     */
    public boolean hasFlag(final String flag) {
        return this.flags.containsKey(flag);
    }

    /**
     * Gets a immutable-view of the arguments provided.
     *
     * @return The arguments
     */
    public List<String> getArgs() {
        return Collections.unmodifiableList(this.args);
    }

    /**
     * Gets an immutable-view of all the flags.
     *
     * @return The flags
     */
    public Map<String, String> getFlags() {
        return Collections.unmodifiableMap(this.flags);
    }

}
