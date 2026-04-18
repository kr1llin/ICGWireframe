package util;

import model.Point3D;
import view.RenderPanel;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class LineTool {
    private static LineTool instance;
    private int x0, y0 = -1;
    private RenderPanel canvas;
    private BufferedImage img;

    private static int FAR_COLOR_RGB = 0;
    private static int NEAR_COLOR_RGB = Color.GREEN.getRGB();

    private LineTool(){}

    public static LineTool getInstance(){
        if (instance == null){
            instance = new LineTool();
        }
        return instance;
    }

    public void redraw() {
        canvas.repaint();
    }

    public void setImg(BufferedImage img) {
        this.img = img;
    }

    public void setCanvas(RenderPanel canvas) {
        this.canvas = canvas;
    }

    public void drawLine(Point2D p1, Point2D p2, float z1, float z2) {
        if (p1 == null || p2 == null) return;
        int x1 = (int) p1.getX();
        int x2 = (int) p2.getX();
        int y1 = (int) p1.getY();
        int y2 = (int) p2.getY();

        // if steep
        if (Math.abs(y2 - y1) > Math.abs(x2 - x1)) {
            drawLineV(x1, y1, x2, y2, z1, z2);
        } else {
            drawLineH(x1, y1, x2, y2, z1, z2);
        }
    }


    public void drawLineH(int x1, int y1, int x2, int y2, float z1, float z2) {
        // just mirror coordinates
        if (x1 > x2) {
            int tmp = x1; x1 = x2; x2 = tmp;
            tmp = y1; y1 = y2; y2 = tmp;
            float tmpZ = z1; z1 = z2; z2 = tmpZ;
        }
        int dx = x2 - x1;
        int dy = y2 - y1;
        int stepY = (dy > 0) ? 1 : -1;
        dy = Math.abs(dy);
        float stepZ = (z2 - z1) / dx;
        int err = -dx;
        int y = y1;
        float z = z1;
        for (int i = 0; i <= dx; i++) {
            putPixel(x1 + i, y, z);
            err += 2 * dy;
            if (err > 0) {
                err -= 2 * dx;
                y += stepY;
            }
            z += stepZ;
        }
    }

    public void drawLineV(int x1, int y1, int x2, int y2, float z1, float z2) {
        if (y1 > y2) {
            int tmp = y1; y1 = y2; y2 = tmp;
            tmp = x1; x1 = x2; x2 = tmp;
            float tmpZ = z1; z1 = z2; z2 = tmpZ;
        }
        int dx = x2 - x1;
        int dy = y2 - y1;
        int stepX = (dx > 0) ? 1 : -1;
        dx = Math.abs(dx);
        float stepZ = (z2 - z1) / dy;
        int err = -dy;
        int x = x1;
        float z = z1;
        for (int i = 0; i <= dy; i++) {
            putPixel(x, y1 + i, z);
            err += 2 * dx;
            if (err > 0) {
                err -= 2 * dy;
                x += stepX;
            }
            z += stepZ;
        }
    }

    // z is for color (should be normilized from box (-1, 1))
    public void putPixel(int x, int y, float z){
        if (x >= canvas.getWidth() || x < 0 || y >= canvas.getHeight() || y < 0){
            return;
        }
        int r1 = (FAR_COLOR_RGB >> 16) & 0xFF;
        int r2 = (FAR_COLOR_RGB >> 16) & 0xFF;
        int g1 = (FAR_COLOR_RGB >> 8) & 0xFF;
        int g2 = (NEAR_COLOR_RGB >> 8) & 0xFF;
        int b1 = (FAR_COLOR_RGB) & 0xFF;
        int b2 = (NEAR_COLOR_RGB) & 0xFF;
        int rgb = (int) (r1*(1-z) + z * r2) << 16 |
                (int) (g1*(1-z) + z * g2) << 8 |
                (int) (b1*(1-z) + z * b2);
        img.setRGB(x, y, rgb);
    }
}

