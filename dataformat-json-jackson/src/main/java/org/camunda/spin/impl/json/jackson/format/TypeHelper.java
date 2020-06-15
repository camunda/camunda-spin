package org.camunda.spin.impl.json.jackson.format;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.Map;

/**
 * Collection of helper methods to construct types.
 */
public class TypeHelper {
    /**
     * Utility class.
     */
    private TypeHelper() {}

    /**
     * Checks if the erased type has the correct number of type bindings.
     *
     * @param erasedType                  class of the type.
     * @param expectedTypeParametersCount expected number of bindings.
     *
     * @return true if the number of type binding matches expected value.
     */
    static boolean bindingsArePresent(Class<?> erasedType, int expectedTypeParametersCount) {
        if (erasedType == null) {
            return false;
        }
        final TypeVariable<? extends Class<?>>[] typeParameters = erasedType.getTypeParameters();
        if (typeParameters.length == 0) {
            return false;
        }
        if (typeParameters.length != expectedTypeParametersCount) {
            throw new IllegalArgumentException("Cannot create TypeBindings for class " + erasedType.getName() + " with " + expectedTypeParametersCount
                                                   + " type parameter: class expects " + typeParameters.length + " type parameters.");
        }
        return true;
    }

    /**
     * Constructs Java type based on the content values.
     *
     * @param value value with values.
     *
     * @return Java type.
     */
    static JavaType constructType(Object value) {
        final TypeFactory typeFactory = TypeFactory.defaultInstance();
        if (value instanceof Collection<?> && !((Collection<?>) value).isEmpty()) {
            final Object firstElement = ((Collection<?>) value).iterator().next();
            if (bindingsArePresent(value.getClass(), 1)) {
                final JavaType elementType = constructType(firstElement);
                return typeFactory.constructCollectionType(guessCollectionType(value), elementType);
            }
        } else if (value instanceof Map<?, ?> && !((Map<?, ?>) value).isEmpty()) {
            final Map.Entry<?, ?> firstEntry = ((Map<?, ?>) value).entrySet().iterator().next();
            if (bindingsArePresent(firstEntry.getClass(), 2)) {
                final JavaType keyType = constructType(firstEntry.getKey());
                final JavaType valueType = constructType(firstEntry.getValue());
                return typeFactory.constructMapType(Map.class, keyType, valueType);
            }
        }
        return typeFactory.constructType(value.getClass());
    }

    /**
     * Guess collection class.
     *
     * @param collection collection.
     *
     * @return class of th collection implementation.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    static Class<? extends Collection> guessCollectionType(Object collection) {
        if (collection instanceof Collection<?>) {
            return (Class<? extends Collection>) collection.getClass();
        } else {
            throw new IllegalArgumentException("Could not detect class for " + collection + " of type " + collection.getClass().getName());
        }
    }

}
