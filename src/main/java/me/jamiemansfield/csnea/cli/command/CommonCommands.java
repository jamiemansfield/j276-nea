//******************************************************************************
// Copyright (c) Jamie Mansfield <https://jamiemansfield.me/>
// This Source Code Form is subject to the terms of the Mozilla Public
// License, v. 2.0. If a copy of the MPL was not distributed with this
// file, You can obtain one at http://mozilla.org/MPL/2.0/.
//******************************************************************************

package me.jamiemansfield.csnea.cli.command;

import me.jamiemansfield.csnea.FergusMain;
import me.jamiemansfield.csnea.command.CommandDispatcher;

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
