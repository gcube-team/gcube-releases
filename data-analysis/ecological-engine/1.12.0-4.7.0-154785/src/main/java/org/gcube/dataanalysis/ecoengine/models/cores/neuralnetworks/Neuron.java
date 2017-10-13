
package org.gcube.dataanalysis.ecoengine.models.cores.neuralnetworks;
import java.io.Serializable;
import java.util.Random;

public class Neuron implements Serializable{
	
	public static final long serialVersionUID=1;
    public int attivfun;
    public float[] W;


  public Neuron (int N,int numero){
  attivfun = numero;
  W = new float[N];

  Random rand = new Random();
  for (int i = 0; i < N ;i++) {
  W[i] = 2*rand.nextFloat()-1;} //0.5f;
   }

   public Neuron (int N,int numero,float[] V){
    attivfun = numero;
    W = new float[N];

    if (V.length == N){
      for (int i = 0; i < N ;i++) {
    W[i] = V[i];}
  }
    else
      System.out.println("Error : weights vector lenght is not correct");
     }


  public double generaOutput(double input) {
if (attivfun == 1)
{if (input > 0) return 1;
 else return 0;}
else
if (attivfun == 2)
{return (1/(1 + Math.exp(-1 * input)));}
if (attivfun == 3)
{return input;}
else
return 1;
}


public void aggiornaPesi(float[] V) {
 for(int i = 0;i < V.length;i++)
 W[i] -= V[i];
 }




  public static void main(String[] args) {
    Neuron neuron1 = new Neuron(4,1);
    System.out.println("con la sigmoide: "+ neuron1.generaOutput(1));
     System.out.println("con heaviside: " + neuron1.generaOutput(1));
      System.out.println("heaviside e input negativo: " + neuron1.generaOutput(-12));
       System.out.println("unitaria e input positivo: " + neuron1.generaOutput(12));
    float[] G = new float[4];
    G[0] =  0.1F;
    G[1] =  0.2F;
    G[2] =  0.1F;
    G[3] = 2F;
    neuron1.aggiornaPesi(G);
    for (int i = 0; i < G.length;i++)
    System.out.println(neuron1.W[i]);
  }

}