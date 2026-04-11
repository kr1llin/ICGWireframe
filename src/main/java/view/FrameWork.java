package view;

import model.ModelContext;

import javax.swing.*;
import java.awt.*;

public class FrameWork extends JFrame {
    public static final int DEFAULT_WIDTH = 1500;
    public static final int DEFAULT_HEIGHT = 1100;

    private ImagePanel imagePanel;
    private JScrollPane scrollPane;
    private ToolBarPanel toolBar;

    private ModelContext modelContext;

    public FrameWork(){
        super("Filter");
        this.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        addGuiComponents();
        pack();

        imagePanel.resizeCanvas(scrollPane.getViewport().getWidth(), scrollPane.getHeight());

        revalidate();
        setLocationRelativeTo(null);

        modelContext = new ModelContext();
    }

    private void addGuiComponents(){
        // 1. Add edit canvas
        scrollPane = new JScrollPane();

        imagePanel = new ImagePanel(scrollPane, this);

        scrollPane.setViewportView(imagePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);

        // 2. Add menu
        var menuBar = new MenuPanel(imagePanel);
        setJMenuBar(menuBar);

        // 3. Add toolbar
        toolBar = new ToolBarPanel(imagePanel);
        add(toolBar, BorderLayout.NORTH);
    }

    public void updateModel(){
        repaint();
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

    public ImagePanel getImagePanel() {
        return imagePanel;
    }
}