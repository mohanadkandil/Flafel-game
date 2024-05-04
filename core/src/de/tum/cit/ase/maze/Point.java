package de.tum.cit.ase.maze;

public class Point<K, V> {
    K x;
    V y;

    public Point(K x, V y) {
        this.x = x;
        this.y = y;
    }

    public K getX() {
        return x;
    }

    public void setX(K x) {
        this.x = x;
    }

    public V getY() {
        return y;
    }

    public void setY(V y) {
        this.y = y;
    }
}
