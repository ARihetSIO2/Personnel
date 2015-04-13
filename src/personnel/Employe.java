package personnel;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.regex.Pattern;

import commandLine.Connexion;

/**
 * Employé d'une ligue hébergée par la M2L. Certains peuvent 
 * être administrateurs des employés de leur ligue.
 * Un seul employé, rattaché à aucune ligue, est le root.
 * Il est impossible d'instancier directement un employé, 
 * il faut passer la méthode {@link Ligue#addEmploye addEmploye}.
 */

public class Employe implements Serializable, Comparable<Employe>
{
	private static final long serialVersionUID = 4795721718037994734L;
	private int id_employe;
	private String nom, prenom, password, mail;
	private Ligue ligue;
	private static int id_max=0;
	
	Employe(int id_employe, Ligue ligue, String nom, String prenom, String mail, String password)
	{
		this.id_employe = id_employe;
		if (id_employe > id_max){
	  		  id_max = id_employe;
	  	    }
		this.nom = nom;
		this.prenom = prenom;
		this.password = password;
		this.mail = mail;
		this.ligue = ligue;
	}
	
	public boolean estAdmin(Ligue ligue)
	{
		return ligue.getAdministrateur() == this;
	}
	
	public boolean estRoot()
	{
		return GestionPersonnel.getGestionPersonnel().getRoot() == this;
	}
	
	public String getNom()
	{
		return nom;
	}

	public void setNom(String nom) throws SQLException
	{
		Connexion.modifierEmploye("nom_employe",nom,this.getId_employe());
		this.nom = nom;
	}

	public String getPrenom()
	{
		return prenom;
	}
	
	public void setPrenom(String prenom) throws SQLException
	{
		Connexion.modifierEmploye("prenom_employe",prenom,this.getId_employe());
		this.prenom = prenom;
	}

	public String getMail()
	{
		return mail;
	}
	
	public void setMail(String mail) throws SQLException
	{
		Connexion.modifierEmploye("mail",mail,this.getId_employe());
		this.mail = mail;
	}

	public static boolean verifieMail(String mail){
		boolean res;
		if (Pattern.matches("^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)+$", mail)){
			res = true;
		}
		else{
			res = false;
		}
		return res;
	}
	
	public boolean checkPassword(String password)
	{
		return this.password.equals(password);
	}

	public void setPassword(String password) throws SQLException
	{
		String mdp = getEncodedPassword(password);
		Connexion.modifierEmploye("password",mdp,this.getId_employe());
		this.password= mdp;
	}

	public static String getEncodedPassword(String key) 
    { 
 
     byte[] uniqueKey = key.getBytes(); 
     byte[] hash = null; 
 
     try 
	 { 
        hash = MessageDigest.getInstance("MD5").digest(uniqueKey); //MD2, MD5, SHA-1, SHA-256, SHA-384, SHA-512
 
     } 
     catch (NoSuchAlgorithmException e) { 
        throw new Error("no MD5 support in this VM"); 
     }
     catch (Exception e) {
        e.printStackTrace();
     }
 
     StringBuffer hashString = new StringBuffer(); 
     for ( int i = 0; i < hash.length; ++i ) { 
        String hex = Integer.toHexString(hash[i]); 
        if ( hex.length() == 1 ) { 
         hashString.append('0'); 
         hashString.append(hex.charAt(hex.length()-1)); 
        } else { 
         hashString.append(hex.substring(hex.length()-2)); 
        } 
     } 
     return hashString.toString(); 
    }

	
	public Ligue getLigue()
	{
		return ligue;
	}


	public void remove() throws SQLException
	{
		Employe root = GestionPersonnel.getGestionPersonnel().getRoot();
		if (this != root)
		{
			if (estAdmin(getLigue()))
				getLigue().setAdministrateurBis(root);
			ligue.remove(this);
		}
		else
			throw new ImpossibleDeSupprimerRoot();
	}

	@Override
	public int compareTo(Employe autre)
	{
		int cmp = getNom().compareTo(autre.getNom());
		if (cmp != 0)
			return cmp;
		return getPrenom().compareTo(autre.getPrenom());
	}
	
	@Override
	public String toString()
	{
		String res = nom + " " + prenom + " " + mail + " (";
		if (estRoot())
			res += "super-utilisateur";
		else
			res += ligue.toString();
		return res + ")";
	}

	public int getId_employe() {
		return id_employe;
	}

	public void setId_employe(int id_employe) {
		this.id_employe = id_employe;
	}

	public static int getIdMax() {
		return id_max;
	}
	
	
	public static void setIdMax(int id_max) {
		Employe.id_max = id_max;
	}
}