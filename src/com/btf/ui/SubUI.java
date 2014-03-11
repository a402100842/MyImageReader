package com.btf.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;

import com.btf.graphics.Charts;
import com.btf.utils.MyFile;
import com.btf.utils.MyImageFilter;
import com.btf.utils.MyImageFilter2;

@SuppressWarnings("serial")
public class SubUI extends JFrame implements CommandList, ActionListener{
	private static final SubUI INSTANCE = new SubUI("PhotoBrowser-Version_1.1");
	
	public static final Path rootPath = Paths.get(System.getProperty("user.home"),"btf_image_browser");
	public static final Path imgPath = rootPath.resolve("thumb");
	public static final Path dataPath = rootPath.resolve("metadata");
	public static final Path resultsPath = rootPath.resolve("results.txt");
	public static final String emptyString = new String();
	public static final String emptyString2 = new String(" ");
	public static final int NumPhotoPerPage = 15;
	
	private JFileChooser fileChooser;
	private JPanel desktop;
	private JPanel imgsPane;
	private JTextField textField;
	
	private ArrayList<File> fileList;
	private ArrayList<ImagePane> photoList;
	private TreeMap<String, Floats> map;
	private int nextIndex;
	private int[] indices;
	private boolean[] selected;//只会记住历史而不会记住当前页面的选择情况，只有才换页的时候才会检测一次
	private boolean randomMode;
	
	public static SubUI getInstance(){
		return INSTANCE;
	}
	
	private SubUI(String str){
		super(str);
		
		fileList = new ArrayList<File>();
		photoList = new ArrayList<ImagePane>();
		map = null;
		
		// Create/set menu bar and content pane.
		setJMenuBar(createMenuBar());
		desktop = new JPanel(new BorderLayout()); 
		desktop.setOpaque(true); // content panes must be opaque
		setContentPane(desktop);

//		setSize(scrSize.width,
//				scrSize.height);

		fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new MyImageFilter());
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

//		imagePane = new ImagePane((int) ((scrSize.width - scrInsets.left - scrInsets.right)*0.7), (int) ((scrSize.height - scrInsets.top - scrInsets.bottom) * 0.9));
		imgsPane = new JPanel(new GridLayout(3, 5, 5, 5));
		imgsPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
//		scrollPane.setPreferredSize(new Dimension(768, 512));
		JToolBar toolBar = createToolBar();
		desktop.add(toolBar, BorderLayout.PAGE_START);
		desktop.add(imgsPane, BorderLayout.CENTER);
//		this.getContentPane().add(BorderLayout.CENTER, imagePane);
		
		for (int i = 0; i < NumPhotoPerPage; i++){
			ImagePane imgPane = new ImagePane();
			imgPane.addMouseListener(new MyMouseListener(imgPane));
			photoList.add(imgPane);
			imgsPane.add(imgPane, null);
		}
		nextIndex = 0;
		randomMode = false;
		indices = new int[NumPhotoPerPage];
		
		this.addWindowListener(new MyWindowListener());
	}
	
	private JMenuBar createMenuBar() {
		// Create the menu bar.
		JMenuBar menuBar = new JMenuBar();
		// Create the menuitems.
		JMenuItem menuItem = null;
		// Create the menus.
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		
		menuItem = new JMenuItem("Add");
		menuItem.setActionCommand(EVENT_OPEN);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				ActionEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		fileMenu.add(menuItem);
		
		menuItem = new JMenuItem("Remove Selected Photo");
		menuItem.setActionCommand(EVENT_PB_REMOVE);
		menuItem.addActionListener(this);
		fileMenu.add(menuItem);
		
		menuItem = new JMenuItem("Final Project Results");
		menuItem.setActionCommand(EVENT_PB_FINAL);
		menuItem.addActionListener(this);
		fileMenu.add(menuItem);
		
		menuItem = new JMenuItem("Exit");
		menuItem.setActionCommand(EVENT_EXIT);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
				ActionEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		fileMenu.add(menuItem);
		
		// Set up the menu bar.
		menuBar.add(fileMenu);
		return menuBar;
	}
	
	private JToolBar createToolBar(){
		JToolBar toolBar = new JToolBar("Not draggable");
		JButton button = null;
		
		//zero button
		button = makeNavigationButton("View", EVENT_PB_VIEW,
                "Normal page view",
                "Normal");
		toolBar.add(button);
        
		//first button
        button = makeNavigationButton("Back24", EVENT_PB_PREVIOUS,
                                      "Back to previous page",
                                      "Previous");
        toolBar.add(button);

        //second button
        button = makeNavigationButton("Forward24", EVENT_PB_NEXT,
                                      "Forward to next page",
                                      "Next");
        toolBar.add(button);
        
        //third button
        button = makeNavigationButton("Random", EVENT_PB_RANDOM,
                "Random search",
                "Random");
        toolBar.add(button);

        //fourth button
        button = makeNavigationButton("Cancel", EVENT_PB_CANCEL,
                "Deselect images",
                "Deselect");
        toolBar.add(button);
        
        //fifth button
        button = makeNavigationButton("Search", EVENT_PB_SEARCH,
                "Search for similar images",
                "Search");
        toolBar.add(button);

        //sixth component is NOT a button!
        textField = new JTextField("A text field");
        textField.setColumns(10);
        textField.setEditable(false);
        toolBar.add(textField);
		
        toolBar.setFloatable(false);
        toolBar.setBackground(Color.WHITE);
		return toolBar;
	}
	
	private JButton makeNavigationButton(String imageName,
			String actionCommand, String toolTipText, String altText) {
		// Look for the image.
		String imgLocation = "images/" + imageName + ".jpg";
		URL imageURL = SubUI.class.getResource(imgLocation);

		// Create and initialize the button.
		JButton button = new JButton();
		button.setActionCommand(actionCommand);
		button.setToolTipText(toolTipText);
		button.addActionListener(this);
		button.setMargin(new Insets(0,0,0,0));

		if (imageURL != null) { // image found
			button.setIcon(new ImageIcon(imageURL, altText));
		} else { // no image found
			button.setText(altText);
			System.err.println("Resource not found: " + imgLocation);
		}

		return button;
	}

	public void init(){
		File dir = rootPath.toFile();
		if (!dir.exists()){
			return;
		}
		//check the directory to store thumbnails
		File imgDir = imgPath.toFile();
		if (!imgDir.exists()){
			return;
		}
		
		//将thumbnails目录下的小图片的路径记录到一个fileList中
		fileList.clear();
		File[] files = imgDir.listFiles(new MyImageFilter2());
		for (File f : files){
			fileList.add(f);
		}
		
		selected = new boolean[fileList.size()];
		for (int i = 0; i < selected.length; i++)
			selected[i] = false;
		
		randomMode = false;
		nextIndex = 0;
		try {
			for (int i = 0; i < NumPhotoPerPage; i++){
				ImagePane temp = photoList.get(i);
				temp.clicked = false;
				if ((nextIndex + i) < fileList.size()){
					File tempFile = fileList.get(nextIndex + i);
					temp.setImage(ImageIO.read(tempFile));
					temp.setToolTipText(tempFile.getName());
				} else{
					temp.setImage(null);
					temp.setToolTipText(emptyString);
				}
				temp.repaint();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			nextIndex += NumPhotoPerPage;
		}
		
		int numPage;
		if (fileList.size() % NumPhotoPerPage != 0)
			numPage = fileList.size() / NumPhotoPerPage + 1;
		else
			numPage = fileList.size() / NumPhotoPerPage;
		textField.setText("page view: page 1 of " + numPage + ", (total " + fileList.size() + " images)");
	}
	
	public void do_search(){
		//先把已选择的图片记录下来
		if (randomMode){
			for (int i = 0; i < NumPhotoPerPage && i < fileList.size(); i++){
				ImagePane temp = photoList.get(i);
				if (temp.clicked){
					selected[indices[i]] = true;
				} else{
					selected[indices[i]] = false;
				}
			}
		} else {
			for (int i = 0, j = nextIndex - NumPhotoPerPage; i < NumPhotoPerPage && (i + j) < fileList.size(); i++) {
				ImagePane temp = photoList.get(i);
				if (temp.clicked) {
					selected[j + i] = true;
				} else {
					selected[j + i] = false;
				}
			}
		}
		
		int index = -1;//用来记录被选中的图片的下标
		for (int i = 0; i < selected.length; i++){
			if (selected[i]){
				index = i;
				break;//虽然支持选择多个图片做参照物,但是现阶段只取第一张被选的图片来做相似性搜索
			}
		}
		if (index != -1){//如果有图片被选中作为模版
			JDialog dialog = new JDialog(this, true);
			dialog.setTitle("Searching...");
			dialog.setSize(100, 80);
			dialog.getContentPane().add(new JLabel("Please wait......"));
			dialog.setModal(true);
			
			SwingWorker<Float, String> swingWorker = new SimilaritySearchSwingWorker(index);
			swingWorker.addPropertyChangeListener(new SwingWorkerCompletionWaiter(dialog, swingWorker));
			swingWorker.execute();
			// the dialog will be visible until the SwingWorker is done
			dialog.setVisible(true);
		}
	}
	
	private void finalProject(){
		JDialog dialog = new JDialog(this, true);
		dialog.setTitle("Searching...");
		dialog.setSize(100, 80);
		dialog.getContentPane().add(new JLabel("Please wait......"));
		dialog.setModal(true);
		
		SwingWorker<Void, String> swingWorker = new FinalProjectSwingWorker();
		swingWorker.addPropertyChangeListener(new SwingWorkerCompletionWaiter(dialog, true));
		swingWorker.execute();
		// the dialog will be visible until the SwingWorker is done
		dialog.setVisible(true);
	}
	
	private void cancel() {
		for (int i = 0; i < selected.length; i++)
			selected[i] = false;
		for (int i = 0; i < NumPhotoPerPage; i++){
			ImagePane temp = photoList.get(i);
			temp.clicked = false;
			temp.repaint();
		}
	}

	private void random() {
		randomMode = true;
		for (int i = 0; i < selected.length; i++)
			selected[i] = false;
		try {
			for (int i = 0; i < NumPhotoPerPage; i++){
				ImagePane temp = photoList.get(i);
				temp.clicked = false;
				if (!fileList.isEmpty()){
					int index = (int)(Math.random() * fileList.size());
					indices[i] = index;
					File tempFile = fileList.get(index);
					temp.setImage(ImageIO.read(tempFile));
					temp.setToolTipText(tempFile.getName());
				} else {
					temp.setImage(null);
					temp.setToolTipText(emptyString);
				}
				temp.repaint();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		textField.setText("random search, only one page");
	}

	private void previous() {
		nextIndex -= 2 * NumPhotoPerPage;
		try {
			for (int i = 0, j = nextIndex + NumPhotoPerPage; i < NumPhotoPerPage; i++){
				ImagePane temp = photoList.get(i);
				if (i + j < selected.length){
					if (temp.clicked){
						selected[j + i] = true;
					} else {
						selected[j + i] = false;
					}
				}
				temp.clicked = false;
				if ((nextIndex + i) < fileList.size()){
					File tempFile = fileList.get(nextIndex + i);
					temp.setImage(ImageIO.read(tempFile));
					if (selected[nextIndex + i])
						temp.clicked = true;
					temp.setToolTipText(tempFile.getName());
				} else{
					temp.setImage(null);
					temp.setToolTipText(emptyString);
				}
				temp.repaint();
			}
			int numPage;
			if (fileList.size() % NumPhotoPerPage != 0)
				numPage = fileList.size() / NumPhotoPerPage + 1;
			else
				numPage = fileList.size() / NumPhotoPerPage;
			textField.setText("page " + (nextIndex / NumPhotoPerPage + 1) + " of " + numPage + ", (total " + fileList.size() + " images)");
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			nextIndex += NumPhotoPerPage;
		}
	}

	private void next() {
		try {
			for (int i = 0, j = nextIndex - NumPhotoPerPage; i < NumPhotoPerPage; i++){
				ImagePane temp = photoList.get(i);
				if (i + j < selected.length){
					if (temp.clicked){
						selected[j + i] = true;
					} else {
						selected[j + i] = false;
					}
				}
				temp.clicked = false;
				if ((nextIndex + i) < fileList.size()){
					File tempFile = fileList.get(nextIndex + i);
					temp.setImage(ImageIO.read(tempFile));
					if (selected[nextIndex + i])
						temp.clicked = true;
					temp.setToolTipText(tempFile.getName());
				} else{
					temp.setImage(null);
					temp.setToolTipText(emptyString);
				}
				temp.repaint();
			}
			int numPage;
			if (fileList.size() % NumPhotoPerPage != 0)
				numPage = fileList.size() / NumPhotoPerPage + 1;
			else
				numPage = fileList.size() / NumPhotoPerPage;
			textField.setText("page " + (nextIndex / NumPhotoPerPage + 1) + " of " + numPage + ", (total " + fileList.size() + " images)");
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			nextIndex += NumPhotoPerPage;
		}
	}
	
	@Override
 	public void actionPerformed(ActionEvent e) {
		String str = e.getActionCommand();
		int returnVal = -1;
		switch (str) {
		case EVENT_OPEN:
			returnVal = fileChooser.showOpenDialog(SubUI.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				JDialog dialog = new JDialog(this, true);
				dialog.setTitle("Loading...");
				dialog.setSize(100, 80);
				dialog.getContentPane().add(new JLabel("Please wait......"));
				dialog.setModal(true);
				
				SwingWorker<Void, String> swingWorker = new AddImagesSwingWorker(file);
				swingWorker.addPropertyChangeListener(new SwingWorkerCompletionWaiter(dialog, true));
				swingWorker.execute();
				// the dialog will be visible until the SwingWorker is done
				dialog.setVisible(true);
			}
			break;
		
		case EVENT_EXIT:
			SubUI.this.setVisible(false);
			break;
		case EVENT_PB_VIEW:
			init();
			break;
		case EVENT_PB_NEXT:
			if (nextIndex >= fileList.size() || nextIndex == 0 || randomMode)
				break;
			next();
			break;
		case EVENT_PB_PREVIOUS:
			if (nextIndex <= NumPhotoPerPage || randomMode)
				break;
			previous();
			break;
		case EVENT_PB_CANCEL:
			if (selected != null)
				cancel();
			break;
		case EVENT_PB_RANDOM:
			if (selected != null)
				random();
			break;
		case EVENT_PB_SEARCH:
			if (selected != null)
				do_search();
			break;
		case EVENT_PB_FINAL:
			finalProject();
			break;
		default:
			break;
		}
	}
	
	public int R(int ori, int n){
		int ret = 0;
		
		if (ori < 100){
			if (n < 100)
				ret = 1;
		} else if (ori < 200){
			if (n > 99 && n < 200)
				ret = 1;
		} else if (ori < 300){
			if (n > 199 && n < 300)
				ret = 1;
		} else if (ori < 400){
			if (n > 299 && n < 400)
				ret = 1;
		} else if (ori < 500){
			if (n > 399 && n < 500)
				ret = 1;
		} else if (ori < 600){
			if (n > 499 && n < 600)
				ret = 1;
		} else if (ori < 700){
			if (n > 599 && n < 700)
				ret = 1;
		} else if (ori < 800){
			if (n > 699 && n < 800)
				ret = 1;
		} else if (ori < 900){
			if (n > 799 && n < 900)
				ret = 1;
		} else if (ori < 1000){
			if (n > 899 && n < 1000)
				ret = 1;
		}
		
		return ret;
	}
	
	public int parse(String template_file_name){
		int d = template_file_name.lastIndexOf('_');
		int ret = 0;
		if (d > 0 &&  d < template_file_name.length() - 1) {
			ret = Integer.parseInt(template_file_name.substring(0, d)); 
        }
		
		return ret;
	}

	private class SwingWorkerCompletionWaiter implements PropertyChangeListener {
	     private JDialog dialog;
	     private boolean informWhenFinished;
	     private SwingWorker<Float, String> worker;
	     
	     public SwingWorkerCompletionWaiter(JDialog dialog, SwingWorker<Float, String> worker) {
	    	 this.dialog = dialog;
	    	 this.informWhenFinished = true;
	    	 this.worker = worker;
	     }

	     public SwingWorkerCompletionWaiter(JDialog dialog, boolean informWhenFinished) {
	         this.dialog = dialog;
	         this.informWhenFinished = informWhenFinished;
	         this.worker = null;
	     }

	     public void propertyChange(PropertyChangeEvent event) {
	         if ("state".equals(event.getPropertyName()) && SwingWorker.StateValue.DONE == event.getNewValue()) {
	             dialog.setVisible(false);
	             dialog.dispose();
	             if (informWhenFinished){
	            	 if (worker == null){
	            		 JOptionPane.showMessageDialog(SubUI.this,
	     	 				    "Finished!!!",
	     	 				    "Hint",
	     	 				    JOptionPane.INFORMATION_MESSAGE);
	            	 } else {
	            		 try {
	     					JOptionPane.showMessageDialog(SubUI.this,
	     						    "平均查准率为：" + worker.get(),
	     						    "Hint",
	     						    JOptionPane.INFORMATION_MESSAGE);
	     				} catch (HeadlessException | InterruptedException
	     						| ExecutionException e) {
	     					e.printStackTrace();
	     				}
	            	 }
	             }
	         }
	     }
	 }
	
	private class ReadFileSwingWorker extends SwingWorker<Void, String>{

		@Override
		protected Void doInBackground() throws Exception {
			init();
			File metadata = dataPath.toFile();
			if (metadata.exists()){
				DataInputStream in = null;
				try {
					in = new DataInputStream(new FileInputStream(metadata));
					map = new TreeMap<String, Floats>();
					for (int i = 0; i < fileList.size(); i++){
						float[][] rgb_histogram = new float[3][];//用来储存文件中读入的数据
						for (int j = 0; j < 3; j++){
							rgb_histogram[j] = new float[256];
						}
						
						String filename = in.readUTF();//读取文件路径
						for (int k = 0; k < 3; k++){
							for (int m = 0; m < 256; m++){
								rgb_histogram[k][m] = in.readFloat();//读取这张图片的直方图数据
							}
						}
						map.put(filename, new Floats(rgb_histogram));
						publish(filename);
					}
				} catch (FileNotFoundException e) {
					this.cancel(true);
					e.printStackTrace();
				} catch (IOException e) {
					this.cancel(true);
					e.printStackTrace();
				} finally {
					if (in != null)
						try {
							in.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
				}
			}
			return null;
		}
		
		@Override
		protected void process(List<String> chunks) {
			for (String str : chunks)
				textField.setText(str);
		}
		
		@Override
		protected void done() {
			
		}
	}
	
	private class FinalProjectSwingWorker extends SwingWorker<Void, String>{

		@Override
		protected Void doInBackground() throws Exception {
			if (map != null && !map.isEmpty()) {
				File results = resultsPath.toFile();
				if (results.exists())
					results.delete();
				results.createNewFile();
				
				PrintWriter out = new PrintWriter(results);
				
				Collections.sort(fileList, new Comparator<File>(){

					@Override
					public int compare(File f1, File f2) {
						return (parse(f1.getName()) - parse(f2.getName()));
					}
					
				});
				
				for (File template_file : fileList){
					float[][] template_histogram = Charts.histogram(ImageIO.read(template_file));// 对缩略图计算直方图
					ArrayList<MyFile> tempFileList = new ArrayList<MyFile>();
					for (Entry<String, Floats> entry : map.entrySet()) {
						String filename = entry.getKey();
						float[][] rgb_histogram = entry.getValue().data;
						float similarity = 0;
						for (int k = 0; k < 3; k++) {
							float variance = 0;
							float dif;
							for (int m = 0; m < 256; m++) {
								dif = rgb_histogram[k][m] - template_histogram[k][m];
								variance += dif * dif;
							}
							similarity += variance;
						}
						tempFileList.add(new MyFile(new File(filename), similarity));
					}
					Collections.sort(tempFileList);
					for (MyFile file: tempFileList){
						out.write(String.valueOf(parse(file.file.getName()))+emptyString2);
					}
					out.println();
					publish(template_file.getName());
				}
				out.close();
			} else {
				this.cancel(true);
			}

			return null;
		}
		
		@Override
		protected void process(List<String> chunks) {
			for (String str : chunks)
				textField.setText(str);
		}

	}
	
	private class SimilaritySearchSwingWorker extends SwingWorker<Float, String>{
		int index;
		
		public SimilaritySearchSwingWorker(int i) {
			index = i;
		}

		@Override
		protected Float doInBackground() throws Exception {
			if (map != null && !map.isEmpty()) {
				File template_file = fileList.get(index);
				float[][] template_histogram = Charts.histogram(ImageIO.read(template_file));// 对缩略图计算直方图

				ArrayList<MyFile> tempFileList = new ArrayList<MyFile>();
				for (Entry<String, Floats> entry : map.entrySet()) {
					String filename = entry.getKey();
					float[][] rgb_histogram = entry.getValue().data;

					// TODO
					// float sum = 0;
					float similarity = 0;
					for (int k = 0; k < 3; k++) {
						float variance = 0;
						float dif;
						for (int m = 0; m < 256; m++) {
							dif = rgb_histogram[k][m]
									- template_histogram[k][m];
							// sum = rgb_histogram[k][m] +
							// template_histogram[k][m];
							variance += dif * dif;
						}
						// variance = variance / 256;
						similarity += variance;
					}

					tempFileList.add(new MyFile(new File(filename), similarity));
					publish(filename);
				}

				Collections.sort(tempFileList);
				fileList.clear();
				for (int i = 0; i < tempFileList.size(); i++) {
					fileList.add(tempFileList.get(i).file);
				}

				int ori = parse(template_file.getName());
				
				float p = 0;
				float sum = 0;//到目前为止相似图像的总数
				for (int i = 1; i <= fileList.size(); i++){
					File file = fileList.get(i - 1);
					if (R(ori, parse(file.getName())) == 1){
						sum += 1f;
						p += sum / i;
					}
				}
				
				return (p / 100);
			} else {
				this.cancel(true);
			}

			return null;
		}
		
		@Override
		protected void process(List<String> chunks) {
			for (String str : chunks)
				textField.setText(str);
		}
		
		@Override
		protected void done() {
			if (!this.isCancelled()){
				randomMode = false;
				nextIndex = 0;
				for (int i = 0; i < selected.length; i++)
					selected[i] = false;
				try {
					for (int i = 0; i < NumPhotoPerPage; i++){
						ImagePane temp = photoList.get(i);
						temp.clicked = false;
						if ((nextIndex + i) < fileList.size()){
							File tempFile = fileList.get(nextIndex + i);
							temp.setImage(ImageIO.read(tempFile));
							temp.setToolTipText(tempFile.getName());
						} else{
							temp.setImage(null);
							temp.setToolTipText(emptyString);
						}
						temp.repaint();
					}
					textField.setText("similarity search, page 1");
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					nextIndex += NumPhotoPerPage;
				}
				
			}
		}
	}
	
	private class AddImagesSwingWorker extends SwingWorker<Void, String>{
		private File file;
		
		public AddImagesSwingWorker(File f) {
			file = f;
		}

		@Override
		protected Void doInBackground() throws Exception {
			//create the root directory for this program, in /user/
			File dir = rootPath.toFile();
			if (!dir.exists()){
				dir.mkdir();
			}
			//create the directory to store thumbnails
			File imgDir = imgPath.toFile();
			if (!imgDir.exists()){
				imgDir.mkdir();
			}else {
				for (File f : imgDir.listFiles()){
					f.delete();
				}
			}
			//create the file to store metadata
			File metadata = dataPath.toFile();
			if (metadata.exists())
				metadata.delete();
			metadata.createNewFile();
			
			DataOutputStream out = new DataOutputStream(new FileOutputStream(metadata));
//			int id = 0;//文件中唯一标识一张图片
			if (map == null)
				map = new TreeMap<String, Floats>();
			else
				map.clear();
			
			File[] files = file.listFiles(new MyImageFilter2());//得到文件夹下的所有图片
			for (File f : files){//对每一张图片
				BufferedImage img = ImageIO.read(f);//把它读出来
				int width = img.getWidth();//得到宽度
				int height = img.getHeight();//得到高度
				int desiredW, desiredH;//设置缩小后的尺寸
				if (width > height){
					desiredW = 160;
					desiredH = 160 * height / width;
				} else{
					desiredH = 160;
					desiredW = 160 * width / height;
				}

		    	String newFileName = null;//thumbnail的文件名（路径）
		    	String s = f.getName();//当前图片的文件名
		    	int index = s.lastIndexOf('.');
		    	//根据当前图片的文件名构造thumbnail的文件名
		    	if (index > 0 &&  index < s.length() - 1) {
		    		newFileName = s.substring(0, index) + "_tmb." + s.substring(index+1).toLowerCase();
		        }
		    	
				File newFile = imgPath.resolve(newFileName).toFile();//thumbnail的路径
				ImageIO.write(getScaledImage(img, desiredW, desiredH), "jpg", newFile);//把缩略图输出
				
				out.writeUTF(newFile.toString());
				float[][] datas = Charts.histogram(img);
				for (int i = 0; i < 3; i++)
					for (int j = 0; j < 256; j++)
						out.writeFloat(datas[i][j]);
				
				map.put(imgPath.resolve(newFileName).toString(), new Floats(datas));
				publish(newFile.getName());//把进度告知UI
			}
			out.close();
			
			return null;
		}
		
		@Override
		protected void process(List<String> chunks) {
			for (String str : chunks)
				textField.setText(str);
		}
		
		@Override
		protected void done() {
			init();
		}
		/**
	     * Resizes an image using a Graphics2D object backed by a BufferedImage.
	     * @param srcImg - source image to scale
	     * @param w - desired width
	     * @param h - desired height
	     * @return - the new resized image
	     */
	    private BufferedImage getScaledImage(BufferedImage srcImg, int w, int h){
	        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
	        Graphics2D g2 = resizedImg.createGraphics();
	        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	        g2.drawImage(srcImg, 0, 0, w, h, null);
	        g2.dispose();
	        return resizedImg;
	    }
	    
	}

	private class MyMouseListener implements MouseListener {
		private ImagePane imagePane;

		public MyMouseListener(ImagePane ip) {
			imagePane = ip;
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {

		}

		@Override
		public void mousePressed(MouseEvent arg0) {

		}

		@Override
		public void mouseExited(MouseEvent arg0) {

		}

		@Override
		public void mouseEntered(MouseEvent arg0) {

		}

		@Override
		public void mouseClicked(MouseEvent arg0) {
			if (imagePane.getImage() != null) {
				if (imagePane.clicked) {
					imagePane.clicked = false;
				} else {
					imagePane.clicked = true;
				}
				imagePane.repaint();
			}
		}
	}

	private class MyWindowListener implements WindowListener {

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
			JDialog dialog = new JDialog(SubUI.this, true);
			dialog.setTitle("Loading...");
			dialog.setSize(100, 80);
			dialog.getContentPane().add(new JLabel("Please wait......"));
			dialog.setModal(true);
			
			SwingWorker<Void, String> swingWorker = new ReadFileSwingWorker();
			swingWorker.addPropertyChangeListener(new SwingWorkerCompletionWaiter(dialog, false));
			swingWorker.execute();
			// the dialog will be visible until the SwingWorker is done
			dialog.setVisible(true);
		}
	}
}
