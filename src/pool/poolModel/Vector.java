package pool.poolModel;

import java.io.Serializable;

/**
 * This class represents a mathematical Vector with its arithmetic operations. Vectors are elements of the linear
 * algebra and indicate in which way a point can be parallel displaced.
 */
public class Vector implements Serializable {
    private final float x, y;

    public Vector(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * This method divides a Vector through a given value (float).
     *
     * @param x the value of the divisor
     * @return the Vector after dividing
     */
    public Vector divide(float x) {
        return new Vector(this.x / x, this.y / x);
    }

    /**
     * This method multiplies a Vector by a given value (float).
     *
     * @param x the factor
     * @return the vector after multiplying
     */
    public Vector multiply(float x) {
        return new Vector(this.x * x, this.y * x);
    }

    /**
     * This method calculates the dot product of two Vectors.
     *
     * @param other the second Vector the dot product is calculated with
     * @return the dot product
     */
    public float skalar(Vector other) {
        return this.x * other.x + this.y * other.y;
    }

    /**
     * This method calculates the length of a Vector.
     *
     * @return the length of a Vector
     */
    public float length() {
        return (float) Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2));
    }

    /**
     * This method calculates the sum of two Vectors.
     *
     * @param other the second Vector the sum is calculated with
     * @return the new Vector after adding
     */
    public Vector add(Vector other) {
        return new Vector(this.x + other.x, this.y + other.y);
    }

    /**
     * This method returns the x-Value of a Vector.
     *
     * @return the x-Value of a Vector
     */
    public float getVectorX() {
        return this.x;
    }

    /**
     * This method returns the y-Value of a Vector.
     *
     * @return the y-Value of a Vector
     */
    public float getVectorY() {
        return this.y;
    }
}
