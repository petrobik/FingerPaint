package com.bikshanov.fingerpaint;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.security.Policy;

public class DrawModes {

    public static final int DRAW = 0;
    public static final int ERASE = 1;
    public static final int PATTERN = 2;

    public DrawModes(@DrawMode int mode) {
    }

    @IntDef({DRAW, ERASE, PATTERN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DrawMode {

    }

}
