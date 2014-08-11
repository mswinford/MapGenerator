package noise;
/**
 *
 * @author Michael
 */
import com.noise.*;

import javax.swing.JFrame;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
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
        f = new AppFrame();
        //frame = new MyFrame();
    }
    
    public static void main(String[] args){
        new NoiseApp();
    }
}

class AppFrame extends JFrame{
	private static final long serialVersionUID = 5893367620671645772L;

	public AppFrame(){
    	//initialize the window
        setTitle("NoiseApp");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBackground(Color.DARK_GRAY);
        setResizable(true);
        setSize(800, 560);
        setLayout(new BorderLayout());
        
        //create the canvas which shows the map
        final AppCanvas canvas = new AppCanvas(256, 256);
        canvas.addMouseWheelListener(new MouseWheelListener(){
        	@Override
        	public void mouseWheelMoved(MouseWheelEvent e){
        		int notches = e.getWheelRotation();
        		canvas.zoom(notches);
        	}
        });
        canvas.addComponentListener(new ComponentListener() {
			
			@Override
			public void componentShown(ComponentEvent e) {
				
				
			}
			
			@Override
			public void componentResized(ComponentEvent e) {
				canvas.setResized(true);
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {
				
				
			}
			
			@Override
			public void componentHidden(ComponentEvent e) {
				
				
			}
		});
        final JScrollPane s = new JScrollPane(canvas, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(s, BorderLayout.CENTER);
        
        //create JPanel which holds options
        JPanel p = new JPanel();
        //p.setPreferredSize(new Dimension(270, this.getHeight()));
        p.setLocation(530, 0);
        p.setBackground(Color.DARK_GRAY);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        NoiseType[] tmp = {NoiseType.Value, NoiseType.Perlin, NoiseType.Simplex};
        final JComboBox<NoiseType> noiseBox = new JComboBox<NoiseType>(tmp);
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
        final JTextField seedText = new JTextField("100000");
        seedText.setSize(160, 30);
        seedText.setLocation(88, 100);
        p.add(seedText);
        final JButton button1 = new JButton("seed");
        button1.setSize(80, 30);
        button1.setLocation(5, 100);
        button1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                if(e.getButton() == MouseEvent.BUTTON1){
                	canvas.setClicked(true);
                    canvas.setSeed(Integer.parseInt(seedText.getText()));
                    canvas.setPreferredSize(new Dimension(s.getViewport().getWidth(), s.getViewport().getHeight()));
                    canvas.revalidate();
                    System.out.println(canvas.getWidth() + "    " + canvas.getHeight());
                    canvas.repaint();
                }
            }
        });
        p.add(button1);
        final JScrollPane t = new JScrollPane(p, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(t, BorderLayout.LINE_END);
        
        setVisible(true);
    }
}

class AppCanvas extends JPanel{
	private static final long serialVersionUID = -7640766025233749863L;
	
	private int seed = 100000;
    private boolean seedChanged = true;
    private BufferedImage curImage;
    private NoiseType noiseType;
    private boolean noiseTypeChanged = false;
    private int currentZoomLevel = 1;
    private boolean resized = false;
    private boolean clicked = false;
    
    public AppCanvas(int width, int height){
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
    	if(noiseType == nt) return;
        noiseType = nt;
        noiseTypeChanged = true;
    }
    
    public void setResized(boolean b){
    	resized = b;
    }
    
    public void setClicked(boolean b){
    	clicked = b;
    }
    
    public int getCurrentZoomLevel(){
    	return currentZoomLevel;
    }
    
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        if(clicked) {
        	currentZoomLevel = 1;
        }
        if(seedChanged || noiseTypeChanged || (resized && clicked)){
            seedChanged = false;
            noiseTypeChanged = false;
            resized = false;
            clicked = false;
            System.out.println(this.getWidth() + "    " +  this.getHeight());
            drawImage(g);
        }
        else drawCurImage(g);
    }
    
    private void drawCurImage(Graphics g){
        g.drawImage(curImage, 0, 0, this.getWidth(), this.getHeight(), null);
    }
    
    private void drawImage(Graphics g){
        Noise n;
        double sideSum = this.getWidth() + this.getHeight();
        double xRatio = 1.6;//(this.getWidth() / sideSum) + 1;
        double yRatio = 1.6;//(this.getHeight() / sideSum) + 1;
        switch(noiseType){
            case Value:
                n = new ValueNoise();
                break;
            case Perlin:
                n = new PerlinNoise();
                xRatio *= 0.35d;
                yRatio *= 0.35d;
                break;
            case Simplex:
                n = new SimplexNoise();
                xRatio *= 0.2d;
                yRatio *= 0.2d;
                break;
            default:
                n = new ValueNoise();
        }
        n.setSeed(seed);
        BufferedImage im = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
        
        for(int x=0; x<im.getWidth(); x++){
                for(int y=0; y<im.getHeight(); y++){
                	double noise = n.noise2D((double)x * (5d*xRatio)/im.getWidth(), (double)y * (5d*yRatio)/im.getHeight()) * 128d + 128d;
                    im.setRGB((int)x, (int)y, getRGB(noise));
                }
        }
        curImage = im;
        g.drawImage(im, 0, 0, null);
        
    }
    
    private int getRGB(double noise){
        int rgb = (int)noise;
        
        if(rgb <= 120){ return Color.BLUE.getRGB(); }
        else if(rgb > 120 && rgb <= 210){ return Color.GREEN.getRGB(); }
        else if(rgb > 210 && rgb <= 235){ return Color.ORANGE.getRGB(); }
        else if(rgb > 235 && rgb <= 245){ return Color.GRAY.getRGB(); }
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
    	//JFrame saveFrame = new JFrame();
    	//saveFrame.setPreferredSize(new Dimension(200, 50));
    	//saveFrame.setVisible(true);
        try{ ImageIO.write(curImage, "PNG", new File("ValueNoiseImg.png")); }
        catch(IOException e){ }
    }
    
    protected void zoom(int notches){
    	if(notches > 0){ //zoom in
    		if(currentZoomLevel == 10) return;
    		currentZoomLevel++;
    		this.setPreferredSize(new Dimension(this.getWidth() * currentZoomLevel , this.getHeight() * currentZoomLevel));
    	}
    	else { //zoom out
    		System.out.println(currentZoomLevel);
    		if(currentZoomLevel == 1) return;
    		this.setPreferredSize(new Dimension(this.getWidth() / currentZoomLevel , this.getHeight() / currentZoomLevel));
    		currentZoomLevel--;
    	}
    	
    	this.revalidate();
    	this.repaint();
    	
    }
}

enum NoiseType{
    Value, Perlin, Simplex
}

class MyFrame extends JFrame{
    /**
	 * 
	 */
	private static final long serialVersionUID = 7647077576845989352L;

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