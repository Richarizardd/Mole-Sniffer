package org.centum.android.molescan.processing.filter;

import android.graphics.Bitmap;

/**
 * Created by Phani on 9/6/2014.
 */
public class ScaleFilter implements Filter {

    private int maxDimension = 1000;

    public ScaleFilter(int maxDimension) {
        this.maxDimension = maxDimension;
    }

    @Override
    public Bitmap filter(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        if (w * h > maxDimension * maxDimension) {
            int newW;
            int newH;
            double aspectRatio = ((double) w) / ((double) h);
            if (w > h) {
                newW = maxDimension;
                newH = (int) (newW / aspectRatio);
            } else {
                newH = maxDimension;
                newW = (int) (aspectRatio * newH);
            }
            bitmap = Bitmap.createScaledBitmap(bitmap, newW, newH, false);
        }
        return bitmap;
    }

    public int getMaxDimension() {
        return maxDimension;
    }

    public void setMaxDimension(int maxDimension) {
        this.maxDimension = maxDimension;
    }
}
