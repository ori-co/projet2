package calculitineraire

import groovy.sql.Sql

class ItineraireController {

	def dataSource
	
    def index= { 
		// Conversion de coordonnées
			/////Début du Script de Conversion Long/Lat => Lambert93.///////////
			
			//Mettre ci-dessous les coordonnées à calculer.Implémentation qui sera ensuite automatique lorsque
			//la correspondance de la view vers le controller sera effective pour recuperer les Lat/Long cliquées par l'utilisateur
			//Pour le moment je prends (-1.5, 47).
			
			def longitude = new Float("-1.5")
			def latitude   = new Float("47.0")
			
			//(C'est un Point au Sud de Nantes dans un champ quadrillé grâce auquel on peut mesurer les incertitudes)
			
			
			//Paramètres et variables de la projection Lambert93 (consultables sur le site de l'IGN ou Geodesie)
					//assert Math.toDegrees(Math.PI) == 180.0
			def pi=new Double(Math.PI)
			def a        = new Float("6378137") //parametre : demi grand axe de l'ellipse
			def e        = new Float("0.08181919106") //parametre : excentricité de l'ellipse considérée
			def fi0 = new Float("46.5f")//pour calculer la latitude d'origine calculée en radian
			def fi1 = new Float("44.f")//pour calculer le 1er parallele automécoïque
			def fi2 = new Float("49.f")//pour calculer le 2eme parallele automécoïque
			def x0    = new Float("699367.5736") //coordonnées à l'origine
			def y0    = new Float("6600000") //coordonnées à l'origine
			 
			//Grandeurs de Normales de la projection
			def gN1    = a/(Math.sqrt(1-e**2*Math.sin(fi1*pi/180)**2))
			def gN2    = a/(Math.sqrt(1-e**2*Math.sin(fi2*pi/180)*Math.sin(fi2*pi/180)))
			
			//Grandeurs de latitude isométrique
			def gl1    = Math.log(Math.tan( pi / 4 + (fi1*pi/360)) * ((1-e * Math.sin(fi1*pi/180))/(1 + e*Math.sin(fi1*pi/180)))**(e/2))
			def gl2    = Math.log( Math.tan( pi / 4 + (fi2*pi/360)) *((1-e * Math.sin(fi2*pi/180) ) / ( 1 + e * Math.sin( fi2*pi/180) ))**(e / 2) )
			def gl0    = Math.log( Math.tan( pi / 4 + (fi0*pi/360)) * ((1-e * Math.sin( fi0*pi/180) ) / ( 1 + e * Math.sin( fi0*pi/180) ))**(e / 2))
			
			//Grandeur de latitude isométrique principale
			def gl    = Math.log( Math.tan( pi / 4 + latitude*pi/360) *(1-e*Math.sin(latitude*pi/180)/(1+e*Math.sin(latitude*pi/180)))**(e/2))
					
			//Exposant de la projection
			def n        = ( Math.log( ( gN2 * Math.cos(fi2*pi/180) ) / ( gN1 * Math.cos( fi1*pi/180)))) / ( gl1 -gl2)
			//Valeur prise ici ("0.7256077650")
			 
			//calcul de la constante de projection
			def c        = (( gN1 * Math.cos( fi1*pi/180 )) /n) * Math.exp( n * gl1)
			//Valeur prise ici ("11754255.426")
			 
			//Calcul de l'ordonnée référentielle en Lambert 93
			//Formule théorique y0 + c * Math.exp( -1 * n *gl0)
			def ys = new Float("12644654.86531")
			
			//Résultat. Les deux coordonnées en Lambert93 sont
			def x    = x0 + c * Math.exp( -1 * n * gl)* Math.sin( n * ( (longitude*pi/180) - pi/60))
			def y    = ys - c * Math.exp( -1 * n * gl)* Math.cos( n * ( (longitude*pi/180) - pi/60))
			

			
			
		//requêtes SQL

			// Créer une table avec les points sélectionnés sur la carte, en coordonnées Lambert 93
			
			def sql = new Sql(dataSource)
			
			//CREATE TABLE points_selectionnes (the_geom GEOMETRY, name VARCHAR(50));
			
			sql.execute '''create table points_selectionnes (the_geom GEOMETRY,name VARCHAR(50))'''
			
			sql.execute  'INSERT INTO points_selectionnes(the_geom, name) VALUES (
				( ST_Transform(ST_GeomFromText('POINT(point_depart_latpoint_depart_long)', 4326), 2154), 'point de depart'),
				( ST_Transform(ST_GeomFromText('POINT(point_arrivee_latpoint_arrivee_long)', 4326), 2154), 'point d arrivee'))'
			
			
			// Il faudra importer point_depart_lat, point_depart_long, point_arrivee_lat  et point_arrivee_long depuis la view
			
			 // 4326 : code EPSG pour latlong
			 // 2154 : code EPSG pour Lambert 93
			
			 // On cherche le noeud du graphe le plus proche du point de départ sélectionné sur la carte
			
			defid_noeud_depart = sql.rows (
			“SELECT id AS id_noeud_depart FROM ROADS_NODES
			WHERE ST_Distance(ST_Transform(ST_GeomFromText('POINT(point_depart_latpoint_depart_long)', 4326), 2154), the_geom) IN
				(SELECT min(ST_Distance( ST_Transform(ST_GeomFromText('POINT(point_depart_latpoint_depart_long)', 4326), 2154), the_geom)) FROM ROADS_NODES);
			“)
			
			
			// On cherche le noeud du graphe le plus proche du point d'arrivée sélectionné sur la carte
			
			defid_noeud_arrivee = sql.rows (
			“SELECT id AS id_noeud_arrivee FROM ROADS_NODES
			WHERE ST_Distance(ST_Transform(ST_GeomFromText('POINT(point_arrivee_latpoint_arrivee_long)', 4326), 2154), the_geom) IN
				(SELECT min(ST_Distance( ST_Transform(ST_GeomFromText('POINT(point_arrivee_latpoint_arrivee_long)', 4326), 2154), the_geom)) FROM ROADS_NODES);
			“)
			
			
			
			// Créer une table pour le noeud de départ et une pour le noeud d'arrivée
			
			//CREATE TABLE startnode AS SELECT * FROM ROADS_NODES where id=id_noeud_depart;
			
			sql.execute '''
create table startnodeAS SELECT * FROM ROADS_NODES where id=id_noeud_depart
 '''
			
			//CREATE TABLE endnode AS SELECT * FROM ROADS_NODES where id=id_noeud_arrivee;
			sql.execute '''
create table endnodeAS SELECT * FROM ROADS_NODES where id=id_noeud_arrivee
 '''
			
			
			
			 // On execute la fonctionST_ShortestPathLength entre ces 2 noeuds
			
			//CREATE TABLE distance AS SELECT * FROM ST_ShortestPathLength('ROADS_EDGES', 'directed - eo', 'w', id_noeud_depart, id_noeud_arrivee);
			
			
			sql.execute '''
create tabledistance AS SELECT * FROM ST_ShortestPathLength('ROADS_EDGES', 'directed - eo', 'w', id_noeud_depart, id_noeud_arrivee) '''
			
			
			
			
		// Exemple de distance			
		def distance=12;
		
		// retour
		[distance:distance]
	}
}
