
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


/**
 * Spiral Tunnel Effect Test #1
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class View extends JPanel {
    
    private static final int SCREEN_WIDTH = 300;
    private static final int SCREEN_HEIGHT = 240;
    private static final int SCREEN_SCALE = 2;

    private final BufferedImage frameBuffer;
    private final int[] screen;
    
    private double displacement = 0;
        
    // timer for main loop
    private final Timer timer = new Timer();
    
    public View() {
        int sw = SCREEN_WIDTH;
        int sh = SCREEN_HEIGHT;
        frameBuffer = new BufferedImage(sw, sh, BufferedImage.TYPE_INT_ARGB);
        Raster raster = frameBuffer.getRaster();
        screen = ((DataBufferInt) raster.getDataBuffer()).getData();
    }
    
    public void start() {
        // main loop, something close to 30 fps
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                update();
                repaint();
            }
        }, 100, 1000 / 30);
    }
    
    private void update() {
        displacement += 2;
    }
        
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); 
        Graphics2D fbg = (Graphics2D) frameBuffer.getGraphics();
        drawOffscreen(fbg);
        g.drawImage(frameBuffer, 0, 0, 
            SCREEN_WIDTH * SCREEN_SCALE, SCREEN_HEIGHT * SCREEN_SCALE, 
            0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, null);
    }
    
    private void drawOffscreen(Graphics2D g) {
        final double radius = 200;
        for (int screenY = 0; screenY < 240; screenY++) {
            for (int screenX = 0; screenX < SCREEN_WIDTH; screenX++) {
                double vx = screenX - 160;
                double vy = screenY - 120;
                double screenRadius = 150 - Math.sqrt(vx * vx + vy * vy);
                double z = (radius * 560) / (radius - screenRadius);
                double x = 367 * Math.atan2(vx, vy);
                
                double tx = x + 2000 + displacement;
                double tz = z + 2 * displacement;
                
                double colorAttenuation = (1200.0 - z) / 800.0;
                if (colorAttenuation < 0) {
                    colorAttenuation = 0;
                }

                int colorOnIntensity = (int) (((tx + tz) % 256) * 
                        colorAttenuation);
                
                int colorOn = 0xff000000 + (colorOnIntensity << 16);
                int colorOff = 0xff000000;
                
                int color = (((int) (tx / 64)) % 3 == 0 && 
                    ((int) (tz / 64)) % 2 == 1) || 
                    (((int) (tx / 64)) % 3 == 1 && 
                    ((int) (tz / 64)) % 2 == 0) ? colorOn : colorOff;
                
                frameBuffer.setRGB(screenX, screenY, color);
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            View view = new View();
            view.setPreferredSize(new Dimension(SCREEN_WIDTH * SCREEN_SCALE, 
                    SCREEN_HEIGHT * SCREEN_SCALE));
            
            JFrame frame = new JFrame();
            frame.setTitle("Java Spiral Tunnel Effect Test #1");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(view);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            view.start();
        });
    }
    
}
