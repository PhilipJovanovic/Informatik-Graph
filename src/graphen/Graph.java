package graphen;
import java.util.ArrayList;
import java.util.Arrays;

public class Graph {
	private ArrayList<ArrayList<Element>> kanten;
	public final int UNENDLICH = Integer.MAX_VALUE / 2;
	private int[][] matrix = null;
	
	public Graph() {
		kanten = new ArrayList<ArrayList<Element>>();
	}
	
	public Graph(int size) {
		kanten = new ArrayList<ArrayList<Element>>();
		for(int i = 0; i <= size; i++) {
			addKnoten();
		}
	}
	
	public void addKnoten() {
		kanten.add(new ArrayList());
	}
	
	public void addKante(int von, int nach, int wert) {
		kanten.get(von).add(new Element(nach, wert));
	}
	
	public void addUngerichteteKante(int von, int nach, int wert) {
		kanten.get(von).add(new Element(nach, wert));
		kanten.get(nach).add(new Element(von, wert));
	}
	
	public ArrayList<ArrayList<Element>> getKanten() {
		return kanten;
	}

	public void setKanten(ArrayList<ArrayList<Element>> kanten) {
		this.kanten = kanten;
	}

	public String toString() {
		String res = "";
		for(int i = 0; i < kanten.size(); i++) {
			res += i + ": ";
			for(Element e : kanten.get(i)) {
				res += e.toString() + ", ";
			}
			res += "\n";
		}
		return res;
	}
	
	public int[] tiefensuche(int x) {
		int[] vorg = new int[kanten.size()];
		for(int i = 0; i < kanten.size(); i++) vorg[i] = 0;
		vorg[x] = x;
		besuche(x, vorg);
		return vorg;
	}
	
	private void besuche(int x, int[] vorg) {
		for(int i = 0; i < kanten.get(x).size(); i++) {
			int next = kanten.get(x).get(i).getKnoten();
			if(vorg[next] == 0) {
				vorg[next] = x;
				besuche(next, vorg);
			}
		}
	}
	
	public int[] breitensuche(int x) {
		ArrayList<Element> knotenSchlange = new ArrayList<Element>();
		int[] vorg = new int[kanten.size()];
		for(int i = 0; i < kanten.size(); i++) vorg[i] = 0;
		vorg[x] = x;
		knotenSchlange.add(new Element(x, 0));	
		while(!knotenSchlange.isEmpty()) {
			Element current = knotenSchlange.get(0);
			knotenSchlange.remove(0);
			for(int i = 0; i < kanten.get(current.getKnoten()).size(); i++) {
				int next = kanten.get(current.getKnoten()).get(i).getKnoten();
				int nextWert = kanten.get(current.getKnoten()).get(i).getWert();
				if(vorg[next] == 0) {
					vorg[next] = current.getKnoten();
					knotenSchlange.add(new Element(next, nextWert));
				}
			}
		}
		return vorg;
	}
	
	public Graph kruskal() {
		ArrayList<Element> sortKanten = new ArrayList<Element>();
		ArrayList<Integer> vonKnoten = new ArrayList<Integer>();
		int next, w;
		for(int i = 0; i < kanten.size(); i++) {
			for(int j = 0; j < kanten.get(i).size(); j++) {
				next = kanten.get(i).get(j).getKnoten();
				w = kanten.get(i).get(j).getWert();
				if(next > i) {
					int k = 0;
					while(sortKanten.size() > 0 && k < sortKanten.size() && w > sortKanten.get(k).getWert()) k++;
					if(k == sortKanten.size()) {
						sortKanten.add(new Element(next, w));
						vonKnoten.add(i);
					}
					else {
						sortKanten.add(k, new Element(next, w));
						vonKnoten.add(k, i);
					}
				}
			}
		}
		Graph neu = new Graph(kanten.size() - 1);
		int count = 0;
		for(int i = 0; i < sortKanten.size(); i++) {
			if(count < kanten.size() - 1) {
				if(neu.tiefensuche(vonKnoten.get(i))[sortKanten.get(i).getKnoten()] == 0) {
					neu.addUngerichteteKante(vonKnoten.get(i), sortKanten.get(i).getKnoten(), sortKanten.get(i).getWert());
					count++;
				}
			}
		}
		return neu;
	}
	
	public int[] dijkstra(int start) {
		int[] vorg = new int[kanten.size()];
		int[] entf = new int[kanten.size()];
		boolean[] mark = new boolean[kanten.size()];
		for(int i = 0; i < kanten.size(); i++) {
			vorg[i] = 0;
			entf[i] = Integer.MAX_VALUE;
			mark[i] = false;
		}
		entf[start] = 0;
		int current = start;
		int next = 0;
		do {
			next = 0;
			mark[current] = true;

			for(int i = 0; i < kanten.get(current).size(); i++) {
				int mgl = kanten.get(current).get(i).getKnoten();
				int mglW = entf[current] + kanten.get(current).get(i).getWert();
				if(mglW < entf[mgl]) {
					entf[mgl] = mglW;
					vorg[mgl] = current;
				}
			}
			for(int i = 0; i < kanten.size(); i++) {
				if(entf[i] < entf[next] && !mark[i]) next = i;
			}
			current = next;
		} while(next > 0);
		return vorg;
	}
	
	private int[][] adj_matrix() {
		int[][] entf = new int[kanten.size()][kanten.size()];
		for(int i = 0; i < kanten.size(); i++) {
			for(int j = 0; j < kanten.size(); j++) {
				entf[i][j] = UNENDLICH;
			}
			entf[i][i] = 0;
		}
		for(int i = 0; i < kanten.size(); i++) {
			for(int j = 0; j < kanten.get(i).size(); j++) {
				entf[i][kanten.get(i).get(j).getKnoten()] = kanten.get(i).get(j).getWert();
			}
		}
		return entf;
	}
	
	public int[][] floyd_warshall() {
		// Erstelle 2D Matrix
		int[][] entf = adj_matrix();
		for(int i = 0; i < kanten.size(); i++) {
			for(int j = 0; j < kanten.get(i).size(); j++) {
				entf[i][kanten.get(i).get(j).getKnoten()] = kanten.get(i).get(j).getWert();
			}
		}
		for(int k = 0; k < kanten.size(); k++) {
			for(int i = 0; i < kanten.size(); i++) {
				for(int j = 0; j < kanten.size(); j++) {
					entf[i][j] = Math.min(entf[i][j], entf[i][k] + entf[k][j]);
				}
			}
		}
		return entf;
	}
	
	public void deleteKnoten(int x) {
		for(int i = 0; i < kanten.size(); i++) {
			int rmIndex = -1;
			for(int j = 0; j < kanten.get(i).size(); j++) {
				if(kanten.get(i).get(j).getKnoten() == x) {
					rmIndex = j;
				}
				if(kanten.get(i).get(j).getKnoten() > x) {
					kanten.get(i).get(j).setKnoten(kanten.get(i).get(j).getKnoten() - 1);;
				}
			}
			if(rmIndex != -1) kanten.get(i).remove(rmIndex);
		}
		kanten.remove(x);
	}
	
	public void deleteKante(int von, int zu) {
		for(int j = 0; j < kanten.get(von).size(); j++) {
			if(kanten.get(von).get(j).getKnoten() == zu) {
				kanten.get(von).remove(j);
				break;
			}
		}
	}
	
	private int[][] matrix(){
		int[][] matrix = new int[kanten.size()][kanten.size()];
		for(int i = 0;i<kanten.size();i++) {
			if(kanten.get(i).size() != 0)
				for(int j=0;j<kanten.get(i).size();j++) 
					matrix[i][kanten.get(i).get(j).getKnoten()] = 1;
		}
		return matrix;
	}
	
	public int[] grad(int[][] matrix) {
		int[] result = new int[matrix.length];
		for(int c = 0;c<matrix.length;c++) {
			for(int i=0;i<matrix.length;i++)
				if(matrix[i][c] != 0 || matrix[c][i] != 0) result[c]++;
		}
		return result;
	}
	
	public int[] ein(int[][] matrix) {
		int[] result = new int[matrix.length];
		for(int c=0;c<matrix.length;c++) {
			for(int i=0;i<matrix.length;i++) {
				if(matrix[i][c] != 0) result[c]++;
			}
		}
		return result;
	}
	
	public int[] aus(int[][] matrix) {
		int[] result = new int[matrix.length];
		for(int c=0;c<matrix.length;c++) {
			for(int i=0;i<matrix.length;i++) {
				if(matrix[c][i] != 0) result[c]++;
			}
		}
		return result;
	}
	
	public Result hierholzer() {
		matrix = matrix();
		int[] grad = grad(matrix);
		int[] aus = aus(matrix);
		int[] ein = ein(matrix);
		ArrayList<ArrayList<Integer>> ret = new ArrayList<>();
		int e = 0;
		for(int i = 0; i< matrix.length;i++)
			if(!(grad[i] % 2 == 0 && aus[i] == ein[i])) e++;
		
		int knoten = 0;
		String art = "";
		if(e == 0) {
			art = "Eulersch";
		}else if(e == 2){
			art = "Semi-Eulersch";
			for(int i = 0;i<grad.length;i++)
				if(grad[i] % 2 != 0) {
					knoten = i; 
					i = grad.length;
				}
		}else{
			art = "Geht nicht!";
			Result back = new Result(art,null);
			return back;
		}
		ret.add(runde(knoten));
		for(int k = 1;k<matrix.length;k++) {
			if(check(grad(matrix)) != -1)
				ret.add(runde(check(grad(matrix))));
		}
		Result back = new Result(art,toArr(ret));
		return back;
	}
	
	private int[] toArr(ArrayList<ArrayList<Integer>> liste) {
		System.out.println("Liste: " +liste.toString() +" - "+ liste.size());
		if(liste.get(0).size() == 1 && liste.get(0).get(0).intValue() == 0) liste.remove(0);
		if(liste.size() > 1) {
			int size = liste.size()-1;
			for(int i = 0;i<size;i++) {
				int wert = liste.get(1).get(0).intValue();
				int pos = liste.get(0).lastIndexOf(wert);
				System.out.println("Liste Nr."+i+ ": Wert: "+wert+", Pos: " +pos + "; Liste: " + liste.toString());
				if(pos != -1) {
					liste.get(0).remove(pos);
					for(int o=0;o<liste.get(1).size();o++) {
						liste.get(0).add(pos+o, liste.get(1).get(o).intValue());
						System.out.println("Liste: " + liste.toString());
					}
					liste.remove(1);
					System.out.println("Liste: " + liste.toString());
				}
			}
		}
		int[] endgültig = new int[liste.get(0).size()];
		for(int p = 0;p<liste.get(0).size();p++)
			endgültig[p] = liste.get(0).get(p).intValue();
		return endgültig;
	}
	
	private ArrayList<Integer> runde(int start) {
		ArrayList<Integer> runde = new ArrayList<>();
		int knoten = start;
		int t = 0;
		int[] grad = grad(matrix);
		boolean b = true;
		runde.add(start);
		do {
			t = knoten;
			knoten = find(matrix,knoten);
			if(knoten != -1) {
				runde.add(knoten);
				matrix[t][knoten] = 0;
				matrix[knoten][t] = 0;
				grad = grad(matrix);
			}else b = false;
		}while(start != knoten && b);
		return runde;
	}

 	private int check(int[] c) {
		for(int i = 0;i<c.length;i++) {
			if(c[i] != 0) return i;
		}
		return -1;
	}
	
	private int find(int[][] matrix, int c) {
		for(int i = 0;i<matrix.length;i++) {
			if(matrix[c][i] != 0) return i;
		}
		return -1;
	}
	
	public ArrayList<Integer> combine_array(ArrayList<Integer> ziel, ArrayList<Integer> liste, int pos) {
		for(int i=0;i<liste.size();i++)
			ziel.add(pos+i,liste.get(i));
		return ziel;
	}
	
	public void print_matrix(int[][] matrix) {
		System.out.print("    ");
		for(int i = 0;i<kanten.size();i++) {
			System.out.print(i+"  ");
		}
		System.out.println();
		for(int k = 0;k<kanten.size();k++) {
			System.out.print(k + ": ");
			for(int i = 0;i<kanten.size();i++)
				System.out.print(" "+(matrix[k][i]) + " ");
			System.out.println();
		}
	}
}



