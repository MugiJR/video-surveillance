import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.media.Buffer;
import javax.media.Effect;
import javax.media.Format;
import javax.media.Player;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.media.util.BufferToImage;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class MotionDetectionEffect implements Effect {
	/**
	 * The initial square side.
	 */
	public static Player player = null;
	public Buffer buf;
	public BufferToImage btoi;
	public Image img;
	public int OPTIMIZATION = 0;
    boolean tag=true;
	/**
	 * Maximum threshold setting. Setting the threshold above this means to get
	 * the motion detection to pass the frame you pretty much have to full the
	 * whole frame with lots of motions (ie: drop the camera)
	 */
	public int THRESHOLD_MAX = 10000;

	/**
	 * By what value you should increment.
	 */
	public int THRESHOLD_INC = 1000;
	public boolean debug = false;
	/**
	 * The initial threshold setting.
	 */
	public int THRESHOLD_INIT = 5000;
	public int blob_threshold = THRESHOLD_INIT;
	private final static int INITIAL_SQUARE_SIZE = 5;

	public final static Format[] supportedFormat = new Format[] { //
	new RGBFormat(null, //
			Format.NOT_SPECIFIED, //
			Format.byteArray, //
			Format.NOT_SPECIFIED, //
			24, //
			3, 2, 1, //
			3, Format.NOT_SPECIFIED, //
			Format.TRUE, //
			Format.NOT_SPECIFIED) //
	};

	private Format inputFormat;

	private Format outputFormat;

	private Format[] inputFormats;

	private Format[] outputFormats;

	private int[] bwPixels;

	private byte[] bwData;

	/**
	 * Visual mode is set.
	 */
	private boolean visualize = true;

	/**
	 * Server mode is set.
	 */
	private boolean serverActive = true;

	/**
	 * Update requested is set.
	 */
	private boolean updateRequested;

	private int avg_ref_intensity;

	private int avg_img_intensity;

	/**
	 * The RGBFormat of the inbuffer.
	 */
	private RGBFormat vfIn = null;

	/**
	 * Four different thresholds. Set initial values here.
	 */
	private int[] threshs = { 50, 100, 150, 200 };

	private int det_thresh = threshs[1];

	/**
	 * The corresponding colours to the four different thresholds.
	 */
	private int[] colors = { 0x00FF0000, 0x00FF9900, 0x00FFFF00, 0x00FFFFFF };

	/**
	 * The mean values of all squares in an image.
	 */
	private int[] newImageSquares = null;

	/**
	 * The mean values of all squares in an image.
	 */
	private int[] oldImageSquares = null;

	/**
	 * The difference of all the mean values of all squares in an image.
	 */
	private int[] changedSquares = null;

	/**
	 * The number of squares fitted in the image.
	 */
	private int numberOfSquaresWide;

	/**
	 * The number of squares fitted in the image.
	 */
	private int numberOfSquaresHigh;

	/**
	 * The number of squares fitted in the image.
	 */
	private int numberOfSquares;

	/**
	 * The square side, in pixels.
	 */
	private int sqSide = INITIAL_SQUARE_SIZE;

	/**
	 * The square area, in pixels.
	 */
	private int sqArea = 0;

	/**
	 * The amount of pixels left when all normal sized squares have been
	 * removed.
	 */
	private int sqWidthLeftover = 0;

	/**
	 * The amount of pixels left when all normal sized squares have been
	 * removed.
	 */
	private int sqHeightLeftover = 0;

	/**
	 * Optional, less processing is needed if some pixels are left out during
	 * some of the calculations.
	 */
	private int pixelSpace = 0;

	/**
	 * Image property.
	 */
	private int imageWidth = 0;

	/**
	 * Image property.
	 */
	private int imageHeight = 0;

	/**
	 * Image property.
	 */
	private int imageArea = 0;

	/**
	 * Initialize the effect plugin.
	 */
	public MotionDetectionEffect() {
		inputFormats = new Format[] { new RGBFormat(null, Format.NOT_SPECIFIED,
				Format.byteArray, Format.NOT_SPECIFIED, 24, 3, 2, 1, 3,
				Format.NOT_SPECIFIED, Format.TRUE, Format.NOT_SPECIFIED) };

		outputFormats = new Format[] { new RGBFormat(null,
				Format.NOT_SPECIFIED, Format.byteArray, Format.NOT_SPECIFIED,
				24, 3, 2, 1, 3, Format.NOT_SPECIFIED, Format.TRUE,
				Format.NOT_SPECIFIED) };

	}

	/**
	 * Get the inputformats that we support.
	 * 
	 * @return All supported Formats.
	 */
	public Format[] getSupportedInputFormats() {
		return inputFormats;
	}

	/**
	 * Get the outputformats that we support.
	 * 
	 * @param input
	 *            the current inputformat.
	 * @return All supported Formats.
	 */
	public Format[] getSupportedOutputFormats(Format input) {
		if (input == null) {
			return outputFormats;
		}
		if (matches(input, inputFormats) != null) {
			return new Format[] { outputFormats[0].intersects(input) };
		} else {
			return new Format[0];
		}
	}

	/**
	 * Set the input format.
	 * 
	 */
	public Format setInputFormat(Format input) {
		inputFormat = input;
		return input;
	}

	/**
	 * Set our output format.
	 * 
	 */
	public Format setOutputFormat(Format output) {

		if (output == null || matches(output, outputFormats) == null)
			return null;

		RGBFormat incoming = (RGBFormat) output;

		Dimension size = incoming.getSize();
		int maxDataLength = incoming.getMaxDataLength();
		int lineStride = incoming.getLineStride();
		float frameRate = incoming.getFrameRate();
		int flipped = incoming.getFlipped();
		int endian = incoming.getEndian();

		if (size == null)
			return null;
		if (maxDataLength < size.width * size.height * 3)
			maxDataLength = size.width * size.height * 3;
		if (lineStride < size.width * 3)
			lineStride = size.width * 3;
		if (flipped != Format.FALSE)
			flipped = Format.FALSE;

		outputFormat = outputFormats[0].intersects(new RGBFormat(size,
				maxDataLength, null, frameRate, Format.NOT_SPECIFIED,
				Format.NOT_SPECIFIED, Format.NOT_SPECIFIED,
				Format.NOT_SPECIFIED, Format.NOT_SPECIFIED, lineStride,
				Format.NOT_SPECIFIED, Format.NOT_SPECIFIED));

		return outputFormat;
	}

	/**
	 * Process the buffer. This is where motion is analysed and optionally
	 * visualized.
	 * 
	 */

	public synchronized int process(Buffer inBuffer, Buffer outBuffer) {
		int outputDataLength = ((VideoFormat) outputFormat).getMaxDataLength();
		validateByteArraySize(outBuffer, outputDataLength);
		outBuffer.setLength(outputDataLength);
		outBuffer.setFormat(outputFormat);
		outBuffer.setFlags(inBuffer.getFlags());

		byte[] inData = (byte[]) inBuffer.getData();
		byte[] outData = (byte[]) outBuffer.getData();
		int[] sqAvg = null;
		int[] refsqAvg = null;

		vfIn = (RGBFormat) inBuffer.getFormat();
		Dimension sizeIn = vfIn.getSize();

		int pixStrideIn = vfIn.getPixelStride();
		int lineStrideIn = vfIn.getLineStride();

		imageWidth = (vfIn.getLineStride()) / 3; // Divide by 3 since each pixel
													// has 3 colours.
		imageHeight = ((vfIn.getMaxDataLength()) / 3) / imageWidth;
		imageArea = imageWidth * imageHeight;

		int r, g, b = 0; // Red, green and blue values.

		if (oldImageSquares == null) { // For the first frame.
			changeSqSize(INITIAL_SQUARE_SIZE);
			updateRequested = true;
		}

		// Copy all data from the inbuffer to the outbuffer. The purpose is to
		// display the video input on the screen.
		System.arraycopy(inData, 0, outData, 0, outData.length);

		// Simplify the image to black and white, image information shrinks to
		// one third of the original amount. Less processing needed.
		bwPixels = new int[outputDataLength / 3];
		for (int ip = 0; ip < outputDataLength; ip += 3) {
			int bw = 0;
			r = (int) inData[ip] & 0xFF;
			g = (int) inData[ip + 1] & 0xFF;
			b = (int) inData[ip + 2] & 0xFF;
			bw = (int) ((r + b + g) / (double) 3);
			bwPixels[ip / 3] = bw; // Now containing a black and white image.
		}

		if (updateRequested) {
			updateRequested = false;
			updateSquares();
			return BUFFER_PROCESSED_OK;
		} else {
			updateSquares();
			oldNewChange();
			int c = 0;
			for (int i = 0; i < changedSquares.length; i++) {
				if (changedSquares[i] > det_thresh) {
					c++;
				}
			}
			
				
				 

			if (c > 40 && serverActive && tag) {

				tag = false;
				
				Timer t = new Timer();
				t.schedule(new TimerTask(){
					public void run(){
						tag =true;
					}
					
				},1*1000);

				Date date = new Date();
				//DateFormat formatter = new SimpleDateFormat("yyyy-MMM-dd-HH-mm-ss");
				DateFormat formatter = new SimpleDateFormat("MMMddHHmmss");
				
				String s = formatter.format(date.getTime()) + ".jpg";
                String filename=s;
				s = "C:\\Program Files\\Apache Software Foundation\\Tomcat 7.0\\webapps\\LoginRegistration\\images\\" + s;
				//C:\Program Files\Apache Software Foundation\Tomcat 7.0\webapps\LoginRegistration\images
				System.out.println(s);
				try {
				
					buf = outBuffer;
					btoi = new BufferToImage((VideoFormat) buf.getFormat());
					img = btoi.createImage(buf);
					MessageSender send=new MessageSender();
					send.sendPostAlert(filename);
					saveJPG(img, s);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				System.out.println("Motion detected (motion at " + c + "areas");

			}

			// If chosen, the detected motion is presented on top of the video
			// input, thus covering the edges of the moving object.
			if (visualize) {
				for (int i = 1; i <= numberOfSquares; i++) { // For all blobs
					if ((changedSquares[i - 1] > threshs[0])) { // Critical
																// threshold, if
																// less, then no
																// motion is
																// said to have
																// occured.
						if (((i % numberOfSquaresWide) != 0)
								&& (numberOfSquares - i) > numberOfSquaresWide) {// Normal
																					// square,
																					// the
																					// other
																					// cases
																					// is
																					// not
																					// presented!
							int begin = ((((i % numberOfSquaresWide) - 1) * sqSide) + ((i / numberOfSquaresWide)
									* imageWidth * sqSide)) * 3; // Calculate
																	// start of
																	// square.

							if (changedSquares[i - 1] > threshs[3]) { // Very
																		// strong
																		// motion.
								b = (byte) (colors[3] & 0xFF);
								g = (byte) ((colors[3] >> 8) & 0xFF);
								r = (byte) ((colors[3] >> 16) & 0xFF);
							} else if (changedSquares[i - 1] > threshs[2]) { // Strong
																				// motion.
								b = (byte) (colors[2] & 0xFF);
								g = (byte) ((colors[2] >> 8) & 0xFF);
								r = (byte) ((colors[2] >> 16) & 0xFF);
							} else if (changedSquares[i - 1] > threshs[1]) { // Weak
																				// motion.
								b = (byte) (colors[1] & 0xFF);
								g = (byte) ((colors[1] >> 8) & 0xFF);
								r = (byte) ((colors[1] >> 16) & 0xFF);
							} else { // The Weakest motion detected.
								b = (byte) (colors[0] & 0xFF);
								g = (byte) ((colors[0] >> 8) & 0xFF);
								r = (byte) ((colors[0] >> 16) & 0xFF);
							}
							for (int k = begin; k < (begin + (sqSide
									* imageWidth * 3)); k = k
									+ (imageWidth * 3)) { // Ev <=
								for (int j = k; j < (k + (sqSide * 3)); j = j + 3) {
									try {
										outData[j] = (byte) b;
										outData[j + 1] = (byte) g;
										outData[j + 2] = (byte) r;
									} catch (ArrayIndexOutOfBoundsException e) {
										System.out.println("Nullpointer: j = "
												+ j + ". Outdata.length = "
												+ outData.length);
										System.exit(1);
									}
								}
							}

						}
					}
				}
			}
		}
		return BUFFER_PROCESSED_OK;
	}

	public static void saveJPG(Image img, String sav) {

		BufferedImage bi = new BufferedImage(img.getWidth(null),
				img.getHeight(null), 1);
		Graphics2D g2 = bi.createGraphics();
		g2.drawImage(img, null, null);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(sav);
		} catch (FileNotFoundException io) {
			System.out.println("File Not Found");
		}
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
		JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bi);
		param.setQuality(0.5F, false);
		encoder.setJPEGEncodeParam(param);
		try {
			encoder.encode(bi);
			out.close();
		} catch (IOException io) {
			System.out.println("IOException");
		}
	}

	// Methods for interface PlugIn
	public String getName() {
		return "Motion Detection Codec";
	}

	public void open() {
	}

	public void close() {
	}

	public void reset() {
	}

	// Methods for interface javax.media.Controls
	public Object getControl(String controlType) {
		System.out.println(controlType);
		return null;
	}

	public Object[] getControls() {
		return null;
	}

	// Utility methods.
	public Format matches(Format in, Format outs[]) {
		for (int i = 0; i < outs.length; i++) {
			if (in.matches(outs[i]))
				return outs[i];
		}

		return null;
	}

	// Credit : example at www.java.sun.com
	byte[] validateByteArraySize(Buffer buffer, int newSize) {
		Object objectArray = buffer.getData();
		byte[] typedArray;

		if (objectArray instanceof byte[]) { // Has correct type and is not null
			typedArray = (byte[]) objectArray;
			if (typedArray.length >= newSize) { // Has sufficient capacity
				return typedArray;
			}

			byte[] tempArray = new byte[newSize]; // Reallocate array
			System.arraycopy(typedArray, 0, tempArray, 0, typedArray.length);
			typedArray = tempArray;
		} else {
			typedArray = new byte[newSize];
		}

		buffer.setData(typedArray);
		return typedArray;
	}

	/**
	 * Sets the current pixelspace, default is zero. This is mainly for use
	 * where limited processing capacity are availible. Some pixels are left out
	 * in the calculations.
	 * 
	 * @param newSpace
	 *            the space between two successive pixels.
	 */
	private void setPixelSpace(int newSpace) {
		pixelSpace = newSpace;
	}

	/**
	 * Changes the size of the square shaped area that divides the detection
	 * area into many small parts.
	 * 
	 * @param newSide
	 *            the side of the square, in pixels.
	 */
	private void changeSqSize(int newSide) {
		sqSide = newSide;
		sqArea = newSide * newSide;
		int wid = (imageWidth / sqSide); // The number of squares wide.
		int hei = (imageHeight / sqSide); // The number of squares high.
		sqWidthLeftover = imageWidth % sqSide;
		sqHeightLeftover = imageHeight % sqSide;
		if (sqWidthLeftover > 0) {
			wid++;
		}
		if (sqHeightLeftover > 0) {
			hei++;
		}

		numberOfSquaresWide = wid;
		numberOfSquaresHigh = hei;
		numberOfSquares = wid * hei;

		newImageSquares = new int[numberOfSquares];
		oldImageSquares = new int[numberOfSquares];
		changedSquares = new int[numberOfSquares];
	}

	/**
	 * Calculates the average colour in each square thus indirect eliminate
	 * noise.
	 * 
	 * @param startX
	 *            the starting position of this square, in pixels, left edge.
	 * @param startY
	 *            the starting position of this square, in pixels, bottom edge.
	 * @param sqWidth
	 *            the width of this square, in pixels.
	 * @param sqHeight
	 *            the height of this square, in pixels.
	 * @return The average greyscale value for this square.
	 */
	private int averageInSquare(int startX, int startY, int sqWidth,
			int sqHeight) {
		int average = 0;
		for (int i = 0; i < sqHeight; i = i + 1 + pixelSpace) {// For all pixels
			for (int j = 0; j < sqWidth; j = j + 1 + pixelSpace) {
				average += bwPixels[(((startY + i) * imageWidth) + (startX + j))]; // Sum
																					// all
																					// the
																					// pixel
																					// values.
			}
		}
		average = average / (sqWidth * sqHeight); // Divide by the number of
													// pixels to get the average
													// value.
		return average;
	}

	/**
	 * Backup the most recent frame examined. For the new frame, calculate the
	 * average greyscale value for all squares.
	 */
	private void updateSquares() {
		System.arraycopy(newImageSquares, 0, oldImageSquares, 0,
				newImageSquares.length);
		int sqCount = 0; // Keep track of the current square
		for (int j = 0; j < (imageHeight); j = j + sqSide) { // For all squares
			for (int i = 0; i < (imageWidth); i = i + sqSide) {
				if (i <= (imageWidth - sqSide) && j <= (imageHeight - sqSide)) {
					newImageSquares[sqCount] = averageInSquare(i, j, sqSide,
							sqSide); // No edge!
				} else if (i > (imageWidth - sqSide)
						&& j <= (imageHeight - sqSide)) {
					newImageSquares[sqCount] = averageInSquare(i, j,
							sqWidthLeftover, sqSide); // Right edge!
				} else if (i <= (imageWidth - sqSide)
						&& j > (imageHeight - sqSide)) {
					newImageSquares[sqCount] = averageInSquare(i, j, sqSide,
							sqHeightLeftover); // Bottom edge!
				} else if (i > (imageWidth - sqSide)
						&& j > (imageHeight - sqSide)) {
					newImageSquares[sqCount] = averageInSquare(i, j,
							sqWidthLeftover, sqHeightLeftover); // Bottom right
																// edge!
				}
				sqCount++;
			}
		}
	}

	/**
	 * Calculate the difference per square between currently stored frames.
	 */
	private void oldNewChange() {
		for (int i = 0; i <= (numberOfSquares - 1); i++) { // For all squares
			int difference = Math.abs((newImageSquares[i])
					- (oldImageSquares[i])); // Compare each square with the
												// corresponding square in the
												// previous frame.
			changedSquares[i] = difference; // Save the difference.
		}
	}

	public synchronized void updateModel(boolean visualize,
			boolean serverActive, boolean simplified, int[] threshs,
			int[] colors, int sqSide, int det_thresh) {

		this.visualize = visualize;
		this.serverActive = serverActive;
		if (sqSide != this.sqSide)
			changeSqSize(sqSide);
		if (!simplified) {
			System.out.println((colors == null) + " " + (this.colors == null));
			System.arraycopy(colors, 0, this.colors, 0, colors.length);
			System.arraycopy(threshs, 0, this.threshs, 0, colors.length);
			this.det_thresh = det_thresh;
			System.out.println("New det_threhsh: " + this.det_thresh);
		}
		updateRequested = true;
	}

	/**
	 * Check if the visualize variable is set.
	 * 
	 * @returns the current value.
	 */
	public boolean isVisual() {
		return visualize;
	}

	/**
	 * Get the current threshold values in a vector.
	 * 
	 * @returns the current values.
	 */
	public int[] getThreshholds() {
		return threshs;
	}

	/**
	 * Check if the server is active.
	 * 
	 * @returns the current value.
	 */
	public boolean isServerActive() {
		return serverActive;
	}

	public int[] getColors() {
		return colors;
	}

	/**
	 * Get the current square side.
	 * 
	 * @returns the current value.
	 */
	public int getSqSide() {
		return sqSide;
	}

}