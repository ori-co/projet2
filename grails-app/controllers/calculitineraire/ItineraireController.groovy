package calculitineraire

import groovy.sql.Sql

class ItineraireController {

	def dataSource
	
	def index ={
		
	}
	
	
	def resultat ={
		// Récupération des coordonnées des points
				def depart_lat =params.dep_lat;
				def depart_lng =params.dep_lng;
				def arrivee_lat =params.arr_lat;
				def arrivee_lng =params.arr_lng;
			
		// Conversion des coordonnées des points
				// depart_lat, depart_lng vers x_dep, y_dep
				// arrivee_lat, arrivee_lng vers x_arr, y_arr
				
		// Requetes SQL
				//envoi requete SQL pour avoir le noeud le plus proche
				//def noeudplusproche_dep = sql.rows("SELECT node_id FROM ROADS_NODES WHERE ST_Distance(ST_GeomFromText('POINT("+x_dep+" "+y_dep+")'), the_geom) IN (SELECT min(ST_Distance(ST_GeomFromText('POINT("+x_dep+" "+y_dep+")'), the_geom)) FROM ROADS_NODES);");
				//def noeudplusproche_arr = sql.rows("SELECT node_id FROM ROADS_NODES WHERE ST_Distance(ST_GeomFromText('POINT("+x_arr+" "+y_arr+")'), the_geom) IN (SELECT min(ST_Distance(ST_GeomFromText('POINT("+x_arr+" "+y_arr+")'), the_geom)) FROM ROADS_NODES);");
				
				// st_shortestpathlengh entre noeudplusproche_dep et noeudplusproche_arr				
				
				def distance = 77
		
		// Retours
				 // point de depart et d'arrivée, lat et lng et distance mini
				 [depart_lat:depart_lat,depart_lng:depart_lng,arrivee_lat:arrivee_lat,arrivee_lng:arrivee_lng ,distance:distance]
	 }
	
    def conversion(lat, lng) { 
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

			return [x,y]
	}
}
