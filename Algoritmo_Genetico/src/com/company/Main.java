package com.company;

import java.util.Arrays;
import java.util.Scanner;
import java.util.Random;
import java.util.stream.IntStream;

public class Main {

	static class Cromosoma implements Comparable<Cromosoma>{
		String valor;
		int fitness;

		public Cromosoma(String string, int i) {
			valor = string;
			fitness = i;
		}
		public void setFitness(int x){
			fitness=x;
		}
		public void aumentarFitness(){
			fitness++;
		}

		@Override
		public int compareTo(Cromosoma o) {
			if (fitness < o.fitness) return -1;
			if (fitness > o.fitness) return 1;
			return 0;
		}
	}
	static int ELEMENTOS_POBLACION = 500;
	static int ELEMENTOS_ELITE = (int) (ELEMENTOS_POBLACION*0.1); //10% de la poblacion
	static final int MAX_ITERACIONES = 10000;
	static final int PROBABILIDAD_MUTACION = 50; //en porcentaje
	static Cromosoma[] cromosomas = new Cromosoma[ELEMENTOS_POBLACION]; //poblacion principal
	static Cromosoma[] sgteGeneracion = new Cromosoma[ELEMENTOS_ELITE]; //un hijo por cada elite
	static Cromosoma[] poblacionMasHijos = new Cromosoma[ELEMENTOS_POBLACION + ELEMENTOS_ELITE]; //juntamos las generaciones para eveluarlas
	static final String cadena = "esta es la cadena char objetivo"; //string objetivo
	static final int longitudCadena = cadena.length();
	static int cantidadPruebas = 1;


	public static void main(String[] args) {

		System.out.print("Cadena objetivo: " + cadena);

		primeraGeneracion();
		evaluarCromosoma(cromosomas);
		ordenarPoblacion(cromosomas);

		while (!resultadoObtenido() && cantidadPruebas != MAX_ITERACIONES){
			siguienteGeneracion();
			mutacion();
			poblacionMasHijos = concatenar(cromosomas, sgteGeneracion);
			evaluarCromosoma(poblacionMasHijos);
			ordenarPoblacion(poblacionMasHijos);
			generarPoblacionPrincipal();
			cantidadPruebas++;
		}

		System.out.println("\nCantidad de generaciones: " + cantidadPruebas);
	}

	public static void primeraGeneracion(){
		for (int i = 0; i < ELEMENTOS_POBLACION; i++) {

			String stringRandom = generarCadena();

			try {
				cromosomas[i] = new Cromosoma(stringRandom, 0);
			}
			catch (NullPointerException exception){
				System.out.println(exception.getCause());
			}
		}
	}
	public static String generarCadena(){
		int limiteIzquierdo = 32; // limites de caracteres imprimibles
		int limiteDerecho = 126; // limites de caracteres imprimibles
		Random random = new Random();

		String stringRandom = random.ints(limiteIzquierdo, limiteDerecho + 1)
				.limit(longitudCadena)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();

		return stringRandom;
	}

	public static void evaluarCromosoma(Cromosoma poblacion[]){
		for (Cromosoma x: poblacion) {
			x.setFitness(0);
		}
		for (Cromosoma x:poblacion) {
			for (int i = 0; i < longitudCadena; i++) {
				if (x.valor.charAt(i) != cadena.charAt(i)) x.aumentarFitness();
			}
		}
	}

	public static void ordenarPoblacion(Cromosoma poblacion[]) {
		Arrays.sort(poblacion);
	}

	public static void siguienteGeneracion(){
		Random random = new Random();
		String padre2;
		for (int i = 0; i < ELEMENTOS_ELITE; i++) {
			padre2 = cromosomas[random.nextInt(ELEMENTOS_POBLACION)].valor;
			sgteGeneracion[i] = new Cromosoma( cruzar(cromosomas[i].valor, padre2),0 );
		}
	}

	public static String cruzar(String padre, String padre2){
		Random random = new Random();
		char[] charHijo = padre.toCharArray();
		for (int i = 0; i < longitudCadena; i++) {
			if(random.nextBoolean())  charHijo[i] = padre2.charAt(i);
		}
		return String.valueOf(charHijo);
	}

	public static void mutacion(){
		Random random = new Random();
		for (Cromosoma x: sgteGeneracion) {
			if (random.nextInt(100)<PROBABILIDAD_MUTACION){
				char[] cadenaChar = x.valor.toCharArray();
				for (int i = 0; i < longitudCadena; i++) {
					if (random.nextInt(100)<10){ //10% de posibilidad de mutar por cada gen
						cadenaChar[i] = (char)(random.nextInt(126-32)+32); //rango ASCII imprimible
					}
				}
				x.valor = String.valueOf(cadenaChar);
			}
		}
	}

	public static Cromosoma[] concatenar(Cromosoma[] cadena1, Cromosoma[] cadena2) {
		Cromosoma[] result = new Cromosoma[cadena1.length + cadena2.length];

		System.arraycopy(cadena1, 0, result, 0, cadena1.length);
		System.arraycopy(cadena2, 0, result, cadena1.length, cadena2.length);

		return result;
	}

	public static void generarPoblacionPrincipal(){  //metodo que elimina los cromosomas con menor calidad
		for (int i = 0; i < ELEMENTOS_POBLACION; i++) {
			cromosomas[i] = poblacionMasHijos[i];
		}
	}

	public static boolean resultadoObtenido(){  //evalua si el mejor cromosoma no difiere del objetivo
		if (cromosomas[0].fitness == 0) return true;
		return false;
	}
}
