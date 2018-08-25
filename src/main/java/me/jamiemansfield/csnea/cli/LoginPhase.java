//******************************************************************************
// Copyright (c) Jamie Mansfield <https://jamiemansfield.me/>
// This Source Code Form is subject to the terms of the Mozilla Public
// License, v. 2.0. If a copy of the MPL was not distributed with this
// file, You can obtain one at http://mozilla.org/MPL/2.0/.
//******************************************************************************

package me.jamiemansfield.csnea.cli;

import me.jamiemansfield.csnea.command.CommandDispatcher;
import me.jamiemansfield.csnea.cli.command.CommonCommands;
import me.jamiemansfield.csnea.cli.command.LoginPhaseCommands;

import java.util.Arrays;

/**
 * The login phase, used for before a student has logged into the
 * program.
 */
public class LoginPhase implements Phase<Object> {

    private final CommandDispatcher<Object> dispatcher = new CommandDispatcher<>();

    public LoginPhase() {
        CommonCommands.registerCommands(this.dispatcher);
        LoginPhaseCommands.registerCommands(this.dispatcher);
    }

    @Override
    public void displayHelp() {
        Arrays.asList(
                "Fergus' Quiz",
                "",
                "Commands:",
                "  login <username> <password>",
                "    Allows a user to login to Fergus' Quiz",
                "  signup",
                "    Allows a student to signup to Fergus' Quiz",
                "  exit",
                "    Exits the program"
        ).forEach(System.out::println);

    }

    @Override
    public void enter() {
        this.displayHelp();
    }

    @Override
    public void exit() {
    }

    @Override
    public Object getCaller() {
        return null;
    }

    @Override
    public CommandDispatcher<Object> getDispatcher() {
        return this.dispatcher;
    }

}
