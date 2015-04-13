package commandLine;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import personnel.GestionPersonnel;
import personnel.Ligue;

import java.sql.Connection;
import java.sql.Statement;

public class Connexion {
	private static Connection conn;
	private static Statement state;
	
	public Connexion(){
		try{
			Class.forName( "com.mysql.jdbc.Driver" );
		    System.out.println("Bienvenue M. Antivirus.");
		    String url = "jdbc:mysql://localhost/gestion_personnel";
		    String user = "root";
		    String passwd = "";
		    conn = (Connection) DriverManager.getConnection(url, user, passwd);
		    System.out.println("Connexion effective !"); 
		}
		catch (Exception e){
		      e.printStackTrace();
		}  
	}
	
	public static void remplirLigues() throws SQLException{
		  ResultSet result = state.executeQuery("SELECT id_ligue,nom_ligue FROM ligue");
		  while(result.next()){
	    	  String nom_ligue = result.getString("nom_ligue");
	    	  int id_L = result.getInt("id_ligue");
	    	  Ligue ligueBD = new Ligue(nom_ligue,id_L);
	    	  GestionPersonnel.getGestionPersonnel().add(ligueBD);
	    	  remplirEmployes(ligueBD,id_L);
	      }
	}
	
	public static void remplirRoot() throws SQLException{
		state = (Statement) conn.createStatement();
		ResultSet resultRoot= state.executeQuery("SELECT id_employe,nom_employe,prenom_employe,mail,password FROM employe WHERE id_employe = 1");
		while(resultRoot.next()){
				int id_employe = resultRoot.getInt("id_employe");
		    	String nom = resultRoot.getString("nom_employe");
		      	String prenom = resultRoot.getString("prenom_employe");;
		    	String mail = resultRoot.getString("mail");
		    	String password = resultRoot.getString("password");
		    	GestionPersonnel.getGestionPersonnel().setRoot(id_employe,nom,prenom,mail,password);
		}
	}
	
	public static void remplirEmployes(Ligue ligue,int idLigue) throws SQLException{
		state = (Statement) conn.createStatement();
		ResultSet resultEmploye= state.executeQuery("SELECT id_employe,nom_employe,prenom_employe,mail,password,id_admin,id_ligue FROM employe WHERE id_ligue = "+idLigue);
		while(resultEmploye.next()){
				int id_employe = resultEmploye.getInt("id_employe");
		    	String nom = resultEmploye.getString("nom_employe");
		      	String prenom = resultEmploye.getString("prenom_employe");;
		    	String mail = resultEmploye.getString("mail");
		    	String password = resultEmploye.getString("password");
		    	int id_admin = resultEmploye.getInt("id_admin");
		    	ligue.addEmployeBis(ligue,id_admin,id_employe,nom, prenom, mail, password);
		}
	}

	
	public static void ajouterLigue(String nom,int id_ligue) throws SQLException{
		state = (Statement) conn.createStatement();
		state.executeUpdate("INSERT INTO ligue (id_ligue,nom_ligue,id_employe_admin) VALUES ("+id_ligue+",'"+nom+"',1)");
	}
	public static void ajouterEmploye(int id_employe,String nom,String prenom,String mail,String password,int id_ligue) throws SQLException{
		state = (Statement) conn.createStatement();
		state.executeUpdate("INSERT INTO employe (id_employe,nom_employe,prenom_employe,mail,password,id_ligue,id_admin) VALUES ("+id_employe+",'"+nom+"','"+prenom+"','"+mail+"','"+password+"',"+id_ligue+",0)");
	}
	
	
	public static void supprimerEmployesLigue(int id_ligue) throws SQLException{
		state = (Statement) conn.createStatement();
		state.executeUpdate("DELETE FROM employe WHERE id_ligue ="+id_ligue);
	}
	public static void supprimerLigue(int id_ligue) throws SQLException{
		state = (Statement) conn.createStatement();
		state.executeUpdate("DELETE FROM ligue WHERE id_ligue ="+id_ligue);
	}
	public static void supprimerEmploye(int id_employe) throws SQLException{
		state = (Statement) conn.createStatement();
		state.executeUpdate("DELETE FROM employe WHERE id_employe ="+id_employe);
	}
	
	
	
	public static void modifierEmploye(String champs, String donnee, int id_employe) throws SQLException{
		state = (Statement) conn.createStatement();
		state.executeUpdate("UPDATE employe set "+champs+"='"+donnee+"' WHERE id_employe="+id_employe);
	}

	
	
	public static void setAdmin(int id_employe,int id_admin) throws SQLException{
		state = (Statement) conn.createStatement();
		state.executeUpdate("UPDATE employe SET id_admin="+id_admin+" WHERE id_employe='"+id_employe+"'");
	}
	public static void setAdminLigue(int id_ligue_admin, int id_employe) throws SQLException {
		state = (Statement) conn.createStatement();
		state.executeUpdate("UPDATE ligue SET id_employe_admin="+id_employe+" WHERE id_ligue="+id_ligue_admin);
	}

	public static void renommerLigue(int id_ligue,String nom) throws SQLException {
		state = (Statement) conn.createStatement();
		state.executeUpdate("UPDATE ligue SET nom_ligue='"+nom+"' WHERE id_ligue="+id_ligue);
	}
	
	
}