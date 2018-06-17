package de.frozenbytes.kickermost.dto.property.basic;


import com.google.common.base.Preconditions;

import java.io.Serializable;
import java.util.Objects;

public abstract class Property<T> implements Serializable {

    private static final long serialVersionUID = -6634565114087203376L;

    protected final T value;

    public Property(T value) {
        super();
        Preconditions.checkNotNull(value, "value should not be null!");
        this.value = value;
    }

    public final T getValue(){
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Property<?> property = (Property<?>) o;
        return Objects.equals(value, property.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
