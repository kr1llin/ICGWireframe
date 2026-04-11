package util;


import view.ImagePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class LineTool {
    private static LineTool instance;
    private int x0, y0 = -1;
    private ImagePanel canvas;
    private BufferedImage img;

    private static int FAR_COLOR_RGB = Color.GREEN.getRGB();
    private static int NEAR_COLOR_RGB = Color.BLACK.getRGB();

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

    public void setCanvas(ImagePanel canvas) {
        this.canvas = canvas;
    }

    public void drawLine(int x1, int y1, int x2, int y2, int z1, int z2) {
        // if steep
        if (Math.abs(y2 - y1) > Math.abs(x2 - x1)) {
            drawLineV(x1, y1, x2, y2, z1, z2);
        } else {
            drawLineH(x1, y1, x2, y2, z1, z2);
        }
    }


    public void drawLineH(int x1, int y1, int x2, int y2, int z1, int z2){
        // just mirror coordinates
        if (x1 > x2) {
            int tmp = x1;
            x1 = x2;
            x2 = tmp;

            tmp = y1;
            y1 = y2;
            y2 = tmp;
        }

        int dx = x2 - x1;
        int dy = y2 - y1;

        int stepY = (dy > 0)? 1 : -1;
        dy *= stepY;

        float stepZ = Math.abs(z2 - z1) / (float)(dx + 1);

        int err = -dx;
        int y = y1;
        float z = Math.min(z1, z2);
        for (int i = 0; i < dx + 1; i++){
            err += 2*dy;
            if (err > 0){
                err -= 2*dx;
                y += stepY;
                z += stepZ;
            }
            putPixel(x1 + i, y, z);
        }
    }

    public void drawLineV(int x1, int y1, int x2, int y2, int z1, int z2){
        if (y1 > y2) {
            int tmp = y1;
            y1 = y2;
            y2 = tmp;

            tmp = x1;
            x1 = x2;
            x2 = tmp;
        }

        int dx = x2 - x1;
        int dy = y2 - y1;

        int stepX = (dx > 0)? 1 : -1;
        dx *= stepX;

        float stepZ = Math.abs(z2 - z1) / (float)(dx + 1);

        int err = -dy;
        int x = x1;
        float z = Math.min(z1,z2);

        for (int i = 0; i < dy + 1; i++){
            err += 2*dx;
            if (err > 0){
                err -= 2*dy;
                x += stepX;
                z += stepZ;
            }
            putPixel(x, y1 + i, z);
        }
    }

    // z is for color (should be normilized from box (-1, 1))
    public void putPixel(int x, int y, float z){
        if (x >= canvas.getWidth() || x < 0 || y >= canvas.getHeight() || y < 0){
            return;
        }
        int rgb = (int) (((1 - z)*NEAR_COLOR_RGB + z*FAR_COLOR_RGB)*255);
        img.setRGB(x, y, rgb);
    }
}

