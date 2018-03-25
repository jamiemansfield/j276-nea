package me.jamiemansfield.csnea.util;

/**
 * A common interface used to denote an object as having
 * a string identifier.
 */
public interface StringIdentifiable extends Identifiable<String> {

    /**
     * Gets the string identifier of the object.
     *
     * @return The identifier
     */
    @Override
    String getId();

}
