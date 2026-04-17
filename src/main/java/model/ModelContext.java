package model;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ModelContext implements Serializable {
    public int N = 2;
    public int M = 3;
    public int M1 = 1;

    public List<Point2D.Float> controlPoints = new ArrayList<>();

    public ModelContext() {
        controlPoints.add(new Point2D.Float(0.0f, 1f));
        controlPoints.add(new Point2D.Float(0.1f, 0.9f));
        controlPoints.add(new Point2D.Float(0.9f, 0.1f));
        controlPoints.add(new Point2D.Float(1f, 0.0f));
    }
}
