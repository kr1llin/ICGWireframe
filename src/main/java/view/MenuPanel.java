package view;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class MenuPanel extends JMenuBar {
    RenderPanel panel;
    FrameWork frameWork;
    ButtonGroup imageModeGroup;
    ButtonGroup viewModeGroup;

    public MenuPanel(RenderPanel p){
        imageModeGroup = new ButtonGroup();
        viewModeGroup = new ButtonGroup();

        panel = p;
        frameWork = panel.getFrameWork();

        var fileMenu = new JMenu("File");
        add(fileMenu);
        var openItem = new JMenuItem("Open");
        openItem.addActionListener(e -> frameWork.getFileManager().openModel());
        fileMenu.add(openItem);

        var saveItem = new JMenuItem("Save");
        saveItem.addActionListener(e -> frameWork.getFileManager().saveModel());
        fileMenu.add(saveItem);

        // About
        JMenu helpMenu = new JMenu("Help");

        var aboutAction = new AbstractAction("About") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "ICG Filter\n" +
                        "\n Features:\n - Simple filters (B&W, Inversion, Gamma correction, Emboss, Sharpening)\n - Gaussian Filter (>=7 kernel size -> median filter)\n" +
                        " - Sobel and Roberts filter (with binarization)\n - Dithering (ordered and Floyd-Steinberg)\n" +
                        " - Affine rotation (using bilinear interpolation)\n - Artistic filters (Aquarelization and RollingGuidance (with parametrized spatial and range values)\n\n" +
                        "Made by Knyazkov Kirill, 23203 (2026)\n" );
            }
        };

        JMenuItem aboutItem = new JMenuItem(aboutAction);
        helpMenu.add(aboutItem);
        add(helpMenu);

        // Exit

        var exitAction = new AbstractAction("Exit"){
            public void actionPerformed(ActionEvent e){
                System.exit(0);
            }
        };
        JMenuItem exitItem = new JMenuItem(exitAction);
        fileMenu.add(exitItem);
    }


}
