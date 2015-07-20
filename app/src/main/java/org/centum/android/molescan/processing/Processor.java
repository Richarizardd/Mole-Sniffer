package org.centum.android.molescan.processing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import com.sromku.polygon.PointFloat;
import com.sromku.polygon.Polygon;

import org.centum.android.molescan.processing.filter.ContrastFilter;
import org.centum.android.molescan.processing.filter.GrayScaleFilter;
import org.centum.android.molescan.processing.filter.ScaleFilter;
import org.centum.android.molescan.processing.filter.ThresholdFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Phani on 9/6/2014.
 */
public class Processor {

    private static int MAX_IMAGE_DIMENSION = 500;
    private static int MAX_IMAGE_SIZE = MAX_IMAGE_DIMENSION * MAX_IMAGE_DIMENSION;

    private static ScaleFilter scaleFilter = new ScaleFilter(500);
    private static GrayScaleFilter grayScaleFilter = new GrayScaleFilter();
    private static ContrastFilter contrastFilter = new ContrastFilter(1.85f);
    private static ThresholdFilter thresholdFilter = new ThresholdFilter(170);

    public static ProcessingResults process(Bitmap bitmap) {
        ProcessingResults results = new ProcessingResults();
        bitmap = scaleFilter.filter(bitmap);
        results.setOriginalBitmap(bitmap);

        Bitmap processedBitmap = grayScaleFilter.filter(bitmap);
        processedBitmap = contrastFilter.filter(processedBitmap);
        processedBitmap = thresholdFilter.filter(processedBitmap);

        results.setConvexHull(QuickHull.quickHull(processedBitmap, Color.BLACK));
        results.setBoundingBox(BoundingBox.getOrientedBoundingBox(results.getConvexHull()));
        results.setSizePass(BoundingBox.getSquareness() > .7);

        Polygon.Builder polygonBuilder = new Polygon.Builder();
        for (Point p : results.getBoundingBox()) {
            polygonBuilder.addVertex(new PointFloat(p.x, p.y));
        }
        Polygon poly = polygonBuilder.build();

        results.setColorPass(processColour(bitmap, poly));
        results.setRenderedBitmap(drawHull(drawBoundingBox(results.getOriginalBitmap(), results.getBoundingBox()), results.getConvexHull()));
        results.setProcessedBitmap(processedBitmap);

        return results;
    }

    private static boolean processColour(Bitmap bitmap, Polygon p) {
        int width, height;
        height = bitmap.getHeight();
        width = bitmap.getWidth();

        ArrayList<Integer> Red_Array = new ArrayList<Integer>();
        ArrayList<Integer> Green_Array = new ArrayList<Integer>();
        ArrayList<Integer> Blue_Array = new ArrayList<Integer>();

        // create a mutable empty bitmap
        Bitmap bmpMole = Bitmap.createBitmap(width, height, bitmap.getConfig());

        // create a canvas so that we can draw the bmOut Bitmap from source bitmap
        Canvas c = new Canvas();
        c.setBitmap(bmpMole);

        // color information
        int R, G, B, pixel;
        double Red_Sum = 0;
        double Green_Sum = 0;
        double Blue_Sum = 0;

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                if (p.contains(new PointFloat(x, y))) {
                    pixel = bitmap.getPixel(x, y);
                    R = Color.red(pixel);
                    G = Color.green(pixel);
                    B = Color.blue(pixel);

                    Red_Sum += R;
                    Green_Sum += G;
                    Blue_Sum += B;

                    Red_Array.add(R);
                    Green_Array.add(G);
                    Blue_Array.add(B);

                    bmpMole.setPixel(x, y, Color.rgb(R, G, B));
                }//end if
            }//end for
        }//end for

        double Red_Ave = Red_Sum / Red_Array.size();
        double Green_Ave = Green_Sum / Green_Array.size();
        double Blue_Ave = Blue_Sum / Blue_Array.size();

        double Red_Dif_Sq = 0;
        double Green_Dif_Sq = 0;
        double Blue_Dif_Sq = 0;

        for (int i = 0; i < Red_Array.size(); i++) {
            Red_Dif_Sq += Math.pow((Red_Ave - Red_Array.get(i)), 2);
            Green_Dif_Sq += Math.pow((Green_Ave - Green_Array.get(i)), 2);
            Blue_Dif_Sq += Math.pow((Blue_Ave - Blue_Array.get(i)), 2);
        }
        double Red_StDev = Math.pow(Red_Dif_Sq / (Red_Array.size() - 1), 0.5);
        double Green_StDev = Math.pow(Green_Dif_Sq / (Green_Array.size() - 1), 0.5);
        double Blue_StDev = Math.pow(Blue_Dif_Sq / (Blue_Array.size() - 1), 0.5);

        System.out.println("These are the StDEVs. R = " + Red_StDev + "  G = " + Green_StDev + " B = " + Blue_StDev);

        if (Red_StDev <= 39 && Green_StDev > 40 && Blue_StDev > 36) {
            return true;
        }
        return false;
    }

    private static Bitmap drawBoundingBox(Bitmap source, List<Point> points) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(3f);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);

        return renderPolygon(source, points, paint);
    }

    private static Bitmap drawHull(Bitmap source, List<Point> points) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(3f);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);

        return renderPolygon(source, points, paint);
    }

    private static Bitmap renderPolygon(Bitmap source, List<Point> points, Paint paint) {
        Bitmap rendered = Bitmap.createBitmap(source.getWidth(), source.getHeight(), source.getConfig());
        Canvas canvas = new Canvas(rendered);

        canvas.drawBitmap(source, 0, 0, null);
        for (int i = 0; i < points.size() - 1; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);
            canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
        }

        Point p1 = points.get(0);
        Point p2 = points.get(points.size() - 1);
        canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);

        return rendered;
    }
}
