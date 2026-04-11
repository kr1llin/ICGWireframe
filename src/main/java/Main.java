import view.FrameWork;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            var frameWork = new FrameWork();
            frameWork.setVisible(true);
        });
    }
}