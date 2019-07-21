package todo;

public class Todo {
		
	// conditions starts blinking even when games ended => corrected 
	// clean todo/remove
	
	//TODO: create a fps conter (for better debugging purposes)
	//TODO: add random start time for spirel so that all spirel don't update at the same time (do that only when optimisation is done)
	
	//TODO: Spirel chang mouv: falling: 3.5339229999999997ms / 24.015653ms
	//TODO: 5: Time: get all collidable effect: 7.418347ms / 12.601457ms (for heros in deplace)
	//TODO: 5: Time: got all collidable effects: 0.860737ms / 18.309613ms (for heros in collision)
	
	//TODO: Huge lag is due to No Gc: "used for CMS to indicate concurrent phases"
	//TODO: when huge lags occur, code cache and eden space increase. => mark the frame of the lag to understand what happens before (is it due to projectiles?)
	//TODO: => it looks like that the first major gc calls slow down the first seconds of the game (since it lasts for 1 second)
	
	//TODO: when shooting roche arrow for the first time => laf og 100 ms in collision of deplace projectile (handle world collision)
	//TODO: => on planted or create roche_effect might be slow the first time 
	
	//TODO: solve lag at start of the game (first few seconds) 
	// => monde bloc(Heros): 11.704056ms / 15.593418ms => because bloc.getHitbox is computed here for the first time 
	// => First changeMouv for spirel -> 0.5ms
	

	
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
	//TODO: Faucon : lightning going through enemies  (effect OK) 
	//TODO: Explosif : huge damage (+ projection? ): need time out so that combo with ombre: tp ennemy in explosion? (effect OK) 
	//TODO: Trou noir: suck ennemis + damage  (effect OK) 
	// Barrage: arrows all around player 
	
	//RUSE
	//TODO:marque_mortelle: goes to nearest enemy and "cripple it"
																							//effect : white trail behind arrrow
	//TODO: leurre: create a clone
																							//effect : purple trail + effect at impact 
	//TODO: v-fleche=> ninja: 5/10 :  become invisible + regeneration  
																							//effect : dark blue lines shifting left and right 
	//TODO: cac=> sangsue : 20/20 : each projectile hit is absorb, multiplying the arrow damage + releasing seyeri at the end 
																							//effect : red/orange balls floating around 
	
	//TODO: BUGS:
	//TODO: Problème si le jeu est ralenti (t=8), spam touche marche => mouvement rapide
	//TODO: warning, arrow speed slow down is broken: can gather arrows 

	//TODO: INTERFACE:
	//TODO: affichage des niveaux avec preview et difficulté? 
	//TODO: laisser l'ecran de chargement jusqu'a ce que l'affichage soit pret lors d'un changement de fenetre (permet d'éviter les flash)
	//TODO: Améliorer slider option (ne se déplace pas à l'endroit où on clique)
	//TODO: Switch game mode to option when partie/pause/option? (allow smooth transitions)
	
	//TODO: GRAPHISME:
	//TODO: effet ralentissement: taille en fonction de la fenetre
	//TODO: effet slow motion/ bullet time (garder en mémoire les images des mouvements précédants et les afficher avec transparance)
	//TODO: permttre le resize fenetre: utiliser plugin graphique pour interface: windowBuilder ou 
	//TODO: animation de mort 
	//TODO: faire ses propres boutons jolis

	//TODO: GAMEPLAY:
	//TODO: shoot special arrow with hotkey (1 per slot) instead of right click
	//TODO: finish implementing all arrows
	//TODO: add specialization (ie: invisible when sliding)
	//TODO: creer des "blocs" event
	//TODO: creer un tutoriel
	//TODO: permettre de replay le niveau: mémoriser les temps et valeurs des "touches down" et "touches released"
	
	//TODO: ENNEMY:
	//TODO: Improve spriel AI by making it more configurable (and parametrize to probability to shoot based on the last shoot time, exponential function over time? so that it can shoot twice quickly sometimes)
	//TODO: Make it explicit when the spirel is going to shoot 
	//TODO: SOUND 
	//TODO: add more sounds for arrows/effects
	//TODO: ajouter des plugins angel chore, ou celesta

	//TODO: EDITEUR
	//TODO: improve how blocs are handled: transform world to a matrix of list of blocs. C
	//Create bloc as big as desired. If bloc falls inside subdivision of matrix, add it to corresponding array list (used for fast access to whic bloc can collide-
	//In order to draw, create a function to get all unique objects: for each bloc and each item in list: if !item.added: {item.added=true; ret_list.add(item)}
	//for each item in ret_list: item.added=false; return ret_list;
	
	//TODO: use affineTransform to make imahe shorter (avoid to create _p images)
	//TODO: create an object editor (panel to the left/right of the screen). Can also indicates the num of objects in editor cell to show all overlappng objects
	//TODO mettre à jour deplaceEcran editeur par rapport a celui de AbstractModelPartie
	//TODO: editeur: Split bloc class used for editeur and bloc used for partie and split TypeBloc (type monstre,...) or rename it (type Object)
	//TODO: editeur: refactor the code + use correct image loader (create loader for image editeur instead of image monde) + add option to convert all world to new version 
	//TODO: editeur afficher tous les niveaux a charger
	//TODO: editeur: dans le menu objet, mettre des catégories de niveux
	//TODO: editeur: mettre un rectangle rouge autour de la zone de spawn de monstre, améliorer en bloc spawnable ?? 
	//TODO: create own image loader
	//TODO: dans la scrollbar, mettre des catégories
	//TODO: editeur fonction deplacer ensemble bloc 
	//TODO: ameliorer editeur notamment la gestion des monstes: le nombre, effacer intelligement, obtenir des infos sur une case, select all pour delete les monstres ....

	//TODO: OPTIMIZATION 
	//TODO: load only relevant part of the world 
	//TODO: more optimization: use sprite sheet 
	
	//TODO: PRODUCTION
	//TODO: JAR: solve the 2 icons opening ? 
	//TODO: JAR: delete previous jar in installation, check the manifest version , https://stackoverflow.com/questions/3493416/how-to-run-a-file-before-setup-with-inno-setup
	//TODO: créer un fichier user pref/ fichier de config utilisateur (+lecture/ecriture) 
	
	//TODO: ONLINE
	//TODO: version multijoueur: gérer les manettes, compatibilité multijoueur des variables, classe joueur 
	
	
	//USEFULL INFOS: 
	//09/07/2017: all Thread : Main, ThreadAffichage, ThreadMusique
	//All loading needed MusicBruitage, Music, ImagesHeros, ImagesEffect, ImagesFleche, ImagesMonstre, ImagesTirMonstre, world
	//Parameters for garabage collector: -XX:+PrintGCDetails -XX:+PrintGCTimeStamps
	//Currently the application starts with 2048mb of memory allocation pool for the JVM 
	//Get swing source code: search for MyComponent-source.html on internet
}
