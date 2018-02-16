package me.jamiemansfield.csnea.phase;

import me.jamiemansfield.csnea.cli.CommandDispatcher;
import me.jamiemansfield.csnea.phase.command.CommonCommands;
import me.jamiemansfield.csnea.phase.command.LoggedInPhaseCommands;
import me.jamiemansfield.csnea.xml.Student;

import java.util.Arrays;

public class LoggedInPhase implements Phase<Student> {

    private final CommandDispatcher<Student> dispatcher = new CommandDispatcher<>();

    private final Student student;

    public LoggedInPhase(final Student student) {
        CommonCommands.registerCommands(this.dispatcher);
        LoggedInPhaseCommands.registerCommands(this.dispatcher);
        ReportGenerator.registerCommand(student, this.dispatcher);
        this.student = student;
    }

    @Override
    public void displayHelp() {
        Arrays.asList(
                "Welcome to Fergus' Quiz",
                "",
                "Available Subjects:",
                "  maths",
                "  computer-science",
                "",
                "Commands:",
                "  quiz <subject> <difficulty>",
                "    Take a quiz",
                "  logout",
                "    Logs the student out",
                "  exit",
                "    Exits the program"
        ).forEach(System.out::println);

        // Display the administrator commands, if the student has the admin flag
        if (student.isAdmin()) {
            Arrays.asList(
                    "",
                    "Administrator Commands:",
                    "  report -g <student|quiz> [-o <out.txt>] [generator options]"
            ).forEach(System.out::println);
        }
    }

    @Override
    public void enter() {
        this.displayHelp();
    }

    @Override
    public void exit() {
    }

    @Override
    public Student getCaller() {
        return this.student;
    }

    @Override
    public CommandDispatcher<Student> getDispatcher() {
        return this.dispatcher;
    }

}