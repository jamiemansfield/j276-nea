package me.jamiemansfield.csnea.util;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * A common interface used to denote an object as having a
 * identifier, of type {@code T}.
 *
 * @param <T> The type of the identifier
 */
public interface Identifiable<T> {

    /**
     * Gets the identifier of the object.
     *
     * @return The identifier
     */
    T getId();

    /**
     * Gets the object, wrapped in an {@link Optional}, from an array of objects, and the
     * identifier of the desired object.
     *
     * @param objects    The array of objects
     * @param identifier The identifier of the desired object
     * @param <O>        The type of the object
     * @param <T>        The type of the identifier
     * @return           The object, wrapped in an {@link Optional}
     */
    static <O extends Identifiable<T>, T> Optional<O> getById(final O[] objects, final T identifier) {
        return Arrays.stream(objects)
                .filter(obj -> Objects.equals(obj.getId(), identifier))
                .findFirst();
    }

}
