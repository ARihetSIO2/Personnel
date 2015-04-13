package commandLine;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import personnel.*;
import utilitaires.ligneDeCommande.*;
import static utilitaires.EntreesSorties.*;

public class PersonnelConsole
{
	private GestionPersonnel gestionPersonnel;
	
	public PersonnelConsole(GestionPersonnel gestionPersonnel)
	{
		this.gestionPersonnel = gestionPersonnel;
	}
	
	public void start() throws SQLException
	{
		menuPrincipal().start();
	}
	
	private Menu menuPrincipal() throws SQLException
	{
		Menu menu = new Menu("Gestion du personnel des ligues");
		menu.ajoute(editerEmploye(gestionPersonnel.getRoot()));
		menu.ajoute(menuLigues());
		menu.ajoute(menuQuitter());
		return menu;
	}

	
	private Menu menuLigues() throws SQLException
	{
		Menu menu = new Menu("Gérer les ligues", "l");
		menu.ajoute(afficherLigues());
		menu.ajoute(ajouterLigue());
		menu.ajoute(selectionnerLigue());
		menu.ajoute(supprimerLigue());
		menu.ajouteRevenir("q");
		return menu;
	}

	private Option afficherLigues()
	{
		Option option = new Option("Afficher les ligues", "l");
		option.setAction(new Action()
		{
			@Override
			public void optionSelectionnee()
			{
				System.out.println(gestionPersonnel.getLigues());
			}
		});
		return option;
	}
	
	private Option afficherEmployes(final Ligue ligue)
	{
		Option option = new Option("Afficher les employes", "l");
		option.setAction(new Action()
		{
			@Override
			public void optionSelectionnee()
			{
				System.out.println(ligue.getEmployes());
			}
		});
		return option;
	}
	
	private Option afficher(final Ligue ligue)
	{
		Option option = new Option("Afficher la ligue", "l");
		option.setAction(new Action()
		{
			@Override
			public void optionSelectionnee()
			{
				System.out.println(ligue);
				System.out.println("administrée par " + ligue.getAdministrateur());
			}
		});
		return option;
	}

	private Option afficher(final Employe employe)
	{
		Option option = new Option("Afficher l'employé", "l");
		option.setAction(new Action()
		{
			@Override
			public void optionSelectionnee()
			{
				System.out.println(employe);
			}
		});
		return option;
	}

	private Option ajouterLigue() throws SQLException
	{
		Option option = new Option("Ajouter une ligue", "a");
		option.setAction(new Action()
		{
			@Override
			public void optionSelectionnee()
			{
				try {
					int id_ligue = Ligue.getIdMax()+1;
					Ligue ligue = new Ligue (getString("nom : "),id_ligue);
					String nom =ligue.getNom();
					Connexion.ajouterLigue(nom,id_ligue);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
		return option;
	}
	
	private Option ajouterEmploye(final Ligue ligue)
	{
		Option option = new Option("Ajouter un employé", "a");
		option.setAction(new Action()
		{
			@Override
			public void optionSelectionnee()
			{
				int id_employe = Employe.getIdMax()+1;
				try {
					String mail;
					int i=0;
					do{
						if (i>0){
							System.out.println("Veuillez saisir une adresse mail valide");
						}
						i=i+1;
						mail=getString("mail : ");
					}while(Employe.verifieMail(mail)==false);
					ligue.addEmploye(id_employe,getString("nom : "), 
							getString("prenom : "),mail, 
							Employe.getEncodedPassword(getString("password : ")));
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
		return option;
	}
	
	private Menu editerLigue(Ligue ligue)
	{
		Menu menu = new Menu("Editer " + ligue.getNom());
		menu.ajoute(afficher(ligue));
		menu.ajoute(gererEmployes(ligue));
		menu.ajoute(changerAdministrateur(ligue));
		menu.ajoute(changerNom(ligue));
		menu.ajouteRevenir("q");
		return menu;
	}

	private Menu editerEmploye(Employe employe)
	{
		Menu menu = new Menu("Gérer le compte " + employe.getNom(), "c");
		menu.ajoute(afficher(employe));
		menu.ajoute(changerNom(employe));
		menu.ajoute(changerPrenom(employe));
		menu.ajoute(changerMail(employe));
		menu.ajoute(changerPassword(employe));
		menu.ajouteRevenir("q");
		return menu;
	}

	private Menu gererEmployes(Ligue ligue)
	{
		Menu menu = new Menu("Gérer les employés de " + ligue.getNom(), "e");
		menu.ajoute(afficherEmployes(ligue));
		menu.ajoute(ajouterEmploye(ligue));
		menu.ajoute(modifierEmploye(ligue));
		menu.ajoute(supprimerEmploye(ligue));
		menu.ajouteRevenir("q");
		return menu;
	}

	private Liste<Employe> modifierEmploye(final Ligue ligue)
	{
		return new Liste<>("Modifier un employé", "e", 
				new ActionListe<Employe>()
		{
			@Override
			public List<Employe> getListe()
			{
				return new ArrayList<>(ligue.getEmployes());
			}
			@Override
			public void elementSelectionne(int indice, Employe element)
			{
				editerEmploye(element).start();
			}
		});
	}
	
	private Liste<Employe> supprimerEmploye(final Ligue ligue)
	{
		return new Liste<>("Supprimer un employé", "s", 
				new ActionListe<Employe>()
		{
			@Override
			public List<Employe> getListe()
			{
				return new ArrayList<>(ligue.getEmployes());
			}
			@Override
			public void elementSelectionne(int indice, Employe element)
			{
				try {
					int id_ligue = ligue.getId_ligue();
					Employe admin = ligue.getAdministrateur();
					//Si l'élement supprimé est un admin, nouvel admin de la ligue = root
					if (admin == element){
						Connexion.setAdminLigue(id_ligue,1);
					}
					int id_employe = element.getId_employe(); 
					Connexion.supprimerEmploye(id_employe);
					element.remove();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private Liste<Employe> changerAdministrateur(final Ligue ligue)
	{
		return new Liste<Employe>("Changer d'administrateur", "c", 
				new ActionListe<Employe>()
				{
					@Override
					public List<Employe> getListe()
					{
						return new ArrayList<>(ligue.getEmployes());
					}
					@Override
					public void elementSelectionne(int indice, Employe element)
					{
						try {
							ligue.setAdministrateur(element);
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				});
	}		
	
	private Option changerNom(final Ligue ligue)
	{
		Option option = new Option("Renommer", "r");
		option.setAction(new Action()
		{
			@Override
			public void optionSelectionnee()
			{
				int id_ligue = ligue.getId_ligue();
				try {
					ligue.setNom(id_ligue,getString("Nouveau nom : "));
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
		return option;
	}

	private Liste<Ligue> selectionnerLigue()
	{
		Liste<Ligue> liste = new Liste<Ligue>("Sélectionner une ligue", "e", 
				new ActionListe<Ligue>()
				{
					@Override
					public List<Ligue> getListe()
					{
						return new ArrayList<>(gestionPersonnel.getLigues());
					}
					@Override
					public void elementSelectionne(int indice, Ligue element)
					{
						editerLigue(element).start();
					}
				});
		return liste;
	}
	
	private Liste<Ligue> supprimerLigue()
	{
		Liste<Ligue> liste = new Liste<Ligue>("Supprimer une ligue", "d", 
				new ActionListe<Ligue>()
				{
					@Override
					public List<Ligue> getListe()
					{
						return new ArrayList<>(gestionPersonnel.getLigues());
					}
					@Override
					public void elementSelectionne(int indice, Ligue element)
					{
						int id_ligue = element.getId_ligue();
						try {
							//change l'admin de la ligue a supprimer pour eviter des problemes d'integrité ref
							Connexion.setAdminLigue(id_ligue,1);
							//supprime les employés de la ligue
							Connexion.supprimerEmployesLigue(id_ligue);
							Connexion.supprimerLigue(id_ligue);
						} catch (SQLException e) {
							e.printStackTrace();
						}
						element.remove();
					}
				});
		return liste;
	}
	
	private Option changerNom(final Employe employe)
	{
		Option option = new Option("Changer le nom", "n");
		option.setAction(new Action()
		{
			@Override
			public void optionSelectionnee()
			{
				try {
					employe.setNom(getString("Nouveau nom : "));
				} catch (SQLException e) {
					e.printStackTrace();
				}				
			}
		});
		return option;
	}
	
	private Option changerPrenom(final Employe employe)
	{
		Option option = new Option("Changer le prénom", "p");
		option.setAction(new Action()
		{
			@Override
			public void optionSelectionnee()
			{
				try {
					employe.setPrenom(getString("Nouveau prénom : "));
				} catch (SQLException e) {
					e.printStackTrace();
				}				
			}
		});
		return option;
	}
	
	private Option changerMail(final Employe employe)
	{
		Option option = new Option("Changer le mail", "e");
		option.setAction(new Action()
		{
			@Override
			public void optionSelectionnee()
			{
				String mail;
				int i=0;
				try {
					do{
						if (i>0){
							System.out.println("Veuillez saisir une adresse mail valide");
						}
						i=i+1;
						mail=getString("Nouveau mail : ");
					}while(Employe.verifieMail(mail)==false);
					employe.setMail(mail);
				} catch (SQLException e) {
					e.printStackTrace();
				}				
			}
		});
		return option;
	}
	
	private Option changerPassword(final Employe employe)
	{
		Option option = new Option("Changer le password", "x");
		option.setAction(new Action()
		{
			@Override
			public void optionSelectionnee()
			{
				try {
					employe.setPassword(getString("Nouveau password : "));
				} catch (SQLException e) {
					e.printStackTrace();
				}				
			}
		});
		return option;
	}
	

	private Option menuQuitter()
	{
		return new Option("Quitter", "q", Action.QUITTER);
	}
	
	private boolean verifiePassword()
	{
		boolean ok = gestionPersonnel.getRoot().checkPassword(Employe.getEncodedPassword(getString("password : ")));
		if (!ok)
			System.out.println("Password incorrect.");
		return ok;
	}
	
	public static void main(String[] args) throws SQLException
	{
		new Connexion();
		Connexion.remplirRoot();
		Connexion.remplirLigues();
		PersonnelConsole personnelConsole = 
				new PersonnelConsole(GestionPersonnel.getGestionPersonnel());
		if (personnelConsole.verifiePassword())
			personnelConsole.start();
		
	}
}
