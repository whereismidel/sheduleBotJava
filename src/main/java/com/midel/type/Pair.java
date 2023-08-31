package com.midel.type;

public class Pair<T> extends Tuple<T>{

    private T left;
    private T right;

    public Pair(T left, T right) {
        this.left = left;
        this.right = right;
    }

    public T getLeft() {
        return left;
    }

    public void setLeft(T left) {
        this.left = left;
    }

    public T getRight() {
        return right;
    }

    public void setRight(T right) {
        this.right = right;
    }

    public T get(int pos) {
        return pos <= 0? left : right;
    }

    @Override
    public String toString() {
        return "Pair{" + left + " / " + right + '}';
    }
}
