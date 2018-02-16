package me.jamiemansfield.csnea;

import me.jamiemansfield.csnea.xml.QuestionDefinitions;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * An enumeration used to represent the subject of which a
 * quiz is on.
 */
public enum Subject implements Serializable {

    COMPUTER_SCIENCE("computer_science"),
    MATHS           ("maths"),
    ;

    static {
        // This will iterate over all of the subjects to establish
        // whether its question definition file actually exists.
        // it it doesn't, the program will exception out.
        for (final Subject subject : values()) {
            if (Files.notExists(subject.questionsDefinitionPath)) {
                throw new RuntimeException(
                        "The question definition file for " + subject.id +
                                " does not exist!");
            }

            // Initialise definitions field
            try (final InputStream inputStream =
                         Files.newInputStream(subject.questionsDefinitionPath)) {
                subject.definitions = QuestionDefinitions.deserialise(inputStream);
            } catch (final IOException ex) {
                throw new RuntimeException(
                        "The question definition file for " + subject.id +
                                " was invalid!", ex);
            }
        }
    }

    private final String id;
    private final Path questionsDefinitionPath;

    // This needs to be initialised in the static block, as to ensure the
    // existence of the definitions file.
    private QuestionDefinitions definitions;

    /**
     * Constructs the subject, from the given identifier.
     *
     * @param id The identifier, that is used by the subject
     *           for its externally stored questions.
     */
    Subject(final String id) {
        this.id                      = id;
        this.questionsDefinitionPath = Paths.get(id + ".xml");
    }

    /**
     * Gets the identifier that is used to represent this subject.
     *
     * @return The subject's identifier
     */
    public final String getId() {
        return this.id;
    }

    /**
     * Gets the path, external-to-the-game, of which the subject's
     * questions definition file exists.
     *
     * @return The path to the questions definition file
     */
    public final Path getQuestionsDefinitionPath() {
        return this.questionsDefinitionPath;
    }

    /**
     * Gets the question definitions model, used for accessing the subject's
     * questions.
     *
     * @return The question definition model
     */
    public final QuestionDefinitions getDefinitions() {
        return this.definitions;
    }

    /**
     * Gets a {@link Subject} from its string identifier.
     *
     * @param rawSubject The subject's string identifier
     * @return The subject, wrapped in an {@link Optional}
     */
    public static Optional<Subject> get(final String rawSubject) {
        return Arrays.stream(values())
                .filter(subject -> Objects.equals(subject.id, rawSubject))
                .findFirst();
    }

}
