/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package noise;
/**
 *
 * @author Michael
 */
import com.noise.SimplexNoise;
import com.noise.Noise;
import com.noise.ValueNoise;
import com.noise.PerlinNoise;
import javax.swing.JFrame;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SpringLayout;

public class NoiseApp {
    AppFrame f;
    MyFrame frame;
    
    public NoiseApp(){
        //f = new AppFrame();
        frame = new MyFrame();
    }
    
    public static void main(String[] args){
        NoiseApp app = new NoiseApp();
    }
}

class AppFrame extends JFrame{
    
    public AppFrame(){
        setTitle("NoiseApp");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBackground(Color.DARK_GRAY);
        setResizable(false);
        setSize(800, 560);
        setLayout(null);
        final AppCanvas canvas = new AppCanvas(512, 512);
        final JScrollPane s = new JScrollPane(canvas, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        s.setSize(new Dimension(530, 532));
        s.setLocation(0, 0);
        add(s);
        
        JPanel p = new JPanel();
        p.setSize(270, this.getHeight());
        p.setLocation(530, 0);
        p.setBackground(Color.DARK_GRAY);
        p.setLayout(null);
        NoiseType[] tmp = {NoiseType.Value, NoiseType.Perlin, NoiseType.Simplex};
        final JComboBox<NoiseType> noiseBox = new JComboBox(tmp);
        noiseBox.setSelectedIndex(0);
        noiseBox.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                NoiseType nt = (NoiseType)noiseBox.getSelectedItem();
                canvas.setNoiseType(nt);
            }
        });
        noiseBox.setSize(new Dimension(80, 30));
        noiseBox.setLocation(5, 135);
        p.add(noiseBox);
        final JTextField text = new JTextField("100000");
        text.setSize(160, 30);
        text.setLocation(88, 100);
        p.add(text);
        final JButton button1 = new JButton("seed");
        button1.setSize(80, 30);
        button1.setLocation(5, 100);
        button1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                if(e.getButton() == MouseEvent.BUTTON1){
                    canvas.setSeed(Integer.parseInt(text.getText()));
                    canvas.repaint(0);
                }
            }
        });
        p.add(button1);
        add(p);
        setVisible(true);
    }
}

class AppCanvas extends JPanel{
    private int seed = 100000;
    private boolean first = true;
    private boolean seedChanged = true;
    private BufferedImage curImage;
    private NoiseType noiseType;
    
    public AppCanvas(int width, int height){
        //setSize(width, width);
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.WHITE);
        noiseType = NoiseType.Value;
    }
    
    public void setSeed(int seed){
        if(this.seed == seed) return;
        this.seed = seed;
        seedChanged = true;
    }
    
    public void setNoiseType(NoiseType nt){
        noiseType = nt;
    }
    
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        if(seedChanged){
            seedChanged = false;
            drawImage(g);
        }
        else drawCurImage(g);
    }
    
    private void drawCurImage(Graphics g){
        g.drawImage(curImage, 0, 0, null);
    }
    
    private void drawImage(Graphics g){
        Noise n;
        switch(noiseType){
            case Value:
                n = new ValueNoise();
                break;
            case Perlin:
                n = new PerlinNoise();
                break;
            case Simplex:
                n = new SimplexNoise();
                break;
            default:
                n = new ValueNoise();
        }
        n.setSeed(seed);
        System.out.println(this.getWidth());
        BufferedImage im = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
        for(int x=0; x<im.getWidth(); x++){
                for(int y=0; y<im.getHeight(); y++){
                    double xRatio = (double)this.getWidth()/(double)this.getHeight();
                    double yRatio = 1d/xRatio;
                    double noise = n.noise2D((double)x * 5d/im.getWidth(), (double)y * (5d*yRatio)/im.getHeight()) * 128d + 128d;
                    im.setRGB((int)x, (int)y, getRGB(noise));
                }
        }
        curImage = im;
        g.drawImage(im, 0, 0, null);
        saveImage();    
    }
    
    private int getRGB(double noise){
        int rgb = (int)noise;
        
        if(rgb <= 100){ return Color.BLUE.getRGB(); }
        else if(rgb > 100 && rgb <= 190){ return Color.GREEN.getRGB(); }
        else if(rgb > 190 && rgb <= 215){ return Color.ORANGE.getRGB(); }
        else if(rgb > 215 && rgb <= 245){ return Color.GRAY.getRGB(); }
        else{ return Color.WHITE.getRGB(); }
    }
    
    private void drawImage2(Graphics g){
        if(g == null){ return; }
        BufferedImage im;
        try{ 
            im = ImageIO.read(new File("ValueNoiseImg.png"));
            this.setPreferredSize(new Dimension(im.getWidth(), im.getHeight()));
            g.drawImage(im, 0, 0, null);
        }
        catch(IOException e){ }
    }
    
    private void saveImage(){
        try{ ImageIO.write(curImage, "PNG", new File("ValueNoiseImg.png")); }
        catch(IOException e){ }
    }
}

enum NoiseType{
    Value, Perlin, Simplex
}

class MyFrame extends JFrame{
    public MyFrame(){
        init();
    }
    
    private void init(){
        setTitle("App");
        setResizable(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(1, 3));
        setPreferredSize(new Dimension(1200, 1024));
        
        String[] labels = {"seed: ", "frequency: ", "lacunartiy: ", "amplitude: ", "persistence: ", "octaves: "};
        
        JPanel p1 = new JPanel();
        p1.setPreferredSize(new Dimension(this.getPreferredSize().width/3, this.getPreferredSize().height));
        p1.setBackground(Color.WHITE);
        SpringLayout l1 = new SpringLayout();
        p1.setLayout(l1);
        AppCanvas c1 = new AppCanvas(p1.getPreferredSize().width - 15, p1.getPreferredSize().width - 15);
        l1.putConstraint(SpringLayout.WEST, c1, 5, SpringLayout.WEST, p1);
        l1.putConstraint(SpringLayout.NORTH, c1, 5, SpringLayout.NORTH, p1);
        l1.putConstraint(SpringLayout.EAST, c1, -3, SpringLayout.EAST, p1);
        l1.putConstraint(SpringLayout.SOUTH, c1, -600, SpringLayout.SOUTH, p1);
        p1.add(c1);
        JTextField text = new JTextField(5);
        text.setFont(new Font("serif", Font.PLAIN, 18));
        JLabel l = new JLabel(labels[0], JLabel.TRAILING);
        l.setFont(new Font("serif", Font.PLAIN, 18));
        l.setLabelFor(text);
        l1.putConstraint(SpringLayout.NORTH, l, 20, SpringLayout.SOUTH, c1);
        l1.putConstraint(SpringLayout.EAST, l, -3, SpringLayout.WEST, text);
        l1.putConstraint(SpringLayout.NORTH, text, 20, SpringLayout.SOUTH, c1);
        l1.putConstraint(SpringLayout.WEST, text, 170, SpringLayout.WEST, p1);
        p1.add(l);
        p1.add(text);
        for(int i=1; i<labels.length; i++){
            
        }
        
        //p1.setLayout(l1);
        //p1.add(c1);
        
        JPanel p2 = new JPanel();
        p2.setPreferredSize(new Dimension(this.getPreferredSize().width/3, this.getPreferredSize().height));
        p2.setBackground(Color.LIGHT_GRAY);
        SpringLayout l2 = new SpringLayout();
        AppCanvas c2 = new AppCanvas(p2.getPreferredSize().width - 15, p2.getPreferredSize().width - 15);
        l2.putConstraint(SpringLayout.WEST, c2, 4, SpringLayout.WEST, p2);
        l2.putConstraint(SpringLayout.NORTH, c2, 5, SpringLayout.NORTH, p2);
        l2.putConstraint(SpringLayout.EAST, c2, -4, SpringLayout.EAST, p2);
        l2.putConstraint(SpringLayout.SOUTH, c2, -600, SpringLayout.SOUTH, p2);
        p2.setLayout(l2);
        p2.add(c2);
        
        JPanel p3 = new JPanel();
        p3.setPreferredSize(new Dimension(this.getPreferredSize().width/3, this.getPreferredSize().height));
        p3.setBackground(Color.LIGHT_GRAY);
        SpringLayout l3 = new SpringLayout();
        AppCanvas c3 = new AppCanvas(p3.getPreferredSize().width - 15, p3.getPreferredSize().width - 15);
        l3.putConstraint(SpringLayout.WEST, c3, 3, SpringLayout.WEST, p3);
        l3.putConstraint(SpringLayout.NORTH, c3, 5, SpringLayout.NORTH, p3);
        l3.putConstraint(SpringLayout.EAST, c3, -5, SpringLayout.EAST, p3);
        l3.putConstraint(SpringLayout.SOUTH, c3, -600, SpringLayout.SOUTH, p3);
        p3.setLayout(l3);
        p3.add(c3);
        
        add(p1);
        add(p2);
        add(p3);
        
        pack();
        setVisible(true);
    }
}