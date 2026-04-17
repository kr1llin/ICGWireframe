package view;

import model.ModelContext;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

public class FrameWork extends JFrame {
    public static final int DEFAULT_WIDTH = 1500;
    public static final int DEFAULT_HEIGHT = 1100;

    private RenderPanel renderPanel;
    private JScrollPane scrollPane;
    private ToolBarPanel toolBar;

    private ModelContext modelContext;

    public FrameWork(){
        super("Filter");
        this.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        modelContext = new ModelContext();

        addGuiComponents();
        pack();

        renderPanel.resizeCanvas(scrollPane.getViewport().getWidth(), scrollPane.getHeight());

        revalidate();
        setLocationRelativeTo(null);

    }

    private void addGuiComponents(){
        // 1. Add edit canvas
        scrollPane = new JScrollPane();

        renderPanel = new RenderPanel(scrollPane, this);

        scrollPane.setViewportView(renderPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);

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

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public ModelContext getModelContext() {
        return modelContext;
    }

    public RenderPanel getImagePanel() {
        return renderPanel;
    }
}