package todo;

public class Todo {
		
	// conditions starts blinking even when games ended => corrected 
	// clean todo/remove
	// corrected button slots trigerring when disabled and preventing arrow shooting
	// Removed slot change, binded input directly to special arrows
	// Change game mode when partie => option; option => partie
	// Added 1-> 4 keys to quickly change arrows within a slot 
	// Add randomnest to the start of spirel so that they don't all move at the same time 
	//Change Marque_mortelle so that it targets an enemy close to the mouse click 
	//Improve where electrique effect for projectiles are instantiated (they crash to the ground too often)
	//Improve fleche icon in partie so that they pop in the same order 
	
	//TODO: remove all TODO / remove
		
	//MATERIEL
	//TODO: Electrique; rework animation
	
	//DESTRUCTREUR
	//TODO: Faucon : lightning going through enemies  
	//TODO: Explosif : huge damage (+ projection? ): need time out so that combo with ombre: tp ennemy in explosion? 
	//TODO: Trou noir: suck ennemis + damage , change animation
	//TODO: REWORK  Barrage: arrows all around player 
	
	//RUSE
	//TODO:marque_mortelle: goes to nearest enemy and "cripple it" => add cripple effect 
	//TODO: leurre: create a clone
	//TODO: v-fleche=> ninja: 5/10 :  become invisible + regeneration  
																							//effect : dark blue lines shifting left and right 
	//TODO: absorption : 20/20 : each projectile hit is absorb, multiplying the arrow damage + releasing seyeri at the end 
																							//effect : red/orange balls floating around 
	
	//TODO: BUGS:
	//TODO: Probl�me si le jeu est ralenti (t=8), spam touche marche => mouvement rapide
	//TODO: fps is broken, if switch to 30, movement is not the same 
	//TODO: warning, arrow speed slow down is broken: can gather arrows 

	//TODO: INTERFACE:
	//TODO: correct camera to show interesting part of the world instead of too much floor/not enough bottom (https://www.gamasutra.com/blogs/JochenHeizmann/20171127/310386/Camera_Logic_in_a_2D_Platformer.php)
		//TODO: focus zone based on the direction of the heros
		//TODO: camera speed based on the speed of the heros
		//TODO: camera favours a bit the up direction 
		//TODO: when reached bottom of level: don't let the camera go down 
	
	//TODO: affichage des niveaux avec preview et difficult�? 
	//TODO: laisser l'ecran de chargement jusqu'a ce que l'affichage soit pret lors d'un changement de fenetre (permet d'�viter les flash)
	//TODO: Am�liorer slider option (ne se d�place pas � l'endroit o� on clique)
	//TODO: Switch game mode to option when partie/pause/option? (allow smooth transitions)
	
	//TODO: GRAPHISME:
	//TODO: effet ralentissement: taille en fonction de la fenetre
	//TODO: effet slow motion/ bullet time (garder en m�moire les images des mouvements pr�c�dants et les afficher avec transparance)
	//TODO: permttre le resize fenetre: utiliser plugin graphique pour interface: windowBuilder ou 
	//TODO: animation de mort 
	//TODO: faire ses propres boutons jolis

	//TODO: GAMEPLAY:
	//TODO: shoot special arrow with hotkey (1 per slot) instead of right click
	//TODO: finish implementing all arrows
	//TODO: add specialization (ie: invisible when sliding)
	//TODO: creer des "blocs" event
	//TODO: creer un tutoriel
	//TODO: permettre de replay le niveau: m�moriser les temps et valeurs des "touches down" et "touches released"
	
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
	//TODO mettre � jour deplaceEcran editeur par rapport a celui de AbstractModelPartie
	//TODO: editeur: Split bloc class used for editeur and bloc used for partie and split TypeBloc (type monstre,...) or rename it (type Object)
	//TODO: editeur: refactor the code + use correct image loader (create loader for image editeur instead of image monde) + add option to convert all world to new version 
	//TODO: editeur afficher tous les niveaux a charger
	//TODO: editeur: dans le menu objet, mettre des cat�gories de niveux
	//TODO: editeur: mettre un rectangle rouge autour de la zone de spawn de monstre, am�liorer en bloc spawnable ?? 
	//TODO: create own image loader
	//TODO: dans la scrollbar, mettre des cat�gories
	//TODO: editeur fonction deplacer ensemble bloc 
	//TODO: ameliorer editeur notamment la gestion des monstes: le nombre, effacer intelligement, obtenir des infos sur une case, select all pour delete les monstres ....

	//TODO: OPTIMIZATION 
	//TODO: load only relevant part of the world 
	//TODO: more optimization: use sprite sheet 
	//TODO: IDEA: pre create projectiles + animations (ie: arrow) to avoid massive instantiation in game. Ie: recycle existing arrows (less pressure on garbage collector)

	//TODO: PRODUCTION
	//TODO: JAR: solve the 2 icons opening ? 
	//TODO: JAR: delete previous jar in installation, check the manifest version , https://stackoverflow.com/questions/3493416/how-to-run-a-file-before-setup-with-inno-setup
	//TODO: cr�er un fichier user pref/ fichier de config utilisateur (+lecture/ecriture) 
	
	//TODO: ONLINE
	//TODO: version multijoueur: g�rer les manettes, compatibilit� multijoueur des variables, classe joueur 
	
	
	//USEFULL INFOS: 
	//09/07/2017: all Thread : Main, ThreadAffichage, ThreadMusique
	//All loading needed MusicBruitage, Music, ImagesHeros, ImagesEffect, ImagesFleche, ImagesMonstre, ImagesTirMonstre, world
	//Parameters for garabage collector: -XX:+PrintGCDetails -XX:+PrintGCTimeStamps
	//Currently the application starts with 2048mb of memory allocation pool for the JVM 
	//Get swing source code: search for MyComponent-source.html on internet
}
