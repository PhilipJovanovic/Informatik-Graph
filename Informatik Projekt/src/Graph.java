import java.util.ArrayList;
import java.util.Arrays;

public class Graph {
	private ArrayList<ArrayList<Element>> kanten;
	private final int UNENDLICH = Integer.MAX_VALUE / 2;
	
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
}