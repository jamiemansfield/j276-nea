//******************************************************************************
// Copyright (c) Jamie Mansfield <https://jamiemansfield.me/>
// This Source Code Form is subject to the terms of the Mozilla Public
// License, v. 2.0. If a copy of the MPL was not distributed with this
// file, You can obtain one at http://mozilla.org/MPL/2.0/.
//******************************************************************************

package me.jamiemansfield.csnea.model;

import me.jamiemansfield.csnea.Difficulty;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A JAXB model for a quiz attempt.
 */
@XmlRootElement
public class Attempt {

    /**
     * Creates a builder that can be used to construct an attempt.
     *
     * @return The builder
     */
    public static Builder builder() {
        return new Builder();
    }

    @XmlAttribute private final String     subject;
    @XmlAttribute private final Difficulty difficulty;
    @XmlAttribute private final int        percentage;

    /**
     * A parameter-less constructor for the use of JAXB.
     */
    private Attempt() {
        // These values are null, as JAXB will initialise them
        // The actual fields are final, so they need to be
        // initialised to something.
        this.subject    = null;
        this.difficulty = null;
        this.percentage = 0;
    }

    /**
     * Creates the attempt from the builder.
     *
     * @param builder The builder
     * @param percentage The percentage the student attained
     */
    private Attempt(final Builder builder, final int percentage) {
        this.subject    = builder.subject.getId();
        this.difficulty = builder.difficulty;
        this.percentage = percentage;
    }

    /**
     * Gets the subject of which the quiz attempt was made at.
     *
     * @return The subject
     */
    public final String getSubject() {
        return this.subject;
    }

    /**
     * Gets the difficulty of which the quiz attempt was made at.
     *
     * @return The difficulty
     */
    public final Difficulty getDifficulty() {
        return this.difficulty;
    }

    /**
     * Gets the percentage attained on the quiz attempt.
     *
     * @return The percentage attained
     */
    public final int getPercentage() {
        return this.percentage;
    }

    /**
     * A builder that will be used to construct a student's attempt
     * at a quiz - allowing for it to be finally constructed with
     * the percentage right as the quiz has been completed.
     */
    public static final class Builder {

        private Subject    subject;
        private Difficulty difficulty = Difficulty.EASY;

        /**
         * Private constructor, this should be constructed through
         * {@link Attempt#builder()}.
         */
        private Builder() {
        }

        /**
         * Sets the {@link Subject} of the quiz attempt.
         *
         * @param subject The subject
         * @return {@code this} for chaining
         */
        public Builder subject(final Subject subject) {
            this.subject = subject;
            return this;
        }

        /**
         * Sets the {@link Difficulty} of the quiz attempt.
         *
         * @param difficulty The difficulty
         * @return {@code this} for chaining
         */
        public Builder difficulty(final Difficulty difficulty) {
            this.difficulty = difficulty;
            return this;
        }

        /**
         * Builds the {@link Attempt}, from the given percentage
         * and previous values.
         *
         * @param percentage The percentage of the quiz the student
         *                   attained.
         * @return The constructed attempt
         */
        public Attempt build(final int percentage) {
            // The difficulty has a default value, however the subject does not.
            // I will need to ensure that it has been set
            if (this.subject == null)
                throw new RuntimeException("Subject has not been set!");

            return new Attempt(this, percentage);
        }

    }

}
