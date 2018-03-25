package me.jamiemansfield.csnea.cli.command;

import me.jamiemansfield.csnea.FergusMain;
import me.jamiemansfield.csnea.util.command.CommandArgsReader;
import me.jamiemansfield.csnea.command.CommandDispatcher;
import me.jamiemansfield.csnea.cli.LoggedInPhase;
import me.jamiemansfield.csnea.model.Student;

/**
 * A class used to register all of the login phase commands,
 * to the appropriate {@link CommandDispatcher}.
 */
public final class LoginPhaseCommands {

    /**
     * Registers all of the login phase commands to the {@link CommandDispatcher}
     * responsible for the login phase.
     *
     * @param dispatcher The command dispatcher
     */
    public static void registerCommands(final CommandDispatcher<Object> dispatcher) {
        // The login command
        dispatcher.register("login", (caller, args) -> {
            // Check the user's input is valid
            if (args.getArgs().size() != 2) {
                System.out.println("Invalid input. login <username> <password>");
                return;
            }

            final CommandArgsReader reader = new CommandArgsReader(args);
            final String rawUsername = reader.next();
            final String rawPassword = reader.next();

            // See if a user of that name exists
            if (!FergusMain.get().hasStudentOfUsername(rawUsername)) {
                // Do not inform whether the username or password was
                // correct/incorrect as a matter of security
                System.out.println("Username or Password is incorrect.");
                return;
            }

            // Get the student object
            final Student student = FergusMain.get().getStudent(rawUsername);

            // See if the password is correct
            if (!student.testPassword(rawPassword)) {
                // Do not inform whether the username or password was
                // correct/incorrect as a matter of security
                System.out.println("Username or Password is incorrect.");
                return;
            }

            // Create, and enter, the LoggedInPhase for the student
            FergusMain.get().transitionToPhase(new LoggedInPhase(student));
        });

        // The signup command
        dispatcher.register("signup", (caller, args) -> {
            // Start the builder
            final Student.Builder builder = Student.builder();

            // First let's check if the student should be an admin
            builder.admin(FergusMain.get().getStudents().size() == 0);

            // Get the fullname of the student
            System.out.println("Enter your full name: ");
            builder.fullname(FergusMain.get().getScanner().nextLine());

            // Get the age of the student
            System.out.println("Enter your age: ");
            builder.age(FergusMain.get().getScanner().nextInt());

            // Get the yearGroup of the student
            System.out.println("Enter your year group: ");
            builder.yearGroup(FergusMain.get().getScanner().next());

            // Get the password for the student
            System.out.println("Enter your password: ");
            builder.password(FergusMain.get().getScanner().next());

            // Register the student
            final Student student = builder.build();
            System.out.println("Your username is: " + student.getUsername());
            FergusMain.get().registerStudent(student);

            // Create, and enter, the LoggedInPhase for the student
            FergusMain.get().transitionToPhase(new LoggedInPhase(student));
        });
    }

    private LoginPhaseCommands() {
    }

}
