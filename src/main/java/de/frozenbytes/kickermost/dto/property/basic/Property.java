package de.frozenbytes.kickermost.dto.property.basic;


import com.google.common.base.Preconditions;

public abstract class Property<T> {

    final T value;

    public Property(T value) {
        super();
        Preconditions.checkNotNull(value, "value should not be null!");
        this.value = value;
    }

    public final T getValue(){
        return value;
    }

}
