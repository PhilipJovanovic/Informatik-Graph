package graphen;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

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
		graph = new Graph();
		positionen = new ArrayList<Koordinate>();
		lastPos = new Koordinate(0, 0);
		markiert = new int[0][0];
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 896, 765);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnDatei = new JMenu("Graph");
		menuBar.add(mnDatei);
		
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton button_add_knoten = new JButton("ADD KNOTEN");
		button_add_knoten.setBounds(666, 10, 214, 23);
		contentPane.add(button_add_knoten);
		
		JComboBox algorithmus = new JComboBox();
		algorithmus.setModel(new DefaultComboBoxModel(new String[] {"Dijkstra", "Tiefensuche", "Breitensuche", "Kruskal", "Hamilton", "Hierholz", "Floyd-Warshall"}));
		algorithmus.setBounds(666, 143, 214, 20);
		contentPane.add(algorithmus);
		
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
		kantenGewicht.setBounds(666, 44, 214, 20);
		contentPane.add(kantenGewicht);
		
		JButton button_add_kante_gerichtet = new JButton("ADD KANTE GERICHTET");
		button_add_kante_gerichtet.setBounds(666, 109, 214, 23);
		contentPane.add(button_add_kante_gerichtet);
		
		JButton btnAddKanteUngerichtet = new JButton("ADD KANTE UNGERICHTET");
		btnAddKanteUngerichtet.setBounds(666, 75, 214, 23);
		contentPane.add(btnAddKanteUngerichtet);
		
		JTextPane textPane = new JTextPane();
		textPane.setBounds(666, 208, 214, 490);
		contentPane.add(textPane);
		
		JButton btnAlgorithmusAusfhren = new JButton("Algorithmus ausf\u00FChren");
		btnAlgorithmusAusfhren.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String t = "";
				erstelleMarkiert();
                switch(algorithmus.getSelectedIndex()) {
                    case 0: t = "Dijkstra";
                        break;
                    case 1: t = "Tiefensuche";
                        break;
                    case 2: t = "Breitensuche";
                        break;
                    case 3:
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
                    		int[] test = graph.hierholzer();
                    		System.out.println(test.toString());
                    		textPane.setText(t);
                        break;
                    case 6: t = "Floyd-Warshall";
                    	String out = new String();
	                    int[][] floyd = graph.floyd_warshall();
	            		for(int i = 0; i < floyd.length; i++) {
	            			for(int j = 0; j < floyd.length; j++) {
	            				out += floyd[i][j] + " ";
	            			}
	            			out += '\n';
	            		}
                    	textPane.setText(out);
                        break;
                }
                System.out.println("Ausgew�hlter Algorithmus: " + t);
			}
		});
		btnAlgorithmusAusfhren.setBounds(666, 174, 214, 23);
		contentPane.add(btnAlgorithmusAusfhren);
		
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
		
		button_add_kante_gerichtet.addActionListener(new ActionListener() {
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
		
		button_add_knoten.addActionListener(new ActionListener() {
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
			}else {
				System.out.println("Empty Database!");
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
			System.out.println("TEst: " + file.getName());
			SQL saveSQL = new SQL(file.getParent() +"\\"+ file.getName().replaceFirst("[.][^.]+$", ""));
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
		
		for(int i = 0; i < graph.getKanten().size(); i++) {
			if(lastKnoten1 == i || lastKnoten2 == i) {
				g.setColor(Color.RED);
			}
			g.drawArc(positionen.get(i).getX() - KNOTEN_DURCHMESSER / 2, positionen.get(i).getY() - KNOTEN_DURCHMESSER / 2, KNOTEN_DURCHMESSER, KNOTEN_DURCHMESSER, 0, 360);
			g.drawString(Integer.toString(i), positionen.get(i).getX(), positionen.get(i).getY());
			if(lastKnoten1 == i || lastKnoten2 == i) {
				g.setColor(Color.BLACK);
			}
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
		for(int i = 0; i < graph.getKanten().size(); i++) {
			if(Math.sqrt(Math.pow(k.getX() - positionen.get(i).getX(), 2) + Math.pow(k.getY() - positionen.get(i).getY(), 2)) < KNOTEN_DURCHMESSER / 2) {
				return i;
			}
		}
		return -1;
	}
}
