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
				def sql = new Sql(dataSource)
				
				// depart_lat, depart_lng vers x_dep, y_dep
				def x_dep = sql.firstRow("SELECT ST_X(ST_Transform(ST_GeomFromText('POINT("+depart_lng+" "+depart_lat+")', 4326), 2154)) AS x_dep").x_dep
				def y_dep = sql.firstRow("SELECT ST_Y(ST_Transform(ST_GeomFromText('POINT("+depart_lng+" "+depart_lat+")', 4326), 2154)) AS y_dep").y_dep
				
				// arrivee_lat, arrivee_lng vers x_arr, y_arr
				def x_arr = sql.firstRow("SELECT ST_X(ST_Transform(ST_GeomFromText('POINT("+arrivee_lng+" "+arrivee_lat+")', 4326), 2154)) AS x_arr").x_arr
				def y_arr = sql.firstRow("SELECT ST_Y(ST_Transform(ST_GeomFromText('POINT("+arrivee_lng+" "+arrivee_lat+")', 4326), 2154)) AS y_arr").y_arr
				
				
		// Requetes SQL
			
				
				//envoi requete SQL pour avoir le noeud le plus proche
				
				def id_noeud_depart = sql.firstRow("SELECT node_id AS id_noeud_depart FROM ROADS_NODES WHERE ST_Distance(ST_GeomFromText('POINT("+x_dep+" "+y_dep+")'), the_geom) IN (SELECT min(ST_Distance(ST_GeomFromText('POINT("+x_dep+" "+y_dep+")'), the_geom)) FROM ROADS_NODES);").id_noeud_depart
				def id_noeud_arrivee = sql.firstRow("SELECT node_id AS id_noeud_arrivee FROM ROADS_NODES WHERE ST_Distance(ST_GeomFromText('POINT("+x_arr+" "+y_arr+")'), the_geom) IN (SELECT min(ST_Distance(ST_GeomFromText('POINT("+x_arr+" "+y_arr+")'), the_geom)) FROM ROADS_NODES);").id_noeud_arrivee
				
						
				// st_shortestpathlength entre id_noeud_depart et id_noeud_arrivee
				def distance = sql.firstRow("SELECT round(distance, 1) as distance FROM ST_ShortestPathLength('ROADS_EDGES', 'directed - eo', 'w', "+id_noeud_depart+", "+id_noeud_arrivee+")").distance
				
				
				// st_shortestpath entre id_noeud_depart et id_noeud_arrivee
				sql.execute 'drop table if exists chemins'
				sql.execute " CREATE TABLE chemins AS SELECT * FROM ST_ShortestPath('ROADS_EDGES', 'directed - eo', 'w', "+id_noeud_depart+", "+id_noeud_arrivee+")"

                def pathEmpty = false
                sql.eachRow("SELECT * FROM chemins") {
                    if (it.the_geom==null) {
                        pathEmpty = true
                    }
                }
                if (pathEmpty) {
                    distance = "Infinity"
                }
				
				
				//Transformation en lat/lng pour les geom correspondant au path_id 1
				sql.execute 'drop table if exists plus_court_chemin'
				sql.execute " CREATE TABLE plus_court_chemin AS (SELECT ST_TRANSFORM(ST_SetSRID(the_geom, 2154), 4326) as arcs_latlng, path_edge_id FROM chemins WHERE path_id=1)"
				
				sql.execute 'drop table if exists geojson'
				sql.execute " CREATE TABLE geojson AS SELECT ST_AsGeoJson(ST_Union(ST_Accum(arcs_latlng))) as arcs_geojson FROM plus_court_chemin"
				
				def trajet = sql.firstRow("SELECT arcs_geojson AS trajet FROM geojson").trajet
				

				
		// Retours
				 // coordonnées points de départ et d'arrivée (en lat/lng et lambert93), et distance minimale
				 [depart_lat:depart_lat,  depart_lng:depart_lng,  arrivee_lat:arrivee_lat,  arrivee_lng:arrivee_lng,  distance:distance, trajet:trajet]
	 }
	
}
