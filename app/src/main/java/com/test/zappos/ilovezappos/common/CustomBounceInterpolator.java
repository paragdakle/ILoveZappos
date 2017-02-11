package com.test.zappos.ilovezappos.common;

/**
 * Created by Parag Dakle on 10/2/17.
 *
 * Class implements the Interpolator Android Interface for the bounch animation.
 */

public class CustomBounceInterpolator implements android.view.animation.Interpolator {

    private double mAmplitude = 1;
    private double mFrequency = 10;

    /*
     * Class Constructor.
     * Configure the amplitude and frequency for the bounce effect.
     */
    public CustomBounceInterpolator(double amplitude, double frequency) {
        mAmplitude = amplitude;
        mFrequency = frequency;
    }

    /*
     * Method computes the Interpolation based on the current time parameter.
     *
     * @param time Current time value.
     *
     * @return interpolation value.
     */
    public float getInterpolation(float time) {
        return (float) (-1 * Math.pow(Math.E, -time/ mAmplitude) *
                Math.cos(mFrequency * time) + 1);
    }
}
