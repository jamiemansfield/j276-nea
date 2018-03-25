package me.jamiemansfield.csnea;

import me.jamiemansfield.csnea.util.Identifiable;
import me.jamiemansfield.csnea.util.StringIdentifiable;

import java.util.Optional;

/**
 * An enumeration used to represent the difficulty of a quiz.
 */
public enum Difficulty implements StringIdentifiable {

    EASY  ("easy"  , 2),
    MEDIUM("medium", 3),
    HARD  ("hard"  , 4),
    ;

    /**
     * A string identifier used to represent the difficulty
     * outside of the codebase.
     */
    private final String id;

    /**
     * This field simply stores the count of available answers
     * at this difficulty rating.
     */
    private final int availableAnswers;

    /**
     * Constructs the difficulty rating, from the count of
     * available answers presented to the student.
     *
     * @param id               The string identifier of the
     *                         difficulty
     * @param availableAnswers The count of available answers
     *                         students can choose from.
     */
    Difficulty(final String id, final int availableAnswers) {
        this.id = id;
        this.availableAnswers = availableAnswers;
    }

    /**
     * Gets the identifier that is used to represent this difficulty.
     *
     * @return The difficulty's identifier
     */
    @Override
    public final String getId() {
        return this.id;
    }

    /**
     * Gets the available answers to the student at this difficulty
     * rating.
     *
     * @return The count of available answers students can choose
     *         from.
     */
    public final int getAvailableAnswers() {
        return this.availableAnswers;
    }

    /**
     * Gets the {@link Difficulty} of the provided string identifier.
     *
     * @param rawDifficulty The string identifier of the difficulty
     * @return The difficulty, wrapped in an {@link Optional}
     */
    public static Optional<Difficulty> get(final String rawDifficulty) {
        return Identifiable.getById(values(), rawDifficulty);
    }

}
