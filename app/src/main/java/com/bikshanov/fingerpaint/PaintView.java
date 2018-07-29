package com.bikshanov.fingerpaint;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    private int pencilWidth = 15;
    private int brushWidth = 15;
    private int eraserWidth;
    private int paintColor = 0xFF000000;
    private int backgroundColor = 0xFFFFFFFF;
    private int eraseColor = 0xFFFFFFFF;

    private int paintOpacity = 255;


    private int drawMode = DrawModes.PENCIL;

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
        pencilWidth = getResources().getDimensionPixelSize(R.dimen.brush_small);
        brushWidth = getResources().getDimensionPixelSize(R.dimen.brush_medium);
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
//                    drawPaint.setStrokeWidth(pencilWidth);
                    drawPaint.setAlpha(PENCIL_OPACITY);
                    break;
            }

//            if (!erase) {
//                drawPaint.setColor(paintColor);
//                drawPaint.setStrokeWidth(pencilWidth);
//            }
//            else {
//                drawPaint.setColor(eraseColor);
//                drawPaint.setStrokeWidth(eraserWidth);
//            }

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

        for (Path p: paths) {
//            currentStroke = strokeMap.get(p);
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
                drawPaint.setStrokeWidth(pencilWidth);
                drawPaint.setAlpha(PENCIL_OPACITY);
                break;
        }

//        if (!erase) {
//            drawPaint.setColor(paintColor);
//            drawPaint.setStrokeWidth(pencilWidth);
//        }
//        else {
//            drawPaint.setColor(eraseColor);
//            drawPaint.setStrokeWidth(eraserWidth);
//        }
//
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

            drawPaint.setAlpha(strokeMap.get(paths.get(0)).getOpacity());

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
                currentStroke.setBrushSize(pencilWidth);
                currentStroke.setOpacity(PENCIL_OPACITY);
//                strokeMap.put(drawPath, currentStroke);
//                drawPaint.setShader(patternShader);
                break;
        }

        strokeMap.put(drawPath, currentStroke);

//        if (!erase) {
//            currentStroke.setColor(paintColor);
//            currentStroke.setBrushSize(pencilWidth);
//            strokeMap.put(drawPath, currentStroke);
////            colorsMap.put(drawPath, paintColor);
//        }
//        else {
//            currentStroke.setColor(eraseColor);
//            currentStroke.setBrushSize(eraserWidth);
//            strokeMap.put(drawPath, currentStroke);
////            colorsMap.put(drawPath, eraseColor);
//        }

//        widthMap.put(drawPath, pencilWidth);
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
//        setErase(false);
        setDrawMode(DrawModes.PENCIL);
//        setEmptyPattern();
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

//        if (!erase) {
//            pencilWidth = width;
//            drawPaint.setStrokeWidth(pencilWidth);
//        }
//        else {
//            eraserWidth = width;
//            drawPaint.setStrokeWidth(eraserWidth);
//        }
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
        pencilWidth = (int) pixelAmount;
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

        return pencilWidth;
    }

//    public void setErase(boolean isErase) {
//
//        erase = isErase;
//
//        if (erase) {
//            drawPaint.setColor(eraseColor);
//            drawPaint.setStrokeWidth(eraserWidth);
//
//        }
//        else {
//            drawPaint.setColor(paintColor);
//            drawPaint.setStrokeWidth(pencilWidth);
//        }
//
//    }

//    public void setPatternMode(boolean pattern) {
//
//        isPattern = pattern;
//
//    }

//    public boolean isErase() {
//        return erase;
//    }
//
//    public boolean isPattern() {
//
//        return isPattern;
//    }

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

//        drawPaint.setColor(0xFFFFFFFF);
        drawPaint.setShader(patternShader);
//
//        isPattern = true;
    }

//    public void setEmptyPattern() {
//
//        patternShader = null;
//        if (isPattern)
//            isPattern = false;
//    }

    public void setDrawMode(int drawMode) {
        this.drawMode = drawMode;
    }

    public int getDrawMode() {
        return drawMode;
    }

    public Bitmap getCanvasBitmap() {
        return canvasBitmap;
    }

    private void addToGallery(String path) {
        File file = new File(path);
        Uri contentUri = Uri.fromFile(file);
//        Uri contentUri = Uri.parse("file://" + path);
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, contentUri);
//        mediaScanIntent.setData(contentUri);
        getContext().sendBroadcast(mediaScanIntent);
    }

    public void saveDrawing() {
//        final String fileName = System.currentTimeMillis() + ".png";
////
        setDrawingCacheEnabled(true);
//        setDrawingCacheQuality(DRAWING_CACHE_QUALITY_HIGH);
////
//        String location = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), getDrawingCache(), fileName,  "Drawing");
////
//        if (location != null) {
//            addToGallery(location);
//            Toast message = Toast.makeText(getContext(), "Drawing saved", Toast.LENGTH_SHORT);
//            message.show();
//        }
//        else {
//            Toast message = Toast.makeText(getContext(), "Drawing not saved", Toast.LENGTH_SHORT);
//            message.show();
//        }



        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/FingerPaint";
        File dir = new File(filePath);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()).concat(".jpg");
        File file = new File(dir, fileName);

        FileOutputStream fout;

        try {
            fout = new FileOutputStream(file);
            getDrawingCache().compress(Bitmap.CompressFormat.JPEG, 85, fout);
            fout.flush();
            fout.close();
            setDrawingCacheEnabled(false);
            addToGallery(file.getAbsolutePath());

//            MediaScannerConnection.scanFile(getContext(), new String[]{file.toString()}, null,
//                    new MediaScannerConnection.OnScanCompletedListener() {
//                        @Override
//                        public void onScanCompleted(String s, Uri uri) {
//                            Log.i("SDCard", "Scanned: " + s + ":");
//                            Log.i("SDCard", "-> uri=" + uri);
//                        }
//                    });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
