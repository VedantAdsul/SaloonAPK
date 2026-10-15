package com.mountreachsolution.SaloonAPK;

import android.graphics.Matrix;
import android.graphics.PointF;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceContour;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class FaceShapeAnalyzer {

    public static class FaceFeatures {
        public final float ratio, jawAngle, foreheadW, cheekW, jawW;

        FaceFeatures(float ratio, float jawAngle, float foreheadW, float cheekW, float jawW) {
            this.ratio = ratio;
            this.jawAngle = jawAngle;
            this.foreheadW = foreheadW;
            this.cheekW = cheekW;
            this.jawW = jawW;
        }

        public String getDebugInfo() {
            return String.format(Locale.US,
                    "DEBUG INFO:\n" +
                            "Ratio (H/W): %.2f\n" +
                            "Jaw Angle: %.1f°\n" +
                            "Forehead Width: %.0f\n" +
                            "Cheek Width: %.0f\n" +
                            "Jaw Width: %.0f",
                    ratio, jawAngle, foreheadW, cheekW, jawW
            );
        }
    }

    public static FaceFeatures extractFeatures(Face face) {
        FaceContour contour = face.getContour(FaceContour.FACE);
        if (contour == null || contour.getPoints().size() < 20) {
            return null;
        }

        List<PointF> rotatedPoints = getRotatedPoints(face);

        float minX = Float.MAX_VALUE, maxX = Float.MIN_VALUE;
        float minY = Float.MAX_VALUE, maxY = Float.MIN_VALUE;
        for (PointF p : rotatedPoints) {
            minX = Math.min(minX, p.x);
            maxX = Math.max(maxX, p.x);
            minY = Math.min(minY, p.y);
            maxY = Math.max(maxY, p.y);
        }
        float height = maxY - minY;
        float width = maxX - minX;
        float ratio = safeDiv(height, width);
        float foreheadW = getBandWidth(rotatedPoints, minY + 0.15f * height, minY + 0.25f * height);
        float cheekW = getBandWidth(rotatedPoints, minY + 0.45f * height, minY + 0.55f * height);
        float jawW = getBandWidth(rotatedPoints, minY + 0.75f * height, minY + 0.85f * height);
        float jawAngle = getJawAngle(rotatedPoints);

        return new FaceFeatures(ratio, jawAngle, foreheadW, cheekW, jawW);
    }

    public static String classifyFromAverageFeatures(FaceFeatures features) {
        if (features.cheekW > features.foreheadW && features.cheekW > features.jawW) {
            if (features.jawAngle < 140) return "Diamond";
            else return (features.ratio > 1.1) ? "Oval" : "Round";
        } else if (Math.abs(features.foreheadW - features.cheekW) < 0.1 * (features.foreheadW) && Math.abs(features.cheekW - features.jawW) < 0.1 * (features.cheekW)) {
            if (features.ratio > 1.4) return "Rectangle";
            else return (features.jawAngle < 135) ? "Square" : "Round";
        } else if (features.foreheadW > features.cheekW && features.foreheadW > features.jawW) {
            return "Heart";
        } else {
            return "Oval";
        }
    }

    private static List<PointF> getRotatedPoints(Face face) {
        List<PointF> originalPoints = face.getContour(FaceContour.FACE).getPoints();
        List<PointF> rotatedPoints = new ArrayList<>();
        float headAngleZ = face.getHeadEulerAngleZ();
        Matrix rotationMatrix = new Matrix();
        rotationMatrix.setRotate(-headAngleZ, face.getBoundingBox().centerX(), face.getBoundingBox().centerY());
        for (PointF point : originalPoints) {
            float[] pts = new float[]{point.x, point.y};
            rotationMatrix.mapPoints(pts);
            rotatedPoints.add(new PointF(pts[0], pts[1]));
        }
        return rotatedPoints;
    }

    private static float getBandWidth(List<PointF> pts, float yMin, float yMax) {
        float minX = Float.MAX_VALUE, maxX = Float.MIN_VALUE;
        boolean pointsFound = false;
        for (PointF p : pts) {
            if (p.y >= yMin && p.y <= yMax) {
                minX = Math.min(minX, p.x);
                maxX = Math.max(maxX, p.x);
                pointsFound = true;
            }
        }
        return pointsFound ? maxX - minX : 0;
    }

    private static float getJawAngle(List<PointF> pts) {
        PointF chin = null, leftJaw = null, rightJaw = null;
        float maxY = Float.MIN_VALUE;
        for (PointF p : pts) {
            if (p.y > maxY) {
                maxY = p.y;
                chin = p;
            }
        }
        if (chin == null) return 180;

        float lowerFaceThreshold = maxY - (maxY - Collections.min(pts, (a, b) -> Float.compare(a.y, b.y)).y) * 0.4f;
        float minX = Float.MAX_VALUE, maxX = Float.MIN_VALUE;
        for (PointF p : pts) {
            if (p.y > lowerFaceThreshold) {
                if (p.x < minX) { minX = p.x; leftJaw = p; }
                if (p.x > maxX) { maxX = p.x; rightJaw = p; }
            }
        }
        if (leftJaw == null || rightJaw == null) return 180;
        PointF v1 = new PointF(leftJaw.x - chin.x, leftJaw.y - chin.y);
        PointF v2 = new PointF(rightJaw.x - chin.x, rightJaw.y - chin.y);
        double dotProduct = v1.x * v2.x + v1.y * v2.y;
        double mag1 = Math.sqrt(v1.x * v1.x + v1.y * v1.y);
        double mag2 = Math.sqrt(v2.x * v2.x + v2.y * v2.y);
        if (mag1 == 0 || mag2 == 0) return 180;
        double angleRad = Math.acos(dotProduct / (mag1 * mag2));
        return (float) Math.toDegrees(angleRad);
    }

    private static float safeDiv(float a, float b) {
        return b == 0 ? 0 : (a / b);
    }
}