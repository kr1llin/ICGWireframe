package model;

public class Line3D {
    public Point3D p1, p2;

    public Line3D(Point3D p1, Point3D p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public String toString(){
        return "p1: " + p1 + ", p2: " + p2;
    }
}