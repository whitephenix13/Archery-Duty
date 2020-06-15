package todo;

import javax.vecmath.Vector2d;

import option.Touches;
import partie.collision.GJK_EPA;

public class Todo {
	
	//DONE SINCE LAST COMMIT 
	//Corrected feu effect so that each pilar can damage
	//Create effectConsequence to keep track of time on affected entities
	//Changed main loop to better ordrer sequence of actions
	//===================================================================================================
	//CURRENTLY 

	//===================================================================================================
	//ANIMATION
	//TODO: flip image horizontally instead of creating new anim g2.drawImage(image, x + width, y, -width, height, null); or negative scale (careful need translation)
	//TODO: create faucon animation
	//TODO: rework trou_noir animation (make it less invasive)
	//TODO: create leurre animation 
	//TODO: create ninja animation 
	//TODO: create absorption animation (x2)
	//TODO: create animation application to directly draw hitbox. this creates a .anim file that has all usefull information 
	//TODO: create custom shoot animation for hero in air based on its motion (like arrow 3 of thief in gw2)
	//TODO: create destroy animation for each objects 
	
	//ARROWS
	//TODO: Glace: update ground (base on documentation: make it an explosion + reflects + move faster)
	//TODO: Create Faucon fleche
	//TODO: Implement Explosive effect (damage + projection) 
	//TODO: Implement Trou noir effect (damage + pull) 
	//TODO: Rework fleche barrage 
	//TODO: Add cripple effect to marque_mortelle (+ new conditon delayed damage?) 
	//TODO: Create leurre fleche 
	//TODO: Create ninja fleche 
	//TODO: Create absorption fleche	
	//TODO: update values based on balance table (and add a utility score for each effects)
		
	//MATERIEL
	
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
	
	//TODO: BUGS:
	//TODO: Problème si le jeu est ralenti (t=8), spam touche marche => mouvement rapide
	//TODO: fps is broken, if switch to 30, movement is not the same 
	//TODO: warning, arrow speed slow down is broken: can gather arrows 
	
	//TODO: REFACTOR
	//TODO: In function where mouvement is updated (like), dont use getAnim()==1 instead use only use mouvement type and isEndedOnce and split saut/accroche in several anim 
	//TODO; refactor heros input => use droite_gauche instead of hard coded anim values
	//TODO: refactor remove useless variables in Heros, create function to determine whether the heros "is sliding, is grounded, isBeginSaut, ..." and maybe generalize for spirel
	//TODO: Change all varables from ModelPartie to private (since it is now a singleton). Better: create a GameInfo class that has the model partie information (singleton with only getters)
	//TODO: remove all TODO / remove
	
	//TODO: INTERFACE:
	//TODO: add a seyeri info (blue) when seyeri is gained
	//TODO: add an endurance bar for dodge
	//TODO: correct camera to show interesting part of the world instead of too much floor/not enough bottom (https://www.gamasutra.com/blogs/JochenHeizmann/20171127/310386/Camera_Logic_in_a_2D_Platformer.php)
		//TODO: focus zone based on the direction of the heros
		//TODO: camera speed based on the speed of the heros
		//TODO: camera favours a bit the up direction 
		//TODO: when reached bottom of level: don't let the camera go down 
	//TODO: create a camera object that has screendisp and INIT rect in it (static) so that it can be accessed from everywhere. Remove partie reference from functions
	//TODO: affichage des niveaux avec preview et difficulté? 
	//TODO: Améliorer slider option (ne se déplace pas à l'endroit où on clique)
	//TODO: make sure that the enemy that is displayed on top of the screen is the first one to be hit by an arrow
	
	//TODO: GRAPHISME:
	//TODO: effet ralentissement: taille en fonction de la fenetre
	//TODO: effet slow motion/ bullet time (garder en mémoire les images des mouvements précédants et les afficher avec transparance)
	//TODO: permttre le resize fenetre: utiliser plugin graphique pour interface: windowBuilder ou 
	//TODO: animation de mort 
	//TODO: faire ses propres boutons jolis
	//TODO: Ajouter une animation d'impact lors de collisions de projectiles (enemy/heros)
	//TODO: do not move projeciles for the first frames (until one loop complete?)to account for  Frame 50: spirel shoot, projectile hit arrow => arrow & proj destroyed (we never saw the projectile!) 

	//TODO: GAMEPLAY:
	//TODO: add specialization (ie: invisible when sliding)
	//TODO: creer des "blocs" event
	//TODO: creer un tutoriel
	//TODO: permettre de replay le niveau: mémoriser les temps et valeurs des "touches down" et "touches released"
	//TODO: improve heros mouvement: default should be running, have increased speed when running longer 
	//TODO: feu effect electrique, explosion, vent, lumiere, absorption : add line of sight: how? => check if center of effect -> heros has collision (top/bottom of heros)// print obstructed

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
