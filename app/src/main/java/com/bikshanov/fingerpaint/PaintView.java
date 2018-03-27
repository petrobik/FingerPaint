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

    private final Map<Integer, Path> pathMap = new HashMap<>();
    private final Map<Integer, Point> previousPointMap = new HashMap<>();

    private ArrayList<Path> paths = new ArrayList<>();
    private ArrayList<Paint> paints = new ArrayList<>();
    private ArrayList<Path> undonePaths = new ArrayList<>();
    private ArrayList<Paint> undonePaints = new ArrayList<>();
    private Path drawPath;

    private int strokeWidth = 15;
    private int paintColor = 0xFF000000;
    private int backgroundColor = 0xFFFFFFFF;
    private int eraseColor = 0xFFFFFFFF;

    boolean erase = false;

    float pointX, pointY;

    boolean isFilling = false;


    public PaintView(Context context, @Nullable AttributeSet attrs) {

        super(context, attrs);
        init();

    }

    private void init() {

//        paintScreen = new Paint();
        strokeWidth = getResources().getDimensionPixelSize(R.dimen.brush_small);
        drawPath = new Path();
        backgroundPaint = new Paint();
        eraseColor = backgroundColor;

        initPaint();

    }

    private void initPaint() {

            drawPaint = new Paint();
            drawPaint.setAntiAlias(true);

            if (!erase) {
                drawPaint.setColor(paintColor);
            }
            else {
                drawPaint.setColor(eraseColor);
            }

            drawPaint.setStrokeWidth(strokeWidth);
            drawPaint.setStyle(Paint.Style.STROKE);
//        drawPaint.setStrokeWidth(getResources().getInteger(R.integer.size_medium));
//        drawPaint.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.brush_small));
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

        int i = 0;
        for (Path p: paths) {
            canvas.drawPath(p, paints.get(i));
            i++;
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {

        drawBackground(canvas);
        drawPaths(canvas);

        canvas.drawPath(drawPath, drawPaint);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        canvasBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);

//        canvasBitmap.eraseColor(Color.WHITE);

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

        paths.add(drawPath);
        paints.add(drawPaint);
        drawPath = new Path();
        drawPath.reset();
        initPaint();

    }

    public void clear() {

        paths.clear();
        paints.clear();
        undonePaths.clear();
        undonePaints.clear();
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
//        canvasBitmap.eraseColor(Color.WHITE);
        setErase(false);
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

        strokeWidth = width;
        drawPaint.setStrokeWidth(strokeWidth);
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

        if (erase)
            drawPaint.setColor(eraseColor);
        else
            drawPaint.setColor(paintColor);

    }

    public boolean isErase() {
        return erase;
    }



//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        int action = event.getActionMasked(); // event type
//        int actionIndex = event.getActionIndex(); // pointer (i.e., finger)
//
//        // determine whether touch started, ended or is moving
//        if (action == MotionEvent.ACTION_DOWN ||
//                action == MotionEvent.ACTION_POINTER_DOWN) {
//            touchStarted(event.getX(actionIndex), event.getY(actionIndex),
//                    event.getPointerId(actionIndex));
//        }
//        else if (action == MotionEvent.ACTION_UP ||
//                action == MotionEvent.ACTION_POINTER_UP) {
//            touchEnded(event.getPointerId(actionIndex));
//        }
//        else {
//            touchMoved(event);
//        }
//
//        invalidate(); // redraw
//        return true;
//    }




//    // called when the user touches the screen
//    private void touchStarted(float x, float y, int lineID) {
//        Path path; // used to store the path for the given touch id
//        Point point; // used to store the last point in path
//
//        // if there is already a path for lineID
//        if (pathMap.containsKey(lineID)) {
//            path = pathMap.get(lineID); // get the Path
//            paths.add(path);
//            path.reset(); // resets the Path because a new touch has started
//            point = previousPointMap.get(lineID); // get Path's last point
//        }
//        else {
//            path = new Path();
//            pathMap.put(lineID, path); // add the Path to Map
//            point = new Point(); // create a new Point
//            previousPointMap.put(lineID, point); // add the Point to the Map
//            paths.add(path); // /
//            Log.d("Path", String.valueOf(paths.size()));
////            drawPath = path;
//        }
//            path = new Path();
//            pathMap.put(lineID, path); // add the Path to Map
//            point = new Point(); // create a new Point
//            previousPointMap.put(lineID, point); // add the Point to the Map
//            paths.add(path); // /
//            Log.d("Path", String.valueOf(paths.size()));
//////            drawPath = path;
//
//        // move to the coordinates of the touch
//        path.moveTo(x, y);
//        point.x = (int) x;
//        point.y = (int) y;
//    }

    // called when the user touches the screen


//    // called when the user drags along the screen
//    private void touchMoved(MotionEvent event) {
//        // for each of the pointers in the given MotionEvent
//        for (int i = 0; i < event.getPointerCount(); i++) {
//            // get the pointer ID and pointer index
//            int pointerID = event.getPointerId(i);
//            int pointerIndex = event.findPointerIndex(pointerID);
//
//            // if there is a path associated with the pointer
//            if (pathMap.containsKey(pointerID)) {
//                // get the new coordinates for the pointer
//                float newX = event.getX(pointerIndex);
//                float newY = event.getY(pointerIndex);
//
//                // get the path and previous point associated with
//                // this pointer
//                Path path = pathMap.get(pointerID);
//                Point point = previousPointMap.get(pointerID);
//
//                // calculate how far the user moved from the last update
//                float deltaX = Math.abs(newX - point.x);
//                float deltaY = Math.abs(newY - point.y);
//
//                // if the distance is significant enough to matter
//                if (deltaX >= TOUCH_TOLERANCE || deltaY >= TOUCH_TOLERANCE) {
//                    // move the path to the new location
//                    path.quadTo(point.x, point.y, (newX + point.x) / 2,
//                            (newY + point.y) / 2);
//
//                    // store the new coordinates
//                    point.x = (int) newX;
//                    point.y = (int) newY;
//
//
////                    if (erase) {
////                        bitmapCanvas.drawPath(path, drawPaint);
////                    }
//
////                    bitmapCanvas.drawPath(path, drawPaint);
//                }
//            }
//        }
//    }




//    // called when the user finishes a touch
//    private void touchEnded(int lineID) {
//        Path path = pathMap.get(lineID); // get the corresponding Path
////        drawPath = path;
////        paths.add(path); // /
////        bitmapCanvas.drawPath(path, drawPaint); // draw to bitmapCanvas
////        path.reset(); // reset the Path
//    }




    public void undo() {

        if (paths.size() > 0) {
            undonePaths.add(paths.remove(paths.size() - 1));
            undonePaints.add(paints.remove(paints.size() - 1));
            invalidate();
        }
    }

    public void redo() {

        if (undonePaths.size() > 0) {
            paths.add(undonePaths.remove(undonePaths.size() - 1));
            paints.add(undonePaints.remove(undonePaints.size() - 1));
            invalidate();
        }
    }

    public void setPattern(String newPattern) {

        invalidate();
        int patternId = getResources().getIdentifier(newPattern, "drawable", "com.bikshanov.fingerpaint");

        Bitmap patternBitmap = BitmapFactory.decodeResource(getResources(), patternId);
        BitmapShader patternShader = new BitmapShader(patternBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);

        drawPaint.setColor(0xFFFFFFFF);
        drawPaint.setShader(patternShader);
    }
}
