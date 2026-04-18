package view;

import util.BSpline;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.logging.Logger;

public class ControlPointCanvas extends JPanel {
    private final List<Point2D.Float> controlPoints;
    private List<Point2D.Float> bSplinePoints;
    private final BSplineEditor parentEditor;

    private double viewCenterX = 0.0;   // world coordinates at canvas center
    private double viewCenterY = 0.0;
    private double scale = 200.0;       // pixels per world unit
    private final double zoomFactor = 1.1;

    private Point2D.Float draggedPoint = null;
    private int dragIndex = -1;
    private Point lastMousePos;
    private boolean panning = false;

    private static final int POINT_RADIUS = 6;
    private static final Color POINT_COLOR = Color.RED;
    private static final Color POLYLINE_COLOR = Color.RED;
    private static final Color BSPLINE_COLOR = Color.BLACK;
    private static final Color AXIS_COLOR = Color.GRAY;

    public ControlPointCanvas(List<Point2D.Float> controlPoints, BSplineEditor parent) {
        this.controlPoints = controlPoints;
        this.parentEditor = parent;

        setBackground(Color.WHITE);
        setFocusable(true);

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
                lastMousePos = e.getPoint();

                if (SwingUtilities.isLeftMouseButton(e) && !panning) {
                        dragIndex = findPointAt(e.getX(), e.getY());
                        if (dragIndex != -1) {
                            draggedPoint = controlPoints.get(dragIndex);
                        } else {
                            addPointAt(e.getX(), e.getY());
                        }
                }
                else if (SwingUtilities.isRightMouseButton(e) && !panning) {
                    int idx = findPointAt(e.getX(), e.getY());
                    if (idx != -1) {
                        removePoint(idx);
                    }
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (draggedPoint != null && dragIndex != -1) {
                    Point2D.Float newWorld = screenToWorld(e.getX(), e.getY());
                    draggedPoint.setLocation(newWorld);
                    repaint();
                    parentEditor.onPointsChanged();
                } else if (panning) {
                    double dx = (lastMousePos.x - e.getX()) / scale;
                    double dy = (lastMousePos.y - e.getY()) / scale;
                    viewCenterX -= dx;
                    viewCenterY += dy;
                    lastMousePos = e.getPoint();
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                draggedPoint = null;
                dragIndex = -1;
            }
        };
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);

        addMouseWheelListener(e -> {
            Point mouse = e.getPoint();
            Point2D.Float before = screenToWorld(mouse.x, mouse.y);

            if (e.getWheelRotation() < 0) {
                scale *= zoomFactor;
            } else {
                scale /= zoomFactor;
            }

            Point2D.Float after = screenToWorld(mouse.x, mouse.y);

            viewCenterX += (before.x - after.x);
            viewCenterY += (before.y - after.y);
            repaint();
        });

        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    panning = true;
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                }
            }

            @Override
            public void keyReleased(KeyEvent e){
                super.keyReleased(e);
                if (e.getKeyCode() == KeyEvent.VK_CONTROL){
                    panning = false;
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            }
        };
        addKeyListener(keyAdapter);
    }

    private void addPointAt(int screenX, int screenY) {
        Point2D.Float newPoint = screenToWorld(screenX, screenY);
        int insertIndex = findInsertionIndex(screenX, screenY);
        controlPoints.add(insertIndex, newPoint);
        repaint();
        parentEditor.onPointsChanged();
        parentEditor.updatePointCountLabel();
    }

    private int findInsertionIndex(int screenX, int screenY) {
        if (controlPoints.size() < 2) {
            return controlPoints.size();
        }
        Point mousePoint = new Point(screenX, screenY);
        int bestSegment = 0;
        double minDist = Double.MAX_VALUE;

        for (int i = 0; i < controlPoints.size() - 1; i++) {
            Point p1 = worldToScreen(controlPoints.get(i).x, controlPoints.get(i).y);
            Point p2 = worldToScreen(controlPoints.get(i + 1).x, controlPoints.get(i + 1).y);
            double dist = distanceToSegment(mousePoint, p1, p2);
            if (dist < minDist) {
                minDist = dist;
                bestSegment = i;
            }
        }
        return bestSegment + 1;
    }

    private double distanceToSegment(Point p, Point a, Point b) {
        double ax = b.x - a.x;
        double ay = b.y - a.y;
        double len2 = ax * ax + ay * ay;
        if (len2 == 0) return Math.hypot(p.x - a.x, p.y - a.y);
        double t = ((p.x - a.x) * ax + (p.y - a.y) * ay) / len2;
        if (t < 0) t = 0;
        if (t > 1) t = 1;
        double projX = a.x + t * ax;
        double projY = a.y + t * ay;
        return Math.hypot(p.x - projX, p.y - projY);
    }

    private void removePoint(int index) {
        if (controlPoints.size() <= 4) {
            JOptionPane.showMessageDialog(this,
                    "At least 4 control controlPoints are required.",
                    "Cannot delete", JOptionPane.WARNING_MESSAGE);
            return;
        }
        controlPoints.remove(index);
        repaint();
        parentEditor.onPointsChanged();
        parentEditor.updatePointCountLabel();
    }

    private int findPointAt(int screenX, int screenY) {
        final int threshold = 10;
        for (int i = 0; i < controlPoints.size(); i++) {
            Point2D.Float p = controlPoints.get(i);
            Point screen = worldToScreen(p.x, p.y);
            if (Math.hypot(screen.x - screenX, screen.y - screenY) <= threshold) {
                return i;
            }
        }
        return -1;
    }

    private Point worldToScreen(double u, double v) {
        int w = getWidth();
        int h = getHeight();
        int cx = w / 2;
        int cy = h / 2;
        int x = (int) (cx + (u - viewCenterX) * scale);
        int y = (int) (cy - (v - viewCenterY) * scale);
        return new Point(x, y);
    }

    private Point2D.Float screenToWorld(int x, int y) {
        int w = getWidth();
        int h = getHeight();
        int cx = w / 2;
        int cy = h / 2;
        float u = (float) (viewCenterX + (x - cx) / scale);
        float v = (float) (viewCenterY - (y - cy) / scale);
        return new Point2D.Float(u, v);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawAxes(g2);
        drawControlPolygon(g2);
        drawBSplineCurve(g2);
        drawControlPoints(g2);
    }

    private void drawAxes(Graphics2D g2) {
        g2.setColor(AXIS_COLOR);

        // U axis
        Point left = worldToScreen(-100, 0);
        Point right = worldToScreen(100, 0);
        g2.drawLine(left.x, left.y, right.x, right.y);

        // V axis
        Point top = worldToScreen(0, 100);
        Point bottom = worldToScreen(0, -100);
        g2.drawLine(top.x, top.y, bottom.x, bottom.y);
    }

    private void drawControlPolygon(Graphics2D g2) {
        if (controlPoints.size() < 2) return;
        g2.setColor(POLYLINE_COLOR);
        Point prev = worldToScreen(controlPoints.getFirst().x, controlPoints.getFirst().y);

        // this is slow
        for (int i = 1; i < controlPoints.size(); i++) {
            Point cur = worldToScreen(controlPoints.get(i).x, controlPoints.get(i).y);
            g2.drawLine(prev.x, prev.y, cur.x, cur.y);
            prev = cur;
        }
    }

    public List<Point2D.Float> computCurrentBSplineCurve(){
        return BSpline.computeCurve(controlPoints, parentEditor.getN());
    }

    private void drawBSplineCurve(Graphics2D g2) {
        bSplinePoints = BSpline.computeCurve(controlPoints, parentEditor.getN());
        if (bSplinePoints == null) {
            System.out.println("Bspline is null!");
            return;
        }

        g2.setColor(BSPLINE_COLOR);
        Point2D.Float first = bSplinePoints.getFirst();
        Point prev = worldToScreen(first.x, first.y);
        for (int i = 1; i < bSplinePoints.size(); i++) {
            Point cur = worldToScreen(bSplinePoints.get(i).x, bSplinePoints.get(i).y);
            g2.drawLine(prev.x, prev.y, cur.x, cur.y);
            prev = cur;
        }
    }

    private void drawControlPoints(Graphics2D g2) {
        g2.setColor(POINT_COLOR);
        for (Point2D.Float p : controlPoints) {
            Point s = worldToScreen(p.x, p.y);
            g2.fillOval(s.x - POINT_RADIUS, s.y - POINT_RADIUS,
                    2 * POINT_RADIUS, 2 * POINT_RADIUS);
            g2.setColor(Color.BLACK);
            g2.drawOval(s.x - 2*POINT_RADIUS, s.y - 2*POINT_RADIUS,
                    4 * POINT_RADIUS, 4 * POINT_RADIUS);
            g2.setColor(POINT_COLOR);
        }
    }

    public List<Point2D.Float> getbSplinePoints() {
        return bSplinePoints;
    }

    public void clearBSplinePoints(){
        if (bSplinePoints != null) {
            bSplinePoints.clear();
        }
    }

    public List<Point2D.Float> getControlPoints() {
        return controlPoints;
    }

    public void autoFit() {
        if (controlPoints.isEmpty()) return;

        float minX = Float.MAX_VALUE, maxX = -Float.MAX_VALUE;
        float minY = Float.MAX_VALUE, maxY = -Float.MAX_VALUE;

        for (Point2D.Float p : controlPoints) {
            minX = Math.min(minX, p.x);
            maxX = Math.max(maxX, p.x);
            minY = Math.min(minY, p.y);
            maxY = Math.max(maxY, p.y);
        }

        List<Point2D.Float> curve = BSpline.computeCurve(controlPoints, parentEditor.getN());
        if (curve != null) {
            for (Point2D.Float p : curve) {
                minX = Math.min(minX, p.x);
                maxX = Math.max(maxX, p.x);
                minY = Math.min(minY, p.y);
                maxY = Math.max(maxY, p.y);
            }
        }

        if (minX == maxX) {
            minX -= 1.0;
            maxX += 1.0;
        }
        if (minY == maxY) {
            minY -= 1.0;
            maxY += 1.0;
        }

        viewCenterX = (minX + maxX) / 2.0;
        viewCenterY = (minY + maxY) / 2.0;

        double worldWidth = maxX - minX;
        double worldHeight = maxY - minY;

        int panelWidth = getWidth();
        int panelHeight = getHeight();
        if (panelWidth <= 0 || panelHeight <= 0) {
            panelWidth = 800;
            panelHeight = 600;
        }

        double scaleX = (panelWidth * 0.9) / worldWidth;
        double scaleY = (panelHeight * 0.9) / worldHeight;
        scale = Math.min(scaleX, scaleY);

        repaint();
    }
}