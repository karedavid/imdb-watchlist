package utilities;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class ImageProcessor {
	
	//Resize image
	public static BufferedImage resizeImage(BufferedImage img, int newW, int newH) { 
	    Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
	    BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2d = dimg.createGraphics();
	    g2d.drawImage(tmp, 0, 0, null);
	    g2d.dispose();

	    return dimg;
	}
	
	//Save image to PNG
	public static void saveImage(BufferedImage image, String filename) throws IOException{
		if(image == null) {
			return;
		}
		File outputfile = new File("userdata" + File.separator + "covers" + File.separator + filename +".png");
		ImageIO.write(image, "png", outputfile);
	}
	
	//Load cover from storage
	public static BufferedImage loadImage(String filename){
		File inputfile = new File("userdata" + File.separator + "covers" + File.separator + filename +".png");
		BufferedImage image = null;
		
		try {
			image = ImageIO.read(inputfile);
		} catch (IOException e) {
			try {
				//If cover missing, load default cover
				image = ImageIO.read(ImageProcessor.class.getResource("/images/nocover.png"));
			} catch (IOException e1) {
				//Default cover missing : corrupted program files
				System.err.println("Fatal error: program files are missing.");
				e1.printStackTrace();
				System.exit(1);
			}
		}
		
		return image;
	}
	
	//Download cover from Internet
	public static BufferedImage downloadImage(String urlAsString){
		URL url;
		BufferedImage image = null;
		
		try {
			url = new URL(urlAsString);
			image = ImageIO.read(url);
		} catch (IOException e) {
				try {
					//If cover missing, load default cover
					image = ImageIO.read(ImageProcessor.class.getResource("/images/nocover.png"));
				} catch (IOException e1) {
					//Default cover missing : corrupted program files
					System.err.println("Fatal error: program files are missing.");
					e1.printStackTrace();
					System.exit(1);
				}
		}
		
		return image;
	}

}
