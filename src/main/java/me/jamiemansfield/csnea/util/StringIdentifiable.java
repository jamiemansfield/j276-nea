//******************************************************************************
// Copyright (c) Jamie Mansfield <https://jamiemansfield.me/>
// This Source Code Form is subject to the terms of the Mozilla Public
// License, v. 2.0. If a copy of the MPL was not distributed with this
// file, You can obtain one at http://mozilla.org/MPL/2.0/.
//******************************************************************************

package me.jamiemansfield.csnea.util;

/**
 * A common interface used to denote an object as having
 * a string identifier.
 */
public interface StringIdentifiable extends Identifiable<String> {

    /**
     * Gets the string identifier of the object.
     *
     * @return The identifier
     */
    @Override
    String getId();

}
