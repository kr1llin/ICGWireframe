package util;

import model.ModelContext;
import view.FrameWork;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class FileManager {
    private final FrameWork frameWork;
    private final String MODEL_FORMAT_STR = "rvl";

    public FileManager(FrameWork frameWork) {
        this.frameWork = frameWork;
    }

    public void openModel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Revolution model", MODEL_FORMAT_STR));
        fileChooser.setAcceptAllFileFilterUsed(false);

        int result = fileChooser.showOpenDialog(frameWork);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (!selectedFile.getName().toLowerCase().endsWith(".rvl")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".rvl");
            }
            try (FileInputStream fis = new FileInputStream(selectedFile); ObjectInputStream ois = new ObjectInputStream(fis)) {
                ModelContext modelContext = (ModelContext) ois.readObject();
                frameWork.setModelContext(modelContext);
                System.out.println("Model context loaded: " + modelContext);
            } catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(frameWork, "Error reading file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

    }

    public void saveModel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Revolution model", MODEL_FORMAT_STR));
        fileChooser.setAcceptAllFileFilterUsed(false);

        int result = fileChooser.showSaveDialog(frameWork);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (!selectedFile.getName().toLowerCase().endsWith(".rvl")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".rvl");
            }
            try (FileOutputStream fos = new FileOutputStream(selectedFile); ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(frameWork.getModelContext());
                JOptionPane.showMessageDialog(frameWork, "Model save success", "Saving", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frameWork, "Error reading file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
