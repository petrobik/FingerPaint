package com.bikshanov.fingerpaint;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class DrawModes {

    public static final int PENCIL = 0;
    public static final int ERASE = 1;
    public static final int PATTERN = 2;
    public static final int BRUSH = 3;


    public DrawModes(@DrawMode int mode) {
    }

    @IntDef({PENCIL, ERASE, PATTERN, BRUSH})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DrawMode {

    }
}