package density;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.imageio.ImageIO;

// Referenced classes of package density:
//            GridDimension, LazyGrid, Grid, Utils, 
//            Sample

public class WorldImageProducer extends Canvas
{

	public boolean gridnull = false;
	public BufferedImage img;
    public int scale;
    public int mode;
    public static final int LOG = 0;
    public static final int PLAIN = 1;
    public static final int CLASS = 2;
    public int classColor[];
    public String className[];
    public Grid grid;
    public boolean blackandwhite;
    public boolean redandyellow;
    public boolean dichromatic;
    public Color dichromaticColors[];
    public static boolean toggleSampleColor = true;
    public int background;
    public double breakpoint;
    public int pixels[];
    public int minx;
    public int maxx;
    public int miny;
    public int maxy;
    public Sample samples[];
    public Sample testsamples[];
    public boolean visible;
    public boolean makeLegend;
    public boolean makeTimeline=false;
    
    public String initialTimeLabel;
    public String timeLabel;
    public int maxTimeIntervals;
    public int timeIndex;
    
    public static boolean setNumCategoriesByMax = false;
    public static boolean makeNorth = false;
    public static int defaultSampleRadius = 7;
    public static int adjustSampleRadius = 0;
    public static int numCategories = 14;
    public static double categories[] = null;
    public static int maxFracDigits = -1;
    public static double divisor = 2D;
    public double minval;
    public double maxval;
    public int xOffset;
    public int yOffset;
    public static double xline = -1D;
    public static double yline = -1D;
    public int linerow;
    public int linecol;
    public int white;
    public int black;
    public int sampleColor;
    public int testSampleColor;
    public int sampleRadius;
    public double min;
    public double max;
    public int maxborder;
    public int bdist[][];
    public static boolean addTinyVals = true;

    double aspect(int x1, int x2, int y1, int y2)
    {
        return (double)(y2 - y1) / (double)(x2 - x1);
    }

    void setZoom(int x1, int x2, int y1, int y2)
    {
        minx = x1 >= x2 ? x2 : x1;
        maxx = x1 <= x2 ? x2 : x1;
        miny = y1 >= y2 ? y2 : y1;
        maxy = y1 <= y2 ? y2 : y1;
        if(nozoom())
        {
            return;
        }
        double target = aspect(0, getCols(), 0, getRows());
        if(aspect(minx, maxx, miny, maxy) < target)
        {
            maxy = miny + (int)(target * (double)(maxx - minx));
        } else
        {
            maxx = minx + (int)((double)(maxy - miny) / target);
        }
    }

    void zoomOut()
    {
        if(nozoom())
        {
            return;
        }
        if(minx == 0 && maxx == getCols() && miny == 0 && maxy == getRows())
        {
            return;
        } else
        {
            minx = 0;
            maxx = getCols();
            miny = 0;
            maxy = getRows();
            makeImage();
            return;
        }
    }

    public void setClassNames(String s)
    {
        setClassNames(s.split(":"));
    }

    public void setClassNames(String s[])
    {
        className = s;
        if(minval == -1D)
        {
            minval = 0.0D;
            maxval = className.length - 1;
            numCategories = className.length;
        } else
        {
            numCategories = (int)((maxval - minval) + 1.0D);
        }
        mode = 1;
    }

    int[] stringToColors(String s)
    {
        String colors[] = s.split(" ");
        int result[] = new int[colors.length];
        for(int i = 0; i < colors.length; i++)
        {
            if(colors[i].indexOf("|") != -1)
            {
                String rgb[] = colors[i].split("\\|");
                int rgbi[] = new int[3];
                for(int j = 0; j < 3; j++)
                {
                    rgbi[j] = Integer.parseInt(rgb[j]);
                }

                result[i] = (new Color(rgbi[0], rgbi[1], rgbi[2])).getRGB();
                continue;
            }
            try
            {
                result[i] = Integer.decode(colors[i]).intValue();
                continue;
            }
            catch(NumberFormatException e) { }
            try
            {
                result[i] = ((Color)Class.forName("java.awt.Color").getField(colors[i]).get(null)).getRGB();
            }
            catch(Exception ee)
            {
                throw new NumberFormatException((new StringBuilder()).append("Invalid color: ").append(colors[i]).toString());
            }
        }

        return result;
    }

    public void setColorClasses(String s)
    {
        classColor = stringToColors(s);
        mode = 2;
    }

    static void setCategories(String s)
    {
        String cats[] = s.split(" ");
        categories = new double[cats.length];
        for(int i = 0; i < cats.length; i++)
        {
            categories[i] = Double.parseDouble(cats[i]);
        }

        numCategories = cats.length;
    }

    void setline()
    {
        linerow = linecol = -1;
        if(xline != -1D)
        {
            linerow = grid.getDimension().toRow(xline) * scale;
        }
        if(yline != -1D)
        {
            linecol = grid.getDimension().toCol(yline) * scale;
        }
    }

    public void setMinval(double m)
    {
        minval = m;
    }

    public void setMaxval(double m)
    {
        maxval = m;
    }

    public void setMode(int i)
    {
        mode = i;
    }

    void setBreakpoint(double x)
    {
        breakpoint = x;
    }

    void setColorScheme(int i)
    {
        blackandwhite = i == 0;
    }

    public void setGrid(Grid grid, int minrows, int mincols)
    {
        this.grid = grid;
        scale = 1;
        double xscale = Math.floor((double)mincols / (double)getCols());
        double yscale = Math.floor((double)minrows / (double)getRows());
        scale = (int)(xscale >= yscale ? yscale : xscale);
        if(scale < 1)
        {
            scale = 1;
        }
        img = new BufferedImage(getCols(), getRows(), 1);
        pixels = ((DataBufferInt)img.getRaster().getDataBuffer()).getData();
        setZoom(-1, -1, -1, -1);
        setline();
        if(blackandwhite)
        {
            newborder();
        }
    }

    void setSamples(Sample s[])
    {
        samples = s;
    }

    public  void setTestSamples(Sample s[])
    {
        testsamples = s;
    }

    double max(double x, double y)
    {
        return x <= y ? y : x;
    }

    public void setTime(int maxTimeFrames, int timeFrame,String initialTimeLabel,String currentTimeLabel){
    	maxTimeIntervals=maxTimeFrames;
    	timeIndex=timeFrame;
    	this.initialTimeLabel=initialTimeLabel;
    	this.timeLabel=currentTimeLabel;
    }
    
    GridDimension viewDimension()
    {
        GridDimension dim = grid.getDimension();
        double cs = dim.getcellsize() / (double)scale;
        return new GridDimension(dim.getxllcorner() + max(minx, 0.0D) * cs, dim.getyllcorner() + (double)(maxy != -1 ? getRows() - maxy : 0) * cs, cs * (maxx != -1 ? (double)(maxx - minx) / (double)getCols() : 1.0D), getRows(), getCols());
    }

    int getRows()
    {
    	if (grid!=null)
    		return scale * grid.getDimension().nrows;
    	else
    		return scale * 480;
    }

    int getCols()
    {
    	if (grid!=null)
    		return scale * grid.getDimension().ncols;
    	else
    		return scale * 640;
    }

    boolean inBounds(int r, int c)
    {
        return r >= 0 && r < getRows() && c >= 0 && c < getCols();
    }

    public WorldImageProducer(Grid grid, int minrows, int mincols)
    {
        blackandwhite = false;
        redandyellow = false;
        dichromatic = false;
        dichromaticColors = (new Color[] {
            Color.red, Color.blue
        });
        breakpoint = -9999D;
        minx = -1;
        maxx = -1;
        miny = -1;
        maxy = -1;
        samples = null;
        testsamples = null;
        visible = true;
        makeLegend = true;
        minval = -1D;
        maxval = -1D;
        xOffset = -1;
        yOffset = -1;
        white = -1;
        black = Color.black.getRGB();
        sampleColor = white;
        testSampleColor = 0xff8a2be2;
        sampleRadius = defaultSampleRadius;
        min = 0.0D;
        max = 0.0D;
        maxborder = 5;
        setGrid(grid, minrows, mincols);
    }

    public WorldImageProducer(Grid grid)
    {
        blackandwhite = false;
        redandyellow = false;
        dichromatic = false;
        dichromaticColors = (new Color[] {
            Color.red, Color.blue
        });
        breakpoint = -9999D;
        minx = -1;
        maxx = -1;
        miny = -1;
        maxy = -1;
        samples = null;
        testsamples = null;
        visible = true;
        makeLegend = true;
        minval = -1D;
        maxval = -1D;
        xOffset = -1;
        yOffset = -1;
        white = -1;
        black = Color.black.getRGB();
        sampleColor = white;
        testSampleColor = 0xff8a2be2;
        sampleRadius = defaultSampleRadius;
        min = 0.0D;
        max = 0.0D;
        maxborder = 5;
        if (grid!=null){
        	setGrid(grid, 1200, 1600);
        }
        else
        	gridnull=true;
    }

    boolean nozoom()
    {
        return minx == -1 || maxx == -1 || miny == -1 || maxy == -1;
    }

    int windowx2imgx(int x)
    {
        return (int)(((double)getCols() / (double)getSize().width) * (double)x);
    }

    int windowy2imgy(int y)
    {
        return (int)(((double)getRows() / (double)getSize().height) * (double)y);
    }

    int gridrow(int r)
    {
        if(nozoom())
        {
            return r / scale;
        } else
        {
            int rr = (int)((double)miny + ((double)r / (double)getRows()) * (double)(maxy - miny));
            return rr / scale;
        }
    }

    int gridcol(int c)
    {
        if(nozoom())
        {
            return c / scale;
        } else
        {
            int cc = (int)((double)minx + ((double)c / (double)getCols()) * (double)(maxx - minx));
            return cc / scale;
        }
    }

    boolean hasData(int r, int c)
    {
    	if (!gridnull)
    		return grid.hasData(gridrow(r), gridcol(c));
    	else
    		return false;
    }

    float eval(int r, int c)
    {
        return grid.eval(gridrow(r), gridcol(c));
    }

    public void makeImage()
    {
        boolean start = true;
        background = redandyellow ? 0x8080ff : blackandwhite ? white : 0xff000000;
      
        if(minval != -1D || maxval != -1D)
        {
            min = minval;
            max = maxval;
        } else
        {
            for(int i = 0; i < getRows(); i++)
            {
                for(int j = 0; j < getCols(); j++)
                {
                    if(!hasData(i, j))
                    {
                        continue;
                    }
                    if(start)
                    {
                        min = max = eval(i, j);
                        start = false;
                        continue;
                    }
                    float val = eval(i, j);
                    if((double)val < min && (mode != 0 || val > 0.0F) || min <= 0.0D && mode == 0)
                    {
                        min = val;
                    }
                    if((double)val > max)
                    {
                        max = val;
                    }
                }

            }

            if(grid instanceof LazyGrid)
            {
                try
                {
                    ((LazyGrid)grid).initialize();
                }
                catch(IOException e)
                {
                    Utils.fatalException((new StringBuilder()).append("Error initializing file ").append(grid.name).toString(), null);
                }
            }
        }
        if(max == 100D && min >= 0.0D && min < 1.0000000000000001E-005D)
        {
            min = 1.0000000000000001E-005D;
        }
        if(max == 100D && min >= 0.0D && min < 0.001D && blackandwhite)
        {
            min = 0.01D;
        }
        if(min > 0.0D && max / min > 1000000000000000D)
        {
            min = max / 1000000000000000D;
        }
        if(min >= 0.0D && min <= 0.10000000000000001D && max >= 0.69999999999999996D && max <= 1.0D && mode != 0 && maxval == -1D)
        {
            min = 0.0D;
            max = 1.0D;
            numCategories = 11;
        }
        for(int i = 0; i < getRows(); i++)
        {
            Utils.reportProgress((double)(i * 100) / (double)getRows());
            for(int j = 0; j < getCols(); j++)
            {
                pixels[i * getCols() + j] = i != linerow ? j != linecol ? hasData(i, j) ? showColor(eval(i, j), min, max) : !blackandwhite || !isborder(i, j) ? background : black : white : white;
                if (pixels[i * getCols() + j] == black && !blackandwhite) //patch by Gianpaolo Coro to exclude black colors
                	pixels[i * getCols() + j] = Color.LIGHT_GRAY.getRGB();
            }

        }

        int sr = sampleRadius;
        int rr = getRows() * scale;
        int cc = getCols() * scale;
        if(scale > 1)
        {
            sr = (int)Math.ceil((double)sr / (double)scale);
        }
        if(rr < 900 && cc < 900 && sr * scale >= 5 && sr > 2)
        {
            sr -= 2;
        }
        if(rr < 600 && cc < 600 && sr * scale >= 5 && sr > 2)
        {
            sr -= 2;
        }
        if(rr < 300 && cc < 300 && sr * scale >= 5 && sr > 2)
        {
            sr -= 2;
        }
        if(rr > 2000 || cc > 2000)
        {
            sr += 1 / scale;
        }
        if(rr > 4000 || cc > 4000)
        {
            sr += 1 / scale;
        }
        sr += adjustSampleRadius;
        if(samples != null)
        {
            showSamples(samples, sampleColor, sr);
        }
        if(testsamples != null)
        {
            showSamples(testsamples, testSampleColor, sr);
        }
        if(makeLegend)
        {
            makeLegend();
        }
        if(makeNorth)
        {
            makeNorth();
        }
        if(visible)
        {
            repaint();
        }
        if(makeTimeline)
        	makeTimeline(maxTimeIntervals,timeIndex,initialTimeLabel,timeLabel);
        
    }

    void newborder()
    {
        int nr = getRows();
        int nc = getCols();
        bdist = new int[nr][nc];
        for(int i = 0; i < getRows(); i++)
        {
            for(int j = 0; j < getCols(); j++)
            {
                bdist[i][j] = hasData(i, j) ? 0 : 0x186a0;
            }

        }

        for(int iter = 0; iter < maxborder; iter++)
        {
            for(int i = 0; i < nr; i++)
            {
                for(int j = 0; j < nc; j++)
                {
                    for(int id = -1; id <= 1; id += 2)
                    {
                        for(int jd = -1; jd <= 1; jd += 2)
                        {
                            int ii = i + id;
                            int jj = j + jd;
                            if(ii >= 0 && jj >= 0 && ii < nr && jj < nc && bdist[ii][jj] < bdist[i][j] - 1)
                            {
                                bdist[i][j] = bdist[ii][jj] + 1;
                            }
                        }

                    }

                }

            }

        }

    }

    boolean isborder(int i, int j)
    {
        return bdist[i][j] < maxborder;
    }

    void showSamples(Sample samples[], int color, int sr)
    {
        GridDimension dim = viewDimension();
        int under[] = null;
        if(blackandwhite && toggleSampleColor)
        {
            under = (int[])(int[])pixels.clone();
        }
        for(int i = 0; i < samples.length; i++)
        {
        	if (samples[i]==null)
        		continue;
            int r = samples[i].getRow(dim);
            int c = samples[i].getCol(dim);
            int color2 = color;
            if(blackandwhite && toggleSampleColor)
            {
                int cnt = 0;
                int tot = 0;
                for(int j = (-sr + 1) * scale; j < sr * scale; j++)
                {
                    for(int k = (-sr + 1) * scale; k < sr * scale; k++)
                    {
                        if(inBounds(r + j, c + k))
                        {
                            cnt++;
                            tot += (new Color(under[(r + j) * getCols() + (c + k)])).getBlue();
                        }
                    }

                }

                if(cnt > 0 && tot / cnt > 128)
                {
                    color2 = black;
                }
            } else
            if(blackandwhite)
            {
                color2 = black;
            }
            for(int j = (-sr + 1) * scale; j < sr * scale; j++)
            {
                for(int k = (-sr + 1) * scale; k < sr * scale; k++)
                {
                    if(inBounds(r + j, c + k))
                    {
                        pixels[(r + j) * getCols() + (c + k)] = color2;
                    }
                }

            }

        }

    }

    
    void makeTimeline(int maxtimeframes, int timeidx, String initialTimeLabel, String timeLabel)
    {
    	
    	int num = timeidx+1;
    	int fontSize = getRows() <= 2000 && getCols() <= 2000 ? ((int) (getRows() <= 1000 && getCols() <= 1000 ? 11 : 18)) : 24;
      
        Graphics2D image = (Graphics2D)img.getGraphics();
        Font font = new Font("Dialog", 1, fontSize);
        image.setFont(font);
        FontMetrics fm = image.getFontMetrics(font);
//        int height = fm.getHeight() + 2;
        String labels[] = new String[timeidx+1];
        labels[0] = initialTimeLabel;
        labels[timeidx] = timeLabel;
        int w = fm.stringWidth(labels[timeidx]);
        int width  =  fm.getWidths()[0] + w;
        int heigh  =  fm.getWidths()[0] ;
        
        //computeOffsets(maxtimeframes + 6 + 2 * height, (timeidx+1 + 2) * height);
        int y = getRows()-50;
        int x0 = (getCols()/2)-(maxtimeframes/2);
        for(int i = 0; i < num; i++)
        {
            //((num - i) + 1) * (height-yOffset);
            image.setColor(Color.white);
            int x = (i+1) + x0;
            if (!initialTimeLabel.equals(timeLabel))
            {
            	image.fill(new Rectangle(x, y, width, heigh));
            	image.setColor(Color.white);
            }
            String label = labels[i]==null?"":labels[i];
            if (i==0)
            	image.drawString(label, (x-width/2), (y +heigh+ 15));
            else
            	image.drawString(label, (x+width), (y +heigh+ 15));
        }

    }
    
    void makeLegend()
    {
        int num = setNumCategoriesByMax ? (int)max + 1 : numCategories;
        double vals[] = categories != null ? categories : new double[num];
        int fontSize = getRows() <= 2000 && getCols() <= 2000 ? ((int) (getRows() <= 1000 && getCols() <= 1000 ? 11 : 18)) : 24;
        if(categories == null)
        {
            if(mode == 0)
            {
                double x = max;
                if(max > 50D && max <= 100D)
                {
                    for(int i = 0; i < num; i++)
                    {
                        vals[i] = x;
                        x /= divisor;
                    }

                    if(addTinyVals)
                    {
                        vals[num - 3] = 0.01D;
                        vals[num - 2] = 0.001D;
                        vals[num - 1] = min <= 0.0001D ? min : 0.0001D;
                    } else
                    {
                        vals[num - 1] = 0.0D;
                    }
                } else
                {
                    double div = Math.exp(Math.log(min >= max / 1000000000000000D ? max / min : 1000000000000000D) / (double)(num - 1));
                    for(int i = 0; i < num; i++)
                    {
                        vals[i] = x;
                        x /= div;
                    }

                }
            } else
            {
                for(int i = 0; i < num; i++)
                {
                    vals[i] = max - ((double)i * (max - min)) / (double)(num - 1);
                }

                if(vals[num - 1] < 0.01D && vals[num - 2] > 1.0D)
                {
                    vals[num - 1] = 0.0D;
                }
            }
            if(min == max)
            {
                num = 1;
                vals = (new double[] {
                    min
                });
            }
        }
        Graphics2D g = (Graphics2D)img.getGraphics();
        Font font = new Font("Dialog", 1, fontSize);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics(font);
        int height = fm.getHeight() + 2;
        NumberFormat nf = ((NumberFormat) (max > 1.0D || mode == 0 ? NumberFormat.getNumberInstance() : ((NumberFormat) (new DecimalFormat()))));
        nf.setGroupingUsed(false);
        String labels[] = new String[num];
        int legendWidth = 0;
        for(int i = 0; i < num; i++)
        {
            if(max < 0.5D || max < 2D && mode == 0)
            {
                ((DecimalFormat)nf).applyPattern("0.#E0");
            } else
            {
                nf.setMaximumFractionDigits(maxFracDigits == -1 ? ((int) (vals[i] < 1.0D ? ((int) (vals[i] < 0.01D ? ((int) (vals[i] < 0.001D ? ((int) (vals[i] <= 0.0001D ? ((int) (vals[i] >= 0.0D ? 5 : 1)) : 4)) : 3)) : 2)) : 1)) : maxFracDigits);
            }
            labels[i] = className != null && (double)className.length > vals[i] ? className[(int)vals[i]] : nf.format(vals[i]);
            int w = fm.stringWidth(labels[i]);
            if(w > legendWidth)
            {
                legendWidth = w;
            }
        }

        computeOffsets(legendWidth + 6 + 2 * height, (num + 2) * height);
        for(int i = 0; i < num; i++)
        {
            int y = getRows() - ((num - i) + 1) * height - yOffset;
            g.setColor(new Color(showColor(vals[i], min, max)));
            g.fill(new Rectangle(xOffset + 4, y, height, height));
            g.setColor(blackandwhite ? Color.black : Color.white);
            g.drawString(labels[i], xOffset + 6 + height, (y + height) - 2);
        }

    }

    void computeOffsets(int w, int h)
    {
        xOffset = 0;
        yOffset = 0;
        int overlap = computeOverlap(0, 0, w, h);
        if(overlap == 0)
        {
            return;
        }
        int overlap2 = computeOverlap(getCols() - w, 0, w, h);
        if(overlap2 < overlap)
        {
            overlap = overlap2;
            xOffset = getCols() - w;
            yOffset = 0;
        }
        if(overlap == 0)
        {
            return;
        }
        overlap2 = computeOverlap(getCols() - w, getRows() - h, w, h);
        if(overlap2 < overlap)
        {
            overlap = overlap2;
            xOffset = getCols() - w;
            yOffset = getRows() - h;
        }
        if(overlap == 0)
        {
            return;
        }
        overlap2 = computeOverlap(0, getRows() - h, w, h);
        if(overlap2 < overlap)
        {
            overlap = overlap2;
            xOffset = 0;
            yOffset = getRows() - h;
        }
    }

    int computeOverlap(int llx, int lly, int w, int h)
    {
        int cnt = 0;
        for(int y = lly; y < lly + h; y++)
        {
            for(int x = llx; x < llx + w; x++)
            {
                if(nonBackground(getRows() - y, x))
                {
                    cnt++;
                }
            }

        }

        return cnt;
    }

    boolean nonBackground(int r, int c)
    {
        int i = r * getCols() + c;
        return i >= 0 && i < pixels.length && pixels[i] != background;
    }

    void makeNorth()
    {
        Graphics2D g = (Graphics2D)img.getGraphics();
        g.setColor(blackandwhite ? Color.black : Color.white);
        Font font = new Font("Dialog", 1, 48);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics(font);
        int height = fm.getHeight() + 2;
        int nx = (int)((double)getCols() * 0.94999999999999996D);
        g.drawString("N", nx, (int)((double)getRows() * 0.10000000000000001D) + height);
        int x = nx + fm.stringWidth("N") / 2;
        int yb = (int)((double)getRows() * 0.10000000000000001D - (double)height * 1.25D);
        int yheight = (int)((double)getRows() * 0.050000000000000003D);
        int w = 6;
        g.fillRect(x - w / 2, yb, w, yheight);
        g.fillPolygon(new int[] {
            x, x + 3 * w, x - 3 * w
        }, new int[] {
            yb - w / 2, yb + 3 * w, yb + 3 * w
        }, 3);
    }

    public void writeImage(String outFile)
    {
        writeImage(outFile, 1);
    }

    public  void writeImage(String outFile, int magstep)
    {
        int ncols = getCols();
        int nrows = getRows();
        BufferedImage toWrite = magstep != 1 ? new BufferedImage(ncols * magstep, nrows * magstep, 1) : img;
        if(magstep > 1)
        {
            int p[] = ((DataBufferInt)toWrite.getRaster().getDataBuffer()).getData();
            for(int r = 0; r < nrows * magstep; r++)
            {
                for(int c = 0; c < ncols * magstep; c++)
                {
                    p[r * ncols * magstep + c] = pixels[(r / magstep) * ncols + c / magstep];
                }

            }

        }
        try
        {
            ImageIO.write(toWrite, "png", new File(outFile));
        }
        catch(IOException e)
        {
            System.out.println((new StringBuilder()).append("Error: ").append(e.toString()).toString());
        }
    }

    int showColor(double val, double min, double max)
    {
        if(mode == 2)
        {
            return classColor[(int)val];
        }
        if(mode == 0)
        {
            val = val > min ? Math.log(val) : Math.log(min);
            min = min > 0.0D ? Math.log(min) : 0.0D;
            max = max > 0.0D ? Math.log(max) : 0.0D;
        }
        if(val < min)
        {
            val = min;
        }
        if(val > max)
        {
            val = max;
        }
        if(dichromatic)
        {
            if(breakpoint == -9999D)
            {
                breakpoint = (max - min) / 2D;
            }
            double frac = val >= breakpoint ? (val - breakpoint) / (max - breakpoint) : (breakpoint - val) / (breakpoint - min);
            int end = val >= breakpoint ? 1 : 0;
            return fadedColor(dichromaticColors[end], frac);
        }
        int red;
        int green;
        int blue;
        if(redandyellow)
        {
            if(breakpoint == -9999D)
            {
                breakpoint = 50D;
            }
            int index = (int)(((max - val) * 100D) / (max - min));
            red = 255;
            green = (int)((double)index >= breakpoint ? 255D : (double)(index * 255) / breakpoint);
            blue = (int)((double)index >= breakpoint ? (((double)index - breakpoint) * 255D) / (511D - breakpoint) : 0.0D);
        } else
        if(blackandwhite)
        {
            double index = (max - val) / (max - min);
            red = green = blue = (int)(220D * index) + 30;
        } else
        {
            int i = (int)(((max - val) * 1020D) / (max - min));
            red = i >= 256 ? i <= 510 ? 510 - i : 0 : 255;
            green = i >= 256 ? i >= 765 ? 1020 - i : 255 : i;
            blue = i >= 510 ? i >= 765 ? 255 : i - 510 : 0;
        }
        return 0xff000000 | red << 16 | green << 8 | blue;
    }

    int fadedColor(Color c, double frac)
    {
        int rgb[] = {
            c.getRed(), c.getGreen(), c.getBlue()
        };
        for(int i = 0; i < 3; i++)
        {
            rgb[i] = rgb[i] + (int)((1.0D - frac) * (double)(255 - rgb[i]));
        }

        return (new Color(rgb[0], rgb[1], rgb[2])).getRGB();
    }

    public void paint(Graphics g)
    {
        int w = getSize().width;
        int h = getSize().height;
        if(img != null)
        {
            g.drawImage(img, 0, 0, w, h, this);
        }
    }

    public void update(Graphics g)
    {
        paint(g);
    }

    public Dimension getPreferredSize()
    {
        return new Dimension(getCols(), getRows());
    }

   
}
