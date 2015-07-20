package org.centum.android.molescan.processing.filter;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by Phani on 9/6/2014.
 */
public class ThresholdFilter implements Filter {

    private int threshold = 128;

    public ThresholdFilter(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public Bitmap filter(Bitmap bitmap) {
        Bitmap bwBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        for (int x = 0; x < bitmap.getWidth(); x++) {
            for (int y = 0; y < bitmap.getHeight(); y++) {
                bwBitmap.setPixel(x, y, (bitmap.getPixel(x, y) & 255) > threshold ? Color.WHITE : Color.BLACK);
            }
        }

        return bwBitmap;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
}
