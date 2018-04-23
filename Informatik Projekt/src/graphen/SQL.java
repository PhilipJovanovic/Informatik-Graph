package graphen;
import java.sql.*;

public class SQL {
	private Connection conn = null;
	private Statement SQLite = null;
	
	public SQL(String path) throws SQLException {
		conn = DriverManager.getConnection("jdbc:sqlite:"+path+".db");
		SQLite = conn.createStatement();
	}
	public boolean isempty() throws SQLException {
		try {
			ResultSet res = SQLite.executeQuery("SELECT COUNT(*) FROM knoten");
			return false;
		}catch(Exception e) {
			return true;
		}
	}
	public void create_new() throws SQLException {
		if(isempty()) {
			SQLite.executeUpdate("CREATE TABLE knoten (id INTEGER, posX INTEGER, posY INTEGER)");
			SQLite.executeUpdate("CREATE TABLE kanten (_from INTEGER, _to INTEGER, _entf INTEGER)");
		}else {
			System.out.println("Existiert bereits!");
		}
	}
	public boolean drop() {
		try {
			SQLite.executeUpdate("DELETE FROM knoten");
			SQLite.executeUpdate("DELETE FROM kanten");
			return true;
		}catch(Exception e) {
			return false;
		}
	}
	public boolean insert_knoten(int id, int posX, int posY) {
		try {
			SQLite.executeUpdate("INSERT INTO knoten (id, posX, posY) VALUES (" + id + "," + posX + "," + posY + ")");
			return true;
		}catch(Exception e) {
			return false;
		}
	}
	public boolean insert_kante(int from, int to, int entf) {
		try {
			SQLite.executeUpdate("INSERT INTO kanten (_from, _to, _entf) VALUES (" + from + "," + to + "," + entf + ")");
			return true;
		}catch(Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}
	public boolean exec(String query) {
		try {
			ResultSet res = SQLite.executeQuery(query);
			return true;
		}catch(Exception e) {
			return false;
		}
	}
	public ResultSet execR(String query) {
		try {
			ResultSet res = SQLite.executeQuery(query);
			return res;
		}catch(Exception e) {
			return null;
		}
	}
	public ResultSet getKnoten() throws SQLException {
		if(!isempty()) {
			try {
				ResultSet res = SQLite.executeQuery("SELECT * FROM knoten ORDER BY id");
				return res;
			}catch(Exception e) {
				return null;
			}
		}else {
			return null;
		}
	}
	public ResultSet getKanten() throws SQLException {
		if(!isempty()) {
			try {
				ResultSet res = SQLite.executeQuery("SELECT * FROM kanten");
				return res;
			}catch(Exception e) {
				return null;
			}
		}else {
			return null;
		}
	}
	public int getKnotenAnzahl() throws SQLException {
		int anzahl = 0;
		if(!isempty()) {
			try {
				ResultSet res = SQLite.executeQuery("SELECT COUNT(id) FROM knoten");
				anzahl = res.getInt(1);
				return anzahl;
			}catch(Exception e) {
				return anzahl;
			}
		}else {
			return anzahl;
		}
	}
	public int getKantenAnzahl() throws SQLException {
		int anzahl = 0;
		if(!isempty()) {
			try {
				ResultSet res = SQLite.executeQuery("SELECT COUNT(*) FROM kanten");
				anzahl = res.getInt(1);
				return anzahl;
			}catch(Exception e) {
				return anzahl;
			}
		}else {
			return anzahl;
		}
	}
}
