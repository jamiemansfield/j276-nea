package me.jamiemansfield.csnea.model;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * A JAXB model for a student.
 */
@XmlRootElement
@XmlSeeAlso(Attempt.class)
public class Student {

    /**
     * The JAXBContext to use for both serialising and de-serialising.
     */
    private static final JAXBContext JAXB_CONTEXT;

    /**
     * De-serialises the XML from the given input stream.
     *
     * @param is The given input stream
     * @return A list of students
     */
    public static List<Student> deserialise(final InputStream is) {
        try {
            // Create the un'marshaller' to de-serialise the students XML
            final Unmarshaller unmarshaller = JAXB_CONTEXT.createUnmarshaller();
            // Return the list of students
            return ((StudentSet) unmarshaller.unmarshal(is)).students;
        } catch (final JAXBException ex) {
            throw new RuntimeException("Failed to de-serialise students.", ex);
        }
    }

    /**
     * Serialises the given set of students to formatted XML.
     *
     * @param students The students to serialise
     * @param os       The stream to output the XML to
     */
    public static void serialise(final List<Student> students, final OutputStream os) {
        try {
            // Create the 'marshaller' to serialise the students
            final Marshaller marshaller = JAXB_CONTEXT.createMarshaller();
            // Ensure indentation, etc, is present
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            // Serialise the students list
            final StudentSet studentSet = new StudentSet();
            studentSet.students = students;
            marshaller.marshal(studentSet, os);
        } catch (final JAXBException ex) {
            throw new RuntimeException("Failed to serialise students.", ex);
        }
    }

    /**
     * The Java object for interacting with the SHA-256 hash.
     */
    private static final MessageDigest SHA_256;

    static {
        try {
            JAXB_CONTEXT = JAXBContext.newInstance(StudentSet.class);
        } catch (final JAXBException ex) {
            throw new RuntimeException("Failed to initialise JAXB.", ex);
        }

        // Get the SHA-256 hash tool from Java
        try {
            SHA_256 = MessageDigest.getInstance("sha-256");
        } catch (final NoSuchAlgorithmException ex) {
            throw new RuntimeException("Failed to initialise SHA-256.", ex);
        }
    }

    /**
     * Creates a builder that can be used to construct a student.
     *
     * @return The builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Generates a salt that the password can be salted with.
     *
     * @return A 20-byte salt, encoded with base64
     */
    public static String generateSalt() {
        // So the password can be stored in XML, it is represented in base64
        return Base64.getEncoder().encodeToString(
                // Generate 20 bytes of securely random bytes
                new SecureRandom().generateSeed(20)
        );
    }

    @XmlAttribute private final String  username;
    @XmlAttribute private final String  fullname;
    @XmlAttribute private final int     age;
    @XmlAttribute private final String  yearGroup;
    @XmlAttribute private final String  salt;
    @XmlAttribute private final String  password;
    @XmlAttribute private final boolean admin;

    @XmlElement(name = "attempt") private final List<Attempt> attempts;

    /**
     * A parameter-less constructor for the use of JAXB.
     */
    public Student() {
        // These values are null/defaults, as JAXB will initialise
        // them.
        // The actual fields are final, so they need to be
        // initialised to something.
        this.username  = null;
        this.fullname  = null;
        this.age       = 0;
        this.yearGroup = null;
        this.salt      = null;
        this.password  = null;
        this.attempts  = new ArrayList<>();
        this.admin     = false;
    }

    /**
     * Creates the student from the builder.
     *
     * @param builder The builder
     */
    private Student(final Builder builder) {
        this.username  = builder.fullname.substring(0, 3) + builder.age;
        this.fullname  = builder.fullname;
        this.age       = builder.age;
        this.yearGroup = builder.yearGroup;
        this.salt      = generateSalt();
        this.password  = this.saltAndHashPassword(builder.password);
        this.attempts  = new ArrayList<>();
        this.admin     = builder.admin;
    }

    /**
     * Salts and hashes the provided raw password.
     *
     * @param password The password to salt and hash
     * @return The salt and hashed password
     */
    private String saltAndHashPassword(final String password) {
        // So the password can be stored in XML, it is represented in base64
        return Base64.getEncoder().encodeToString(
                // Add the salt, get the bytes, and hash
                SHA_256.digest((password + this.salt).getBytes())
        );
    }

    /**
     * Checks whether the two salted and hashed passwords are equal (the same)
     * as each other.
     *
     * @param password The raw password
     * @return {@code true} if the passwords match.,
     *         {@code false} otherwise
     */
    public final boolean testPassword(final String password) {
        // Check if the two salted and hashed passwords are equal.
        return Objects.equals(this.password, this.saltAndHashPassword(password));
    }

    /**
     * Gets the username of the student.
     *
     * @return The username
     */
    public final String getUsername() {
        return this.username;
    }

    /**
     * Gets the full name of the student.
     *
     * @return The full name
     */
    public final String getFullname() {
        return this.fullname;
    }

    /**
     * Gets the age of the student.
     *
     * @return The age
     */
    public final int getAge() {
        return this.age;
    }

    /**
     * Gets the year group of the student.
     *
     * @return The year group
     */
    public final String getYearGroup() {
        return this.yearGroup;
    }

    /**
     * Gets an immutable view of the quiz attempts the student
     * has made.
     *
     * @return An unmodifiable list of quiz attempts
     */
    public final List<Attempt> getAttempts() {
        // Return an immutable (not changeable) view of the attempts list
        return Collections.unmodifiableList(this.attempts);
    }

    /**
     * Adds an attempt to the student model.
     *
     * @param attempt The attempt the student made
     */
    public final void addAttempt(final Attempt attempt) {
        this.attempts.add(attempt);
    }

    /**
     * Establishes whether the student has the admin flag.
     *
     * @return {@code true} if the student has the admin flag;
     *         {@code false} otherwise
     */
    public final boolean isAdmin() {
        return this.admin;
    }

    /**
     * A builder that will be used for constructing the student model.
     */
    public static final class Builder {

        private String fullname;
        private int    age;
        private String yearGroup;
        private String password;
        private boolean admin = false;

        /**
         * Private constructor, this should be constructed through
         * {@link Student#builder()}.
         */
        private Builder() {
        }

        /**
         * Sets whether the student is an admin.
         *
         * @param admin {@code true} if the student is a program
         *              administrator;
         *              {@code false} otherwise
         * @return {@code this}, for chaining
         */
        public Builder admin(final boolean admin) {
            this.admin = admin;
            return this;
        }

        /**
         * Sets the fullname of the student.
         *
         * @param fullname The student's fullname
         * @return {@code this}, for chaining
         */
        public Builder fullname(final String fullname) {
            this.fullname = fullname;
            return this;
        }

        /**
         * Sets the age of the student.
         *
         * @param age The student's age
         * @return {@code this}, for chaining
         */
        public Builder age(final int age) {
            this.age = age;
            return this;
        }

        /**
         * Sets the year group of the student.
         *
         * @param yearGroup The student's year group
         * @return {@code this}, for chaining
         */
        public Builder yearGroup(final String yearGroup) {
            this.yearGroup = yearGroup;
            return this;
        }

        /**
         * Sets the password of the student.
         *
         * @param password The student's password
         * @return {@code this}, for chaining
         */
        public Builder password(final String password) {
            this.password = password;
            return this;
        }

        /**
         * Builds the {@link Attempt}, from the previous values.
         *
         * @return The constructed student
         */
        public Student build() {
            return new Student(this);
        }

    }

}

/**
 * An intermediary object, used to de/serialise the students list
 * easily.
 */
@XmlRootElement(name = "students")
@XmlSeeAlso(Student.class)
class StudentSet {

    /**
     * A intermediary field, for the students.
     */
    @XmlElement(name = "student")
    public List<Student> students = new ArrayList<>();

}
