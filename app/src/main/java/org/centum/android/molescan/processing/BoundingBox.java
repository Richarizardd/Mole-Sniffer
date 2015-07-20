package org.centum.android.molescan.processing;

import android.gesture.GesturePoint;
import android.gesture.GestureUtils;
import android.gesture.OrientedBoundingBox;
import android.graphics.Matrix;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Phani on 9/7/2014.
 */
public class BoundingBox {

    private static double squareness;

    public static List<Point> getOrientedBoundingBox(List<Point> hull) {
        ArrayList<GesturePoint> points = new ArrayList<GesturePoint>();
        for (Point p : hull) {
            points.add(new GesturePoint((float) p.x, (float) p.y, 0));
        }

        OrientedBoundingBox boundingBox = GestureUtils.computeOrientedBoundingBox(points);
        List<Point> box = new ArrayList<Point>();

        float[] point = new float[2];
        point[0] = -boundingBox.width / 2;
        point[1] = boundingBox.height / 2;
        Matrix matrix = new Matrix();
        matrix.setRotate(boundingBox.orientation);
        matrix.postTranslate(boundingBox.centerX, boundingBox.centerY);
        matrix.mapPoints(point);
        box.add(new Point((int) point[0], (int) point[1]));

        point[0] = -boundingBox.width / 2;
        point[1] = -boundingBox.height / 2;
        matrix.mapPoints(point);
        box.add(new Point((int) point[0], (int) point[1]));

        point[0] = boundingBox.width / 2;
        point[1] = -boundingBox.height / 2;
        matrix.mapPoints(point);
        box.add(new Point((int) point[0], (int) point[1]));

        point[0] = boundingBox.width / 2;
        point[1] = boundingBox.height / 2;
        matrix.mapPoints(point);
        box.add(new Point((int) point[0], (int) point[1]));

        squareness = boundingBox.squareness;

        return box;
    }

    public static double getSquareness() {
        return squareness;
    }
}
