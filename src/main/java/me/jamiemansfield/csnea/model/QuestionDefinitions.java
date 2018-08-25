//******************************************************************************
// Copyright (c) Jamie Mansfield <https://jamiemansfield.me/>
// This Source Code Form is subject to the terms of the Mozilla Public
// License, v. 2.0. If a copy of the MPL was not distributed with this
// file, You can obtain one at http://mozilla.org/MPL/2.0/.
//******************************************************************************

package me.jamiemansfield.csnea.model;

import me.jamiemansfield.csnea.Difficulty;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * A JAXB model for the question definition file, used to house
 * the questions that form an individual quiz. There should be
 * questions for all of the available difficulties -
 * {@link me.jamiemansfield.csnea.Difficulty#EASY},
 * {@link me.jamiemansfield.csnea.Difficulty#MEDIUM}, and
 * {@link me.jamiemansfield.csnea.Difficulty#HARD}.
 */
@XmlRootElement
@XmlSeeAlso(Question.class)
public class QuestionDefinitions {

    /**
     * The JAXBContext to use for both serialising and de-serialising.
     */
    private static final JAXBContext JAXB_CONTEXT;

    /**
     * De-serialises the XML from the given input stream.
     *
     * @param is The given input stream
     * @return The question definitions
     */
    public static QuestionDefinitions deserialise(final InputStream is) {
        try {
            // Create the un'marshaller' to de-serialise the question definitions XML
            final Unmarshaller unmarshaller = JAXB_CONTEXT.createUnmarshaller();
            // Return the question definitions
            return ((QuestionDefinitions) unmarshaller.unmarshal(is));
        } catch (final JAXBException ex) {
            throw new RuntimeException("Failed to de-serialise definitions.", ex);
        }
    }

    /**
     * Serialises the given set of students to formatted XML.
     *
     * @param definitions The question definitions to serialise
     * @param os          The stream to output the XML to
     */
    public static void serialise(final QuestionDefinitions definitions, final OutputStream os) {
        try {
            // Create the 'marshaller' to serialise the question definitions
            final Marshaller marshaller = JAXB_CONTEXT.createMarshaller();
            // Ensure indentation, etc, is present
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            // Serialise the question definitions
            marshaller.marshal(definitions, os);
        } catch (final JAXBException ex) {
            throw new RuntimeException("Failed to serialise question definitions.", ex);
        }
    }

    static {
        try {
            JAXB_CONTEXT = JAXBContext.newInstance(QuestionDefinitions.class);
        } catch (final JAXBException ex) {
            throw new RuntimeException("Failed to initialise JAXB.", ex);
        }
    }

    @XmlElement private final List<Question> easy;
    @XmlElement private final List<Question> medium;
    @XmlElement private final List<Question> hard;

    /**
     * A parameter-less constructor for the use of JAXB.
     */
    private QuestionDefinitions() {
        // These values are null, as JAXB will initialise them
        // The actual fields are final, so they need to be
        // initialised to something.
        this.easy   = new ArrayList<>();
        this.medium = new ArrayList<>();
        this.hard   = new ArrayList<>();
    }

    /**
     * A constructor used to create the questions definition model, using
     * the lists of questions for easy {@link me.jamiemansfield.csnea.Difficulty}.
     *
     * <em>This exists largely for the purposes of testing the model.</em>
     *
     * @param easy The questions for the EASY difficulty
     * @param medium The questions for the MEDIUM difficulty
     * @param hard The questions for the HARD difficulty
     */
    public QuestionDefinitions(final List<Question> easy,
                               final List<Question> medium,
                               final List<Question> hard) {
        this.easy   = easy;
        this.medium = medium;
        this.hard   = hard;
    }

    /**
     * Gets an immutable view of all of the questions for the EASY difficulty.
     *
     * @return The EASY questions
     */
    public final List<Question> getEasy() {
        return this.easy;
    }

    /**
     * Gets an immutable view of all of the questions for the MEDIUM difficulty.
     *
     * @return The MEDIUM questions
     */
    public final List<Question> getMedium() {
        return this.medium;
    }

    /**
     * Gets an immutable view of all of the questions for the HARD difficulty.
     *
     * @return The HARD questions
     */
    public final List<Question> getHard() {
        return Collections.unmodifiableList(this.hard);
    }

    /**
     * Gets an immutable-view of all the questions for the given {@link Difficulty}.
     *
     * @param difficulty The difficulty
     * @return The questions
     */
    public final List<Question> get(final Difficulty difficulty) {
        switch (difficulty) {
            case MEDIUM:
                return this.getMedium();
            case HARD:
                return this.getHard();
            case EASY:
            default:
                return this.getEasy();
        }
    }

}
