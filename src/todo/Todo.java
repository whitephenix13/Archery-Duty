package todo;

public class Todo {
	//Version alpha
	
	//En haut d'un bloc: droite gauche rapide pour tomber que d'un pixel = stuck
	//glissade mur avec course vers mur=> quand le heros touche le sol, il court vers le mur. Si saut => stuck
	//course + saut = marche lors de l'atterrissage 
	
	//A AJOUTER: glisser sur le sol avec frottement lors de tir avec vitesse 
	
	//CLEAN: Affichage->Option->Touches
	//CLEAN: Affichage->Option
	//CLEAN : Affichage -> MenuPrincipal
	//CLEAN: Affichage -> Editeur 
	//CLEAN  : Affichage -> PartieRapide
	//EN COURS: Reecriture des fonctions de collision
	//EN COURS: BUG deplacement: saut + course (impossible de courir sur le sol
	//A FAIRE: corriger la collision de ModelPartie
	//A FAIRE supprimer les 4 points de Hitbox et les remplacer par un polygon
	//A FAIRE: Decallage ecran + mise au propre des variables de decallage + remplacer 100 par partie.TAILLE_BLOC
	//A FAIRE: regler les temps (qui sont pass� en nano) : musique
	//A FAIRE: sauvegarde et lecture des fichiers se font pas au meme endroit ? bin VS src	
	//TODO: fonction de collision pour savoir ou il y a eu collision
	/*TODO: transform	bloc, 
	 * 					monstre, 
	 * 					spirel, 
	 * 					heros, 
	 * 					fleche, 
	 * 					tirmonstre, 
	 * 					tirspriel 	to collidable objects 
	 * */
	//TODO: remove angle from IntersecHitbox
	//TODO: mettre toutes les variables dans la fonction init de modelEditeur
	//TODO; utiliser les hash map pour g�rer les touches de option.
	//TODO: test global 
	
	//TODO: empecher l'�diteur en .jar/.exe
	//TODO: changer l'icone du .jar: utiliser JSmooth
	//TODO: Github



	//Version beta 
	//TODO: effet slow motion (garder en m�moire les images des mouvements pr�c�dants et les afficher avec transparance)
	//TODO: permttre le resize fenetre
	//TODO: ajouter des plugins angel chore, ou celesta
	//TODO: cr�er un Thread a part pour charger les niveaux d�s le lancement du jeu
	//TODO: pause sur escape 
	//TODO: optimiser la taille du monde charg� dans partie rapide 
	//TODO: jauge endurence//jauge mana pour eviter le spam tir 
	//TODO: creer des "blocs" event
	//TODO: creer un tutoriel
	//TODO: permettre de replay le niveau: m�moriser les temps et valeurs des "touches down" et "touches released"
	//TODO: ralentir la musique lors du slow down 
	//TODO: ajouter directions tirs 
	//TODO: editeur afficher tous les niveaux a charger
	//TODO: editeur: dans le menu objet, mettre des cat�gories de niveux
	//TODO: editeur: mettre un rectangle rouge autour de la zone de spawn de monstre, am�liorer en bloc spawnable ?? 
	//TODO: dans la scrollbar, mettre des cat�gories
	//TODO: editeur fonction deplacer ensemble bloc 
	//TODO: affichage des niveaux avec preview et difficult�? 
	//TODO: ameliorer editeur notamment la gestion des monstes: le nombre, effacer intelligement, obtenir des infos sur une case, select all pour delete les monstres ....
	//TODO: musiques, effets speciaux, decor 
	//TODO: animation de deces
	//TODO: cr�er un fichier user pref
	//TODO: pouvoir s'accrocher en haut d'un mur
	//TODO: fleches speciales
	//TODO: version multijoueur: g�rer les manettes, compatibilit� multijoueur des variables, classe joeur 
	//~TODO: permettre la sauvegarde des fichiers des fichiers par l'utilisateur
	//TODO:  faire ses propres boutons jolis
}
