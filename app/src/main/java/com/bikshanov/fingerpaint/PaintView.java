package com.bikshanov.fingerpaint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * Created by Peter on 14.03.2018.
 */

public class PaintView extends View {

    private static final float TOUCH_TOLERANCE = 10;

    private Bitmap bitmap;
    private Bitmap canvasBitmap;
    private Canvas bitmapCanvas;
//    private final Paint paintScreen;
    private Paint drawPaint;
    private Paint backgroundPaint;
    private Canvas drawCanvas;

//    private final Map<Integer, Path> pathMap = new HashMap<>();
//    private final Map<Integer, Point> previousPointMap = new HashMap<>();

    private ArrayList<Path> paths = new ArrayList<>();
//    private ArrayList<Paint> paints = new ArrayList<>();
    private ArrayList<Path> undonePaths = new ArrayList<>();
//    private ArrayList<Paint> undonePaints = new ArrayList<>();
//    private HashMap<Path, Integer> colorsMap = new HashMap<Path, Integer>();
//    private HashMap<Path, Integer> widthMap = new HashMap<Path, Integer>();
//    private HashMap<Path, BitmapShader> patternMap = new HashMap<Path, BitmapShader>();

    private Stroke currentStroke = new Stroke();
    private HashMap<Path, Stroke> strokeMap = new HashMap<>();

    private Path drawPath;

    private Bitmap patternBitmap;
    private BitmapShader patternShader;

    private int strokeWidth = 15;
    private int eraserWidth;
    private int paintColor = 0xFF000000;
    private int backgroundColor = 0xFFFFFFFF;
    private int eraseColor = 0xFFFFFFFF;

    boolean erase = false;
    boolean isPattern = false;

    float pointX, pointY;

    private final int MAX_UNDO = 10;


    public PaintView(Context context, @Nullable AttributeSet attrs) {

        super(context, attrs);
        init();

    }

    private void init() {

//        paintScreen = new Paint();
        eraserWidth = getResources().getDimensionPixelSize((R.dimen.brush_small));
        strokeWidth = getResources().getDimensionPixelSize(R.dimen.brush_small);
        drawPath = new Path();
        drawPaint = new Paint();
        backgroundPaint = new Paint();
        eraseColor = backgroundColor;
        patternShader = null;

        initPaint();

    }

    private void initPaint() {

//            drawPaint = new Paint();
            drawPaint.setAntiAlias(true);

            if (!erase) {
                drawPaint.setColor(paintColor);
                drawPaint.setStrokeWidth(strokeWidth);
            }
            else {
                drawPaint.setColor(eraseColor);
                drawPaint.setStrokeWidth(eraserWidth);
            }

            drawPaint.setStyle(Paint.Style.STROKE);
            drawPaint.setStrokeJoin(Paint.Join.ROUND);
            drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    private void drawBackground(Canvas canvas) {

        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), backgroundPaint);

//        canvas.drawBitmap(canvasBitmap, 0, 0, backgroundPaint);

    }

    private void drawPaths(Canvas canvas) {

//        int i = 0;
//        for (Path p: paths) {
//            canvas.drawPath(p, paints.get(i));
//            i++;
//        }

//        for (Path p: paths) {
//            drawPaint.setColor(colorsMap.get(p));
//            drawPaint.setStrokeWidth(widthMap.get(p));
//            drawPaint.setShader(patternMap.get(p));
//            canvas.drawPath(p, drawPaint);
//        }

        for (Path p: paths) {
//            currentStroke = strokeMap.get(p);
            drawPaint.setColor(strokeMap.get(p).getColor());
            drawPaint.setStrokeWidth(strokeMap.get(p).getBrushSize());
            drawPaint.setShader(strokeMap.get(p).getPattern());
            canvas.drawPath(p, drawPaint);
        }



//        for (Path p: paths) {
//            canvas.drawPath(p, drawPaint);
//        }

    }

    @Override
    protected void onDraw(Canvas canvas) {

        drawBackground(canvas);

        canvas.drawBitmap(canvasBitmap, 0, 0, backgroundPaint);
        drawPaths(canvas);

//        canvas.drawPath(drawPath, drawPaint);

        if (!erase) {
            drawPaint.setColor(paintColor);
            drawPaint.setStrokeWidth(strokeWidth);
        }
        else {
            drawPaint.setColor(eraseColor);
            drawPaint.setStrokeWidth(eraserWidth);
        }

        drawPaint.setShader(patternShader);

        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        canvasBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);

        canvasBitmap.eraseColor(Color.WHITE); //

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float touchX = event.getX();
        float touchY = event.getY();


        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                touchStarted(touchX, touchY);
                break;

            case MotionEvent.ACTION_MOVE:
                touchMoved(touchX, touchY);
                break;

            case MotionEvent.ACTION_UP:
                touchEnded(touchX, touchY);
                break;
        }

        invalidate(); // redraw
        return true;
    }

    private void touchStarted(float x, float y) {


        currentStroke = new Stroke();
        // move to the coordinates of the touch
        drawPath.reset();
        drawPath.moveTo(x, y);

        pointX = x;
        pointY = y;

    }

    // called when the user drags along the screen
    private void touchMoved(float x, float y) {

        drawPath.lineTo(x, y);

//        float deltaX = Math.abs(x - pointX);
//        float deltaY = Math.abs(y - pointY);

        // if the distance is significant enough to matter
//        if (deltaX >= TOUCH_TOLERANCE || deltaY >= TOUCH_TOLERANCE) {
//            // move the path to the new location
//            drawPath.quadTo(pointX, pointY, (x + pointX) / 2,
//                    (y + pointY) / 2);

//            // store the new coordinates
//            pointX = (int) x;
//            pointY = (int) y;
//
//            if (!isErase()) {
//                bitmapCanvas.drawPath(drawPath, drawPaint);
//            }
//            else {
//                bitmapCanvas.drawPath(drawPath, eraseLine);
//            }
//


//                    if (erase) {
//                        bitmapCanvas.drawPath(path, drawPaint);
//                    }

//                    bitmapCanvas.drawPath(path, drawPaint);
//        }


    }

    // called when the user finishes a touch
    private void touchEnded(float x, float y) {

        drawPath.lineTo(x, y);

        if (paths.size() < MAX_UNDO) {

            paths.add(drawPath);
//            paints.add(drawPaint);

//            drawCanvas.drawBitmap(canvasBitmap, 0, 0, backgroundPaint);
        }

        else {

//            drawPaint.setColor(colorsMap.get(paths.get(0)));
//            drawPaint.setStrokeWidth(widthMap.get(paths.get(0)));
//            drawPaint.setShader(patternMap.get(paths.get(0)));

            drawPaint.setColor(strokeMap.get(paths.get(0)).getColor());
            drawPaint.setStrokeWidth(strokeMap.get(paths.get(0)).getBrushSize());
            drawPaint.setShader(strokeMap.get(paths.get(0)).getPattern());

            drawCanvas.drawPath(paths.get(0), drawPaint);

            ArrayList<Path> tmpPaths = new ArrayList<Path>(paths.subList(1, MAX_UNDO));
            paths = tmpPaths;
            paths.add(drawPath);
//            ArrayList<Paint> tmpPaints = new ArrayList<>(paints.subList(1, MAX_UNDO));
//            paints = tmpPaints;
//            paints.add(drawPaint);

//            drawCanvas.drawBitmap(canvasBitmap, 0, 0, backgroundPaint);
        }

//        paths.add(drawPath); //


        currentStroke.setPattern(patternShader);

        if (!erase) {
            currentStroke.setColor(paintColor);
            currentStroke.setBrushSize(strokeWidth);
            strokeMap.put(drawPath, currentStroke);
//            colorsMap.put(drawPath, paintColor);
        }
        else {
            currentStroke.setColor(eraseColor);
            currentStroke.setBrushSize(eraserWidth);
            strokeMap.put(drawPath, currentStroke);
//            colorsMap.put(drawPath, eraseColor);
        }

//        widthMap.put(drawPath, strokeWidth);
//        patternMap.put(drawPath, patternShader);


//        drawCanvas.drawPath(drawPath, drawPaint);
        drawPath = new Path();
        drawPath.reset();
        initPaint();

    }

    public void clear() {

        paths.clear();
//        paints.clear();
        undonePaths.clear();
//        undonePaints.clear();
//        colorsMap.clear();
//        widthMap.clear();
        strokeMap.clear();
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        canvasBitmap.eraseColor(Color.WHITE);
        setErase(false);
//        setEmptyPattern();
        invalidate();
    }

//    public void clear() {
//        paths.clear();
//        undoPaths.clear();
//        drawPath.reset();
////        bitmap.eraseColor(Color.TRANSPARENT);
//        invalidate();
//    }

    public void setDrawingColor(int color) {

        paintColor = color;
        drawPaint.setColor(paintColor);
    }

    public int getDrawingColor() {

//        return drawPaint.getColor();
        return paintColor;
    }

    public void setLineWidth(int width) {

        if (!erase) {
            strokeWidth = width;
            drawPaint.setStrokeWidth(strokeWidth);
        }
        else {
            eraserWidth = width;
            drawPaint.setStrokeWidth(eraserWidth);
        }
    }

    public void setBackgroundColor(int color) {

        backgroundColor = color;
        backgroundPaint.setColor(backgroundColor);
        invalidate();

    }

    public void setBrushSize(float brushSize) {

        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, brushSize,
                getResources().getDisplayMetrics());
//        drawPaint.setStrokeWidth(pixelAmount);
        strokeWidth = (int) pixelAmount;
    }

    public int getLineWidth() {

        return (int) drawPaint.getStrokeWidth();
    }

    public void setErase(boolean isErase) {

        erase = isErase;

        if (erase) {
            drawPaint.setColor(eraseColor);
            drawPaint.setStrokeWidth(eraserWidth);

        }
        else {
            drawPaint.setColor(paintColor);
            drawPaint.setStrokeWidth(strokeWidth);
        }

    }

    public void setPatternMode(boolean pattern) {

        isPattern = pattern;

    }

    public boolean isErase() {
        return erase;
    }

    public boolean isPattern() {

        return isPattern;
    }

    public void undo() {

        if (paths.size() > 0) {
            undonePaths.add(paths.remove(paths.size() - 1));
//            undonePaints.add(paints.remove(paints.size() - 1));
            invalidate();
        }
    }

    public void redo() {

        if (undonePaths.size() > 0) {
            paths.add(undonePaths.remove(undonePaths.size() - 1));
//            paints.add(undonePaints.remove(undonePaints.size() - 1));
            invalidate();
        }
    }

    public void setPattern(String newPattern) {

        invalidate();
        int patternId = getResources().getIdentifier(newPattern, "drawable", "com.bikshanov.fingerpaint");

        patternBitmap = BitmapFactory.decodeResource(getResources(), patternId);
        patternShader = new BitmapShader(patternBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);

        drawPaint.setColor(0xFFFFFFFF);
        drawPaint.setShader(patternShader);

        isPattern = true;
    }

    public void setEmptyPattern() {

        patternShader = null;
        isPattern = false;
    }

}
