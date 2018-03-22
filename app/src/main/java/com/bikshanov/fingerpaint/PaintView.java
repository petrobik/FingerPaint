package com.bikshanov.fingerpaint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Peter on 14.03.2018.
 */

public class PaintView extends View {

    private static final float TOUCH_TOLERANCE = 10;

    private Bitmap bitmap;
    private Canvas bitmapCanvas;
    private final Paint paintScreen;
    private final Paint paintLine;

    private final Map<Integer, Path> pathMap = new HashMap<>();
    private final Map<Integer, Point> previousPointMap = new HashMap<>();

    private ArrayList<Path> paths = new ArrayList<Path>();
    private ArrayList<Path> undoPaths = new ArrayList<Path>();
    private Path currentPath = new Path();

    boolean erase = false;
    boolean paint = true;

    int rememberLineId = 0;


    public PaintView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        paintScreen = new Paint();

        paintLine = new Paint();
        paintLine.setAntiAlias(true);
        paintLine.setColor(Color.BLACK);
        paintLine.setStyle(Paint.Style.STROKE);
//        paintLine.setStrokeWidth(getResources().getInteger(R.integer.size_medium));
        paintLine.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.brush_small));
        paintLine.setStrokeJoin(Paint.Join.ROUND);
        paintLine.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);
//        bitmap.eraseColor(Color.WHITE);
        bitmap.eraseColor(Color.TRANSPARENT);
    }

    public void clear() {
        pathMap.clear();
        previousPointMap.clear();
        paths.clear();
        bitmap.eraseColor(Color.TRANSPARENT);
        invalidate();
    }

//    public void clear() {
//        paths.clear();
//        undoPaths.clear();
//        currentPath.reset();
////        bitmap.eraseColor(Color.TRANSPARENT);
//        invalidate();
//    }

    public void setDrawingColor(int color) {

        paintLine.setColor(color);
    }

    public int getDrawingColor() {

        return paintLine.getColor();
    }

    public void setLineWidth(int width) {

        paintLine.setStrokeWidth(width);
    }

    public void setBrushSize(float brushSize) {

        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, brushSize,
                getResources().getDisplayMetrics());
        paintLine.setStrokeWidth(pixelAmount);

    }

    public int getLineWidth() {

        return (int) paintLine.getStrokeWidth();
    }

    public void setErase(boolean isErase) {
        erase = isErase;

        if (erase) {
            paintLine.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
        else {
            paintLine.setXfermode(null);
        }
    }

    public boolean isErase() {
        return erase;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawBitmap(bitmap, 0, 0, paintScreen);

//        if (erase) return;

//        for (Integer key : pathMap.keySet()) {
//            canvas.drawPath(pathMap.get(key), paintLine);
//        }

        for (Path p : paths) {
            canvas.drawPath(p, paintLine);
        }

//        canvas.drawPath(currentPath, paintLine);
//        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked(); // event type
        int actionIndex = event.getActionIndex(); // pointer (i.e., finger)

        // determine whether touch started, ended or is moving
        if (action == MotionEvent.ACTION_DOWN ||
                action == MotionEvent.ACTION_POINTER_DOWN) {
            touchStarted(event.getX(actionIndex), event.getY(actionIndex),
                    event.getPointerId(actionIndex));
        }
        else if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_POINTER_UP) {
            touchEnded(event.getPointerId(actionIndex));
        }
        else {
            touchMoved(event);
        }

        invalidate(); // redraw
        return true;
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//
//        final float x = event.getX();
//        final float y = event.getY();
//
//        switch (event.getAction()) {
//
//            case MotionEvent.ACTION_DOWN:
//                undoPaths.clear();
//                currentPath.moveTo(x, y);
//                break;
//
//            case MotionEvent.ACTION_MOVE:
//                for (int i = 0; i < event.getHistorySize(); i++) {
//                    currentPath.lineTo(event.getHistoricalX(i), event.getHistoricalY(i));
//                }
//
//                currentPath.lineTo(x, y);
//                break;
//
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_CANCEL:
//                currentPath.lineTo(x, y);
//                paths.add(currentPath);
//                currentPath.reset();
//
//                break;
//        }
//
//        invalidate();
//
//        return true;
//    }


    // called when the user touches the screen
    private void touchStarted(float x, float y, int lineID) {
        Path path; // used to store the path for the given touch id
        Point point; // used to store the last point in path

        // if there is already a path for lineID
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
////            currentPath = path;
//        }
            path = new Path();
            pathMap.put(lineID, path); // add the Path to Map
            point = new Point(); // create a new Point
            previousPointMap.put(lineID, point); // add the Point to the Map
            paths.add(path); // /
            Log.d("Path", String.valueOf(paths.size()));
////            currentPath = path;

        // move to the coordinates of the touch
        path.moveTo(x, y);
        point.x = (int) x;
        point.y = (int) y;
    }

    // called when the user drags along the screen
    private void touchMoved(MotionEvent event) {
        // for each of the pointers in the given MotionEvent
        for (int i = 0; i < event.getPointerCount(); i++) {
            // get the pointer ID and pointer index
            int pointerID = event.getPointerId(i);
            int pointerIndex = event.findPointerIndex(pointerID);

            // if there is a path associated with the pointer
            if (pathMap.containsKey(pointerID)) {
                // get the new coordinates for the pointer
                float newX = event.getX(pointerIndex);
                float newY = event.getY(pointerIndex);

                // get the path and previous point associated with
                // this pointer
                Path path = pathMap.get(pointerID);
                Point point = previousPointMap.get(pointerID);

                // calculate how far the user moved from the last update
                float deltaX = Math.abs(newX - point.x);
                float deltaY = Math.abs(newY - point.y);

                // if the distance is significant enough to matter
                if (deltaX >= TOUCH_TOLERANCE || deltaY >= TOUCH_TOLERANCE) {
                    // move the path to the new location
                    path.quadTo(point.x, point.y, (newX + point.x) / 2,
                            (newY + point.y) / 2);

                    // store the new coordinates
                    point.x = (int) newX;
                    point.y = (int) newY;


//                    if (erase) {
//                        bitmapCanvas.drawPath(path, paintLine);
//                    }

//                    bitmapCanvas.drawPath(path, paintLine);
                }
            }
        }
    }

    // called when the user finishes a touch
    private void touchEnded(int lineID) {
        Path path = pathMap.get(lineID); // get the corresponding Path
//        currentPath = path;
//        paths.add(path); // /
//        bitmapCanvas.drawPath(path, paintLine); // draw to bitmapCanvas
//        path.reset(); // reset the Path
    }

    public void undo() {
        if (paths.size() > 0) {
            paths.remove(paths.size() - 1);
            invalidate();
        }
    }
}
