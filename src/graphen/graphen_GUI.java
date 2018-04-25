package graphen;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Canvas;
import javax.swing.JButton;
import javax.swing.JComboBox;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFormattedTextField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JSpinner;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableModel;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.Box;
import javax.swing.JMenuBar;
import java.awt.Toolkit;
import java.awt.Window.Type;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingConstants;
import javax.swing.JTextPane;
import javax.swing.JTable;
import javax.swing.table.*;
import javax.swing.SpinnerNumberModel;
import javax.swing.JLabel;

public class graphen_GUI extends JFrame {
	private JPanel contentPane;
	private ArrayList<Koordinate> positionen;
	private Graph graph;
	
	private int[][] markiert;
	
	private final int KNOTEN_DURCHMESSER = 40;
	
	private Koordinate lastPos;
	private int lastKnoten1 = -1;
	private int lastKnoten2 = -1;
	
	private JPanel panel;
	private JTable table;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					graphen_GUI frame = new graphen_GUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public graphen_GUI() {
		setTitle("UFGRAPH GUI");
		setIconImage(Toolkit.getDefaultToolkit().getImage(".\\graphen-icon.jpeg"));
		setResizable(false);
		graph = new Graph(0);
		positionen = new ArrayList<Koordinate>();
		positionen.add(new Koordinate(0,0));
		lastPos = new Koordinate(0, 0);
		markiert = new int[0][0];
		erstelleMarkiert();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1067, 765);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnDatei = new JMenu("Graph");
		menuBar.add(mnDatei);
		
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnKnotenHinzufgen = new JButton("Knoten hinzuf\u00FCgen");
		btnKnotenHinzufgen.setBounds(666, 11, 187, 23);
		contentPane.add(btnKnotenHinzufgen);
		
		JComboBox algorithmus = new JComboBox();
		algorithmus.setModel(new DefaultComboBoxModel(new String[] {"Dijkstra", "Tiefensuche", "Breitensuche", "Kruskal", "Hamilton", "Hierholz", "Floyd-Warshall"}));
		algorithmus.setBounds(794, 143, 257, 20);
		contentPane.add(algorithmus);
		
		table = new JTable();
		table.setBounds(665, 231, 385, 327);
		contentPane.add(table);
		
		panel = new JPanel();
		panel.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent arg0) {
				int k = knotenHit(new Koordinate(arg0.getX(),arg0.getY()));
				if(k != -1) {
					positionen.get(k).setX(arg0.getX());
					positionen.get(k).setY(arg0.getY());
					update(panel.getGraphics());
				}
			}
			
		});
		panel.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		panel.setBackground(Color.WHITE);
		panel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(knotenHit(new Koordinate(e.getX(), e.getY())) >= 0) {
					lastKnoten2 = lastKnoten1;
					lastKnoten1 = knotenHit(new Koordinate(e.getX(), e.getY()));
				} else {
					lastPos = new Koordinate(e.getX(), e.getY());
				}
				update(panel.getGraphics());
			}
		});
		panel.setBounds(10, 10, 646, 688);
		contentPane.add(panel);
		
		JSpinner kantenGewicht = new JSpinner();
		kantenGewicht.setModel(new SpinnerNumberModel(0, null, 9999999, 1));
		kantenGewicht.setBounds(794, 44, 257, 20);
		contentPane.add(kantenGewicht);
		
		JButton btnGerichteteKanteHinzufgen = new JButton("Gerichtete Kante hinz.");
		btnGerichteteKanteHinzufgen.setBounds(666, 109, 187, 23);
		contentPane.add(btnGerichteteKanteHinzufgen);
		
		JTextPane textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.setBounds(666, 586, 385, 77);
		contentPane.add(textPane);
		
		JButton btnAddKanteUngerichtet = new JButton("Ungerichtete Kante hinz.");
		btnAddKanteUngerichtet.setBounds(666, 75, 187, 23);
		contentPane.add(btnAddKanteUngerichtet);
		
		JButton btnAlgorithmusAusfhren = new JButton("Algorithmus ausf\u00FChren");
		btnAlgorithmusAusfhren.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String t = "";
				erstelleMarkiert();
                switch(algorithmus.getSelectedIndex()) {
                    case 0:
                    	t = "Dijkstra";
                    	int[] vorDijkstra = graph.dijkstra(lastKnoten1);
                    	erstelleMarkiert();
                    	for(int i = 1; i < vorDijkstra.length; i++) {
                    		System.out.println(vorDijkstra[i]);
                    		markiert[vorDijkstra[i]][i] = 0;
                    	}
                    	update(panel.getGraphics());
                        break;
                    case 1: 
                    	t = "Tiefensuche";
                    	int[] vorTiefensuche = graph.tiefensuche(lastKnoten1);
                    	erstelleMarkiert();
                    	for(int i = 1; i < vorTiefensuche.length; i++) {
                    		markiert[vorTiefensuche[i]][i] = 0;
                    	}
                    	update(panel.getGraphics());
                        break;
                    case 2: 
                    	t = "Breitensuche";
                    	int[] vorBreitensuche = graph.breitensuche(lastKnoten1);
                    	erstelleMarkiert();
                    	for(int i = 1; i < vorBreitensuche.length; i++) {
                    		markiert[vorBreitensuche[i]][i] = 0;
                    	}
                    	update(panel.getGraphics());
                        break;
                    case 3:
                    	t = "Kruskal";
                    	Graph k = graph.kruskal();
                    	for(int i = 0; i < k.getKanten().size(); i++) {
                    		for(int j = 0; j < k.getKanten().get(i).size(); j++) {
                    			markiert[i][k.getKanten().get(i).get(j).getKnoten()] = 0;
                    		}
                    	}
                    	update(panel.getGraphics());
                        break;
                    case 4: t = "Hamilton";
                        break;
                    case 5: t = "Hierholz";
							Result result = graph.hierholzer();
							textPane.setText(result.toString());
                        break;
                    case 6: 
                    	t = "Floyd-Warshall";
	                    int[][] floyd = graph.floyd_warshall();
	                    String[] columns = new String[floyd.length];
	                    Object[][] out = new Object[floyd.length][floyd.length];
	            		for(int i = 0; i < floyd.length; i++) {
	            			columns[i] = Integer.toString(i);
	            			for(int j = 0; j < floyd.length; j++) {
	            				if(floyd[i][j] >= graph.UNENDLICH / 2)
	            					out[i][j] = "INF";
	            				else {
	            					out[i][j] = Integer.toString(floyd[i][j]);
	            				}
	            			}
	            			out[i][0] = i;
	            			out[0][i] = i;
	            		}
	            		out[0][0] = "von\\zu";
	            		table.setModel(new DefaultTableModel(out, columns));
                        break;
                }
                System.out.println("Ausgewählter Algorithmus: " + t);
			}
		});
		btnAlgorithmusAusfhren.setBounds(666, 174, 385, 23);
		contentPane.add(btnAlgorithmusAusfhren);
		
		JButton btnGraphLschen = new JButton("Graph l\u00F6schen");
		btnGraphLschen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				graph = new Graph(0);
				positionen = new ArrayList<Koordinate>();
				positionen.add(new Koordinate(0,0));
				erstelleMarkiert();
				update(panel.getGraphics());
			}
		});
		btnGraphLschen.setBounds(666, 675, 385, 23);
		contentPane.add(btnGraphLschen);
		
		JButton btnLscheKnoten = new JButton("Knoten l\u00F6schen");
		btnLscheKnoten.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				graph.deleteKnoten(lastKnoten1);
				positionen.remove(lastKnoten1);
				erstelleMarkiert();
				update(panel.getGraphics());
			}
		});
		btnLscheKnoten.setBounds(864, 11, 187, 23);
		contentPane.add(btnLscheKnoten);
		
		JButton btnKanteLschen = new JButton("Kante l\u00F6schen");
		btnKanteLschen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				graph.deleteKante(lastKnoten2, lastKnoten1);
				update(panel.getGraphics());
			}
		});
		btnKanteLschen.setBounds(863, 109, 188, 23);
		contentPane.add(btnKanteLschen);
		
		JButton btnBeideKantenLschen = new JButton("Beide Kanten l\u00F6schen");
		btnBeideKantenLschen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				graph.deleteKante(lastKnoten2, lastKnoten1);
				graph.deleteKante(lastKnoten1, lastKnoten2);
				update(panel.getGraphics());
			}
		});
		btnBeideKantenLschen.setBounds(863, 75, 188, 23);
		contentPane.add(btnBeideKantenLschen);
		
		JLabel lblAdjazenzmatrix = new JLabel("Adjazenzmatrix:");
		lblAdjazenzmatrix.setBounds(665, 212, 114, 14);
		contentPane.add(lblAdjazenzmatrix);
		
		JLabel lblAusgabe = new JLabel("Ausgabe:");
		lblAusgabe.setBounds(668, 569, 61, 16);
		contentPane.add(lblAusgabe);
		
		JMenuItem mntmImport = new JMenuItem("Import");
		mntmImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Import");
				try {
					_import(panel.getGraphics());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		mntmImport.setHorizontalAlignment(SwingConstants.LEFT);
		mnDatei.add(mntmImport);
		
		JMenuItem mntmExport = new JMenuItem("Export");
		mntmExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Export");
				try {
					_export();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		mntmExport.setHorizontalAlignment(SwingConstants.LEFT);
		mnDatei.add(mntmExport);
		
		btnGerichteteKanteHinzufgen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(lastKnoten1 >= 0 && lastKnoten2 >= 0 && lastKnoten1 != lastKnoten2) {
					graph.addKante(lastKnoten2, lastKnoten1, (int) kantenGewicht.getValue());
					update(panel.getGraphics());
				}
			}
		});
		
		btnAddKanteUngerichtet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(lastKnoten1 >= 0 && lastKnoten2 >= 0 && lastKnoten1 != lastKnoten2) {
					graph.addUngerichteteKante(lastKnoten2, lastKnoten1, (int) kantenGewicht.getValue());
					update(panel.getGraphics());
				}
			}
		});
		
		btnKnotenHinzufgen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(knotenHit(lastPos) == -1) {
					positionen.add(lastPos);
					graph.addKnoten();
					erstelleMarkiert();
					update(panel.getGraphics());
				}
			}
		});
	}
	
	
	
	public void _import(Graphics g) throws SQLException {
		JFileChooser open = new JFileChooser();
		open.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("SQL Database (*.db)","db");
		open.addChoosableFileFilter(filter);
		open.setCurrentDirectory(new File("."));
		
		int val = open.showOpenDialog(this);
		
		if(val == JFileChooser.APPROVE_OPTION) {
			File file = open.getSelectedFile();
			String path ="";
			if(!System.getProperty("os.name").startsWith("Windows")) {
				path = file.getParent() +"/"+ file.getName().replaceFirst("[.][^.]+$", "");
			}else {
				path = file.getParent() +"\\"+ file.getName().replaceFirst("[.][^.]+$", "");
			}
			SQL sql = new SQL(path);
			if(!sql.isempty()) {
				//System.out.println("Anzahl: " + sql.getKnotenAnzahl());
				Graph new_graph = new Graph(sql.getKnotenAnzahl()-1);
				ArrayList<Koordinate> new_positionen = new ArrayList<Koordinate>();
				if(sql.getKnotenAnzahl() != 0) {
					ResultSet res = sql.getKnoten();
					while(res.next()) {
						new_positionen.add(new Koordinate(res.getInt("posX"), res.getInt("posY")));
					}
					if(sql.getKantenAnzahl() != 0) {
						ResultSet resu = sql.getKanten();
						System.out.println(resu.toString());
						while(resu.next()) {
							new_graph.addKante(resu.getInt("_from"), resu.getInt("_to"), resu.getInt("_entf"));
						}
					}
				}
				graph = new_graph;
				positionen = new_positionen;
				erstelleMarkiert();
				update(g);
			}
		}
	}
	
	public void erstelleMarkiert() {
		markiert = new int[graph.getKanten().size()][graph.getKanten().size()];
		for(int i = 0; i < graph.getKanten().size(); i++) {
			for(int j = 0; j < graph.getKanten().size(); j++) {
				markiert[i][j] = -1;
			}
		}
	}
	
	public void _export() throws SQLException {
		JFileChooser open = new JFileChooser();
		open.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("SQL Database (*.db)","db");
		open.addChoosableFileFilter(filter);
		open.setCurrentDirectory(new File("."));
		int val = open.showSaveDialog(this);
		if(val == JFileChooser.APPROVE_OPTION) {
			File file = open.getSelectedFile();
			String path ="";
			if(!System.getProperty("os.name").startsWith("Windows")) {
				path = file.getParent() +"/"+ file.getName().replaceFirst("[.][^.]+$", "");
			}else {
				path = file.getParent() +"\\"+ file.getName().replaceFirst("[.][^.]+$", "");
			}
			SQL saveSQL = new SQL(path);
			if(!saveSQL.drop()){
				saveSQL.create_new();
			}
			for(int i = 0;i<positionen.size();i++){
				saveSQL.insert_knoten(i, positionen.get(i).getX(), positionen.get(i).getY());
			}
			for(int i = 0;i<graph.getKanten().size();i++){
				for(int j = 0; j<graph.getKanten().get(i).size();j++) {
					saveSQL.insert_kante(i, (int) graph.getKanten().get(i).get(j).getKnoten(), (int) graph.getKanten().get(i).get(j).getWert());
					System.out.println("Von: " +i+ " Zu: "+ (int) graph.getKanten().get(i).get(j).getKnoten() + " Wert: " + (int) graph.getKanten().get(i).get(j).getWert()); 
				}
			}
		}
		System.out.println("Koordinaten:");
		for(int i = 0; i< positionen.size();i++) {
			System.out.println("X: " + positionen.get(i).getX() + " Y:" + positionen.get(i).getY());
		}
	}
	public void update(Graphics g) {
		System.out.println(graph.toString());
		g.clearRect(0, 0, (int) super.getSize().getWidth(), (int) super.getSize().getHeight());
		g.setColor(Color.GRAY);
		g.drawArc(lastPos.getX() - KNOTEN_DURCHMESSER / 2, lastPos.getY() - KNOTEN_DURCHMESSER / 2, KNOTEN_DURCHMESSER, KNOTEN_DURCHMESSER, 0, 360);
		
		g.setColor(Color.BLACK);
		
		for(int i = 1; i < graph.getKanten().size(); i++) {
			if(lastKnoten1 == i)
				g.setColor(Color.RED);
			else if(lastKnoten2 == i)
				g.setColor(Color.MAGENTA);
			g.drawArc(positionen.get(i).getX() - KNOTEN_DURCHMESSER / 2, positionen.get(i).getY() - KNOTEN_DURCHMESSER / 2, KNOTEN_DURCHMESSER, KNOTEN_DURCHMESSER, 0, 360);
			g.drawString(Integer.toString(i), positionen.get(i).getX(), positionen.get(i).getY());
			
			g.setColor(Color.BLACK);
			
			for(int j = 0; j < graph.getKanten().get(i).size(); j++) {
				int index = graph.getKanten().get(i).get(j).getKnoten();
				if(markiert[i][index] >= 0) {
					g.setColor(Color.BLUE);
					if(markiert[i][index] > 0)
						g.drawString(Integer.toString(markiert[i][index]), (int) ((positionen.get(i).getX() + positionen.get(index).getX()) / 2), (int) ((positionen.get(i).getY() + positionen.get(index).getY()) / 2 - 20));
				}
				double angle1 = Math.atan2(positionen.get(index).getY() - positionen.get(i).getY(), positionen.get(index).getX() - positionen.get(i).getX());
				double angle2 = Math.atan2(positionen.get(i).getY() - positionen.get(index).getY(), positionen.get(i).getX() - positionen.get(index).getX());
				Koordinate von = new Koordinate((int) (Math.cos(angle1) * (KNOTEN_DURCHMESSER / 2)) + positionen.get(i).getX(), (int) (Math.sin(angle1) * (KNOTEN_DURCHMESSER / 2)) + positionen.get(i).getY());
				Koordinate zu = new Koordinate((int) (Math.cos(angle2) * (KNOTEN_DURCHMESSER / 2)) + positionen.get(index).getX(), (int) (Math.sin(angle2) * (KNOTEN_DURCHMESSER / 2)) + positionen.get(index).getY());
				g.drawLine(von.getX(), von.getY(), zu.getX(), zu.getY());
				Koordinate dr1 = new Koordinate((int) (Math.cos(angle2 + Math.PI / 8) * (KNOTEN_DURCHMESSER)) + positionen.get(index).getX(), (int) (Math.sin(angle2 + Math.PI / 8) * (KNOTEN_DURCHMESSER)) + positionen.get(index).getY());
				Koordinate dr2 = new Koordinate((int) (Math.cos(angle2 - Math.PI / 8) * (KNOTEN_DURCHMESSER)) + positionen.get(index).getX(), (int) (Math.sin(angle2 - Math.PI / 8) * (KNOTEN_DURCHMESSER)) + positionen.get(index).getY());
				g.fillPolygon(new int[] {zu.getX(),dr1.getX(),dr2.getX()}, new int[] {zu.getY(),dr1.getY(),dr2.getY()}, 3);
				g.setColor(Color.RED);
				g.drawString(Integer.toString(graph.getKanten().get(i).get(j).getWert()), (int) ((positionen.get(i).getX() + positionen.get(index).getX() * 2) / 3), (int) ((positionen.get(i).getY() + positionen.get(index).getY() * 2) / 3));
				g.setColor(Color.BLACK);
			}
		}
	}
	
	public int knotenHit(Koordinate k) {
		for(int i = 1; i < graph.getKanten().size(); i++) {
			if(Math.sqrt(Math.pow(k.getX() - positionen.get(i).getX(), 2) + Math.pow(k.getY() - positionen.get(i).getY(), 2)) < KNOTEN_DURCHMESSER / 2) {
				return i;
			}
		}
		return -1;
	}
}
