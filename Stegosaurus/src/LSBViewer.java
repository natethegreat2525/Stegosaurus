import java.util.Scanner;

import processing.core.PApplet;
import processing.core.PImage;

public class LSBViewer extends PApplet {

	private static final long serialVersionUID = 8310742690921415484L;

	private PImage original;
	private PImage modified;
	
	public void setup() {
		size(800, 600);
		ellipseMode(CENTER);
		rectMode(CENTER);
		colorMode(RGB, 255, 255, 255, 255);
		noStroke();
		imageMode(CENTER);
		
		original = this.loadImage(inFile1);
		modified = genLowBitImage(original);
	}
	
	public void draw() {
		background(0);
		g.pushMatrix();
		
		
		g.translate(400, 300);
		
		if (modified.height > 500) {
			g.scale(500.0f/modified.height);
		}
		
		this.image(modified, 0, 0);
		g.popMatrix();
	}
	
	public PImage genLowBitImage(PImage original) {
		PImage img = createImage(original.width, original.height, ARGB);
		int twoToLSB = 0x00000001 << lsbs;
		for (int x = 0; x < original.width; x++) {
			for (int y = 0; y < original.height; y++) {
				int col = original.get(x, y);
				int red = getRed(col) % twoToLSB;
				int green = getGreen(col) % twoToLSB;
				int blue = getBlue(col) % twoToLSB;
				//int fin = 0x00 | (red << 7) | (green << 6) | (blue << 5);
				col = 0xFF000000 | (red << (8-lsbs+16)) | (green << (8-lsbs+8)) | (blue << (8-lsbs));
				img.set(x, y, col | 0x00000000);
			}
		}
		img.updatePixels();
		return img;
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
	
	static String inFile1;
	static int lsbs = 1;
	
	public static void main(String[] args) {
		
		Scanner in = new Scanner(System.in);
		
		System.out.print("File: ");
		inFile1 = "images/" + in.nextLine();
		
		System.out.print("Number of LSBs: ");
		lsbs = Integer.parseInt(in.nextLine());
		
		PApplet.main(new String[] {LSBViewer.class.getName()});
	}
	
}
