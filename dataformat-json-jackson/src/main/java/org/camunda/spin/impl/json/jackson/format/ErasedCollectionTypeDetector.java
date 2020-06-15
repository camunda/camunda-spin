package org.camunda.spin.impl.json.jackson.format;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.camunda.spin.impl.json.jackson.format.TypeHelper.constructType;

/**
 * Detects erased types of Java Collection classes and Map.
 */
public class ErasedCollectionTypeDetector extends AbstractJacksonJsonTypeDetector {
    /**
     * Object instance to use.
     */
    public static ErasedCollectionTypeDetector INSTANCE = new ErasedCollectionTypeDetector();

    /**
     * The client is not intended to instantiate this class.
     * Please use {@link ErasedCollectionTypeDetector#INSTANCE}.
     */
    private ErasedCollectionTypeDetector() {
    }

    @Override
    public boolean canHandle(Object value) {
        return value instanceof Collection<?>
            || value instanceof Map<?, ?>;
    }

    @Override
    public String detectType(Object value) {
        return constructType(value).toCanonical();
    }
}
