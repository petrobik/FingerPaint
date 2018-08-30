package com.bikshanov.fingerpaint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Peter on 14.03.2018.
 */

public class PaintView extends View {

    private static final float TOUCH_TOLERANCE = 10;

    private static final int BRUSH_OPACITY = 127;
    private static final int PENCIL_OPACITY = 255;
    private static final int ERASER_OPACITY = 255;

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
    private ArrayList<Path> undonePaths = new ArrayList<>();

    private Stroke currentStroke = new Stroke();
    private HashMap<Path, Stroke> strokeMap = new HashMap<>();

    private Path drawPath;

    private Bitmap patternBitmap;
    private BitmapShader patternShader;

    private int pencilWidth = 15;
    private int brushWidth = 15;
    private int eraserWidth;
    private int patternWidth;
    private int paintColor = 0xFF000000;
    private int backgroundColor = 0xFFFFFFFF;
    private int eraseColor = 0xFFFFFFFF;

    private int paintOpacity = 255;


    private int drawMode = DrawModes.PENCIL;

    float pointX, pointY;

    private final int MAX_UNDO = 10;


    public PaintView(Context context, @Nullable AttributeSet attrs) {

        super(context, attrs);
        init();
    }

    private void init() {

        eraserWidth = getResources().getDimensionPixelSize((R.dimen.brush_medium));
        pencilWidth = getResources().getDimensionPixelSize(R.dimen.brush_small);
        brushWidth = getResources().getDimensionPixelSize(R.dimen.brush_medium);
        patternWidth = getResources().getDimensionPixelSize(R.dimen.brush_pattern);
        drawPath = new Path();
        drawPaint = new Paint();
        backgroundPaint = new Paint();
        eraseColor = backgroundColor;
        patternShader = null;

        initPaint();
    }

    private void initPaint() {

            drawPaint.setAntiAlias(true);

            switch (drawMode) {
                case DrawModes.PENCIL:
                    drawPaint.setColor(paintColor);
                    drawPaint.setStrokeWidth(pencilWidth);
                    drawPaint.setAlpha(PENCIL_OPACITY);
                    break;
                case DrawModes.BRUSH:
                    drawPaint.setColor(paintColor);
                    drawPaint.setStrokeWidth(brushWidth);
                    drawPaint.setAlpha(BRUSH_OPACITY);
                    break;
                case DrawModes.ERASE:
                    drawPaint.setColor(eraseColor);
                    drawPaint.setStrokeWidth(eraserWidth);
                    drawPaint.setAlpha(ERASER_OPACITY);
                    break;
                case DrawModes.PATTERN:
//                    drawPaint.setColor(paintColor);
                    drawPaint.setStrokeWidth(patternWidth);
                    drawPaint.setAlpha(PENCIL_OPACITY);
                    break;
            }

            drawPaint.setStyle(Paint.Style.STROKE);
            drawPaint.setStrokeJoin(Paint.Join.ROUND);
            drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    private void drawBackground(Canvas canvas) {

        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), backgroundPaint);
    }

    private void drawPaths(Canvas canvas) {

        for (Path p: paths) {

            drawPaint.setColor(strokeMap.get(p).getColor());
            drawPaint.setStrokeWidth(strokeMap.get(p).getBrushSize());
            drawPaint.setShader(strokeMap.get(p).getPattern());

            drawPaint.setAlpha(strokeMap.get(p).getOpacity());

            canvas.drawPath(p, drawPaint);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        drawBackground(canvas);

        canvas.drawBitmap(canvasBitmap, 0, 0, backgroundPaint);
        drawPaths(canvas);

//        canvas.drawPath(drawPath, drawPaint);

        switch (drawMode) {
            case DrawModes.PENCIL:
                drawPaint.setColor(paintColor);
                drawPaint.setStrokeWidth(pencilWidth);
                patternShader = null;
                drawPaint.setAlpha(PENCIL_OPACITY);
                break;
            case DrawModes.BRUSH:
                drawPaint.setColor(paintColor);
                drawPaint.setStrokeWidth(brushWidth);
                patternShader = null;
                drawPaint.setAlpha(BRUSH_OPACITY);
                break;
            case DrawModes.ERASE:
                drawPaint.setColor(eraseColor);
                drawPaint.setStrokeWidth(eraserWidth);
                patternShader = null;
                drawPaint.setAlpha(ERASER_OPACITY);
                break;
            case DrawModes.PATTERN:
//                drawPaint.setColor(paintColor);
                drawPaint.setStrokeWidth(patternWidth);
                drawPaint.setAlpha(PENCIL_OPACITY);
                break;
        }

        drawPaint.setShader(patternShader);

        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        canvasBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);

        canvasBitmap.eraseColor(Color.WHITE);
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
        }

        else {

            drawPaint.setColor(strokeMap.get(paths.get(0)).getColor());
            drawPaint.setStrokeWidth(strokeMap.get(paths.get(0)).getBrushSize());
            drawPaint.setShader(strokeMap.get(paths.get(0)).getPattern());

            drawPaint.setAlpha(strokeMap.get(paths.get(0)).getOpacity());

            drawCanvas.drawPath(paths.get(0), drawPaint);

            ArrayList<Path> tmpPaths = new ArrayList<Path>(paths.subList(1, MAX_UNDO));
            paths = tmpPaths;
            paths.add(drawPath);
        }

        currentStroke.setPattern(patternShader);

        switch (drawMode) {
            case DrawModes.PENCIL:
                currentStroke.setColor(paintColor);
                currentStroke.setBrushSize(pencilWidth);

                currentStroke.setOpacity(PENCIL_OPACITY);
//                strokeMap.put(drawPath, currentStroke);
                break;
            case DrawModes.BRUSH:
                currentStroke.setColor(paintColor);
                currentStroke.setBrushSize(brushWidth);

                currentStroke.setOpacity(BRUSH_OPACITY);
//                strokeMap.put(drawPath, currentStroke);
                break;
            case DrawModes.ERASE:
                currentStroke.setColor(eraseColor);
                currentStroke.setBrushSize(eraserWidth);
//                strokeMap.put(drawPath, currentStroke);
                currentStroke.setOpacity(ERASER_OPACITY);
                break;
            case DrawModes.PATTERN:
                currentStroke.setColor(paintColor);
                currentStroke.setBrushSize(patternWidth);
                currentStroke.setOpacity(PENCIL_OPACITY);
//                strokeMap.put(drawPath, currentStroke);
//                drawPaint.setShader(patternShader);
                break;
        }

        strokeMap.put(drawPath, currentStroke);

        drawPath = new Path();
        drawPath.reset();
        initPaint();
    }

    public void clear() {

        paths.clear();
        undonePaths.clear();
        strokeMap.clear();
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        canvasBitmap.eraseColor(Color.WHITE);

        invalidate();
    }

    public void setDrawingColor(int color) {

        paintColor = color;
        drawPaint.setColor(paintColor);
    }

    public int getDrawingColor() {

        switch (drawMode) {
            case DrawModes.PENCIL:
                return paintColor;
            case DrawModes.BRUSH:
                return paintColor;
            case DrawModes.ERASE:
                return eraseColor;
        }
        return paintColor;
    }

    public void setLineWidth(int width) {

        switch (drawMode) {
            case DrawModes.PENCIL:
                pencilWidth = width;
                drawPaint.setStrokeWidth(pencilWidth);
                break;
            case DrawModes.BRUSH:
                brushWidth = width;
                drawPaint.setStrokeWidth(brushWidth);
                break;
            case DrawModes.ERASE:
                eraserWidth = width;
                drawPaint.setStrokeWidth(eraserWidth);
                break;
        }
    }

    public void setBackgroundColor(int color) {

        backgroundColor = color;
        backgroundPaint.setColor(backgroundColor);
        invalidate();

    }

    public int getPaintOpacity() {

        switch (drawMode) {
            case DrawModes.PENCIL:
                return PENCIL_OPACITY;
            case DrawModes.BRUSH:
                return BRUSH_OPACITY;
            case DrawModes.ERASE:
                return ERASER_OPACITY;
        }
        return PENCIL_OPACITY;
    }

    public int getLineWidth() {

        switch (drawMode) {
            case DrawModes.PENCIL:
                return pencilWidth;
            case DrawModes.BRUSH:
                return brushWidth;
            case DrawModes.ERASE:
                return eraserWidth;
        }

        return patternWidth;
    }

    public void undo() {

        if (paths.size() > 0) {
            undonePaths.add(paths.remove(paths.size() - 1));
            invalidate();
        }
    }

    public void redo() {

        if (undonePaths.size() > 0) {
            paths.add(undonePaths.remove(undonePaths.size() - 1));
            invalidate();
        }
    }

    public void setPattern(String newPattern) {

        invalidate();
        int patternId = getResources().getIdentifier(newPattern, "drawable", "com.bikshanov.fingerpaint");

        patternBitmap = BitmapFactory.decodeResource(getResources(), patternId);
        patternShader = new BitmapShader(patternBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);

//        drawPaint.setColor(0xFFFFFFFF);
        drawPaint.setShader(patternShader);
    }

    public void setDrawMode(int drawMode) {
        this.drawMode = drawMode;
    }

    public int getDrawMode() {
        return drawMode;
    }

    public void saveDrawing() {

        if (Utils.savePicture(this, getContext())) {
            Toast message = Toast.makeText(getContext(), R.string.drawing_saved, Toast.LENGTH_SHORT);
            message.show();
        }
        else {
            Toast message = Toast.makeText(getContext(), R.string.drawing_not_saved, Toast.LENGTH_SHORT);
            message.show();
        }
    }
}