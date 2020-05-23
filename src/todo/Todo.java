package todo;

import javax.vecmath.Vector2d;

import option.Touches;
import partie.collision.GJK_EPA;

public class Todo {
	
	//CURRENLTY
	//Reworking fleche electrique
	//Corrected worldCollision error (GJK and EPA polygon where inverted meaning that the simplex was invalid)
	//Normalized first pos of effect
	//Normalize taille access to take scale into account 
	//Normalized deplace function for all collidable
	//Added test for GJK/EPA
	//Changed Input logic: it now uses event (that can be used to create a pooling as before)
	//Added a "ejectFromCollision" function to allow to eject an object using the shortest path without provinding any direction
	//Made alignMouvement generic
	//Unified "deplace" for all collidable 
	//Corrected getEdge (don't take the one that is closest to the direction, just take the one that intersects with the direction. 
	//Remove scheduleAtFixedRate since it had an extreme catch up, switch back to sleep 
	//Removed collision check when target reached to prevent last minute rotation to htit the ground (due to that arrow targetting the center of the target)
	//A_star: return the best path if no path found, decreased max number of iterations
	//Removed angle from hitox as it was useless and confusing 
	//Corrected the save and loaded from the same location
	//Added control by for spirel
	//Refactored deplace and added scaling parameter
		
	//TODO: delete Touches.inArray
	//TODO: don't use "heros.peutSauter" to check if the heros is grounded (how about double jump?); same for spirel (use was grounded?)
	//TODO: remove mouvement variable (isGlisade)from Heros 
	//TODO: get rid of indices in changeMouvement: only use mouvement type and isEndedOnce => split saut in several anim, same for accroche 
	//TODO: spirel remove all variables like finSaut
	
	//TODO: refactor heros input => use droite_gauche instead of hard coded anim values
	//TODO: create partie.getFlecheEncochee instead of getting it through Heros 
	//TODO: refactor mouvement (split accroche, saut into several mouvement)
	//TODO: refactor remove useless variables in Heros, create function to determine whether the heros "is sliding, is grounded, isBeginSaut, ..."
	
	
	//TODO: normalize overriden deplace (tir spirel)
	//TODO: do not move projeciles for the first frames (until one loop complete?)
	//TODO: check droite_gauche of projectile (inequality is inverted)
	
	//TODO: currently working on monstre => debug life bar (not centered), scale tir (tir scale = base tir scale *= monstre scale)
	//TODO: make everything work with scale : tir,fleche,effects,heros,monstre,bloc 
	//TODO: adjust image drawing based on scaling 
			
	//TODO: FLECHE ELEECTRIQUE: when colliding, set the destroy effect and instantiate two new effect at correct location (use scaling)
	//TODO: set the new effects to smaller size (use scaling in afine transform)
	
	//TODO: handle graphical problems where Frame 50: spirel shoot, projectile hit arrow => arrow & proj destroyed (we never saw the projectile!) 
	//TODO: 1) always display objects for at least 1 frame 
	//TODO: 2) create destroy animation for every objects (so that they don't just disappear)

	//TODO: Fleche_barrage: refactor getShooterTopLeft, use hitbox(?) functions
	//TODO: marque_mortelle: shooting close to screen limite make A_path fails (shoot right-> arrow goes straight to wall at left => not enough blocs considered?)
	//TODO: marque_mortelle: not the shortest path taken? + make the arrow move + then trigger the A_star (force the A path in the direction of the user)
	//TODO: use angle of 1/8 => the target is considered as not reached incorrectly 
	//TODO: showHotkeyWhenPlaying not working (doesn't show at first & doesn't show on selected keys)
	//TODO: correction explosion animation (keep the same enter) 
	
	//TODO: feu effect electrique, explosion, vent, lumiere, absorption : add line of sight: how? => check if center of effect -> heros has collision (top/bottom of heros)// print obstructed
		
	//ANIMATION
	//TODO: flip image horizontally instead of creating new anim g2.drawImage(image, x + width, y, -width, height, null); or negative scale (careful need translation)
	//TODO: rework electrique projectiles
	//TODO: create faucon animation
	//TODO: rework trou_noir animation (make it less invasive)
	//TODO: create leurre animation 
	//TODO: create ninja animation 
	//TODO: create absorption animation (x2)
	//TODO: create animation application to directly draw hitbox. this creates a .anim file that has all usefull information 
	//TODO: create custom shoot animation for hero in air based on its motion (like arrow 3 of thief in gw2)

	//ARROWS
	//TODO: Improve fleche electique: Add new electrique animation + animation of projectile split (make it smaller every time?) and pause before launching projectile so that it is visually more appealing 
	//TODO: Create Faucon fleche
	//TODO: Implement Explosive effect (damage + projection) 
	//TODO: Rework fleche barrage 
	//TODO: Add cripple effect to marque_mortelle (+ new conditon delayed damage?) 
	//TODO: Create leurre fleche 
	//TODO: Create ninja fleche 
	//TODO: Create absorption fleche
	
	//TODO: update balance table (and add a utility score for each effects)
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
	//TODO: Problème si le jeu est ralenti (t=8), spam touche marche => mouvement rapide
	//TODO: fps is broken, if switch to 30, movement is not the same 
	//TODO: warning, arrow speed slow down is broken: can gather arrows 

	//TODO: INTERFACE:
	//TODO: correct camera to show interesting part of the world instead of too much floor/not enough bottom (https://www.gamasutra.com/blogs/JochenHeizmann/20171127/310386/Camera_Logic_in_a_2D_Platformer.php)
		//TODO: focus zone based on the direction of the heros
		//TODO: camera speed based on the speed of the heros
		//TODO: camera favours a bit the up direction 
		//TODO: when reached bottom of level: don't let the camera go down 
	//TODO: create a camera object that has screendisp and INIT rect in it (static) so that it can be accessed from everywhere. Remove partie reference from functions
	//TODO: affichage des niveaux avec preview et difficulté? 
	//TODO: Améliorer slider option (ne se déplace pas à l'endroit où on clique)
	//TODO: create damage numbers when entity is hit 
	
	//TODO: GRAPHISME:
	//TODO: effet ralentissement: taille en fonction de la fenetre
	//TODO: effet slow motion/ bullet time (garder en mémoire les images des mouvements précédants et les afficher avec transparance)
	//TODO: permttre le resize fenetre: utiliser plugin graphique pour interface: windowBuilder ou 
	//TODO: animation de mort 
	//TODO: faire ses propres boutons jolis
	//TODO: Ajouter une animation d'impact lors de collisions de projectiles (enemy/heros)

	//TODO: GAMEPLAY:
	//TODO: shoot special arrow with hotkey (1 per slot) instead of right click
	//TODO: finish implementing all arrows
	//TODO: add specialization (ie: invisible when sliding)
	//TODO: creer des "blocs" event
	//TODO: creer un tutoriel
	//TODO: permettre de replay le niveau: mémoriser les temps et valeurs des "touches down" et "touches released"
	//TODO: improve heros mouvement: default should be running, have increased speed when running longer 

	//TODO: ENNEMY:
	//TODO: Improve spriel AI by making it more configurable (and parametrize to probability to shoot based on the last shoot time, exponential function over time? so that it can shoot twice quickly sometimes)
	//TODO: More precisely, create a State machine for the AI. SO for example if the AI commit not to fall in the hole, it will always avoid to fall
	//TODO: Make it explicit when the spirel is going to shoot 
	//TODO: change spirel so that they can shoot in any direction & the center of the sprite indicates the direction of the shoot 
	
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
	//TODO: IDEA: pre create projectiles + animations (ie: arrow) to avoid massive instantiation in game. Ie: recycle existing arrows (less pressure on garbage collector)
	//TODO: when shooting special arrow for the first time (i.e. roche, marque_mortelle), the game lags 

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
