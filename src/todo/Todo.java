package todo;

public class Todo {
	
	//Version 0.1.0
	//seiery "séyéri" 
	//TODO: fleches speciales
	//TODO: fleche vent graphics :
	//TODO: create animations and set correct values in Vent_effect
	//TODO: handle destruction in play()
	//TODO: handle display in ModelPartie 
	
	
	//TODO: bruitage vent 

	//MATERIEL
	//TODO: Foudre : lightning going through enemies  
	//TODO: Electrique: stun enemy
	//TODO: Glace : slow down enemies
	//TODO: Roche: Crée une colonne de roche collidable
	
	//SPIRITUEL
	//TODO: Feu: crée une colonne de feu détruisant certains projectiles 
	//TODO: Ombre: baisse les statistiques 
	//TODO: Vent: projette en l'air
	//TODO: Grappin
	
	//DESTRUCTREUR
	//TODO: chargée: charge x sec, fleche rapide, cause recul
	//TODO: Explosif 
	//TODO: Trou noir
	//TODO: Bogue (mur de fleche)
	
	//RUSE
	//TODO: auto-teleguidée
	//TODO: retard: reste sur place avant de partir et provoquer recul
	//TODO:v-fleche 
	//TODO: cac: tire 3 flèches à portée réduite 
	
	//TODO: quand heros ralenti et saut pied contre un bord: il ne tombe pas 
	//TODO: memory error : for config 200 000 000, working for 3 000 000 000 (java -XX:+PrintFlagsFinal -version | findstr /i "HeapSize PermSize ThreadStackSize") 
	//see java heap space 
	//=> load correctly the world (with smaller size ? )
	//TODO: make a load screen: load level in a new thread, make sure that all musics are loaded too 
	//TODO: Problème si le jeu est ralenti (t=8), spam touche marche => mouvement rapide
	//TODO mettre à jour deplaceEcran editeur par rapport a celui de AbstractModelPartie
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
	//TODO: version multijoueur: gérer les manettes, compatibilité multijoueur des variables, classe joeur 
	//~TODO: permettre la sauvegarde des fichiers par l'utilisateur
	//TODO:  faire ses propres boutons jolis
}
