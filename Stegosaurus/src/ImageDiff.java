import java.util.Scanner;

import processing.core.PApplet;
import processing.core.PImage;

public class ImageDiff extends PApplet {

	private static final long serialVersionUID = 8310742690921415484L;

	private PImage original1;
	private PImage original2;
	private PImage modified;
	
	public void setup() {
		size(800, 600);
		ellipseMode(CENTER);
		rectMode(CENTER);
		colorMode(RGB, 255, 255, 255, 255);
		noStroke();
		imageMode(CENTER);
		
		original1 = this.loadImage(inFile1);
		original2 = this.loadImage(inFile2);
		modified = genDiff(original1, original2);
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
	
	public PImage genDiff(PImage original1, PImage original2) {
		PImage img = createImage(original1.width, original1.height, ARGB);
		int twoToLSB = 0x00000001 << 1;
		for (int x = 0; x < original1.width; x++) {
			for (int y = 0; y < original1.height; y++) {
				int col1 = original1.get(x, y);
				int col2 = original2.get(x, y);
				
				int red1 = getRed(col1) % twoToLSB;
				int green1 = getGreen(col1) % twoToLSB;
				int blue1 = getBlue(col1) % twoToLSB;
				
				int red2 = getRed(col2) % twoToLSB;
				int green2 = getGreen(col2) % twoToLSB;
				int blue2 = getBlue(col2) % twoToLSB;
				
				int red = red1 ^ red2;
				int green = green1 ^ green2;
				int blue = blue1 ^ blue2;
								
				//int fin = 0x00 | (red << 7) | (green << 6) | (blue << 5);
				int col = 0xFF000000 | (red << (8-1+16)) | (green << (8-1+8)) | (blue << (8-1));
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
	static String inFile2;
	
	public static void main(String[] args) {
		
		Scanner in = new Scanner(System.in);
		
		System.out.print("Image 1: ");
		inFile1 = "images/" + in.nextLine();
		
		System.out.print("Image 2: ");
		inFile2 = "images/" + in.nextLine();
		
		PApplet.main(new String[] {ImageDiff.class.getName()});
	}
	
}
