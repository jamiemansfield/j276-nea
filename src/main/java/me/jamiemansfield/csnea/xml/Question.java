package me.jamiemansfield.csnea.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A JAXB model for a question.
 */
@XmlRootElement
public class Question {

    @XmlAttribute private final String title;
    @XmlAttribute private final int    correctAnswer;

    @XmlElementWrapper @XmlElement(name = "answer") private final List<String> answers;

    /**
     * A parameter-less constructor for the use of JAXB.
     */
    private Question () {
        // These values are null, as JAXB will initialise them
        // The actual fields are final, so they need to be
        // initialised to something.
        this.title         = null;
        this.correctAnswer = 0;
        this.answers       = new ArrayList<>();
    }

    /**
     * A constructor for constructing a question programmatically
     * for testing purposes.
     *
     * @param title         The title of the question
     * @param correctAnswer The index of the correct answer
     * @param answers       A list of potential answers
     */
    public Question(final String title, final int correctAnswer,
                    final List<String> answers) {
        this.title = title;
        this.correctAnswer = correctAnswer;
        this.answers = answers;
    }

    /**
     * Gets the title of the question.
     *
     * @return The title
     */
    public final String getTitle() {
        return this.title;
    }

    /**
     * Gets an immutable view of the question answers.
     *
     * @return An unmodifiable list of question answers
     */
    public final List<String> getAnswers() {
        // Return an immutable (not changeable) view of the attempts list
        return Collections.unmodifiableList(this.answers);
    }

    /**
     * Gets the index of the correct answer.
     *
     * @return The index of the correct answer
     */
    public final int getCorrectAnswer() {
        return this.correctAnswer;
    }
}
