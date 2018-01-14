package todo;

public class Todo {
	
	//Version 0.2.3
	//DONE: corrected aim accuracy
	//DONE: better feel for fleche vent + corrected bugs 
	//DONE: correct super slide and heavy gravity when jump on corner of a block
	//DONE: corrected problem condition display 
	//DONE: created a custom image display system that allows for different layers 
	//DONE: maximum arrow distance 
	
	//TODO: Optimization: optimize game when spam arrow (lag) + optimize allocated memory for the program (currently -Xms3000m)

	//MATERIEL
	//TODO: Electrique: Object: create area that paralyse ennemy: can't shoot for a while.
	//TODO: Glace : Object: create a stalactite submitted to gravity                      
	// Roche: Object : create a pillar                                              
	// Feu: Object: create a wave of fire pillar                                 
	
	//SPIRITUEL
	// Lumiere: boost surrounding beings : speed, jump, resistance? x2 
	// Ombre: teleport+ malus
	// Vent: projette en l'air
	// Grappin
	
	//DESTRUCTREUR
	//TODO: Foudre : lightning going through enemies  
	//TODO: Explosif : huge damage (+ projection? ): need time out so that combo with ombre: tp ennemy in explosion?
	//TODO: Trou noir: suck ennemis + damage 
	// Bogue: arrows all around player 
	
	//RUSE
	//TODO: auto-teleguidée: goes to nearest ennemi
	//TODO: retard: stay where shot then move fast + push ennemis 
	//TODO: v-fleche lente //bounce shots // make no gravity area 
	//TODO: cac: tire 3 flèches à portée réduite 
	
	//TODO: load only relevant part of the world 
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
	//TODO: permttre le resize fenetre: utiliser plugin graphique pour interface: windowBuilder ou 
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
