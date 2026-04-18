package view;

import model.*;
import util.LineTool;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.sound.sampled.Line;
import javax.swing.*;


public class RenderPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener
{
    private final int DEFAULT_HEIGHT = 900;
    private final int DEFAULT_WIDTH = 1000;

    private LineTool lineTool;
    private final FrameWork frameWork;

    private int imgWidth = DEFAULT_WIDTH, imgHeight = DEFAULT_HEIGHT;
    private BufferedImage img;

    private float rotX = 0.0f;
    private float rotY = 0.0f;
    private float rotZ = 0.0f;
    private boolean mouseRotating = false;
    private int lastX, lastY;

    private ModelContext modelContext;
    private final Camera camera = new Camera();
    private final RevolutionModel model = new RevolutionModel();

    private final Color defaultColor = Color.white;

    public RenderPanel(JScrollPane scrollPane, FrameWork frameWork)
    {
        scrollPane.setWheelScrollingEnabled(false);
        scrollPane.setViewportView(this);

        scrollPane.setBackground(defaultColor);
        scrollPane.validate();

        setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        this.frameWork = frameWork;
        img = new BufferedImage(DEFAULT_WIDTH, DEFAULT_HEIGHT, BufferedImage.TYPE_INT_RGB);
        modelContext = frameWork.getModelContext();

        Graphics2D g2d = img.createGraphics();
        g2d.setColor(defaultColor);
        g2d.fillRect(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        g2d.dispose();

        lineTool = LineTool.getInstance();
        lineTool.setCanvas(this);
        lineTool.setImg(img);

        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        //clear
        Graphics2D g2 = img.createGraphics();
        g2.setColor(defaultColor);
        g2.fillRect(0, 0, imgWidth, imgHeight);

        List<Line3D> modelLines = model.getLines();
        if (modelLines == null){
            return;
        }

        camera.updateMatrices(getWidth(), getHeight()); // when we changed window
        Point3D cameraPos = camera.getCameraPosition();

        List<Point3D> rotatedPoints = new ArrayList<>();
        List<Float> distances = new ArrayList<>();

        for (Line3D l : modelLines){
            Point3D rotP1 = l.p1.rotate(rotX, rotY, rotZ);
            Point3D rotP2 = l.p2.rotate(rotX, rotY, rotZ);
            rotatedPoints.add(rotP1);
            rotatedPoints.add(rotP2);

            float distanceToP1 = (float) Math.sqrt(Math.pow(rotP1.x - cameraPos.x, 2) + Math.pow(rotP1.y - cameraPos.y, 2) + Math.pow(rotP1.z - cameraPos.z, 2));
            float distanceToP2 = (float) Math.sqrt(Math.pow(rotP2.x - cameraPos.x, 2) + Math.pow(rotP2.y - cameraPos.y, 2) + Math.pow(rotP2.z - cameraPos.z, 2));

            distances.add(distanceToP1);
            distances.add(distanceToP2);
        }

        float minDist = Float.MAX_VALUE, maxDist = -Float.MAX_VALUE;
        for (float d : distances) {
            if (d < minDist) minDist = d;
            if (d > maxDist) maxDist = d;
        }

        int idx = 0;
        for (Line3D l : modelLines) {
            Point3D rotP1 = rotatedPoints.get(idx++);
            Point3D rotP2 = rotatedPoints.get(idx++);
            float d1 = distances.get(idx-2);
            float d2 = distances.get(idx-1);

            // 1 - near, 0 - far
            float depth1 = 1.0f - (d1 - minDist) / (maxDist - minDist);
            float depth2 = 1.0f - (d2 - minDist) / (maxDist - minDist);
            depth1 = Math.min(1.0f, Math.max(0.0f, depth1));
            depth2 = Math.min(1.0f, Math.max(0.0f, depth2));

            Point2D viewP1 = camera.project(rotP1);
            Point2D viewP2 = camera.project(rotP2);
            lineTool.drawLine(viewP1, viewP2, depth1, depth2);
        }

        drawAxes(g2);
        g2.dispose();
        g.drawImage(img, 0, 0, null);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        float delta = e.getWheelRotation() > 0 ? 0.9f : 1.1f;
        float newZn = camera.getZn() * delta;
        camera.setZn(newZn);
        camera.updateMatrices(getWidth(), getHeight());
        repaint();

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        lastX = e.getX();
        lastY = e.getY();
        if (SwingUtilities.isLeftMouseButton(e)) {
            mouseRotating = true;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (mouseRotating) {
            int dx = e.getX() - lastX;
            int dy = e.getY() - lastY;
            rotY += dx * 0.5f;
            rotX += dy * 0.5f;
            rotX %= 360;
            rotY %= 360;
            lastX = e.getX();
            lastY = e.getY();
            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseRotating = false;
    }

    public void resetRotation() {
        rotX = 0;
        rotY = 0;
        rotZ = 0;
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e){}

    @Override
    public void mouseExited(MouseEvent e){}

    @Override
    public void mouseMoved(MouseEvent e){}

    public FrameWork getFrameWork() {
        return frameWork;
    }

    public BufferedImage getImg(){
        return img;
    }

    public void createNewImage(int x, int y) {
        imgWidth = x;
        imgHeight = y;

        BufferedImage newImg = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = newImg.createGraphics();
        g2d.setColor(defaultColor);
        g2d.fillRect(0, 0, imgWidth, imgHeight);
        if (img != null) {
            g2d.drawImage(img, 0, 0, null);
        }
        g2d.dispose();
        img = newImg;
        setPreferredSize(new Dimension(imgWidth, imgHeight));
        lineTool.setImg(img);

        revalidate();
        repaint();
    }

    public void resizeCanvas(int width, int height) {
        if (width <= 0 || height <= 0) return;

        createNewImage(width, height);
        repaint();
    }

    public void updateModel(List<Point2D.Float> bSplinePoints) {
        model.build(bSplinePoints, modelContext.M, modelContext.M1, modelContext.controlPoints.size());
        System.out.println("Revolution model points (" + model.getLines().size() + "): " + model.getLines());
        model.normalize();
        System.out.println("After normalization: " + model.getLines());
        repaint();
    }

    private void drawAxes(Graphics2D g) {
        int startX = getWidth() - 120;
        int startY = 120;
        int axisLength = 60;

        Point3D vecX = new Point3D(1, 0, 0);
        Point3D vecY = new Point3D(0, 1, 0);
        Point3D vecZ = new Point3D(0, 0, 1);

        Point3D rotX_vec = vecX.rotate(rotX, rotY, rotZ);
        Point3D rotY_vec = vecY.rotate(rotX, rotY, rotZ);
        Point3D rotZ_vec = vecZ.rotate(rotX, rotY, rotZ);

        int endX_X = startX + (int) (rotX_vec.x * axisLength);
        int endY_X = startY - (int) (rotX_vec.y * axisLength);
        int endX_Y = startX + (int) (rotY_vec.x * axisLength);
        int endY_Y = startY - (int) (rotY_vec.y * axisLength);
        int endX_Z = startX + (int) (rotZ_vec.x * axisLength);
        int endY_Z = startY - (int) (rotZ_vec.y * axisLength);

        g.setColor(Color.RED);
        g.drawLine(startX, startY, endX_X, endY_X);
        g.drawString("X", endX_X + 5, endY_X - 5);

        g.setColor(Color.GREEN);
        g.drawLine(startX, startY, endX_Y, endY_Y);
        g.drawString("Y", endX_Y + 5, endY_Y - 5);

        g.setColor(Color.BLUE);
        g.drawLine(startX, startY, endX_Z, endY_Z);
        g.drawString("Z", endX_Z + 5, endY_Z - 5);
    }
}