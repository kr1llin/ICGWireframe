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
    private final BSplineEditor parentEditor;

    private double viewCenterX = 0.5;   // world coordinates at canvas center
    private double viewCenterY = 0.0;
    private double scale = 200.0;       // pixels per world unit

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

                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (e.getClickCount() > 1) {
                        dragIndex = findPointAt(e.getX(), e.getY());
                        if (dragIndex != -1) {
                            draggedPoint = controlPoints.get(dragIndex);
                        } else {
                            addPointAt(e.getX(), e.getY());
                        }
                    } else {
                        panning = true;
                        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    }
                }
                else if (SwingUtilities.isRightMouseButton(e)) {
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
                if (panning) {
                    panning = false;
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        };
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);

        addMouseWheelListener(e -> {
            double zoomFactor = 1.1;
            double oldScale = scale;
            if (e.getWheelRotation() < 0) {
                scale *= zoomFactor;
            } else {
                scale /= zoomFactor;
            }
            Point mouse = e.getPoint();
            Point2D.Float before = screenToWorld(mouse.x, mouse.y);
            viewCenterX = before.x;
            viewCenterY = before.y;
            repaint();
        });
    }

    private void addPointAt(int screenX, int screenY) {
        Point2D.Float newPoint = screenToWorld(screenX, screenY);
        controlPoints.add(newPoint);
        repaint();
        parentEditor.onPointsChanged();
        parentEditor.updatePointCountLabel();
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

    private Point2D.Float screenToWorld(int screenX, int screenY) {
        int w = getWidth();
        int h = getHeight();
        int cx = w / 2;
        int cy = h / 2;
        float u = (float) (viewCenterX + (screenX - cx) / scale);
        float v = (float) (viewCenterY - (screenY - cy) / scale);
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
        Point origin = worldToScreen(0, 0);

        // U axis
        Point left = worldToScreen(-100, 0);
        Point right = worldToScreen(100, 0);
        g2.drawLine(left.x, left.y, right.x, right.y);

        // V axis
        Point top = worldToScreen(0, 100);
        Point bottom = worldToScreen(0, -100);
        g2.drawLine(top.x, top.y, bottom.x, bottom.y);

        // Labels
        g2.drawString("U", right.x - 10, right.y - 5);
        g2.drawString("V", bottom.x + 5, bottom.y + 10);
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

    private void drawBSplineCurve(Graphics2D g2) {
        List<Point2D.Float> curvePoints = BSpline.computeCurve(controlPoints, parentEditor.getN());
        if (curvePoints == null) {
            System.out.println("Bspline is null!");
            return;
        }

        g2.setColor(BSPLINE_COLOR);
        Point2D.Float first = curvePoints.getFirst();
        Point prev = worldToScreen(first.x, first.y);
        for (int i = 1; i < curvePoints.size(); i++) {
            Point cur = worldToScreen(curvePoints.get(i).x, curvePoints.get(i).y);
            g2.drawLine(prev.x, prev.y, cur.x, cur.y);
            prev = cur;
        }
        System.out.println("Bspline count: " + curvePoints.size());
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
}