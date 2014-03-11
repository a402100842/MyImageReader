package com.btf.ui;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import com.btf.filter.SpatialFilter;
import com.btf.graphics.Charts;
import com.btf.imgIO.BMPIO;
import com.btf.imgProcess.ColorProcess;
import com.btf.imgProcess.HalfToning;
import com.btf.imgProcess.Noise;
import com.btf.imgProcess.Projection;
import com.btf.imgProcess.PseudoColor;
import com.btf.imgProcess.SizeProcess;
import com.btf.utils.MyImageFilter;
import com.btf.utils.Utils;

@SuppressWarnings("serial")
public class MainUI extends JFrame implements ActionListener, CommandList {
	private static final MainUI INSTANCE = new MainUI("ImageReader-Version_1.2");
	
	public static final Dimension scrSize = Toolkit.getDefaultToolkit()
			.getScreenSize();
	public static final Insets scrInsets = Toolkit
			.getDefaultToolkit()
			.getScreenInsets(
					GraphicsEnvironment.getLocalGraphicsEnvironment()
							.getDefaultScreenDevice().getDefaultConfiguration()
//					this.getGraphicsConfiguration()		
					);

	private JFileChooser fileChooser;
	private JDesktopPane desktop;
	private ImageIcon icon;
	private String info;
	
	private Point lastLocation;
	private int maxX = 500;
    private int maxY = 500;

    public static MainUI getInstance(){
    	return INSTANCE;
    }
    
	private MainUI(String str) {
		super(str);
		icon = new ImageIcon(this.getClass().getResource("images/me.png"));
		info = new String("This is a tool used for digital image processing\n"
				+ "    rendered by\n        11数媒    莫言贺    中山大学\n        contact me: 402100842@qq.com");

		lastLocation = new Point(-40, -40);
		
		// Create/set menu bar and content pane.
		setJMenuBar(createMenuBar());
		desktop = new JDesktopPane(); 
		desktop.setOpaque(true); // content panes must be opaque
		setContentPane(desktop);
		
		 //Make dragging a little faster but perhaps uglier.
        desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);

//		setSize(scrSize.width,
//				scrSize.height);

		fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new MyImageFilter());

//		imagePane = new ImagePane((int) ((scrSize.width - scrInsets.left - scrInsets.right)*0.7), (int) ((scrSize.height - scrInsets.top - scrInsets.bottom) * 0.9));
//		scrollPane = new JScrollPane(imagePane);
//		scrollPane.setPreferredSize(new Dimension(768, 512));

//		this.getContentPane().add(BorderLayout.CENTER, scrollPane);
//		this.getContentPane().add(BorderLayout.CENTER, imagePane);
		this.addWindowListener(new MyWindowListener());
	}

	private JMenuBar createMenuBar() {
		// Create the menu bar.
		JMenuBar menuBar = new JMenuBar();

		// Create the menuitems.
		JMenu subMenu;
		JMenuItem menuItem = null;
		
		// Create the menus.
		JMenu helpMenu = new JMenu("Help");
		JMenu fileMenu = new JMenu("File");
		JMenu processMenu = new JMenu("Process");
		JMenu colorMenu = new JMenu("Color");
		JMenu sizeMenu = new JMenu("Size");
		JMenu spaFilterMenu = new JMenu("Spatial Filter");
		JMenu graphicMenu = new JMenu("Graphic");
		
		helpMenu.setMnemonic(KeyEvent.VK_H);
		fileMenu.setMnemonic(KeyEvent.VK_F);
		processMenu.setMnemonic(KeyEvent.VK_P);
		colorMenu.setMnemonic(KeyEvent.VK_C);
		sizeMenu.setMnemonic(KeyEvent.VK_S);
		graphicMenu.setMnemonic(KeyEvent.VK_G);
		
		//------------------------help menu------------------------
		menuItem = new JMenuItem("About");
		menuItem.setActionCommand(EVENT_ABOUT);
		menuItem.addActionListener(this);
		helpMenu.add(menuItem);
		
		menuItem = new JMenuItem("Common problem");
		menuItem.setActionCommand(EVENT_PROBLEM);
		menuItem.addActionListener(this);
		helpMenu.add(menuItem);

		//------------------------file menu------------------------
		menuItem = new JMenuItem("Open");
		menuItem.setActionCommand(EVENT_OPEN);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				ActionEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		fileMenu.add(menuItem);

		menuItem = new JMenuItem("Save");
		menuItem.setActionCommand(EVENT_SAVE);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				ActionEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		fileMenu.add(menuItem);

		menuItem = new JMenuItem("Exit");
		menuItem.setActionCommand(EVENT_EXIT);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
				ActionEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		fileMenu.add(menuItem);
		
		fileMenu.addSeparator();
		
		menuItem = new JMenuItem("Open Photo Browser");
		menuItem.setActionCommand(EVENT_OPEN_PHOTO_BROWSER);
		menuItem.addActionListener(this);
		fileMenu.add(menuItem);
		
		
		//------------------------process menu------------------------
		subMenu = new JMenu("Halftoning");
		processMenu.add(subMenu);
		
		menuItem = new JMenuItem("Generate gray level image use Halftoning");
		menuItem.setActionCommand(EVENT_HalfToning);
		menuItem.addActionListener(this);
		subMenu.add(menuItem);
		
		menuItem = new JMenuItem("Generate a gray scale wedge of size 256 x 256");
		menuItem.setActionCommand(EVENT_HalfToning_GenerateWedge);
		menuItem.addActionListener(this);
		subMenu.add(menuItem);
		
		subMenu = new JMenu("Projection");
		processMenu.add(subMenu);
		
		menuItem = new JMenuItem("Cylindrical projection");
		menuItem.setActionCommand(EVENT_PROJECTION_CYLINDER);
		menuItem.addActionListener(this);
		subMenu.add(menuItem);
		
		menuItem = new JMenuItem("Cylindrical projection2");
		menuItem.setActionCommand(EVENT_PROJECTION_CYLINDER_2);
		menuItem.addActionListener(this);
		subMenu.add(menuItem);
		
		menuItem = new JMenuItem("图像拼接");
		menuItem.setActionCommand(EVENT_IMAGE_MOSAIC);
		menuItem.addActionListener(this);
		processMenu.add(menuItem);
		
		subMenu = new JMenu("Noise");
		processMenu.add(subMenu);
		
		menuItem = new JMenuItem("Gaussian");
		menuItem.setActionCommand(EVENT_NOISE_GAUSSIAN);
		menuItem.addActionListener(this);
		subMenu.add(menuItem);
		
		menuItem = new JMenuItem("Salt-and-Pepper");
		menuItem.setActionCommand(EVENT_NOISE_SAP);
		menuItem.addActionListener(this);
		subMenu.add(menuItem);
		
		//------------------------color menu------------------------
		subMenu = new JMenu("K-bit image");
		colorMenu.add(subMenu);
		
		menuItem = new JMenuItem("K = 1 （黑白图像）");
		menuItem.setActionCommand(EVENT_TOGRAY_1);
		menuItem.addActionListener(this);
		subMenu.add(menuItem);
		
		for (int i = 2; i < 9; i++){
			menuItem = new JMenuItem("K = " + i);
			menuItem.setActionCommand("event_togray_" + i);
			menuItem.addActionListener(this);
			subMenu.add(menuItem);
		}
		
		colorMenu.addSeparator();
		
		menuItem = new JMenuItem("Show blue channel");
		menuItem.setActionCommand(EVENT_SHOWBLUE);
		menuItem.addActionListener(this);
		colorMenu.add(menuItem);
		
		menuItem = new JMenuItem("Show green channel");
		menuItem.setActionCommand(EVENT_SHOWGREEN);
		menuItem.addActionListener(this);
		colorMenu.add(menuItem);
		
		menuItem = new JMenuItem("Show red channel");
		menuItem.setActionCommand(EVENT_SHOWRED);
		menuItem.addActionListener(this);
		colorMenu.add(menuItem);
		
		colorMenu.addSeparator();
		
		menuItem = new JMenuItem("Pseudo-yellow");
		menuItem.setActionCommand(EVENT_PSEUDO_COLOR_YELLOW);
		menuItem.addActionListener(this);
		colorMenu.add(menuItem);
		
		//------------------------size menu------------------------
		menuItem = new JMenuItem("Zoom out by 2");
		menuItem.setActionCommand(EVENT_ZOOMOUT_2);
		menuItem.addActionListener(this);
		sizeMenu.add(menuItem);
		
		//------------------------spatial filter menu------------------------
		menuItem = new JMenuItem("Select spatial mask");
		menuItem.setActionCommand(EVENT_SPATIAL_FILTER);
		menuItem.addActionListener(this);
		spaFilterMenu.add(menuItem);
		
		menuItem = new JMenuItem("Laplacian");
		menuItem.setActionCommand(EVENT_SPATIAL_LAPLACIAN);
		menuItem.addActionListener(this);
		spaFilterMenu.add(menuItem);
		
		menuItem = new JMenuItem("Average");
		menuItem.setActionCommand(EVENT_SPATIAL_AVERAGE);
		menuItem.addActionListener(this);
		spaFilterMenu.add(menuItem);
		
		menuItem = new JMenuItem("Median");
		menuItem.setActionCommand(EVENT_SPATIAL_MEDIAN);
		menuItem.addActionListener(this);
		spaFilterMenu.add(menuItem);
		
		//------------------------graphic menu------------------------
		menuItem = new JMenuItem("Histogram");
		menuItem.setActionCommand(EVENT_GRAPHIC_HISTOGRAM);
		menuItem.addActionListener(this);
		graphicMenu.add(menuItem);
		
		menuItem = new JMenuItem("Histogram Equalization");
		menuItem.setActionCommand(EVENT_GRAPHIC_HISTOGRAM_EQUALIZATION);
		menuItem.addActionListener(this);
		graphicMenu.add(menuItem);
		
		menuItem = new JMenuItem("Histogram Equalization Average");
		menuItem.setActionCommand(EVENT_GRAPHIC_HISTOGRAM_EQUALIZATION_AVERAGE);
		menuItem.addActionListener(this);
		graphicMenu.add(menuItem);

		// Set up the menu bar.
		menuBar.add(fileMenu);
		menuBar.add(processMenu);
		menuBar.add(colorMenu);
		menuBar.add(sizeMenu);
		menuBar.add(spaFilterMenu);
		menuBar.add(graphicMenu);
		
		menuBar.add(helpMenu);

		return menuBar;
	}
	
	private void showNewFrame(JInternalFrame win) {
		// Move the window over and down 40 pixels.
		lastLocation.translate(40, 40);
		if ((lastLocation.x + maxX > scrSize.width)) {
			lastLocation.setLocation(0, lastLocation.getY());
		}
		if ((lastLocation.y + maxY > scrSize.height)){
			lastLocation.setLocation(lastLocation.getX(), 0);
		}
		// Set window location.
		win.setLocation(lastLocation);

		win.pack();
		win.setVisible(true);
		desktop.add(win);
        try {
        	win.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {}
	}

	public void showImage(BufferedImage img) {
		if (img == null)
			return;
		ImageInternalFrame win = new ImageInternalFrame(img);
		showNewFrame(win);
	} 

	public void showGraphic(float[][] datas){
		if (datas == null)
			return;
		GraphicInternalFrame win;
		for (int i = 0; i < datas.length; i++){
			if (datas[i] == null)
				continue;
			if (datas.length == 3){
				switch (i){
					case 0:
						win = new GraphicInternalFrame(datas[i], "R 直方图");
						break;
					case 1:
						win = new GraphicInternalFrame(datas[i], "G 直方图");
						break;
					case 2:
						win = new GraphicInternalFrame(datas[i], "B 直方图");
						break;
					default:
						win = new GraphicInternalFrame(datas[i]);	
				}
			} else
				win = new GraphicInternalFrame(datas[i]);
			
			showNewFrame(win);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String str = e.getActionCommand();
		int returnVal = -1;
		String[] strs = null;

		switch (str) {
		case EVENT_ABOUT:
			JOptionPane.showMessageDialog(this,
				    info,
				    "About",
				    JOptionPane.INFORMATION_MESSAGE,
				    icon);
			break;
		case EVENT_PROBLEM:
			JOptionPane.showMessageDialog(this,
				    ".tif not supported yet!",
				    "Hint",
				    JOptionPane.INFORMATION_MESSAGE);
			break;
		case EVENT_OPEN:
			returnVal = fileChooser.showOpenDialog(MainUI.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				BufferedImage img = null;
				
				if (Utils.getExtension(file).contains("tif")) {
//					try {
//						TIFFReader reader = new TIFFReader(file);
//						img = (BufferedImage) reader.getPage(0);
//					} catch (IOException e1) {
//						e1.printStackTrace();
//					}
				}else if (Utils.getExtension(file).equals("bmp")){
					try {
						img = BMPIO.myRead(file.getPath());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}else {
					try {
						img = ImageIO.read(file);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				if (img != null) {
					showImage(img);
				}
			}
			break;
		case EVENT_SAVE:
			returnVal = fileChooser.showSaveDialog(MainUI.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				if (Utils.getExtension(file).contains("bmp")) {
					try {
						BMPIO.myWrite(getCurrentImage(), file.getPath());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else
					try {
						ImageIO.write(getCurrentImage(), Utils.getExtension(file), file);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
			}
			break;
		case EVENT_EXIT:
			System.exit(0);
			break;
		case EVENT_OPEN_PHOTO_BROWSER:
			openPhotoBrowser();
			break;
		case EVENT_HalfToning:
			showImage(HalfToning.getInstance().process(getCurrentImage()));
			break;
		case EVENT_HalfToning_GenerateWedge:
			showImage(HalfToning.getInstance().process(HalfToning.getInstance().generateWedge()));
			break;
		case EVENT_NOISE_GAUSSIAN:
			str = (String)JOptionPane.showInputDialog(
                    MainUI.this,
                    "Enter mean, variance:\n"
                    + "split by ','",
                    "Add noise",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "0,20");

			//If a string was returned, say so.
			if ((str != null) && (str.length() > 0)) {
				strs = str.split(",");
				if (strs.length != 2)
					break;
			}
			try{
				showImage(Noise.gaussian(getCurrentImage(), Double.parseDouble(strs[1]), Integer.parseInt(strs[0])));
			} catch (java.lang.NumberFormatException nbfe){
				raiseError();
			}
			break;
		case EVENT_NOISE_SAP:
			str = (String)JOptionPane.showInputDialog(
                    MainUI.this,
                    "Enter Pa, Pb:\n"
                    + "split by ','",
                    "Add noise",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "0.1,0.1");

			//If a string was returned, say so.
			if ((str != null) && (str.length() > 0)) {
				strs = str.split(",");
				if (strs.length != 2)
					break;
			}
			try{
				showImage(Noise.salt_and_pepper(getCurrentImage(), Double.parseDouble(strs[0]), Double.parseDouble(strs[1])));
			} catch (java.lang.NumberFormatException nbfe){
				raiseError();
			}
			break;
		case EVENT_TOGRAY_1:
			showImage(ColorProcess.getKbitImage(1, getCurrentImage()));
			break;
		case EVENT_TOGRAY_2:
			showImage(ColorProcess.getKbitImage(2, getCurrentImage()));
			break;
		case EVENT_TOGRAY_3:
			showImage(ColorProcess.getKbitImage(3, getCurrentImage()));
			break;
		case EVENT_TOGRAY_4:
			showImage(ColorProcess.getKbitImage(4, getCurrentImage()));
			break;
		case EVENT_TOGRAY_5:
			showImage(ColorProcess.getKbitImage(5, getCurrentImage()));
			break;
		case EVENT_TOGRAY_6:
			showImage(ColorProcess.getKbitImage(6, getCurrentImage()));
			break;
		case EVENT_TOGRAY_7:
			showImage(ColorProcess.getKbitImage(8, getCurrentImage()));
			break;
		case EVENT_TOGRAY_8:
			showImage(ColorProcess.getKbitImage(8, getCurrentImage()));
			break;
		case EVENT_SHOWBLUE:
			showImage(ColorProcess.showChanelB(getCurrentImage()));
			break;
		case EVENT_SHOWGREEN:
			showImage(ColorProcess.showChanelG(getCurrentImage()));
			break;
		case EVENT_SHOWRED:
			showImage(ColorProcess.showChanelR(getCurrentImage()));
			break;
		case EVENT_PSEUDO_COLOR_YELLOW:
			str = (String)JOptionPane.showInputDialog(
                    MainUI.this,
                    "Enter low, high:\n"
                    + "split by ','",
                    "Pseudo Color",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "0,20");

			//If a string was returned, say so.
			if ((str != null) && (str.length() > 0)) {
				strs = str.split(",");
				if (strs.length != 2)
					break;
			}
			try{
				showImage(PseudoColor.toYellow(getCurrentImage(), Integer.parseInt(strs[0]), Integer.parseInt(strs[1])));
			} catch (java.lang.NumberFormatException nbfe){
				raiseError();
			}
			break;
		case EVENT_ZOOMOUT_2:
			showImage(SizeProcess.zoomOut(getCurrentImage()));
			break;
		case EVENT_PROJECTION_CYLINDER:
			showImage(Projection.cylindricalProjection(getCurrentImage(), 1.4));
			break;
		case EVENT_PROJECTION_CYLINDER_2:
			showImage(Projection.cylindricalProjection2(getCurrentImage(), 1.4));
			break;
		case EVENT_IMAGE_MOSAIC:
			showImage(Projection.imageMosaic(fileChooser, MainUI.this, 0.75));
			break;
		case EVENT_SPATIAL_FILTER:
			str = (String)JOptionPane.showInputDialog(
                    MainUI.this,
                    "Enter your coefficients:\n"
                    + "Split col by ',', row by ';'",
                    "Spatial Mask",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "0,-1,0;"
                    + "-1,4,-1;"
                    + "0,-1,0");

			//If a string was returned, say so.
			if ((str != null) && (str.length() > 0)) {
				showImage(SpatialFilter.filter(getCurrentImage(), str));
			}
			break;
		case EVENT_SPATIAL_LAPLACIAN:
			str = (String)JOptionPane.showInputDialog(
                    MainUI.this,
                    "Enter your coefficients:\n",
                    "Spatial Mask",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "4");

			//If a string was returned, say so.
			if ((str != null) && (str.length() > 0)) {
				showImage(SpatialFilter.laplacian(getCurrentImage(), str));
			}
			break;
		case EVENT_SPATIAL_AVERAGE:
			str = (String)JOptionPane.showInputDialog(
                    MainUI.this,
                    "Enter one coefficient:\n",
                    "Spatial Mask",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "5");

			//If a string was returned, say so.
			if ((str != null) && (str.length() > 0)) {
				returnVal = Integer.parseInt(str);
			}
			showImage(SpatialFilter.average(getCurrentImage(), returnVal));
			break;
		case EVENT_SPATIAL_MEDIAN:
			str = (String)JOptionPane.showInputDialog(
                    MainUI.this,
                    "Enter mask size:\n",
                    "Spatial Mask",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "3");

			//If a string was returned, say so.
			if ((str != null) && (str.length() > 0)) {
				returnVal = Integer.parseInt(str);
			}
			showImage(SpatialFilter.median(getCurrentImage(), returnVal));
			break;
		case EVENT_GRAPHIC_HISTOGRAM:
			showGraphic(Charts.histogram(getCurrentImage()));
			break;
		case EVENT_GRAPHIC_HISTOGRAM_EQUALIZATION:
			showImage(Charts.histogram_equalization(getCurrentImage()));
			break;
		case EVENT_GRAPHIC_HISTOGRAM_EQUALIZATION_AVERAGE:
			showImage(Charts.histogram_equalization(getCurrentImage()));
			break;
		default:
			break;
		}

	}
	
	private void openPhotoBrowser() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Create and set up the window.
				SubUI frame = SubUI.getInstance();
				frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

				// Display the window.
				frame.setBounds(scrInsets.left, scrInsets.top, scrSize.width - scrInsets.left - scrInsets.right, scrSize.height - scrInsets.top - scrInsets.bottom);
//				frame.pack();
				frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
//				frame.setResizable(false);
				frame.setVisible(true);
			}
		});
	}

	private void raiseError() {
		// TODO Auto-generated method stub
		System.out.println("some error");
	}

	private BufferedImage getCurrentImage(){
		ImageInternalFrame inframe = null;
		BufferedImage img = null;
		if (desktop.getSelectedFrame() instanceof ImageInternalFrame){
			inframe = (ImageInternalFrame) desktop.getSelectedFrame();
			img = inframe.getImagePane().getImage();
		}
		return img;
	}
	
//	private float[] getCurrentDatas(){
//		GraphicInternalFrame inframe = null;
//		float[] res = null;
//		if (desktop.getSelectedFrame() instanceof GraphicInternalFrame){
//			inframe = (GraphicInternalFrame) desktop.getSelectedFrame();
//			res = inframe.getGraphicPane().getData();
//		}
//		return res;
//	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	private static void createAndShowGUI() {
		// Create and set up the window.
		MainUI frame = MainUI.getInstance();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try {  
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
//		JFrame.setDefaultLookAndFeelDecorated(true);

		// Display the window.
		frame.setBounds(scrInsets.left, scrInsets.top, scrSize.width - scrInsets.left - scrInsets.right, scrSize.height - scrInsets.top - scrInsets.bottom);
//		frame.pack();
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
//		frame.setResizable(false);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
//				UIManager.put("swing.boldMetal", Boolean.FALSE);
				createAndShowGUI();
			}
		});
		
	}

	private class MyWindowListener implements WindowListener{

		@Override
		public void windowActivated(WindowEvent arg0) {
			
		}

		@Override
		public void windowClosed(WindowEvent arg0) {
			
		}

		@Override
		public void windowClosing(WindowEvent arg0) {
			
		}

		@Override
		public void windowDeactivated(WindowEvent arg0) {
			
		}

		@Override
		public void windowDeiconified(WindowEvent arg0) {
			
		}

		@Override
		public void windowIconified(WindowEvent arg0) {
			
		}

		@Override
		public void windowOpened(WindowEvent arg0) {
			openPhotoBrowser();
		}
		
	}
}
