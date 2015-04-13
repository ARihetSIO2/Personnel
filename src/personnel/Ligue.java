package personnel;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import commandLine.Connexion;

/**
 * Représente une ligue. Chaque ligue est reliée à une liste
 * d'employés dont un administrateur. Comme il n'est pas possible
 * de créer un employé sans l'affecter à une ligue, le root est 
 * l'administrateur de la ligue jusqu'à ce qu'un administrateur 
 * lui ait été affecté avec la fonction {@link #setAdministrateur}.
 */

public class Ligue implements Serializable, Comparable<Ligue>
{
	private static final long serialVersionUID = 1L;
	private String nom;
	private SortedSet<Employe> employes;
	private Employe administrateur;
	private int id_ligue;
	private static int id_max=0;

	public Ligue(String nom,int id_ligue) throws SQLException
	{
		this.nom = nom;
		this.id_ligue = id_ligue;
		if (id_ligue > id_max){
  		  id_max = id_ligue;
  	    }
		employes = new TreeSet<>();
		administrateur = GestionPersonnel.getGestionPersonnel().getRoot();
		GestionPersonnel.getGestionPersonnel().add(this);
	}

	public String getNom()
	{
		return nom;
	}

	/**
	 * Change le nom.
	 * @param nom le nouveau nom de la ligue.
	 * @throws SQLException 
	 */

	public void setNom(int id_ligue, String nom) throws SQLException
	{
		this.nom = nom;
		Connexion.renommerLigue(id_ligue,nom);
	}

	/**
	 * Retourne l'administrateur de la ligue.
	 * @return l'administrateur de la ligue.
	 */
	
	public Employe getAdministrateur()
	{
		return administrateur;
	}

	/**
	 * Fait de administrateur l'administrateur de la ligue.
	 * Lève DroitsInsuffisants si l'administrateur n'est pas 
	 * un employé de la ligue ou le root. Révoque les droits de l'ancien 
	 * administrateur.
	 * @param administrateur le nouvel administrateur de la ligue.
	 * @throws SQLException 
	 */
	
	public void setAdministrateur(Employe administrateur) throws SQLException
	{
		Employe root = GestionPersonnel.getGestionPersonnel().getRoot();
		Employe old_admin = this.getAdministrateur();
		if (administrateur != root && administrateur.getLigue() != this)
			throw new DroitsInsuffisants();
		this.administrateur = administrateur;
		int id_employe = administrateur.getId_employe();
		Ligue ligueAdmin = administrateur.getLigue();
		int id_ligue_admin = ligueAdmin.getId_ligue();
        //modification de l'id_admin du nouvel admin
		Connexion.setAdmin(id_employe,1);
		//modification de l'id_admin_employe de la ligue concern�e
		Connexion.setAdminLigue(id_ligue_admin,id_employe);
		int id_old_admin = old_admin.getId_employe();
		//Si l'ancien administrateur �tait un employe non root, modification de son id_admin
		if (old_admin != root){
			Connexion.setAdmin(id_old_admin,0);
		}
	}
	
	public void setAdministrateurBis(Employe administrateur) throws SQLException
	{
		this.administrateur = administrateur;
	}

	/**
	 * Retourne les employés de la ligue.
	 * @return les employés de la ligue dans l'ordre alphabétique.
	 */
	
	public SortedSet<Employe> getEmployes()
	{
		return Collections.unmodifiableSortedSet(employes);
	}

	/**
	 * Ajoute un employé dans la ligue. Cette méthode 
	 * est le seul moyen de créer un employé.
	 * @param nom le nom de l'employé.
	 * @param prenom le prénom de l'employé.
	 * @param mail l'adresse mail de l'employé.
	 * @param password le password de l'employé.
	 * @return l'employé créé. 
	 * @throws SQLException 
	 */

	public Employe addEmploye(int id_employe, String nom, String prenom, String mail, String password) throws SQLException
	{
		Employe employe = new Employe(id_employe,this, nom, prenom, mail, password);
		int id_ligue = this.getId_ligue();
		Connexion.ajouterEmploye(id_employe,nom,prenom,mail,password,id_ligue);
		employes.add(employe);
		return employe;
	}
	
	public Employe addEmployeBis(Ligue ligue, int id_admin, int id_employe, String nom, String prenom, String mail, String password) throws SQLException
	{
		Employe employe = new Employe(id_employe,this, nom, prenom, mail, password);
		if (id_admin == 1){
    		ligue.setAdministrateurBis(employe);
    	}
		employes.add(employe);
		return employe;
	}
	
	void remove(Employe employe)
	{
		employes.remove(employe);
	}
	
	/**
	 * Supprime la ligue, entraîne la suppression de tous les employés
	 * de la ligue.
	 */
	
	public void remove()
	{
		GestionPersonnel.getGestionPersonnel().remove(this);
	}
	

	@Override
	public int compareTo(Ligue autre)
	{
		return getNom().compareTo(autre.getNom());
	}
	
	@Override
	public String toString()
	{
		return nom;
	}

	public int getId_ligue() {
		return id_ligue;
	}

	public void setId_ligue(int id_ligue) {
		this.id_ligue = id_ligue;
	}


	public static int getIdMax() {
		return id_max;
	}


	public static void setIdMax(int id_max) {
		Ligue.id_max = id_max;
	}
}
