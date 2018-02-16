package me.jamiemansfield.csnea.xml;

import static me.jamiemansfield.csnea.FergusMain.SUBJECTS_XML;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * A representation of a subject. This will allow Fergus to add or
 * remove subjects as he see's fit without modifying the codebase.
 */
@XmlRootElement
public class Subject {

    private static final Map<String, Subject> REGISTRY = new HashMap<>();

    /**
     * Initialises the subjects registry.
     */
    public static void init() {
        // Clear the registry, in case madness has happened
        REGISTRY.clear();

        // If the subjects.xml doesn't exist, create an empty subject set.
        if (Files.notExists(SUBJECTS_XML)) {
            try (final OutputStream outputStream = Files.newOutputStream(SUBJECTS_XML)) {
                serialise(Collections.emptyList(), outputStream);
            } catch (final IOException ex) {
                throw new RuntimeException("Failed to create the subjects.xml file!", ex);
            }
        }

        // Read the subject set from the subjects.xml file
        final List<Subject> subjects;
        try (final InputStream inputStream = Files.newInputStream(SUBJECTS_XML)) {
            subjects = deserialise(inputStream);
        } catch (final IOException ex) {
            throw new RuntimeException("Failed to open the students.xml file!", ex);
        }

        // Test the subjects' question definition file
        for (final Subject subject : subjects) {
            if (Files.notExists(subject.getQuestionsDefinitionPath())) {
                throw new RuntimeException(
                        "The question definition file for " + subject.id + " does not exist!");
            }

            // Initialise definitions field
            try (final InputStream inputStream = Files.newInputStream(subject.getQuestionsDefinitionPath())) {
                subject.definitions = QuestionDefinitions.deserialise(inputStream);
            } catch (final IOException ex) {
                throw new RuntimeException("The question definition file for " + subject.id +
                        " was invalid!", ex);
            }
        }

        // Register subjects
        subjects.forEach(subject -> REGISTRY.put(subject.id, subject));
    }

    /**
     * Gets an immutable-view of all of the registered {@link Subject}s.
     *
     * @return The registered subjects
     */
    public static Collection<Subject> values() {
        return Collections.unmodifiableCollection(REGISTRY.values());
    }

    /**
     * Gets a {@link Subject} from its string identifier.
     *
     * @param rawSubject The subject's string identifier
     * @return The subject, wrapped in an {@link Optional}
     */
    public static Optional<Subject> get(final String rawSubject) {
        return values().stream()
                .filter(subject -> Objects.equals(subject.id, rawSubject))
                .findFirst();
    }

    /**
     * The JAXBContext to use for both serialising and de-serialising.
     */
    private static final JAXBContext JAXB_CONTEXT;

    static {
        try {
            JAXB_CONTEXT = JAXBContext.newInstance(SubjectSet.class);
        } catch (final JAXBException ex) {
            throw new RuntimeException("Failed to initialise JAXB.", ex);
        }
    }

    /**
     * De-serialises the XML from the given input stream.
     *
     * @param is The given input stream
     * @return A list of subjects
     */
    public static List<Subject> deserialise(final InputStream is) {
        try {
            // Create the un'marshaller' to de-serialise the students XML
            final Unmarshaller unmarshaller = JAXB_CONTEXT.createUnmarshaller();
            // Return the list of students
            return ((SubjectSet) unmarshaller.unmarshal(is)).subjects;
        } catch (final JAXBException ex) {
            throw new RuntimeException("Failed to de-serialise subjects.", ex);
        }
    }

    /**
     * Serialises the given set of subjects to formatted XML.
     *
     * @param subjects The subjects to serialise
     * @param os       The stream to output the XML to
     */
    public static void serialise(final List<Subject> subjects, final OutputStream os) {
        try {
            // Create the 'marshaller' to serialise the subjects
            final Marshaller marshaller = JAXB_CONTEXT.createMarshaller();
            // Ensure indentation, etc, is present
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            // Serialise the subjects list
            final SubjectSet subjectSet = new SubjectSet();
            subjectSet.subjects = subjects;
            marshaller.marshal(subjectSet, os);
        } catch (final JAXBException ex) {
            throw new RuntimeException("Failed to serialise subjects.", ex);
        }
    }

    @XmlAttribute private final String id;
    @XmlAttribute private final String name;
    @XmlAttribute private final String definitionFile;

    private Path questionsDefinitionPath;
    private QuestionDefinitions definitions;

    /**
     * A parameter-less constructor for the use of JAXB.
     */
    private Subject() {
        // These values are null, as JAXB will initialise them
        // The actual fields are final, so they need to be
        // initialised to something.
        this.id             = null;
        this.name           = null;
        this.definitionFile = null;
    }

    /**
     * Creates a subject from the given parameters.
     *
     * @param id             The identifier of the subject
     * @param name           The human-readable name of the subject
     * @param definitionFile The relative location of the definitions file
     */
    public Subject(final String id, final String name, final String definitionFile) {
        this.id             = id;
        this.name           = name;
        this.definitionFile = definitionFile;
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
     * Gets the name of this subject.
     *
     * @return The subject's name
     */
    public final String getName() {
        return this.id;
    }

    /**
     * Gets the path, external-to-the-game, of which the subject's
     * questions definition file exists.
     *
     * @return The path to the questions definition file
     */
    public final Path getQuestionsDefinitionPath() {
        if (this.questionsDefinitionPath == null) this.questionsDefinitionPath = Paths.get(this.definitionFile);
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

}

/**
 * An intermediary object, used to de/serialise the subjects list
 * easily.
 */
@XmlRootElement(name = "subjects")
@XmlSeeAlso(Subject.class)
class SubjectSet {

    /**
     * A intermediary field, for the subjects.
     */
    @XmlElement(name = "subject")
    public List<Subject> subjects = new ArrayList<>();

}
