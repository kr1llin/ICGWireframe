package model;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ModelContext implements Serializable {
    // yes, i'm too lazy to make getters for this
    public int N = 2;
    public int M = 3;
    public int M1 = 1;
    public float rotX = 0.0f;
    public float rotY = 0.0f;
    public float rotZ = 0.0f;
    public float Zn = 1.0f;

    public List<Point2D.Float> controlPoints = new ArrayList<>();

    public ModelContext(int n, int m, int m1, float rotx, float roty, float rotz, float zn, List<Point2D.Float> controlPoints) {
        N = n;
        M = m;
        M1 = m1;
        rotX = rotx;
        rotY = roty;
        rotZ = rotz;
        Zn = zn;
        this.controlPoints = controlPoints;
    }

    public ModelContext() {
        controlPoints.add(new Point2D.Float(0.0f, 1f));
        controlPoints.add(new Point2D.Float(0.1f, 0.9f));
        controlPoints.add(new Point2D.Float(0.9f, 0.1f));
        controlPoints.add(new Point2D.Float(1f, 0.0f));
    }

    @Override
    public String toString(){
        return "N = " + N + ", M = " + M + ", M1 = " + M1 +", rotX = " + rotX
                + ", rotY = " + rotY + ", rotZ = " + rotZ + ", Zn = " + Zn;
    }
}
