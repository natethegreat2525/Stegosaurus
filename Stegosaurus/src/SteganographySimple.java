

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class SteganographySimple {

	public static void start() {
		
		if (encrypt)
			encrypt();
		else
			decrypt();
		
	}
	
	public static void encrypt() {
		
		BufferedImage img = null;
		try {
		    img = ImageIO.read(new File(inFile2));
		} catch (IOException e) {
		}
		
		try {
			byte[] bytes = Files.readAllBytes(Paths.get(inFile1));
			int num = bytes.length;
			int ptr = 0;
						
			ptr = writeByteToImage(img, ptr, (byte) (num & 0x000000FF));
			ptr = writeByteToImage(img, ptr, (byte) ((num & 0x0000FF00) >> 8));
			ptr = writeByteToImage(img, ptr, (byte) ((num & 0x00FF0000) >> 16));
			ptr = writeByteToImage(img, ptr, (byte) ((num & 0xFF000000) >> 24));
			
			for (int i = 0; i < num; i++) {
				ptr = writeByteToImage(img, ptr, bytes[i]);
			}
			
			File outputfile = new File(outFile);
		    ImageIO.write(img, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void decrypt() {
		BufferedImage img = null;
		try {
		    img = ImageIO.read(new File(inFile2));
		} catch (IOException e) {
		}
		
		int ptr = 0;
		
		byte first = readByteFromImage(img, ptr); ptr=incPtr(ptr);
		byte second = readByteFromImage(img, ptr); ptr=incPtr(ptr);
		byte third = readByteFromImage(img, ptr); ptr=incPtr(ptr);
		byte fourth = readByteFromImage(img, ptr); ptr=incPtr(ptr);
		
		int num = first + (second << 8) + (third << 16) + (fourth << 24);

		byte[] bytes = new byte[num];
		for (int i = 0; i < num; i++) {
			bytes[i] = readByteFromImage(img, ptr); ptr=incPtr(ptr);
		}
		
		try {
			BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(new File(inFile1)));
			output.write(bytes);
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static int writeByteToImage(BufferedImage img, int ptr, byte b) {
		int val = ptr/4;
		int x = val%img.getWidth();
		int y = val/img.getWidth();
		
		write4bits(img, x, y, (b & 0x02) != 0, (b & 0x01) != 0, (b & 0x04) != 0, (b & 0x08) != 0);
		
		val++;
		x = val%img.getWidth();
		y = val/img.getWidth();
		
		write4bits(img, x, y, (b & 0x20) != 0, (b & 0x10) != 0, (b & 0x40) != 0, (b & 0x80) != 0);

		return incPtr(ptr);
	}
	
	public static byte readByteFromImage(BufferedImage img, int ptr) {
		int val = ptr/4;
		int x = val%img.getWidth();
		int y = val/img.getWidth();
		
		byte ret = read4bits(img, x, y);
		
		val++;
		x = val%img.getWidth();
		y = val/img.getWidth();
		
		return (byte) (ret | (read4bits(img, x, y) << 4));
	}
	
	public static int incPtr(int ptr) { return ptr + 8; }
	
	public static void write4bits(BufferedImage img, int x, int y, boolean a, boolean b, boolean c, boolean d) {
		int alpha = getAlpha(img.getRGB(x, y));
		int red = getRed(img.getRGB(x, y));
		int green = getGreen(img.getRGB(x, y));
		int blue = getBlue(img.getRGB(x, y));

		alpha = (alpha/2)*2 + (a ? 1 : 0);
		red = (red/2)*2 + (b ? 1 : 0);
		green = (green/2)*2 + (c ? 1 : 0);
		blue = (blue/2)*2 + (d ? 1 : 0);

		img.setRGB(x, y, (alpha << 24) | (red << 16) | (green << 8) | blue);
	}
	
	public static byte read4bits(BufferedImage img, int x, int y) {
		byte ret = 0x00;
		
		boolean a = getAlpha(img.getRGB(x, y)) % 2 == 1;
		boolean b = getRed(img.getRGB(x, y)) % 2 == 1;
		boolean c = getGreen(img.getRGB(x, y)) % 2 == 1;
		boolean d = getBlue(img.getRGB(x, y)) % 2 == 1;
		
		if (a)
			ret = (byte) (ret | 0x02);
		
		if (b)
			ret = (byte) (ret | 0x01);
		
		if (c)
			ret = (byte) (ret | 0x04);
		
		if (d)
			ret = (byte) (ret | 0x08);
		
		return ret;
	}
	
	public static int getAlpha(int val) {
		return ((val & 0xFF000000) >> 24) & 0x000000FF;
	}
	
	public static int getRed(int val) {
		return (val & 0x00FF0000) >> 16;
	}
	
	public static int getGreen(int val) {
		return (val & 0x0000FF00) >> 8;
	}
	
	public static int getBlue(int val) {
		return val & 0x000000FF;
	}
	
	//Encrypt or decrypt flag
	static boolean encrypt;
	
	//Either the secret file to encrypt or the output file
	static String inFile1;
	
	//The secret image file
	static String inFile2;
	
	static String outFile;
	
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		
		System.out.print("(e)ncrypt or (d)ecrypt: ");
		String mode = in.nextLine();
		encrypt = mode.toLowerCase().charAt(0) == 'e';
		
		String path = "images/";
		if (encrypt) {
			System.out.print("Plaintext File Location: ");
			inFile1 = path + in.nextLine();
			
			System.out.print("Original Image Location: ");
			inFile2 = path + in.nextLine();
			
			System.out.print("Steg Image Location: ");
			outFile = path + in.nextLine();
		} else {
			System.out.print("Steg Image Location: ");
			inFile2 = path + in.nextLine();
			
			System.out.print("Output File: ");
			inFile1 = path + in.nextLine();
		}
	
		start();
	}
	
}
