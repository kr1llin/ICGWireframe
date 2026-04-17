package model;

import util.Matrix;

public class Point3D {
    public float x, y, z, w;

    public Point3D(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Point3D(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = 1.0f;
    }

    public Point3D rotate(float rotX, float rotY, float rotZ) {
        float radX = (float) Math.toRadians(rotX);
        float radY = (float) Math.toRadians(rotY);
        float radZ = (float) Math.toRadians(rotZ);

        float cosX = (float) Math.cos(radX), sinX = (float) Math.sin(radX);
        float cosY = (float) Math.cos(radY), sinY = (float) Math.sin(radY);
        float cosZ = (float) Math.cos(radZ), sinZ = (float) Math.sin(radZ);

        float[][] R = {
                {cosY*cosZ, cosX*sinZ + sinX*sinY*cosZ, sinX*sinZ - cosX*sinY*cosZ, 0},
                {-cosY*sinZ, cosX*cosZ - sinX*sinY*sinZ, sinX*cosZ + cosX*sinY*sinZ, 0},
                {sinY, -sinX*cosY, cosX*cosY, 0},
                {0,0,0,1}
        };

        float[] v = {x, y, z, w};
        float[] rotated = Matrix.multiplyVectorByMatrix(v, R);
        if (rotated == null) return this;
        return new Point3D(rotated[0], rotated[1], rotated[2], rotated[3]);
    }

    public Point3D sub(Point3D other) {
        Point3D res = new Point3D(0,0,0,0);
        res.x = x - other.x;
        res.y = y - other.y;
        res.z = z - other.z;
        res.w = w - other.w;
        return res;
    }

    public Point3D cross(Point3D other) {
        Point3D res = new Point3D(0,0,0,0);
        res.x = y*other.z - z*other.y;
        res.y = -(x*other.z - z*other.x);
        res.z = x*other.y - y*other.x;
        res.w = 1;
        return res;
    }

    public float dot(Point3D other) {
        return x*other.x + y*other.y + z*other.z + w*other.w;
    }

    public Point3D normalize(){
        float length = (float) Math.sqrt(x*x + y*y + z*z);
        if (length < 1e-8) return new Point3D(0, 0, 0);
        return new Point3D(x / length, y / length, z / length, 0);
    }

    @Override
    public String toString(){
        return "[" + x + ", " + y + ", " + z + "]";
    }
}
