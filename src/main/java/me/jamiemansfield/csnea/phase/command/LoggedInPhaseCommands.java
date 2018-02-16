package me.jamiemansfield.csnea.phase.command;

import me.jamiemansfield.csnea.Difficulty;
import me.jamiemansfield.csnea.FergusMain;
import me.jamiemansfield.csnea.Grade;
import me.jamiemansfield.csnea.xml.Subject;
import me.jamiemansfield.csnea.cli.CommandArgsReader;
import me.jamiemansfield.csnea.cli.CommandDispatcher;
import me.jamiemansfield.csnea.xml.Attempt;
import me.jamiemansfield.csnea.xml.Question;
import me.jamiemansfield.csnea.xml.Student;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A class used to register all of the logged in phase commands,
 * to the appropriate {@link CommandDispatcher}.
 */
public final class LoggedInPhaseCommands {

    /**
     * Registers all of the logged in phase commands to the {@link CommandDispatcher}
     * responsible for the logged in phase.
     *
     * @param dispatcher The command dispatcher
     */
    public static void registerCommands(final CommandDispatcher<Student> dispatcher) {
        dispatcher.register("quiz", (caller, args) -> {
            // Check the user's input is valid
            if (args.getArgs().size() != 2) {
                System.out.println("Invalid input. quiz <subject> <difficulty>");
                return;
            }

            final CommandArgsReader reader = new CommandArgsReader(args);
            final String rawSubject    = reader.next();
            final String rawDifficulty = reader.next();

            // Validate the student's input
            final Optional<Subject>    subject    = Subject.get(rawSubject);
            final Optional<Difficulty> difficulty = Difficulty.get(rawDifficulty);

            if (!subject.isPresent() || !difficulty.isPresent()) {
                System.out.println("Invalid choice of subject or difficulty!");
                return;
            }

            // Proceed with the quiz.
            final Attempt.Builder attempt = Attempt.builder()
                    .subject(subject.get())
                    .difficulty(difficulty.get());
            final List<Question> questions = subject.get().getDefinitions().get(difficulty.get());

            // Keep count of the correctly answered questions
            final AtomicInteger correctCount = new AtomicInteger(0);

            for (final Question question : questions) {
                // Display the question
                System.out.println(question.getTitle());

                // Display the potential answers, with its index
                for (int i = 0; i < question.getAnswers().size(); i++) {
                    final String potentialAnswer = question.getAnswers().get(i);
                    System.out.println(String.format("%d | %s", i, potentialAnswer));
                }

                // Prompt for answer
                System.out.println("Your answer:");

                // Get student's answer
                final int userAnswer = FergusMain.get().getScanner().nextInt();

                // Check if the answer is correct
                if (userAnswer == question.getCorrectAnswer()) {
                    System.out.println("You answered correctly!");
                    correctCount.incrementAndGet();
                } else {
                    System.out.println("You answered incorrectly!");
                }
            }

            // Establish the percentage achieved
            final int percentage = Math.round((correctCount.get() / questions.size()) * 100);
            final Grade grade = Grade.of(percentage);
            Arrays.asList(
                    "Well Done!",
                    "You achieved a " + grade.getText() + "!",
                    "You scored " + correctCount.get() + "/" + questions.size() + "(" + percentage + ")"
            ).forEach(System.out::println);

            // Store attempt to file
            caller.addAttempt(attempt.build(percentage));
            FergusMain.get().updateStudentsFile();
        });

        dispatcher.register("logout", (caller, args) -> {
            FergusMain.get().transitionToPhase(FergusMain.LOGIN_PHASE);
        });
    }

    private LoggedInPhaseCommands() {
    }

}
