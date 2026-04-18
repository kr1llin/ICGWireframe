package view;

import model.ModelContext;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;
import java.awt.geom.Point2D;

public class BSplineEditor extends JFrame {
    private final ModelContext context;
    private final ModelContext workingCopy;
    private final ControlPointCanvas canvas;
    private FrameWork frameWork;

    private JSpinner spinnerN, spinnerM, spinnerM1;
    private JLabel lblPointCount;
    private boolean changesMade = false;

    public BSplineEditor(ModelContext context) {
        this.context = context;
        this.workingCopy = copyContext(context);

        setTitle("B‑spline Editor");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        canvas = new ControlPointCanvas(workingCopy.controlPoints, this);
        add(canvas, BorderLayout.CENTER);

        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.SOUTH);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("Parameters"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // N
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("N (segments per span):"), gbc);
        spinnerN = new JSpinner(new SpinnerNumberModel(workingCopy.N, 1, 200, 1));
        spinnerN.addChangeListener(e -> updatePreview());
        gbc.gridx = 1;
        panel.add(spinnerN, gbc);

        // M
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("M (number of generative lines):"), gbc);
        spinnerM = new JSpinner(new SpinnerNumberModel(workingCopy.M, 2, 360, 1));
        spinnerM.addChangeListener(e -> updatePreview());
        gbc.gridx = 1;
        panel.add(spinnerM, gbc);

        // M1
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("M1 (circle subdivisions):"), gbc);
        spinnerM1 = new JSpinner(new SpinnerNumberModel(workingCopy.M1, 1, 50, 1));
        spinnerM1.addChangeListener(e -> updatePreview());
        gbc.gridx = 1;
        panel.add(spinnerM1, gbc);

        // Point count
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Control points (K):"), gbc);
        lblPointCount = new JLabel(String.valueOf(workingCopy.controlPoints.size()));
        gbc.gridx = 1;
        panel.add(lblPointCount, gbc);
        updatePointCountLabel();

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton autoFitBtn = new JButton("Auto Fit");
        autoFitBtn.addActionListener(e -> canvas.autoFit());
        buttonPanel.add(autoFitBtn);

        JButton applyBtn = new JButton("Apply");
        applyBtn.addActionListener(e -> applyChanges());
        buttonPanel.add(applyBtn);

        JButton okBtn = new JButton("OK");
        okBtn.addActionListener(e -> {
            applyChanges();
            dispose();
        });
        buttonPanel.add(okBtn);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());
        buttonPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        return panel;
    }

    void onPointsChanged() {
        changesMade = true;
        updatePointCountLabel();

        applyChanges();
    }

    void updatePointCountLabel() {
        int k = workingCopy.controlPoints.size();
        lblPointCount.setText(k + (k < 4 ? " (needs at least 4)" : ""));
        if (k < 4) {
            lblPointCount.setForeground(Color.RED);
        } else {
            lblPointCount.setForeground(Color.BLACK);
        }
    }

    int getN() {
        return (Integer) spinnerN.getValue();
    }

    private void applyChanges() {
        if (workingCopy.controlPoints.size() < 4) {
            JOptionPane.showMessageDialog(this,
                    "At least 4 control points are required.",
                    "Invalid data", JOptionPane.ERROR_MESSAGE);
            return;
        }
        context.N = getN();
        context.M = (Integer) spinnerM.getValue();
        context.M1 = (Integer) spinnerM1.getValue();
        context.controlPoints.clear();
        for (Point2D.Float p : workingCopy.controlPoints) {
            context.controlPoints.add(new Point2D.Float(p.x, p.y));
        }
        changesMade = false;

        if (frameWork != null) {
            List<Point2D.Float> bSpline = canvas.computCurrentBSplineCurve();
            frameWork.updateModel(bSpline);
        }
    }

    private ModelContext copyContext(ModelContext src) {
        ModelContext copy = new ModelContext();
        copy.N = src.N;
        copy.M = src.M;
        copy.M1 = src.M1;
        copy.controlPoints.clear();
        for (Point2D.Float p : src.controlPoints) {
            copy.controlPoints.add(new Point2D.Float(p.x, p.y));
        }
        return copy;
    }

    public void setFrameWork(FrameWork framework) {
        this.frameWork = framework;
    }

    private void updatePreview() {
        workingCopy.N = (Integer) spinnerN.getValue();
        workingCopy.M = (Integer) spinnerM.getValue();
        workingCopy.M1 = (Integer) spinnerM1.getValue();

        changesMade = true;
        applyChanges();
        canvas.repaint();
    }
}