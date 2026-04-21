package model;

import util.Matrix;

import java.awt.geom.Point2D;

public class Camera {
    private Point3D eye = new Point3D(0, 0, -10); // along z
    private Point3D target = new Point3D(0, 0, 10);
    private Point3D up = new Point3D(0, 1, 0);

    private float zn = 1.0f;
    private float zf = 20.0f;

    private float[][] rotationMatrix = Matrix.getRotationMatrix(0,0,0);
    private float[][] rotateViewProjMatrix;

    private int screenWidth;
    private int screenHeight;

    public void updateMatrices(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        float[][] viewMatrix = buildLookAt();
        float aspect = (float) screenHeight / screenWidth;
        float sh = 1;
        float sw = sh * aspect;
        float[][] projMatrix = buildPerspective(zn, zf, sw, sh);

        float[][] viewProjMatrix = Matrix.multiplyMatrices(projMatrix, viewMatrix);
        rotateViewProjMatrix = Matrix.multiplyMatrices(viewProjMatrix, rotationMatrix);
    }

    // to camera coordinate system
    private float[][] buildLookAt() {
        Point3D k = target.sub(eye).normalize();
        Point3D i = up.cross(k).normalize();
        Point3D j = k.cross(i).normalize();

        float[][] m = new float[4][4];
        m[0][0] = i.x; m[0][1] = i.y; m[0][2] = i.z; m[0][3] = -i.dot(eye);
        m[1][0] = j.x; m[1][1] = j.y; m[1][2] = j.z; m[1][3] = -j.dot(eye);
        m[2][0] = k.x; m[2][1] = k.y; m[2][2] = k.z; m[2][3] = -k.dot(eye);
        m[3][0] = 0; m[3][1] = 0; m[3][2] = 0; m[3][3] = 1;
        return m;
    }

    private float[][] buildPerspective(float zn, float zf, float sw, float sh) {
        float[][] m = new float[4][4];
        m[0][0] = 2 * zn / sw;
        m[1][1] = 2 * zn / sh;
        float a = zf / (zf - zn);
        float b = -zn * zf / (zf - zn);
        m[2][2] = a;
        m[2][3] = b;
        m[3][2] = 1.0f;
        m[3][3] = 0;
        return m;
    }

    public Point2D simpleProject(Point3D p) {
        float x = p.x;
        float y = p.y;

        float screenX = (x + 1.0f) * 0.5f * screenWidth;
        float screenY = (1.0f - (y + 1.0f) * 0.5f) * screenHeight;

        return new Point2D.Float(screenX, screenY);
    }

    public Point2D project(Point3D p) {
        float[] v = {p.x, p.y, p.z, p.w};
        float[] clip = Matrix.multiplyMatrixByVector(rotateViewProjMatrix, v);
        if (clip == null) return null;

        float w = 1.0f / clip[3];
        float xW = clip[0] * w;
        float yW = clip[1] * w;

        float screenX = (xW + 1.0f) * 0.5f * screenWidth;
        float screenY = (1.0f - (yW + 1.0f) * 0.5f) * screenHeight;

        if (Float.isNaN(screenX) || Float.isInfinite(screenX) ||
                Float.isNaN(screenY) || Float.isInfinite(screenY)) {
            return null;
        }
        return new Point2D.Float(screenX, screenY);
    }

    public float getZn() {
        return zn;
    }

    public float getZf() {
        return zf;
    }

    public void setZn(float zn) {
        this.zn = zn;
    }

    public void setZf(float zf) {
        this.zf = zf;
    }

    public Point3D getCameraPosition() {
        return eye;
    }

    public void setRotationMatrix(float[][] rot) {
        this.rotationMatrix = rot;
        if (screenWidth > 0 && screenHeight > 0) {
            updateMatrices(screenWidth, screenHeight);
        }
    }
}