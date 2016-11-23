package org.gcube.contentmanagement.lexicalmatcher.analysis.guesser.treeStructure.graph;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.swing.JApplet;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgrapht.ext.JGraphModelAdapter;

public class GraphDisplayer extends JApplet {
	private static final Color DEFAULT_BG_COLOR = Color.decode("#FAFBFF");
	private static final Dimension DEFAULT_SIZE = new Dimension(530, 320);

	private JGraphModelAdapter m_jgAdapter;

	public static int WIDTH = 1000;
	public static int HEIGHT = 800;

	public static int WIDTHBOX = 1280;
	public static int HEIGHTBOX = 1024;

	private int newxposition;
	private int newyposition;

	private CustomListenableDirectedWeightedGraph g;
	private int nodesCounter;
	private static final int minx = 10;
	private static final int miny = 10;
	ArrayList<String> VertexNames;
	HashMap<String, String> Edges;

	public void generatePosition(int lastxPosition, int lastyposition) {

		int rangex = (int) WIDTH - (int) lastxPosition;
		// compute a fraction of the range, 0 <= frac < range
		Random a = new Random();
		int newx = lastxPosition + 70 + (int) (rangex * a.nextDouble());
		int epsilon = 1;
		int newy = (int) lastyposition + (int) (epsilon * 20f * Math.random());
		if (newx > WIDTH)
			newx = WIDTH - 100;
		if (newx < lastxPosition - 90)
			newx = lastxPosition + 90;
		if (newy > HEIGHT)
			newy = HEIGHT - 10;
		if (newy < 0)
			newy = 0;
		newxposition = newx;
		newyposition = newy;
		// System.out.println("LAST X "+lastxPosition+" NEW X "+newxposition);
		// System.out.println("LAST Y "+lastyposition+" NEW Y "+newyposition);
	}

	public void init() {
		AnalysisLogger.getLogger().debug("INIZIALIZZATO!");
		
		JGraph jgraph = new JGraph(m_jgAdapter);

		adjustDisplaySettings(jgraph);
		getContentPane().add(jgraph);
		resize(DEFAULT_SIZE);

		AnalysisLogger.getLogger().debug("RESIZED!");
	}

	public void generateGraph() {

		for (String v : VertexNames) {
			genPositionVertex(v);
		}
	}

	public void generateRandomGraph() {

		for (String v : VertexNames) {
			int randx = minx + (int) ((WIDTH - 100) * Math.random());
			int randy = miny + (int) ((HEIGHT - 100) * Math.random());
			positionVertexAt(v, randx, randy);
		}
	}

	public void generateUpTo5StarGraph() {

		// individua le star
		HashMap<String, Integer> vertexFrequencies = new HashMap<String, Integer>();
		// calcolo le frequenze dei vertici
		for (String edge : Edges.values()) {
			System.out.println(edge + "-" + vertexFrequencies.get(edge));
			if (vertexFrequencies.get(edge) != null) {
				int f = vertexFrequencies.get(edge).intValue();
				vertexFrequencies.put(edge, new Integer(f + 1));
			} else
				vertexFrequencies.put(edge, new Integer(0));

		}

		for (String vertex : VertexNames) {

			if (Edges.get(vertex) == null) {
				boolean trovato = false;
				// cerco ogni vertice tra gli archi
				for (String starvertex : Edges.values()) {
					if (vertex.equals(starvertex)) {
						trovato = true;
						break;
					}
				}
				if (!trovato) {
					System.out.println("aggiunto vertice isolato " + vertex);
					vertexFrequencies.put(vertex, new Integer(0));
				}
			}

		}

		System.out.println("FEQS " + vertexFrequencies.toString());
		// ordino le star
		ArrayList<String> starList = new ArrayList<String>();
		for (String vertex : vertexFrequencies.keySet()) {

			int freq = vertexFrequencies.get(vertex);
			int i = 0;
			boolean trovato = false;
			for (String element : starList) {

				int referfreq = vertexFrequencies.get(element);
				if (referfreq < freq) {
					starList.add(i, vertex);
					trovato = true;
					break;
				}
				i++;
			}
			if (!trovato)
				starList.add(vertex);
		}

		// dispongo le star nel layout
		System.out.println(starList.toString());
		int bound = 200;
		int[] boundedXIndexex = { bound, WIDTH - bound, bound, WIDTH - bound, WIDTH / 2 };
		int[] boundedYIndexex = { bound, bound, HEIGHT - bound, HEIGHT - bound, HEIGHT / 2 };
		int sizeStar = starList.size();
		// int sizeStar = 1;

		// distribuisco le star sul grafico
		for (int i = 0; i < sizeStar; i++) {

			positionVertexAt(starList.get(i), boundedXIndexex[i], boundedYIndexex[i]);

			// calcolo il numero di elementi della stella
			int countelems = 0;
			for (String edge : Edges.keySet()) {
				if (Edges.get(edge).equals(starList.get(i))) {
					countelems++;
				}
			}

			if (countelems > 0) {
				double subdivision = 360 / countelems;
				double angle = 105f;
				double radius = 200f;
				System.out.println("Numero di elementi nella stella: " + countelems + " suddivisioni: " + subdivision);
				for (String edge : Edges.keySet()) {
					// dispongo gli elementi a stella
					if (Edges.get(edge).equals(starList.get(i))) {
						int currentx = boundedXIndexex[i];
						int currenty = boundedYIndexex[i];
						int epsilonx = (int) (radius * Math.cos(Math.toRadians(angle)));
						int epsilony = (int) (radius * Math.sin(Math.toRadians(angle)));
						System.out.println("angolo attuale: " + angle + " x0: " + currentx + " y0 " + currenty + " ex " + epsilonx + " ey " + epsilony);
						positionVertexAt(edge, currentx + epsilonx, currenty + epsilony);

						angle += subdivision;
					}
				}
			}

		}

	}

	private void genPositionVertex(String vertexName) {

		if (nodesCounter > 0) {
			if ((nodesCounter % 2) == 0) {
				newxposition = 10 + (int) (20f * Math.random());
				newyposition += 100;
			} else
				generatePosition(newxposition, newyposition);
		}

		positionVertexAt(vertexName, newxposition, newyposition);
		nodesCounter++;
	}

	public GraphDisplayer() {
		g = new CustomListenableDirectedWeightedGraph(CustomWeightedEdge.class);
		m_jgAdapter = new JGraphModelAdapter(g);
		VertexNames = new ArrayList<String>();
		Edges = new HashMap<String, String>();
		newxposition = minx;
		newyposition = miny;
		nodesCounter = 0;
	}

	public void addVertex(String name) {
		g.addVertex(name);
		VertexNames.add(name);
	}

	public void addEdge(String v1, String v2, double bi) {
		CustomWeightedEdge ed = (CustomWeightedEdge)g.addEdge(v1,v2);
		g.setEdgeWeight(ed,bi);
		Edges.put(v1, v2);
	}

	private void adjustDisplaySettings(JGraph jg) {
		jg.setPreferredSize(DEFAULT_SIZE);

		Color c = DEFAULT_BG_COLOR;
		String colorStr = null;

		try {
			colorStr = getParameter("bgcolor");
		} catch (Exception e) {
		}

		if (colorStr != null) {
			c = Color.decode(colorStr);
		}

		jg.setBackground(c);
	}

	private void positionVertexAt(Object vertex, int x, int y) {

		// seleziono la cella chiamata vertex
		DefaultGraphCell cell = m_jgAdapter.getVertexCell(vertex);
		
		
		// recupero gli attributi della cella
		Map attr = cell.getAttributes();
		// recupero i boundaries della cella
		Rectangle2D b = GraphConstants.getBounds(attr);
		// setto i parametri del nuovo rettangolo
		GraphConstants.setBounds(attr, new Rectangle(x, y, (int) (((String)vertex).length()+50+b.getWidth()), (int) b.getHeight()));
		// costruisco una nuova cella
		Map cellAttr = new HashMap();
		cellAttr.put(cell, attr);
		
		// posiziono la cella nel grafo
		m_jgAdapter.edit(cellAttr, null, null, null);
		
	}

	public void start() {
		repaint();

	}

	public static void main(String[] args) {

		GraphFramer starter = new GraphFramer("Grafo");

		// create a visualization using JGraph, via an adapter
		String nodi[] = { "ciao", "come", "stai", "oggi", "domani", "dopodomani" };
		for (String nodo : nodi) {
			starter.graphDisplayer.addVertex(nodo);
		}

		for (int j = 0; j < nodi.length; j++) {
			int i0 = (int) (nodi.length * Math.random());
			int i1 = (int) (nodi.length * Math.random());
			System.out.println("i0: " + i0 + " i1: " + i1);
			if (i0 != i1) {
				starter.graphDisplayer.addEdge(nodi[i0], nodi[i1],0);
			}
		}

		starter.graphDisplayer.generateGraph();

		starter.go();

	}

}
