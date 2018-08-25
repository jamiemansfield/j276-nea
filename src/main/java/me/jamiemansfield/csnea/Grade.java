//******************************************************************************
// Copyright (c) Jamie Mansfield <https://jamiemansfield.me/>
// This Source Code Form is subject to the terms of the Mozilla Public
// License, v. 2.0. If a copy of the MPL was not distributed with this
// file, You can obtain one at http://mozilla.org/MPL/2.0/.
//******************************************************************************

package me.jamiemansfield.csnea;

import java.util.Arrays;

/**
 * An enumeration used to represent the grade a student has achieved.
 *
 * <em>In the case, A* is the highest grade attainable.</em>
 */
public enum Grade {

    A_STAR("A*", 100),
    A     ("A" , 80),
    B     ("B" , 60),
    C     ("C" , 40),
    D     ("D" , 20),
    F     ("F" , 0),
    ;

    /**
     * Gets the grade, based on the percentage a student achieved
     * on a quiz.
     *
     * @param percentage The percentage of correct questions the
     *                   student achieved
     * @return The grade
     */
    public static Grade of(final double percentage) {
        // Take all of the grades
        return Arrays.stream(values())
                // Looks if the percentage attained is at, or
                // above the lower bound
                .filter(grade -> percentage >= grade.lowerBound)
                // As the order of the grades is highest->lowest
                // the first grade is the one achieved.
                //
                // As Stream#findFirst() returns an Optional, I
                // use Optional#orElse() to return Grade.F otherwise
                .findFirst().orElse(Grade.F);
    }

    /**
     * This field simply stores the text representation of the
     * grade.
     *
     * <em>i.e. while A* is represented as A_STAR in the enum,
     *          but to the student it is A*.</em>
     */
    private final String textRepresentation;

    /**
     * This field simply stores the lower bound for the grade.
     */
    private final int lowerBound;

    /**
     * Constructs a grade rating, from its text representation,
     * and lower bound.
     *
     * @param textRepresentation A String representation of the
     *                           grade, that can be used when
     *                           informing the student of their
     *                           grade.
     * @param lowerBound The lower bound (as a percentage) that
     *                   is needed to achieve the difficulty.
     */
    Grade(final String textRepresentation, final int lowerBound) {
        this.textRepresentation = textRepresentation;
        this.lowerBound = lowerBound;
    }

    /**
     * Gets the text representation of the grade. (e.g. {@code A*}).
     *
     * @return The text representation
     */
    public final String getText() {
        return this.textRepresentation;
    }

}
