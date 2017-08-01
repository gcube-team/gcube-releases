package org.gcube.dataanalysis.ecoengine.test.neuralnetwork;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.gcube.dataanalysis.ecoengine.models.cores.neuralnetworks.Neural_Network;

public class TestNN {

	public static void main1(String[] args) {

		int numberOfInputs = 2;
		int numberOfOutputs = 1;
		int[] innerLayers = Neural_Network.setupInnerLayers(2);

		Neural_Network nn = new Neural_Network(numberOfInputs, numberOfOutputs, innerLayers, Neural_Network.ACTIVATIONFUNCTION.SIGMOID);

		double[] input1 = { 1, 1 };
		input1 = Neural_Network.preprocessObjects(input1);

		double[] output1 = nn.getPositiveCase();

		double[] input2 = { 0, 0 };
		input1 = Neural_Network.preprocessObjects(input1);

		double[] output2 = nn.getNegativeCase();

		double[] input3 = { 1, 0 };
		input1 = Neural_Network.preprocessObjects(input1);

		double[] output3 = nn.getNegativeCase();

		double[] input4 = { 0, 1 };
		input1 = Neural_Network.preprocessObjects(input1);
		double[] output4 = nn.getNegativeCase();

		double[][] in = new double[4][];
		double[][] out = new double[4][];
		in[0] = input1;
		out[0] = output1;
		in[1] = input2;
		out[1] = output2;
		in[2] = input3;
		out[2] = output3;
		in[3] = input4;
		out[3] = output4;

		nn.train(in, out);

		System.out.println("addestramento compiuto");

		double[] nninput = { 1, 1 };
		Neural_Network.preprocessObjects(nninput);

		nn.setAcceptanceThreshold(0.80f);

		double[] nnout = nn.getClassification(nn.propagate(nninput));

		System.out.println("calcolo compiuto");
		System.out.println();

		for (int i = 0; i < nnout.length; i++) {
			// nn.writeout(nnout[i],0.5);
			System.out.print("valore reale: " + nnout[i]);
		}

	}

	public static void main(String[] args) {

		int numberOfInputs = 13;
		int numberOfOutputs = 1;
		int[] innerLayers = Neural_Network.setupInnerLayers(2);

		Neural_Network nn = new Neural_Network(numberOfInputs, numberOfOutputs, innerLayers, Neural_Network.ACTIVATIONFUNCTION.SIGMOID);

		double[] input1 = new double[numberOfInputs];
		for (int i = 0; i < numberOfInputs; i++) {
			input1[i] = 20 * Math.random();
		}
		input1 = Neural_Network.preprocessObjects(input1);
		double[] output1 = nn.getPositiveCase();

		double[] input2 = new double[numberOfInputs];
		for (int i = 0; i < numberOfInputs; i++) {
			input2[i] = 20 * Math.random();
		}

		input2 = Neural_Network.preprocessObjects(input2);
		double[] output2 = nn.getNegativeCase();

		double[][] in = new double[2][];
		double[][] out = new double[2][];
		in[0] = input1;
		out[0] = output1;
		in[1] = input2;
		out[1] = output2;

		nn.train(in, out);

		System.out.println("addestramento compiuto");
		salva("./cfg/nn_Fis-22747",nn);
		
		double[] inputnn = new double[numberOfInputs];
		for (int i = 0; i < numberOfInputs; i++) {
			inputnn[i] = Math.random();
		}

		Neural_Network.preprocessObjects(inputnn);

		nn.setAcceptanceThreshold(0.80f);

		double[] nnout = nn.getClassification(nn.propagate(inputnn));

		System.out.println("calcolo compiuto");
		System.out.println();

		for (int i = 0; i < nnout.length; i++) {
			// nn.writeout(nnout[i],0.5);
			System.out.print("valore reale: " + nnout[i]);
		}

	}

	public static void salva(String nomeFile, Neural_Network nn) {

		File f = new File(nomeFile);
		FileOutputStream stream = null;
		try {
			stream = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(stream);
			oos.writeObject(nn);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Errore di scrittura dell'oggetto sul File: " + nomeFile);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
			}
		}
		System.out.println("OK scrittura dell'oggetto sul File: " + nomeFile);
	}
}
