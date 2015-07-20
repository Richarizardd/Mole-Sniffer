package org.centum.android.molescan.processing;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;

import java.util.List;

/**
 * Created by Phani on 9/6/2014.
 */
public class ProcessingResults {

    private Bitmap processedBitmap = null;
    private Bitmap originalBitmap = null;
    private Bitmap renderedBitmap = null;
    private List<Point> convexHull = null;
    private List<Point> boundingBox = null;
    private boolean colorPass = true;
    private boolean sizePass = true;

    public Bitmap getProcessedBitmap() {
        return processedBitmap;
    }

    public void setProcessedBitmap(Bitmap processedBitmap) {
        this.processedBitmap = processedBitmap;
    }

    public Bitmap getOriginalBitmap() {
        return originalBitmap;
    }

    public void setOriginalBitmap(Bitmap originalBitmap) {
        this.originalBitmap = originalBitmap;
    }

    public List<Point> getConvexHull() {
        return convexHull;
    }

    public void setConvexHull(List<Point> convexHull) {
        this.convexHull = convexHull;
    }

    public Bitmap getRenderedBitmap() {
        return renderedBitmap;
    }

    public void setRenderedBitmap(Bitmap renderedBitmap) {
        this.renderedBitmap = renderedBitmap;
    }

    public List<Point> getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(List<Point> boundingBox) {
        this.boundingBox = boundingBox;
    }

    public boolean isColorPass() {
        return colorPass;
    }

    public void setColorPass(boolean colorPass) {
        this.colorPass = colorPass;
        Log.d("Results", "ColorPass: " + colorPass);
    }

    public boolean isSizePass() {
        return sizePass;
    }

    public void setSizePass(boolean sizePass) {
        this.sizePass = sizePass;
        Log.d("Results", "SizePass: " + sizePass);
    }
}
