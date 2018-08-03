//******************************************************************************
// Copyright (c) Jamie Mansfield <https://jamiemansfield.me/>
// This Source Code Form is subject to the terms of the Mozilla Public
// License, v. 2.0. If a copy of the MPL was not distributed with this
// file, You can obtain one at http://mozilla.org/MPL/2.0/.
//******************************************************************************

package me.jamiemansfield.csnea.cli;

import me.jamiemansfield.csnea.Difficulty;
import me.jamiemansfield.csnea.FergusMain;
import me.jamiemansfield.csnea.Grade;
import me.jamiemansfield.csnea.model.Subject;
import me.jamiemansfield.csnea.command.Command;
import me.jamiemansfield.csnea.command.CommandArgs;
import me.jamiemansfield.csnea.command.CommandDispatcher;
import me.jamiemansfield.csnea.model.Attempt;
import me.jamiemansfield.csnea.model.Student;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * An enumeration of all the available report generators in
 * Fergus' Quiz.
 */
public enum ReportGenerator {

    /**
     * A report generator that will produce a report for a given
     * {@link me.jamiemansfield.csnea.model.Student}.
     */
    STUDENT("student") {
        @Override
        public void generate(final PrintWriter writer, final CommandArgs args) {
            // Check the flags present are correct first
            if (!args.hasFlag("s")) {
                System.out.println("No student to produce a report on was specified!");
                return;
            }

            // Check the student selection is valid
            final Student student = FergusMain.get().getStudent(args.getFlag("s"));
            if (student == null) {
                System.out.println("Invalid student selection!");
                return;
            }

            // Lets write the report
            writer.println("Report produced for the student: " + student.getFullname()
                    + " (" + student.getUsername() + ")");
            writer.println();

            // Output the quiz attempts made
            writer.println("## Quiz Attempts");
            student.getAttempts().forEach(attempt -> {
                writer.println(String.format("- %s:%s GRADE: %s",
                        attempt.getSubject(),
                        attempt.getDifficulty().getId(),
                        Grade.of(attempt.getPercentage()).getText()
                ));
            });
        }
    },

    /**
     * A report generator that will produce a report for a given
     * quiz - as in a subject with specified difficulty.
     */
    QUIZ("quiz") {
        @Override
        public void generate(final PrintWriter writer, final CommandArgs args) {
            // Check the flags present are correct first
            if (!args.hasFlag("q")) {
                System.out.println("No quiz provided to produce a report on was specified!");
                return;
            }

            // Check the quiz selection is valid
            final Optional<Subject> subject;
            final Optional<Difficulty> difficulty;
            {
                final String[] quizSelectionSplit = args.getFlag("q").split(":");
                subject = Subject.get(quizSelectionSplit[0]);
                difficulty = Difficulty.get(quizSelectionSplit[1]);
            }

            if (!subject.isPresent() || !difficulty.isPresent()) {
                System.out.println("Invalid quiz selection!");
                return;
            }

            // Lets write the report
            writer.println("Report produced for the quiz: "
                    + subject.get().getId() + ":" + difficulty.get().getId());
            writer.println();

            // Get all of the attempts for every student but only
            // for the correct quiz
            final List<Attempt> attempts = FergusMain.get().getStudents().stream()
                    .map(Student::getAttempts)
                    .flatMap(List::stream)
                    .filter(attempt -> Objects.equals(attempt.getSubject(), subject.get().getId()))
                    .filter(attempt -> Objects.equals(attempt.getDifficulty(), difficulty.get()))
                    .collect(Collectors.toList());

            // Use the SE 8 Stream API to get the average percentage attained
            attempts.stream()
                    .mapToInt(Attempt::getPercentage)
                    .average()
                    .ifPresent(average -> {
                        final Grade grade = Grade.of(average);
                        writer.println("The average percentage attained is: " + average + "% (grade: " + grade.getText() + ")");
                    });

            // Use the SE 8 Stream API to get the max percentage attained
            attempts.stream()
                    .mapToInt(Attempt::getPercentage)
                    .max()
                    .ifPresent(max -> {
                        final Grade grade = Grade.of(max);
                        writer.println("The max percentage attained is: " + max + "% (grade: " + grade.getText() + ")");

                        // The use of a HashSet will remove any duplicates
                        final Set<Student> achievedBy = new HashSet<>();
                        attempts.stream()
                                .filter(attempt -> max == attempt.getPercentage())
                                .forEach(attempt -> {
                                    // Find which student the attempt belongs to
                                    FergusMain.get().getStudents().stream()
                                            .filter(student -> student.getAttempts().contains(attempt))
                                            .findFirst()
                                            .ifPresent(achievedBy::add);
                                });

                        // Display who the max score was achieved by (it could be many students)
                        writer.println("Achieved by: " + achievedBy.stream()
                                .map(Student::getFullname)
                                .collect(Collectors.joining(",")));
                    });
        }
    },
    ;

    /**
     * The command used for the reports.
     */
    public static final Command<Student> COMMAND;

    static {
        // Make the command
        COMMAND = (caller, args) -> {
            // Check the flags present are correct first
            if (!args.hasFlag("g")) {
                System.out.println("No report generator was specified!");
                return;
            }

            // Check the report generator selection is valid
            final Optional<ReportGenerator> generator = Arrays.stream(values())
                    .filter(generator1 -> Objects.equals(generator1.id, args.getFlag("g")))
                    .findFirst();
            if (!generator.isPresent()) {
                System.out.println("Invalid report generator selection!");
                return;
            }

            // Get where to store the file (default: report.txt)
            final Path reportPath = Paths.get(args.getFlag("o", "out.txt"));

            // Lets make a report
            try (final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 final PrintWriter writer = new PrintWriter(baos)) {
                // NOTE: I'm going to try keep the output to stick to markdown
                //       that would allow Fergus to convert the reports into
                //       HTML files that would be very easy to read.

                // Make a start on the report
                writer.println("Fergus' Quiz Report");
                writer.println("===================");
                writer.println();

                // Call the generator
                generator.get().generate(writer, args);

                // Store the file
                writer.flush(); // Make sure all the bytes have been written to the
                                // ByteArrayOutputStream first!
                try (final OutputStream os = Files.newOutputStream(reportPath)) {
                    os.write(baos.toByteArray());
                }
            } catch (final IOException ex) {
                throw new RuntimeException("Failed to create the report!", ex);
            }
        };
    }

    /**
     * Registers the report generator commands to the given {@link CommandDispatcher}, if
     * the provided {@link Student} has the necessary permissions.
     *
     * @param student    The student
     * @param dispatcher The command dispatcher
     */
    public static void registerCommand(final Student student,
                                       final CommandDispatcher<Student> dispatcher) {
        // Check the student has the right permissions
        if (student.isAdmin()) {
            // Register the report command
            dispatcher.register("report", COMMAND);
        }
    }

    private final String id;

    /**
     * Creates a report generator from the id of the generator.
     *
     * <strong>The identifier will be used in the report command.</strong>
     *
     * @param id The identifier of the report generator
     */
    ReportGenerator(final String id) {
        this.id = id;
    }

    /**
     * Gets the string identifier of the report generator.
     *
     * @return The identifier
     */
    public final String getId() {
        return this.id;
    }

    /**
     * Generates the report, from the given print writer and command arguments.
     *
     * @param writer The writer to write to
     * @param args   The command args to read from
     */
    public abstract void generate(final PrintWriter writer, final CommandArgs args);

}
