package com.bikshanov.fingerpaint;

import android.graphics.BitmapShader;
import android.graphics.Color;

public class Stroke {

    private int color;
    private int brushSize;
    private BitmapShader pattern;

    public Stroke() {

    }

    public Stroke(int color, int brushSize, BitmapShader pattern) {
        this.color = color;
        this.brushSize = brushSize;
        this.pattern = pattern;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getBrushSize() {
        return brushSize;
    }

    public void setBrushSize(int brushSize) {
        this.brushSize = brushSize;
    }

    public BitmapShader getPattern() {
        return pattern;
    }

    public void setPattern(BitmapShader pattern) {
        this.pattern = pattern;
    }
}
