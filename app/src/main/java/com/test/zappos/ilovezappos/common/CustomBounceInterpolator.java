package com.test.zappos.ilovezappos.common;

/**
 * Created by root on 10/2/17.
 */

public class CustomBounceInterpolator implements android.view.animation.Interpolator {
    double mAmplitude = 1;
    double mFrequency = 10;

    public CustomBounceInterpolator(double amplitude, double frequency) {
        mAmplitude = amplitude;
        mFrequency = frequency;
    }

    public float getInterpolation(float time) {
        return (float) (-1 * Math.pow(Math.E, -time/ mAmplitude) *
                Math.cos(mFrequency * time) + 1);
    }
}
