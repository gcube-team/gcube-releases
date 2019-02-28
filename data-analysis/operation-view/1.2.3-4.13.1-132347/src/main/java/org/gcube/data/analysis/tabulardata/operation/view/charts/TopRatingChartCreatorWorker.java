package org.gcube.data.analysis.tabulardata.operation.view.charts;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import javax.imageio.ImageIO;

import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.MemoryType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.type.MeasureColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.TimeDimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.metadata.common.NamesMetadata;
import org.gcube.data.analysis.tabulardata.model.resources.InternalURI;
import org.gcube.data.analysis.tabulardata.model.resources.Resource;
import org.gcube.data.analysis.tabulardata.model.resources.ResourceType;
import org.gcube.data.analysis.tabulardata.model.resources.Thumbnail;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.LeafParameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ResourcesResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.ImmutableURIResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.remover.ResourceRemover;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ResourceCreatorWorker;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Year;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopRatingChartCreatorWorker extends ResourceCreatorWorker {

	CubeManager cubeManager;
	OperationInvocation sourceInvocation;
	DatabaseConnectionProvider connectionProvider;

	private static Logger logger = LoggerFactory.getLogger(TopRatingChartCreatorWorker.class);
	
	public static final String SERVICE_CLASS = "DataAnalysis";
	public static final String SERVICE_NAME = "TabularData";

	public static final int THUMBNAIL_WIDTH = 190;
	public static final int THUMBNAIL_HEIGHT = 160;
	
	public TopRatingChartCreatorWorker(OperationInvocation sourceInvocation, CubeManager cubeManager, DatabaseConnectionProvider connectionProvider) {
		super(sourceInvocation);
		this.cubeManager = cubeManager;
		this.sourceInvocation = sourceInvocation;
		this.connectionProvider = connectionProvider;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected ResourcesResult execute() throws WorkerException {
		updateProgress(0.1f, "Retrieving parameters");
		Table table = cubeManager.getTable(sourceInvocation.getTargetTableId());

		int sampleSize = OperationHelper.getParameter((LeafParameter<Integer>)TopRatingChartCreatorFactory.SAMPLE_SIZE, sourceInvocation);

		String opValue = OperationHelper.getParameter((LeafParameter<String>)TopRatingChartCreatorFactory.VALUE_OP, sourceInvocation);


		Column dimColumn = table.getColumnById(sourceInvocation.getTargetColumnId());

		Column measureColumn = table.getColumnsByType(MeasureColumnType.class).get(0);
		Column timeColumn = table.getColumnsByType(TimeDimensionColumnType.class).get(0);

		try{
			File image = createTopChart(measureColumn, timeColumn, dimColumn, table, sampleSize, opValue);

			updateProgress(0.8f, "Saving file on storage");

			InternalURI uri = getInternalUri(image);
			String columnLabel = dimColumn.getMetadata(NamesMetadata.class).getTextWithLocale("en").getValue();

			image.delete();

			return new ResourcesResult( new ImmutableURIResult(uri, "Top "+sampleSize+" chart", "Top "+sampleSize+" chart for column "+columnLabel, ResourceType.CHART));
		}catch(Exception e){
			throw new WorkerException("error creating charts",e);
		}

	}

	private InternalURI getInternalUri(File exportFile) throws URISyntaxException {
		IClient client = new StorageClient(SERVICE_CLASS, SERVICE_NAME, "TDM", AccessType.SHARED, MemoryType.PERSISTENT).getClient();
		String remotePath = "/Charts/"+exportFile.getName();
		String resourceId=client.put(true).LFile(exportFile.getAbsolutePath()).RFile(remotePath);

		//CREATING thumbnail
		URI thumbnailURL = null;
		try{
			BufferedImage img = resizeImage(ImageIO.read(exportFile),THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
			
			String remoteThumb = "/Charts/thumb_"+exportFile.getName();
			OutputStream os = client.put(true).RFileAsOutputStream(remoteThumb);;
			ImageIO.write(img,"jpg", os);
			os.close();
			String id = client.getMetaFile().RFile(remoteThumb).getId();
				
			logger.debug("thumbnail identifier is "+id);
			thumbnailURL = new URI(id);
		}catch(Exception e){
			logger.warn("error trying to create thumbnail for chart",e);
			
		}
		URI resourceURL = new URI(resourceId);
		
		if (thumbnailURL!=null)
			return new InternalURI(resourceURL, "image/jpeg", new Thumbnail(thumbnailURL, "image/jpeg") );
		else return new InternalURI(resourceURL, "image/jpeg");
	}

	private BufferedImage resizeImage(BufferedImage originalImage, int newW, int newH){
		BufferedImage resizedImage = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, newW, newH, null);
		g.dispose();	
		return resizedImage;
	}	
		

	private File createTopChart(Column measureColumn, Column timeColumn,
			Column dimColumn, Table targetTable, int sampleSize, String opValue) throws Exception {
		Table externalRefTable = cubeManager.getTable(dimColumn.getRelationship().getTargetTableId());
		Column externalReferenceColumn = externalRefTable.getColumnById(dimColumn.getRelationship().getTargetColumnId());

		updateProgress(0.2f, "Getting top rated values");

		TimeSeriesCollection dataset = new TimeSeriesCollection();
		String distinctValuesQuery = String.format("SELECT top.%1$s, %4$s.%2$s  " +
				"FROM (SELECT %1$s, %6$s(%5$s) as s FROM %3$s GROUP BY %1$s) AS top, %4$s " +
				"WHERE %4$s.id=top.%1$s ORDER BY top.s DESC LIMIT "+sampleSize, 
				dimColumn.getName(), externalReferenceColumn.getName(), targetTable.getName(), externalRefTable.getName(), measureColumn.getName(), opValue);

		Statement distinctStatement = connectionProvider.getConnection().createStatement();
		Statement seriesStatement = connectionProvider.getConnection().createStatement();
		ResultSet distinctValues = distinctStatement.executeQuery(distinctValuesQuery);


		updateProgress(0.4f, "Preparing dataset");

		while (distinctValues.next()){
			String title = distinctValues.getString(2);
			if (title ==null) continue;
			String groupQuery = String.format("SELECT %2$s, AVG(%3$s) as s  FROM %4$s WHERE %1$s = %5$s GROUP BY %1$s, %2$s ", 
					dimColumn.getName(), timeColumn.getName(),  measureColumn.getName(), targetTable.getName(), distinctValues.getLong(1));

			ResultSet series = seriesStatement.executeQuery(groupQuery);

			dataset.addSeries(createSeries(series,title ));

		}

		seriesStatement.close();
		distinctStatement.close();

		String title = "unknow title";
		if (dimColumn.contains(NamesMetadata.class))
			title = dimColumn.getMetadata(NamesMetadata.class).getTextWithLocale("en").getValue();

		title = title.substring(0, 1).toUpperCase() + title.substring(1);
		return getChartImage(title, dataset);

	}

	private File getChartImage(String title, XYDataset dataset) throws Exception{
		updateProgress(0.7f, "Creating chart image");
		JFreeChart chart = createChart( dataset,title);
		File image = File.createTempFile("chart-"+title, ".jpg");
		ChartUtilities.saveChartAsJPEG(image, chart, 1200, 720 );
		return image;

	}


	private JFreeChart createChart(XYDataset dataset, String title) {
		ChartFactory.setChartTheme(new StandardChartTheme("JFree/Shadow",true));
		JFreeChart chart = ChartFactory.createTimeSeriesChart(title, "Time", "Value", 
				dataset,                // data
				true,                   // include legend
				true,
				false);

		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.black);
		plot.setRangeGridlinePaint(Color.lightGray);
		plot.setBackgroundAlpha(0.7f);
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);

		XYItemRenderer r = plot.getRenderer();
		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setBaseShapesVisible(true);
			renderer.setBaseShapesFilled(true);
			renderer.setDrawSeriesLineAsPath(true);
		}

		LegendTitle legend = chart.getLegend();
		legend.setPosition(RectangleEdge.RIGHT);

		DateAxis axis = (DateAxis) plot.getDomainAxis();
		axis.setDateFormatOverride(new SimpleDateFormat("yyyy"));

		return chart;

	}

	private TimeSeries createSeries(ResultSet rs, String title) throws Exception{

		TimeSeries s1 = new TimeSeries(title);

		while (rs.next())
			s1.add(new Year(rs.getInt(1))  , rs.getDouble(2) );
		return s1;
	}

	public static class StorageRemover implements ResourceRemover {

		private static StorageRemover remover = new StorageRemover();

		public static StorageRemover getInstance(){
			return remover;
		}

		@Override
		public void onRemove(Resource resource) throws Exception {
			IClient client = new StorageClient(SERVICE_CLASS, SERVICE_NAME, "TDM", AccessType.SHARED, MemoryType.PERSISTENT).getClient();
			client.remove(((InternalURI)resource).getUri().toString());
		}

	}


}
