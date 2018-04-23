package graphen;

import java.util.Arrays;

public class graphen {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Graph g = new Graph();
		for(int i = 0; i <= 5; i++) {
			g.addKnoten();
		}
		
		g.addKante(1,3,10);
		g.addKante(2,1,1);
		g.addKante(2,4,7);
		g.addKante(3,1,3);
		g.addKante(3,2,4);
		g.addKante(3,4,2);
		g.addKante(5,3,1);
		g.addKante(5,4,2);
		
		System.out.println(g);
		
		int[] vorg = g.tiefensuche(2);
		for(int i = 0; i < vorg.length; i++) {
			System.out.println((i) + " " + vorg[i]);
		}
		
		vorg = g.breitensuche(2);
		for(int i = 0; i < vorg.length; i++) {
			System.out.println((i) + " " + vorg[i]);
		}
		
		Graph g2 = new Graph(7);
		g2.addUngerichteteKante(1,2,10);
		g2.addUngerichteteKante(1,4,9);
		g2.addUngerichteteKante(2,3,5);
		g2.addUngerichteteKante(2,5,4);
		g2.addUngerichteteKante(2,4,3);
		g2.addUngerichteteKante(3,5,10);
		g2.addUngerichteteKante(4,5,8);
		g2.addUngerichteteKante(4,6,6);
		g2.addUngerichteteKante(5,6,2);
		g2.addUngerichteteKante(5,7,7);
		g2.addUngerichteteKante(6,7,11);
		Graph minSpanTree = g2.kruskal();
		System.out.println(minSpanTree);
		
		Graph g3 = new Graph(8);
		g3.addUngerichteteKante(1,2,3);
		g3.addUngerichteteKante(1,3,45);
		g3.addUngerichteteKante(2,3,28);
		g3.addUngerichteteKante(2,4,26);
		g3.addUngerichteteKante(3,8,25);
		g3.addUngerichteteKante(3,5,16);
		g3.addUngerichteteKante(4,5,7);
		g3.addUngerichteteKante(4,6,21);
		g3.addUngerichteteKante(5,7,27);
		g3.addUngerichteteKante(6,7,14);
		g3.addUngerichteteKante(7,8,5);
		
		int[] vor = g3.dijkstra(1);
		for(int i = 0; i < vor.length; i++) {
			System.out.println(i + " : " + vor[i]);
		}
		
		int[][] floyd = g3.floyd_warshall();
		for(int i = 1; i < floyd.length; i++) {
			for(int j = 1; j < floyd.length; j++) {
				System.out.print(floyd[i][j] + " ");
			}
			System.out.println();
		}
	}

}
