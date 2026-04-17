package model;

import java.awt.geom.Point2D;
import java.util.List;

public class SurfacePoints {
    private List<Point2D.Float> generatrixPoints;
    private List<Point2D.Float> circlesPoints;

    public List<Point2D.Float> getGeneratrixPoints() {
        return generatrixPoints;
    }

    public void setGeneratrixPoints(List<Point2D.Float> generatrixPoints) {
        this.generatrixPoints = generatrixPoints;
    }

    public List<Point2D.Float> getCirclesPoints() {
        return circlesPoints;
    }

    public void setCirclesPoints(List<Point2D.Float> circlesPoints) {
        this.circlesPoints = circlesPoints;
    }
}
