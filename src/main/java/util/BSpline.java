package util;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class BSpline {
    private static final float[][] M = {{-1,3,-3,1}, {3,-6,3,0}, {-3,0,3,0}, {1,4,1,0}};

    public static List<Point2D.Float> computeCurve(List<Point2D.Float> controlPoints, int N){
        int K = controlPoints.size();
        if (K < 4) {
            return null;
        }
        List<Point2D.Float> bSpline = new ArrayList<>();
        float stepT = 1f / N; // step of approximation

        // i - segment of bspline
        for (int i = 1; i <= K - 3; i++){
            float[] Giu = getVectorGU(controlPoints, i);
            float[] Giv = getVectorGV(controlPoints, i);
            for (int j = 0; j <= N; j++){
                float t = stepT*j;
                float[] TM = Matrix.multiplyVectorByMatrix(getVectorT(t), M);

                Point2D.Float newPoint = new Point2D.Float();
                assert TM != null;
                newPoint.x = Matrix.multiplyVectorByVector(TM, Giu) / 6.0f;
                newPoint.y = Matrix.multiplyVectorByVector(TM, Giv) / 6.0f;

                bSpline.add(newPoint);
            }
        }

        return bSpline;
    }

    private static float[] getVectorT(float t) {
        return new float[]{(float) Math.pow(t, 3), t * t, t, 1};
    }

    private static float[] getVectorGU(List<Point2D.Float> controlPoints, int i){
        if (i < 1 || i + 2> controlPoints.size() ) {
            return null;
        }

        float[] Gi = new float[4];
        Gi[0] = controlPoints.get(i - 1).x;
        Gi[1] = controlPoints.get(i).x;
        Gi[2] = controlPoints.get(i + 1).x;
        Gi[3] = controlPoints.get(i + 2).x;
        return Gi;
    }
    private static float[] getVectorGV(List<Point2D.Float> controlPoints, int i){
        if (i < 1 || i + 2> controlPoints.size() ) {
            return null;
        }

        float[] Gi = new float[4];
        Gi[0] = controlPoints.get(i - 1).y;
        Gi[1] = controlPoints.get(i).y;
        Gi[2] = controlPoints.get(i + 1).y;
        Gi[3] = controlPoints.get(i + 2).y;
        return Gi;
    }
}
