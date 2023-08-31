package com.midel.type;

public class Common<T> extends Tuple<T> {
    // name with element
    private T value;

    public Common(T value){
        this.value = value;
    }

    public T get() {
        return value;
    }

    public void set(T common) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Common{" + value + '}';
    }
}
