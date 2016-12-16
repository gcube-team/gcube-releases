package org.gcube.contentmanagement.graphtools.plotting.graphs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageObserver;
import java.awt.image.ReplicateScaleFilter;

import javax.swing.JPanel;

public class SpectrumPlot2 extends JPanel {

	private BufferedImage spectrogram = null;

	private Image scaledSpectrogram = null;

	private float zoom = 1.0f;
	private float vzoom = 1.0f;

	private double offsetFactor;

	private double[][] data;

	public int width;
	public int height;

	private ColorMap cmap = ColorMap.getJet(64);
	// private static float minZoom = .1f;

	private double minVal;
	private double maxVal;

	public SpectrumPlot2(double[][] dat) {
		data = dat;
		width = dat.length;
		height = dat[0].length;
		// audio.addChangeListener(new ChangeListener() {
		// public void stateChanged(ChangeEvent event) {
		// computeSpectrogram();
		// }
		// });
		computeSpectrogram();
	}

	private void computeSpectrogram() {
		try {
			// prepare the data:
			maxVal = 0;
			minVal = Integer.MAX_VALUE;
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					if (data[x][y] > maxVal)
						maxVal = data[x][y];
					if (data[x][y] < minVal)
						minVal = data[x][y];
				}
			}
			double minIntensity = Math.abs(minVal);
			double maxIntensity = maxVal + minIntensity;

			int maxYIndex = height - 1;
			Dimension d = new Dimension(width, height);

			setMinimumSize(d);
			setMaximumSize(d);
			setPreferredSize(d);

			/*
			 * Create the image for displaying the data.
			 */
			spectrogram = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

			/*
			 * Set scaleFactor so that the maximum value, after removing the
			 * offset, will be 0xff.
			 */
			double scaleFactor = ((0x3f + offsetFactor) / maxIntensity);

			for (int i = 0; i < width; i++) {
				for (int j = maxYIndex; j >= 0; j--) {

					/*
					 * Adjust the grey value to make a value of 0 to mean white
					 * and a value of 0xff to mean black.
					 */
					int grey = (int) ((data[i][j] + minIntensity) * scaleFactor - offsetFactor);
					// System.out.println(grey);

					// use grey as an index into the colormap;
					spectrogram.setRGB(i, maxYIndex - j, cmap.getColor(grey));
				}
			}

			ImageFilter scaleFilter = new ReplicateScaleFilter((int) (zoom * width), (int) (vzoom * height));
			scaledSpectrogram = createImage(new FilteredImageSource(spectrogram.getSource(), scaleFilter));
			Dimension sz = getSize();
			repaint(0, 0, 0, sz.width - 1, sz.height - 1);
			
//			ImageIO.write(ImageTools.toBufferedImage(scaledSpectrogram), "png", new File("saved.png"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setOffsetFactor(double offsetFactor) {
		this.offsetFactor = offsetFactor;
		computeSpectrogram();
	}

	public void vzoomSet(float vzoom) {
		this.vzoom = vzoom;
		zoom();
	}

	public void hzoomSet(float zoom) {
		zoomSet(zoom);
	}

	protected void zoomSet(float zoom) {
		this.zoom = zoom;
		zoom();
	}

	public void zoom() {
		if (spectrogram != null) {
			int width = spectrogram.getWidth();
			int height = spectrogram.getHeight();

			// do the zooming
			width = (int) (zoom * width);
			height = (int) (vzoom * height);

			ImageFilter scaleFilter = new ReplicateScaleFilter(width, height);
			scaledSpectrogram = createImage(new FilteredImageSource(spectrogram.getSource(), scaleFilter));

			// so ScrollPane gets notified of the new size:
			setPreferredSize(new Dimension(width, height));
			revalidate();

			repaint();
		}
	}

	public float getVZoom() {
		return vzoom;
	}

	public float getHZoom() {
		return zoom;
	}

	public SpectrumPlot2 getColorBar() {
		int barWidth = 20;

		double[][] cb = new double[barWidth][cmap.size];

		for (int x = 0; x < cb.length; x++)
			for (int y = 0; y < cb[x].length; y++)
				cb[x][y] = y;

		return new SpectrumPlot2(cb);
	}

	public double getData(int x, int y) {
		return data[x][y];
	}

	public int getDataWidth() {
		return width;
	}

	public int getDataHeight() {
		return height;
	}

	public void paint(Graphics g) {
		Dimension sz = getSize();

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, sz.width - 1, sz.height - 1);

		if (spectrogram != null) {
			g.drawImage(scaledSpectrogram, 0, 0, (ImageObserver) null);
		}
	}
}
