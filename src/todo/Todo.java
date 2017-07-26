package todo;

public class Todo {
	
	//Version 0.2.2

	//TODO: change effect so that their application area is related to the visual? (vent)
	
	//TODO: load only relevant part of the world 
	//TODO: correct Partie timer when system pause 
	//TODO: CHeck arrows colliding to themselves (the reason why you can't infinite push with wind?)
	//TODO: pre-init grappin to avoid use GC call
	//TODO: set the java parameter for .jar
	//TODO: add switch arrow touch option 
	
		//MATERIEL
	//TODO: Electrique: Object: create area that paralyse ennemy: can't shoot for a while. Ennemi: damage + create a electic ball moving back in the same direction as arrow: destroy all projectiles and disappear when wall it or after time
	//TODO: Glace : Object: create a stalactite submitted to gravity                       Ennemi: damage + slow down
	//TODO: Roche: Object : create a pillar                                                Ennemi: damage + vulnerable
	//TODO: Feu: Object: create a wave of fire pillar                                      Ennemi: damage + burn

	//SPIRITUEL
	//TODO: Lumiere: boost surrounding beings : speed, jump, damage x2 
	//TODO: Ombre: teleport+ malus
	// Vent: projette en l'air
	// Grappin
	
	//DESTRUCTREUR
	//TODO: Foudre : lightning going through enemies  
	//TODO: Explosif : huge damage (+ projection? ): need time out so that combo with ombre: tp ennemy in explosion?
	//TODO: Trou noir: suck ennemis + damage 
	//TODO: Bogue: arrows all around player 
	
	//RUSE
	//TODO: auto-teleguidée: goes to nearest ennemi
	//TODO: retard: stay where shot then move fast + push ennemis 
	//TODO: v-fleche lente 
	//TODO: cac: tire 3 flèches à portée réduite 
	
	//TODO: more optimization: use sprite sheet 
	//TODO: warning, arrow speed slow down is broken: can gather arrows 
	//TODO: optimiser la taille du monde chargé dans partie rapide 
	//TODO: add more sounds for arrows 
	//TODO: JAR: solve the 2 icons opening ? 
	//TODO: JAR: delete previous jar in installation, check the manifest version , https://stackoverflow.com/questions/3493416/how-to-run-a-file-before-setup-with-inno-setup
	//TODO: ajout d'objets comme invicible quand le heros slide 
	//TODO: Problème si le jeu est ralenti (t=8), spam touche marche => mouvement rapide
	//TODO mettre à jour deplaceEcran editeur par rapport a celui de AbstractModelPartie
	//TODO: score quand tue un ennemi + combo + temps de fin de niveau // temps 
	//TODO: effet ralentissement: taille en fonction de la fenetre
	//TODO: effet slow motion (garder en mémoire les images des mouvements précédants et les afficher avec transparance)
	//TODO: permttre le resize fenetre
	//TODO: ajouter des plugins angel chore, ou celesta
	//TODO: creer des "blocs" event
	//TODO: creer un tutoriel
	//TODO: permettre de replay le niveau: mémoriser les temps et valeurs des "touches down" et "touches released"
	//TODO: editeur afficher tous les niveaux a charger
	//TODO: editeur: dans le menu objet, mettre des catégories de niveux
	//TODO: editeur: mettre un rectangle rouge autour de la zone de spawn de monstre, améliorer en bloc spawnable ?? 
	//TODO: dans la scrollbar, mettre des catégories
	//TODO: editeur fonction deplacer ensemble bloc 
	//TODO: affichage des niveaux avec preview et difficulté? 
	//TODO: ameliorer editeur notamment la gestion des monstes: le nombre, effacer intelligement, obtenir des infos sur une case, select all pour delete les monstres ....
	//TODO: musiques, effets speciaux, decor 
	//TODO: animation de deces
	//TODO: créer un fichier user pref/ fichier de config utilisateur (+lecture/ecriture) 
	//TODO: version multijoueur: gérer les manettes, compatibilité multijoueur des variables, classe joueur 
	//TODO: faire ses propres boutons jolis
	//TODO: bullet time ? 
	
	//USEFULL INFOS: 
	//09/07/2017: all Thread : Main, ThreadAffichage, ThreadMusique
	//All loading needed MusicBruitage, Music, ImagesHeros, ImagesEffect, ImagesFleche, ImagesMonstre, ImagesTirMonstre, world
	
}
