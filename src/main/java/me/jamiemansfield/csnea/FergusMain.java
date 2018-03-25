package me.jamiemansfield.csnea;

import me.jamiemansfield.csnea.cli.LoginPhase;
import me.jamiemansfield.csnea.cli.Phase;
import me.jamiemansfield.csnea.model.Student;
import me.jamiemansfield.csnea.model.Subject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 * The Main-Class (as would be specified in the jar's MANIFEST) of Fergus'
 * Quiz.
 */
public final class FergusMain {

    private static FergusMain $;

    /**
     * Gets the global instance of Fergus' Quiz.
     *
     * @return The instance
     */
    public static FergusMain get() {
        return $;
    }

    /**
     * The main method of Fergus's quiz - this is the method that the JVM will
     * invoke upon the execution of the program.
     *
     * @param args The program arguments as provided to the JVM
     */
    public static void main(final String[] args) {
        new FergusMain();
    }

    /**
     * The path that the 'students.xml' file will be found.
     */
    public static final Path STUDENTS_XML = Paths.get("students.xml");

    /**
     * The path that the 'subjects.xml' file will be found.
     */
    public static final Path SUBJECTS_XML = Paths.get("subjects.xml");

    /**
     * The login phase.
     */
    public static final LoginPhase LOGIN_PHASE = new LoginPhase();

    private final List<Student> students;
    private final Scanner scanner = new Scanner(System.in);
    private Phase<?> currentPhase = LOGIN_PHASE;
    private boolean running = true;

    private FergusMain() {
        // Set global instance
        $ = this;

        // If the students.xml doesn't exist, create an empty student set.
        if (Files.notExists(STUDENTS_XML)) {
            try (final OutputStream outputStream = Files.newOutputStream(STUDENTS_XML)) {
                Student.serialise(Collections.emptyList(), outputStream);
            } catch (final IOException ex) {
                throw new RuntimeException("Failed to create the students.xml file!", ex);
            }
        }

        // Read the student set from the students.xml file
        try (final InputStream inputStream = Files.newInputStream(STUDENTS_XML)) {
            this.students = Student.deserialise(inputStream);
        } catch (final IOException ex) {
            throw new RuntimeException("Failed to open the students.xml file!", ex);
        }

        // Initialise subjects registry
        Subject.init();

        // Enter the login phase
        this.currentPhase.enter();

        // Read from System.in
        while(this.running) {
            // Only run if there is input
            if (scanner.hasNextLine()) {
                // Gets the raw input
                final String input = this.scanner.nextLine();

                // Dispatch the command input
                this.currentPhase.execute(input);
            }
        }
    }

    /**
     * Gets the actively running phase.
     *
     * @return The current phase
     */
    public final Phase<?> getCurrentPhase() {
        return this.currentPhase;
    }

    /**
     * Transitions to the given {@link Phase}.
     *
     * @param phase The phase to transition to
     */
    public void transitionToPhase(final Phase<?> phase) {
        this.currentPhase.exit();
        this.currentPhase = phase;
        this.currentPhase.enter();
    }

    /**
     * Gets an immutable view of the students.
     *
     * @return The students
     */
    public final List<Student> getStudents() {
        return Collections.unmodifiableList(this.students);
    }

    /**
     * Registers the given {@link Student} to the game.
     *
     * @param student The student to register
     */
    public void registerStudent(final Student student) {
        this.students.add(student);

        this.updateStudentsFile();
    }

    /**
     * Updates the students.xml file.
     */
    public void updateStudentsFile() {
        // Update the students.xml file
        try (final OutputStream outputStream = Files.newOutputStream(STUDENTS_XML)) {
            Student.serialise(this.students, outputStream);
        } catch (final IOException ex) {
            throw new RuntimeException("Failed to update the students.xml file!", ex);
        }
    }

    /**
     * Gets the student of the provided username.
     *
     * @param username The student's username
     * @return The student
     */
    public Student getStudent(final String username) {
        return this.students.stream()
                .filter(student -> Objects.equals(student.getUsername(), username))
                .findFirst().orElse(null);
    }

    /**
     * Establishes whether a student of the provided username exists.
     *
     * @return {@code true} if a student of that username exists,
     *         {@code false} otherwise
     */
    public boolean hasStudentOfUsername(final String username) {
        return this.students.stream()
                .anyMatch(student -> Objects.equals(student.getUsername(), username));
    }

    /**
     * Gets the {@link Scanner} used for inputting from the console.
     *
     * @return The scanner
     */
    public final Scanner getScanner() {
        return this.scanner;
    }

}
