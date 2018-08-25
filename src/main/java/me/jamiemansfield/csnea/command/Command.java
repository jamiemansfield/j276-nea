//******************************************************************************
// Copyright (c) Jamie Mansfield <https://jamiemansfield.me/>
// This Source Code Form is subject to the terms of the Mozilla Public
// License, v. 2.0. If a copy of the MPL was not distributed with this
// file, You can obtain one at http://mozilla.org/MPL/2.0/.
//******************************************************************************

package me.jamiemansfield.csnea.command;

/**
 * An interface used to describe a command, in the command system.
 *
 * @param <C> The type of the caller, dependant on which system
 *            (e.g. pre-login, or post-logon) is currently on.
 *            This will allow for the Student object to be used
 *            as the command caller.
 */
@FunctionalInterface
public interface Command<C> {

    /**
     * Executes the command, with the caller, and the arguments.
     *
     * <strong>The arguments provided should not include the command
     * name!</strong>
     *
     * @param caller The caller of the command
     * @param args The arguments executed with
     */
    void execute(final C caller, final CommandArgs args);

}
