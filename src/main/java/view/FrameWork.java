package view;

import model.ModelContext;
import util.BSpline;
import util.FileManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Point2D;
import java.util.List;

public class FrameWork extends JFrame {
    public static final int DEFAULT_WIDTH = 1500;
    public static final int DEFAULT_HEIGHT = 1100;

    private RenderPanel renderPanel;
    private ToolBarPanel toolBar;

    private ModelContext modelContext;

    private final FileManager fileManager = new FileManager(this);

    public FrameWork(){
        super("Wireframe");
        this.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        modelContext = new ModelContext();

        addGuiComponents();
        pack();

        revalidate();
        setLocationRelativeTo(null);

        initializeModel();
    }

    private void addGuiComponents(){
        // 1. Add edit canvas

        renderPanel = new RenderPanel(this);

        add(renderPanel, BorderLayout.CENTER);

        // 2. Add menu
        var menuBar = new MenuPanel(renderPanel);
        setJMenuBar(menuBar);

        // 3. Add toolbar
        toolBar = new ToolBarPanel(renderPanel);
        add(toolBar, BorderLayout.NORTH);
    }

    public void updateModel(List<Point2D.Float> bSplinePoints){
        System.out.println("Calculated BSplinePoints(" + bSplinePoints.size() + "): " + bSplinePoints);
        renderPanel.updateModel(bSplinePoints);
    }

    private void initializeModel() {
        List<Point2D.Float> bSplinePoints = BSpline.computeCurve(modelContext.controlPoints, modelContext.N);
        if (bSplinePoints != null && !bSplinePoints.isEmpty()) {
            renderPanel.updateModel(bSplinePoints);
        } else {
            System.err.println("Couldn't build bspline");
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public ModelContext getModelContext() {
        return modelContext;
    }

    public void setModelContext(ModelContext modelContext) {
        this.modelContext = modelContext;
        renderPanel.setModelContext(modelContext);
        initializeModel();
    }

    public RenderPanel getImagePanel() {
        return renderPanel;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

}