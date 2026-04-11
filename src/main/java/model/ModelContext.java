package model;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ModelContext implements Serializable {
    public int N = 20;
    public int M = 24;
    public int M1 = 1;

    // Control points (U horizontal, V vertical)
    public List<Point2D.Float> controlPoints = new ArrayList<>();

    public ModelContext() {
        controlPoints.add(new Point2D.Float(0.0f, 0.5f));
        controlPoints.add(new Point2D.Float(0.3f, 0.6f));
        controlPoints.add(new Point2D.Float(0.5f, 0.8f));
        controlPoints.add(new Point2D.Float(0.7f, 0.9f));
        controlPoints.add(new Point2D.Float(1.0f, 0.8f));
        controlPoints.add(new Point2D.Float(1.0f, 0.3f));
        controlPoints.add(new Point2D.Float(0.8f, 0.0f));
        controlPoints.add(new Point2D.Float(0.5f, -0.2f));
        controlPoints.add(new Point2D.Float(0.2f, -0.3f));
        controlPoints.add(new Point2D.Float(0.0f, -0.2f));
    }
}
