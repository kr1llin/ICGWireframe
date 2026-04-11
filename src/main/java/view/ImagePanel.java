package view;

import util.LineTool;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.nio.file.Path;

import javax.swing.JPanel;
import javax.swing.JScrollPane;


public class ImagePanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener
{
    private final int DEFAULT_HEIGHT = 900;
    private final int DEFAULT_WIDTH = 1000;

    private LineTool lineTool;
    private final JScrollPane scrollPane;
    private final FrameWork frameWork;

    private int imgWidth, imgHeight;
    private BufferedImage img;
    private int lastX=0, lastY=0;		// last captured mouse coordinates

    private final Color defaultColor = Color.white;

    public ImagePanel(JScrollPane scrollPane, FrameWork frameWork)
    {
        this.scrollPane = scrollPane;
        scrollPane.setWheelScrollingEnabled(false);
        scrollPane.setViewportView(this);

        scrollPane.setBackground(defaultColor);
        scrollPane.validate();

        setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        this.frameWork = frameWork;
        img = new BufferedImage(DEFAULT_WIDTH, DEFAULT_HEIGHT, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = img.createGraphics();
        g2d.setColor(defaultColor);
        g2d.fillRect(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        g2d.dispose();

        lineTool = LineTool.getInstance();
        lineTool.setCanvas(this);
        lineTool.setImg(img);

        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }

    public JScrollPane getScrollPane ()	{ return scrollPane; }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if (img != null) {
            g.drawImage(img, 0, 0, this);
        } else {
            g.setColor(defaultColor);
            g.drawRect(0,0,imgWidth, imgHeight);
        }

        // draw all the lines on img

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        lastX = e.getX();
        lastY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
    }

    @Override
    public void mouseReleased(MouseEvent e){
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
    }

    @Override
    public void mouseEntered(MouseEvent e){}

    @Override
    public void mouseExited(MouseEvent e){}

    @Override
    public void mouseMoved(MouseEvent e){}

    public FrameWork getFrameWork() {
        return frameWork;
    }

    public BufferedImage getImg(){
        return img;
    }

    public void createNewImage(int x, int y) {
        imgWidth = x;
        imgHeight = y;

        BufferedImage newImg = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = newImg.createGraphics();
        g2d.setColor(defaultColor);
        g2d.fillRect(0, 0, imgWidth, imgHeight);
        if (img != null) {
            g2d.drawImage(img, 0, 0, null);
        }
        g2d.dispose();
        img = newImg;
        setPreferredSize(new Dimension(imgWidth, imgHeight));

        revalidate();
        repaint();
    }

    public void resizeCanvas(int width, int height) {
        if (width <= 0 || height <= 0) return;

        createNewImage(width, height);
        repaint();
    }
}