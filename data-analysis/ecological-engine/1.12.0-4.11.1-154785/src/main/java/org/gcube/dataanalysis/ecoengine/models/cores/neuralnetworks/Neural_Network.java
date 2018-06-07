package org.gcube.dataanalysis.ecoengine.models.cores.neuralnetworks;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;

public class Neural_Network implements Serializable {
	public Neuron[][] griglia;
	static final long serialVersionUID = 1;
	// originale = 1.2
//	double soglia = 0.001;
//	double maxcycle = 1000;
	
	double soglia = 0.0001;
	double maxcycle = 3000;
	double acceptanceThr = 0.5;
	public double maxfactor=1;
	public double minfactor=0;
	
	public void setThreshold(double soglia) {
		this.soglia = soglia;
	}
	
	public double getCorrectValueFromOutput(double prob){
		return prob*maxfactor+(1-prob)*minfactor;
	}
	
	public double getCorrectValueForOutput(double output){
		return (double)(output-minfactor)/(maxfactor-minfactor);
	}
	
	public void setAcceptanceThreshold(double treshold) {
		this.acceptanceThr = treshold;
	}
	
	public void setCycles(double cycs) {
		this.maxcycle = cycs;
	}

	public static enum ACTIVATIONFUNCTION {
		HEAVYSIDE, SIGMOID, IDENTITY
	}

	public Neural_Network(int N, int M, ACTIVATIONFUNCTION attifun) {
		this(N, M, attifun.ordinal() + 1);
	}

	public Neural_Network(int N, int M, ACTIVATIONFUNCTION attifun, float[] V) {
		this(N, M, attifun.ordinal() + 1, V);
	}

	public static double[] preprocessObjects(Object[] vector) {

		double[] out = new double[vector.length];

		for (int i = 0; i < vector.length; i++) {
			double element = 0;
			if (vector[i] != null)
				element = Double.parseDouble("" + vector[i]);
			
			if (element == 0)
				element = 0.1;
			
			out[i] = element;
		}

		return out;
	}

	public static double[] preprocessObjects(double[] vector) {

		double[] out = new double[vector.length];

		for (int i = 0; i < vector.length; i++) {
			double element = vector[i];
			if (element == 0)
				element = 0.1;
			
			out[i] = element;
		}

		return out;
	}

	public int getNumberOfOutputs(){
		if (griglia!=null)
			return griglia[griglia.length - 1].length;
		else
			return 0;
	}
	
	public int getNumberOfInputs(){
		if (griglia!=null)
			return griglia[0].length;
		else
			return 0;
	}
	
	public double[] getPositiveCase() {
		double[] out = new double[0];

		if (griglia.length > 0) {
			out = new double[griglia[griglia.length - 1].length];
			for (int i = 0; i < out.length; i++) {
				out[i] = 1f;
			}
		}

		return out;
	}

	public double[] getNegativeCase() {
		double[] out = new double[0];

		if (griglia.length > 0) {
			out = new double[griglia[griglia.length - 1].length];
			for (int i = 0; i < out.length; i++) {
				out[i] = 0.0f;
			}
		}

		return out;
	}

	public static int[] setupInnerLayers(int... numberOfNeurons) {
		int[] layers = null;

		if (numberOfNeurons.length > 0) {
			layers = new int[numberOfNeurons.length];

			for (int i = 0; i < numberOfNeurons.length; i++) {
				layers[i] = numberOfNeurons[i];
			}
		}
		return layers;
	}

	public Neural_Network(int N, int M, int[] t, ACTIVATIONFUNCTION attifun) {
		this(N, M, t, attifun.ordinal() + 1);
	}


	public Neural_Network(int N, int M, int attifun) {
		this.griglia = new Neuron[2][];

		Neuron[] input = new Neuron[N + 1];
		input[0] = new Neuron(M, 4);
		for (int i = 1; i < N + 1; i++) {
			input[i] = new Neuron(M, 3);
		}
		this.griglia[0] = input;

		Neuron[] output = new Neuron[M];
		for (int i = 0; i < M; i++) {
			output[i] = new Neuron(0, attifun);
		}
		this.griglia[1] = output;
	}
	public Neural_Network(int N, int M, int attifun, float[] V) {
		griglia = new Neuron[2][];
		Neuron[] input = new Neuron[N + 1];
		input[0] = new Neuron(M, 4);
		for (int i = 1; i < N + 1; i++) {
			input[i] = new Neuron(M, 3, V);
		}
		this.griglia[0] = input;
		Neuron[] output = new Neuron[M];
		for (int i = 0; i < M; i++) {
			output[i] = new Neuron(0, attifun);
		}
		this.griglia[1] = output;
	}
	
	public Neural_Network(int N, int M, int[] t, int attifun) {

		griglia = new Neuron[t.length + 2][];
	
		Neuron[] input = new Neuron[N + 1];
		input[0] = new Neuron(t[0], 4);
		for (int i = 1; i < N + 1; i++) {
			input[i] = new Neuron(t[0], 3);
		}
		this.griglia[0] = input;

		Neuron[] aux;
		for (int i = 0; i < t.length; i++) {
			aux = new Neuron[t[i] + 1];
			if (i != t.length - 1) {
				aux[0] = new Neuron(t[i + 1], 4);
				for (int g = 1; g < (t[i] + 1); g++) {
					aux[g] = new Neuron(t[i + 1], attifun);
				}
			} else {
				aux[0] = new Neuron(M, 4);
				for (int j = 1; j < t[i] + 1; j++) {
					aux[j] = new Neuron(M, attifun);
				}
			}

			this.griglia[i + 1] = aux;
		}

		Neuron[] output = new Neuron[M];
		for (int i = 0; i < M; i++) {
			output[i] = new Neuron(0, attifun);
		}
		this.griglia[t.length + 1] = output;

	}

	public double[] propagate(double[] input) {
		if (input.length == griglia[0].length - 1)
			return prop(input, 0);
		else
			System.out.println("Error : number of inputs not valid!");
		
		return null;
	}

	private double[] prop(double[] input, int i) {
		double multip;
		double[] arrayaux;

		if (griglia[i][0].W.length != 0) {
			arrayaux = new double[griglia[i][0].W.length];
			for (int j = 0; j < griglia[i][0].W.length; j++)

			{
				multip = griglia[i][0].W[j];
				for (int g = 1; g < griglia[i].length; g++) {
					multip += griglia[i][g].W[j] * griglia[i][g].generaOutput(input[g - 1]);
				}
				arrayaux[j] = multip;
			}
			return prop(arrayaux, i + 1);
		} else
		{
			arrayaux = new double[griglia[i].length];
			for (int j = 0; j < griglia[i].length; j++)
				arrayaux[j] = griglia[i][j].generaOutput(input[j]);

			return arrayaux;
		}

	}
	
	private double[][] trainpropagate(double[] input) {
		double[][] arrayout = new double[griglia.length + 1][];
		if (input.length == griglia[0].length - 1)
		{
			arrayout[0] = input;
			for (int i = 0; i < griglia.length; i++)
				arrayout[i + 1] = trainprop(arrayout[i], i);
			return arrayout;
		} else
			System.out.println("Error : number of inputs not valid!");
		return null;
	}
	
	private double[] trainprop(double[] input, int i) {
		double multip;
		double[] arrayaux;

		if (griglia[i][0].W.length != 0) {
			arrayaux = new double[griglia[i][0].W.length];
			for (int j = 0; j < griglia[i][0].W.length; j++)

			{
				multip = griglia[i][0].W[j];
				for (int g = 1; g < griglia[i].length; g++) {
					multip += griglia[i][g].W[j] * griglia[i][g].generaOutput(input[g - 1]);
				}
				arrayaux[j] = multip;
			}

			return arrayaux;
		} else {
			arrayaux = new double[griglia[i].length];
			for (int j = 0; j < griglia[i].length; j++)
				arrayaux[j] = griglia[i][j].generaOutput(input[j]);

			return arrayaux;
		}
	}
	
	private void BProp(double[] input, double[] realvalues) {

		double[][] Ai = trainpropagate(input);
		int lungtrain = Ai.length;
		double[][] arraydelta = new double[griglia.length - 1][];
		int lungdelta = this.griglia.length - 1;
			arraydelta[lungdelta - 1] = new double[Ai[lungtrain - 1].length];
	
		for (int i = 0; i < Ai[lungtrain - 1].length; i++) {
			double Yk = Ai[lungtrain - 1][i];
			double Ak = Ai[lungtrain - 2][i];
			double Tk = realvalues[i];

			double Dk = (Yk - Tk) * (1 / (1 + Math.exp(-1 * Ak))) * (1 - ((1 / (1 + Math.exp(-1 * Ak)))));
			arraydelta[lungdelta - 1][i] = Dk;
			}

		for (int g = lungdelta - 2; g >= 0; g--) {
			arraydelta[g] = new double[Ai[g + 1].length];

			double[] DKnext = arraydelta[g + 1];
			for (int j = 0; j < Ai[g + 1].length; j++) {
				double Ak = Ai[g + 1][j];
				double somma = 0;
				for (int k = 0; k < arraydelta[g + 1].length; k++) {
					somma += griglia[g + 1][j + 1].W[k] * DKnext[k];
				}
				double Dk = somma * (1 / (1 + Math.exp(-1 * Ak))) * (1 - ((1 / (1 + Math.exp(-1 * Ak)))));
				arraydelta[g][j] = Dk;
			}
		}

		for (int g = 0; g < griglia.length - 1; g++) {
			float[] D = new float[Ai[g + 1].length];
			for (int k = 0; k < Ai[g + 1].length; k++) {
				D[k] = (float) (0.5f * arraydelta[g][k]);
			}
			griglia[g][0].aggiornaPesi(D);
			for (int i = 1; i < griglia[g].length; i++) {
				float[] V = new float[Ai[g + 1].length];

				for (int k = 0; k < Ai[g + 1].length; k++) {
					V[k] = (float) (0.5f * (griglia[g][i].generaOutput(Ai[g][i - 1]) * arraydelta[g][k]));
				}

				griglia[g][i].aggiornaPesi(V);
			}
		}
	}
	public float status=0;
	public double en;
	public void train(double[][] inputvet, double[][] correctoutputvet) {
	
		if (griglia[griglia.length - 1].length != correctoutputvet[0].length)
			AnalysisLogger.getLogger().debug("Error :  the vector of outputs has not a lenght equal to the output of the network");
		else {
			en = 2;
			int counter = 0;
			double enprec=2;
			while ((en > soglia) && (counter <= maxcycle)) {
				en = 0;
				for (int i = 0; i < inputvet.length; i++) {
			
					this.BProp(inputvet[i], correctoutputvet[i]);
					en += energy(this.propagate(inputvet[i]), correctoutputvet[i]);
				}
				
				AnalysisLogger.getLogger().debug("Learning Score: " + en);
				counter++;
				status = (float)counter/(float)maxcycle;
				
				if (en==enprec)
					break;
				else
					enprec=en;
			
			}
			System.out.println("Final Error: " + en);
			if (counter >= maxcycle)
				AnalysisLogger.getLogger().debug("training incomplete: didn't manage to reduce the error under the thr!");
			else
				AnalysisLogger.getLogger().debug("training complete!");
			status = 100;
		}
	}
	
	private double energy(double[] vettore1, double[] vettore2) {
		double nrg = (float) Math.pow((vettore1[0] - vettore2[0]), 2);
		for (int i = 1; i < vettore2.length; i++) {
			nrg = nrg + Math.pow((vettore1[i] - vettore2[i]), 2);
		}
		return (float) (0.5 * nrg);
	}
	
	
	
	public void writeout(double numero, double soglia) {
		if (numero < soglia)
			System.out.println("Output : " + 0);
		else
			System.out.println("Output : " + 1);
	}

	//classify
	public double[] getClassification(double[] out){
		double[] o = new double[out.length];
		for (int i=0;i<out.length;i++){
			if (out[i]<acceptanceThr)
				o[i] = 0;
			else
				o[i] = 1;
		}
		return o;
	}
	
	public static synchronized Neural_Network loadNN(String nomeFile) {

		Neural_Network nn = null;
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(nomeFile);
			ObjectInputStream ois = new ObjectInputStream(stream);
			nn = (Neural_Network) ois.readObject();
		} catch (Exception ex) {
			ex.printStackTrace();
			AnalysisLogger.getLogger().debug("Error in reading the object from file " + nomeFile + " .");
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
			}
		}

		return nn;
	}
	
	public static String generateNNName(String referenceEntity,String username,String neuralNetName){
		return referenceEntity+"_"+username+"_"+neuralNetName;
	}
	
	public static void main(String[] args) {
		int[] t = { 2 };
	
		Neural_Network nn = new Neural_Network(2, 1, t, 2);
		double[] input = { 1, 1 };
		double[] output = { 0 };
		double[] input1 = { 0.1, 0.1 };
		double[] output1 = { 0 };
		double[] input2 = { 1, 0.1 };
		double[] output2 = { 1 };
		double[] input3 = { 0.1, 1 };
		double[] output3 = { 1 };
		double[][] in = new double[4][];
		double[][] out = new double[4][];
		in[0] = input;
		in[1] = input1;
		out[0] = output;
		out[1] = output1;
		in[2] = input2;
		out[2] = output2;
		in[3] = input3;
		out[3] = output3;

		nn.train(in, out);
		double[] dummy = { 0, 0 };
		System.out.println("dummy test " + nn.propagate(dummy)[0]);

		nn.writeout(nn.propagate(dummy)[0], 0.5);
	
	}

}
