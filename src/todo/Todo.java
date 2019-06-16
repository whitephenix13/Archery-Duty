package todo;

public class Todo {
	
	//Version 0.2.4	
	
	//TODO: check that the hitbox is not recomputed if the heros does not move (also watch out when setAnim is called just for testing a collision and then reset)
	//TODO: check that the draw_tr is not recomputed if the projectile does not move 
	//TODO: avoid creating a lot of bloc vide when deserializing a world => handle null in bloc[][] to save some space
	//TODO: Split bloc class used for editeur and bloc used for partie 
	
	//TODO: Improve DebugObjectCreation: narrow the try catch from sampleAllocation to only take into account the object (which seems to be the problem)
	//TODO: Improve DebugObjectCreation: Create efficient filter system

	//TODO: lag on start of game 
	
	//TODO: Optimize garbage collector (when called takes 200ms)=> first call is when world is created (doesn't impact that much the game);
	//TODO: wait 3 sec on the world without doing anything and there will be a call of garbage collector taking 70ms  
	//TODO: check for references that are not released 
	
	//TODO: A_Star optimize so that the game doesn't lag => garbage collector make the game lags when too much arrow teleguide are shot 
	
	//TODO: remove getMaxBoundingSquare in movement (instead the function was moved to Mouvement Perso and Mouvement Tir in a more generic method
	//TODO: check that everything that uses rotateTransform still works (fleche, persoTir, tirSprirrel, effectgrappin)
	//TODO: A_Star: Try with the update path function 
	//TODO: A_Star: Try with target changing position 
	//TODO: A_Star: Update the FindTarget function from Fleche_auto_tel to anticipate the location of the enemy
	//TODO: A_Star: Try with the enemy 
	//TODO: run the algorithm in a thread and start moving the arrow when the algorithm ended the run. Make sure that the update only change the path when it
	//finished recomputing it 
	//TODO: Improve where electrique effect for projectiles are instantiated (they crash to the ground too often)
	//TODO: remove all TODO
	
	//OPTIMIZE: pre create projectiles + animations (ie: arrow) to avoid massive instantiation in game. Ie: recycle existing arrows (less pressure on garbage collector)
	
	//MATERIEL
	// Electrique: Object: create 5 projectiles that give seyeri if they collide with heros, explode x3 if collide with enemy
	// Glace : Object: create a stalactite submitted to gravity                      
	// Roche: Object : create a pillar                                              
	// Feu: Object: create a wave of fire pillar                                 
	
	//SPIRITUEL
	// Lumiere: boost surrounding beings : speed, jump, resistance? x2 
	// Ombre: teleport+ malus
	// Vent: projette en l'air
	// Grappin
	
	//DESTRUCTREUR
	//TODO: Foudre : lightning going through enemies  (effect OK) 
	//TODO: Explosif : huge damage (+ projection? ): need time out so that combo with ombre: tp ennemy in explosion? (effect OK) 
	//TODO: Trou noir: suck ennemis + damage  (effect OK) 
	// Bogue: arrows all around player 
	
	//RUSE
	//TODO: auto-teleguidée: goes to nearest enemy
																							//effect : white trail behind arrrow
	//TODO: retard: stay where shot then move fast + push enemies// go through enemies 
																							//effect : purple trail + effect at impact 
	//TODO: v-fleche=> ninja: 5/10 :  become invisible + regeneration  
																							//effect : dark blue lines shifting left and right 
	//TODO: cac=> sangsue : 20/20 : each projectile hit is absorb, multiplying the arrow damage + releasing seyeri at the end 
																							//effect : red/orange balls floating around 
	
	//TODO: add more sounds for arrows 
	//TODO: load only relevant part of the world 
	//TODO: more optimization: use sprite sheet 
	//TODO: warning, arrow speed slow down is broken: can gather arrows 
	//TODO: optimiser la taille du monde chargé dans partie rapide 
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
	//TODO: editeur: refactor the code + use correct image loader + add option to convert all world to new version 
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
