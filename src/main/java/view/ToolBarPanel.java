package view;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class ToolBarPanel extends JToolBar {
    private final RenderPanel imagePanel;
    private final FrameWork frameWork;

    public ToolBarPanel(RenderPanel pp){
        imagePanel = pp;
        frameWork = imagePanel.getFrameWork();

        JMenuItem bSplineEditorItem = new JMenuItem(new AbstractAction("B-spline editor") {
            @Override
            public void actionPerformed(ActionEvent e) {
                BSplineEditor bSplineEditor = new BSplineEditor(frameWork.getModelContext());
                bSplineEditor.setFrameWork(frameWork);
                bSplineEditor.setVisible(true);
            }
        });
        add(bSplineEditorItem);

        addSeparator();
    }
}
