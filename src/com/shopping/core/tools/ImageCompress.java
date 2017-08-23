package com.shopping.core.tools;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Label;
import java.awt.MediaTracker;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import javax.imageio.ImageIO;

public class ImageCompress extends Frame {
	private static final long serialVersionUID = 48L;
	private static final String version = "ImageCompress v1.0";
	private Panel mControlPanel;
	private BufferedImage mBufferedImage;
	private Label labelWidth = new Label("width:");
	private TextField textWidth = new TextField(7);

	private Label labelHeight = new Label("height:");
	private TextField textHeight = new TextField(7);
	private String file;

	public static void main(String[] args) {
		String fileName = "F:/af4496a4-15ae-47c9-8279-6e095ebfd539.png";
		String gui = "";
		if (args.length > 0)
			fileName = args[0];
		if (args.length > 1)
			gui = "gui";
		if (gui.equals("gui")) {
			new ImageCompress(fileName);
		} else {
			long c = System.currentTimeMillis();
			ImageScale(fileName, fileName + "-s." + getFileExt(fileName).toLowerCase(), 160, 160);
			System.out.println("elapse time:" + (float) (System.currentTimeMillis() - c) / 1000.0F + "s");
		}
	}

	public ImageCompress(String fileName) {
		super("ImageCompress v1.0");
		this.file = fileName;
		createUI();
		loadImage(fileName);
		setVisible(true);
	}

	private void createUI() {
		setFont(new Font("Serif", 0, 12));

		setLayout(new BorderLayout());

		Label statusLabel = new Label("Welcome to ImageCompress v1.0.");

		this.textWidth.setText("160");
		this.textHeight.setText("160");

		Button loadButton = new Button("Load...");

		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				FileDialog fd = new FileDialog(ImageCompress.this);
				fd.setVisible(true);
				if (fd.getFile() == null)
					return;
				String path = fd.getDirectory() + fd.getFile();
				ImageCompress.this.file = path;
				ImageCompress.this.loadImage(path);
			}
		});
		Button buttonResize = new Button("Resize");
		buttonResize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				ImageCompress.this.resizeImage(ImageCompress.this.file);
			}
		});
		this.mControlPanel = new Panel();
		this.mControlPanel.add(loadButton);
		this.mControlPanel.add(this.labelWidth);
		this.mControlPanel.add(this.textWidth);
		this.mControlPanel.add(this.labelHeight);
		this.mControlPanel.add(this.textHeight);
		this.mControlPanel.add(buttonResize);

		this.mControlPanel.add(statusLabel);
		add(this.mControlPanel, "South");

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				ImageCompress.this.dispose();
				System.exit(0);
			}
		});
	}

	private void resizeImage(String fileName) {
		try {
			Image image = ImageIO.read(new File(this.file));
			int imageWidth = image.getWidth(null);
			int imageHeight = image.getHeight(null);

			float scale = getRatio(imageWidth, imageHeight, Integer.parseInt(this.textWidth.getText()), Integer.parseInt(this.textWidth.getText()));
			imageWidth = (int) (scale * imageWidth);
			imageHeight = (int) (scale * imageHeight);

			image = image.getScaledInstance(imageWidth, imageHeight, 16);

			this.mBufferedImage = new BufferedImage(imageWidth, imageHeight, 1);
			Graphics2D g2 = this.mBufferedImage.createGraphics();

			g2.drawImage(image, 0, 0, imageWidth, imageHeight, Color.white, null);

			float[] kernelData2 = { -0.125F, -0.125F, -0.125F, -0.125F, 2.0F, -0.125F, -0.125F, -0.125F, -0.125F };
			Kernel kernel = new Kernel(3, 3, kernelData2);
			ConvolveOp cOp = new ConvolveOp(kernel, 1, null);
			this.mBufferedImage = cOp.filter(this.mBufferedImage, null);
			repaint();
		} catch (IOException localIOException) {
		}
	}

	private void loadImage(String fileName) {
		Image image = Toolkit.getDefaultToolkit().getImage(fileName);
		MediaTracker mt = new MediaTracker(this);
		mt.addImage(image, 0);
		try {
			mt.waitForID(0);
		} catch (InterruptedException ie) {
			return;
		}
		if (mt.isErrorID(0)) {
			return;
		}
		this.mBufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), 1);
		Graphics2D g2 = this.mBufferedImage.createGraphics();
		g2.drawImage(image, null, null);
		adjustToImageSize();
		center();
		validate();
		repaint();
		setTitle("ImageCompress v1.0: " + fileName);
	}

	private void adjustToImageSize() {
		if (!isDisplayable())
			addNotify();
		Insets insets = getInsets();
		int imageWidth = this.mBufferedImage.getWidth();
		int imageHeight = this.mBufferedImage.getHeight();
		imageWidth = 600;
		imageHeight = 550;
		int w = imageWidth + insets.left + insets.right;
		int h = imageHeight + insets.top + insets.bottom;
		h += this.mControlPanel.getPreferredSize().height;
		setSize(w, h);
	}

	private void center() {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension d = getSize();
		int x = (screen.width - d.width) / 2;
		int y = (screen.height - d.height) / 2;
		setLocation(x, y);
	}

	public void paint(Graphics g) {
		if (this.mBufferedImage == null)
			return;
		Insets insets = getInsets();
		g.drawImage(this.mBufferedImage, insets.left, insets.top, null);
	}

	public static void ImageScale(String sourceImg, String targetImg, int width, int height) {
		try {
			Image image = ImageIO.read(new File(sourceImg));
			int imageWidth = image.getWidth(null);
			int imageHeight = image.getHeight(null);
			float scale = getRatio(imageWidth, imageHeight, width, height);
			imageWidth = (int) (scale * imageWidth);
			imageHeight = (int) (scale * imageHeight);

			image = image.getScaledInstance(imageWidth, imageHeight, 16);

			BufferedImage mBufferedImage = new BufferedImage(imageWidth, imageHeight, 1);
			Graphics2D g2 = mBufferedImage.createGraphics();

			g2.drawImage(image, 0, 0, imageWidth, imageHeight, Color.white, null);
			g2.dispose();

			float[] kernelData2 = { -0.125F, -0.125F, -0.125F, -0.125F, 2.0F, -0.125F, -0.125F, -0.125F, -0.125F };
			Kernel kernel = new Kernel(3, 3, kernelData2);
			ConvolveOp cOp = new ConvolveOp(kernel, 1, null);
			mBufferedImage = cOp.filter(mBufferedImage, null);
			FileOutputStream out = new FileOutputStream(targetImg);

			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			encoder.encode(mBufferedImage);
			out.close();
		} catch (FileNotFoundException localFileNotFoundException) {
		} catch (IOException localIOException) {
		}
	}

	public static float getRatio(int width, int height, int maxWidth, int maxHeight) {
		float Ratio = 1.0F;

		float widthRatio = (float) maxWidth / width;
		float heightRatio = (float) maxHeight / height;
		if ((widthRatio < 1.0D) || (heightRatio < 1.0D)) {
			Ratio = widthRatio <= heightRatio ? widthRatio : heightRatio;
		}
		return Ratio;
	}

	public static String getFileExt(String filePath) {
		String tmp = filePath.substring(filePath.lastIndexOf(".") + 1);
		return tmp.toUpperCase();
	}

	public static String getFileName(String filePath) {
		int pos = -1;
		int endPos = -1;
		if (!filePath.equals("")) {
			if (filePath.lastIndexOf("/") != -1)
				pos = filePath.lastIndexOf("/") + 1;
			else if (filePath.lastIndexOf("//") != -1) {
				pos = filePath.lastIndexOf("//") + 1;
			}
			if (pos == -1)
				pos = 0;
			filePath = filePath.substring(pos);
			endPos = filePath.lastIndexOf(".");
			if (endPos == -1) {
				return filePath;
			}
			return filePath.substring(0, endPos);
		}

		return "";
	}

	public static String getFileFullName(String filePath) {
		int pos = -1;
		if (!filePath.equals("")) {
			if (filePath.lastIndexOf("/") != -1)
				pos = filePath.lastIndexOf("/") + 1;
			else if (filePath.lastIndexOf("//") != -1) {
				pos = filePath.lastIndexOf("//") + 1;
			}
			if (pos == -1)
				pos = 0;
			return filePath.substring(pos);
		}
		return "";
	}

	public static String getFilePath(String filePath) {
		int pos = -1;
		if (!filePath.equals("")) {
			if (filePath.lastIndexOf("/") != -1)
				pos = filePath.lastIndexOf("/") + 1;
			else if (filePath.lastIndexOf("//") != -1) {
				pos = filePath.lastIndexOf("//") + 1;
			}
			if (pos != -1) {
				return filePath.substring(0, pos);
			}
			return "";
		}

		return "";
	}
}
