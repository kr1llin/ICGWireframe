package model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class RevolutionModel {
    private List<Line3D> modelLines = new ArrayList<>();

    public void build(List<Point2D.Float> bSplinePoints, int M, int M1, int K){
        modelLines.clear();
        int bSplineNum = bSplinePoints.size(); // N*(K-3)+1

        Point3D[][] surface = new Point3D[bSplineNum][M];
        for (int i = 0; i < bSplineNum; i++){
            Point2D.Float p = bSplinePoints.get(i);
            float radius = p.x;
            float z = p.y;

            // rotate
            for (int j = 0; j < M; j++){
                float angle = (float) Math.toRadians(j*(360f/M));
                float x = (float) (radius*Math.cos(angle));
                float y = (float) (radius*Math.sin(angle));
                surface[i][j] = new Point3D(x,y,z);
            }
        }

        for (int i = 0; i < bSplineNum - 1; i++){
            for (int j = 0; j < M; j++){
                modelLines.add(new Line3D(surface[i][j], surface[i+1][j]));
            }
        }

        int nSeg = K - 3;
        int nPerSeg = (bSplineNum ) / nSeg;

        for (int seg = 0; seg <= nSeg; seg++) {
            int idx = seg * nPerSeg;
            if (idx >= bSplineNum) idx = bSplineNum - 1;

            Point3D p = surface[idx][0];
            float radius = (float) Math.sqrt(p.x*p.x + p.y*p.y);

            for (int j = 0; j < M; j++) {
                float initAngle = (float) (j*Math.PI*2) / M;
                float lastAngle = (float) ((j+1)*Math.PI*2) / M;
                for (int s = 0; s < M1; s++) {
                    float s1 = (float) s / M1;
                    float s2 = (float) (s+1) / M1;
                    float angle1 = initAngle + s1*(lastAngle - initAngle);
                    float angle2 = initAngle + s2*(lastAngle - initAngle);

                    Point3D a = new Point3D((float) (radius * Math.cos(angle1)), (float) (radius * Math.sin(angle1)), p.z);
                    Point3D b = new Point3D((float) (radius * Math.cos(angle2)), (float) (radius * Math.sin(angle2)), p.z);
                    modelLines.add(new Line3D(a, b));
                }
            }
        }
    }

    public List<Line3D> getLines() {
        return modelLines;
    }

    private static class Bounds {
        float minX = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxY = -Float.MAX_VALUE;
        float minZ = Float.MAX_VALUE;
        float maxZ = -Float.MAX_VALUE;

        void combine(Point3D p) {
            minX = Math.min(minX, p.x);
            maxX = Math.max(maxX, p.x);
            minY = Math.min(minY, p.y);
            maxY = Math.max(maxY, p.y);
            minZ = Math.min(minZ, p.z);
            maxZ = Math.max(maxZ, p.z);
        }

        void combine(Bounds other) {
            minX = Math.min(minX, other.minX);
            maxX = Math.max(maxX, other.maxX);
            minY = Math.min(minY, other.minY);
            maxY = Math.max(maxY, other.maxY);
            minZ = Math.min(minZ, other.minZ);
            maxZ = Math.max(maxZ, other.maxZ);
        }

        static Bounds accumulate() {
            return new Bounds();
        }
    }

    public void normalize() {
        if (modelLines.isEmpty()) return;

        Bounds bounds = modelLines.stream().flatMap(l -> Stream.of(l.p1, l.p2)).
                        collect(Bounds::accumulate, Bounds::combine, Bounds::combine);

        float centerX = (float) ((bounds.minX + bounds.maxX) / 2.0);
        float centerY = (float) ((bounds.minY + bounds.maxY) / 2.0);
        float centerZ = (float) ((bounds.minZ + bounds.maxZ) / 2.0);

        float sizeX = bounds.maxX - bounds.minX;
        float sizeY = bounds.maxY - bounds.minY;
        float sizeZ = bounds.maxZ - bounds.minZ;
        float maxSize = Math.max(sizeX, Math.max(sizeY, sizeZ));
        if (maxSize == 0) maxSize = 1.0f;

        float scale = (float) (2.0 / maxSize);

        for (Line3D line : modelLines) {
            line.p1 = transform(line.p1, centerX, centerY, centerZ, scale);
            line.p2 = transform(line.p2, centerX, centerY, centerZ, scale);
        }

//        modelLines.add(new Line3D(transform(new Point3D(0,0,0), centerX, centerY, centerZ, scale), transform(new Point3D(0,0,50), centerX, centerY, centerZ, scale)));

        System.out.println("Normalized bounds: x=[" + bounds.minX + "," + bounds.maxX + "] -> [" + (centerX - maxSize/2) + "," + (centerX + maxSize/2) + "]");
        System.out.println("Normalized bounds: y=[" + bounds.minY + "," + bounds.maxY + "] -> [" + (centerY - maxSize/2) + "," + (centerY + maxSize/2) + "]");
        System.out.println("Normalized bounds: z=[" + bounds.minZ + "," + bounds.maxZ + "] -> [" + (centerZ - maxSize/2) + "," + (centerZ + maxSize/2) + "]");
    }

    private static Point3D transform(Point3D p, float cx, float cy, float cz, float scale) {
        return new Point3D(
                (p.x - cx) * scale,
                (p.y - cy) * scale,
                (p.z - cz) * scale
        );
    }
}
