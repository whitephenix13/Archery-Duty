package todo;

public class Todo {
	
	//Version 0.0.0
	//TODO: remove the wait for T milli in play. The fps is determined by how frequently you update your display :
	//Define frame= 1/fps and reason about frames (and not time) for drawing/updating 
	//TODO: handle own repaint cf http://www.dreamincode.net/forums/topic/113977-java-game-the-fastest-way-to-repaint-bis/
	//TODO: replace deplaceobject by more fluid one
	//TODO: marcher sur bord de trou => animation chute 
	//TODO: creer/modifier classe animation ou on specifie le temps de changement d'animation(remplacer reaffiche)
	//DONE 
	
	//Version 0.1.0
	//seiery "séyéri" 
	//TODO: Problème si le jeu est ralenti (t=8), spam touche marche => mouvement rapide
	//TODO mettre à jour deplaceEcran editeur par rapport a celui de AbstractModelPartie
	//TODO: when slow down move: slow down time and do not divide position (collision) by factor
	//TODO: eviter de modifier des variables sur le heros pour revenir a des valeurs antérieurs (anim, mouvement... cela créer des problèmes d'affichages)
	//TODO: score quand tue un ennemi + combo + temps de fin de niveau // temps 
	//TODO: effet ralentissement: taille en fonction de la fenetre
	//TODO: effet slow motion (garder en mémoire les images des mouvements précédants et les afficher avec transparance)
	//TODO: permttre le resize fenetre
	//TODO: ajouter des plugins angel chore, ou celesta
	//TODO: créer un Thread a part pour charger les niveaux dès le lancement du jeu
	//TODO: pause sur escape 
	//TODO: optimiser la taille du monde chargé dans partie rapide 
	//TODO: jauge endurence/fatigue //jauge mana pour eviter le spam tir 
	//TODO: creer des "blocs" event
	//TODO: creer un tutoriel
	//TODO: permettre de replay le niveau: mémoriser les temps et valeurs des "touches down" et "touches released"
	//TODO: ralentir la musique lors du slow down 
	//TODO: ajouter directions tirs 
	//TODO: editeur afficher tous les niveaux a charger
	//TODO: editeur: dans le menu objet, mettre des catégories de niveux
	//TODO: editeur: mettre un rectangle rouge autour de la zone de spawn de monstre, améliorer en bloc spawnable ?? 
	//TODO: dans la scrollbar, mettre des catégories
	//TODO: editeur fonction deplacer ensemble bloc 
	//TODO: affichage des niveaux avec preview et difficulté? 
	//TODO: ameliorer editeur notamment la gestion des monstes: le nombre, effacer intelligement, obtenir des infos sur une case, select all pour delete les monstres ....
	//TODO: musiques, effets speciaux, decor 
	//TODO: animation de deces
	//TODO: créer un fichier user pref/ fichier de config utilisateur 
	//TODO: pouvoir s'accrocher en haut d'un mur
	//TODO: fleches speciales
	//TODO: version multijoueur: gérer les manettes, compatibilité multijoueur des variables, classe joeur 
	//~TODO: permettre la sauvegarde des fichiers par l'utilisateur
	//TODO:  faire ses propres boutons jolis
}
