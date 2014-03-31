-- Script SQL - H2GIS

CREATE ALIAS IF NOT EXISTS SPATIAL_INIT FOR
    "org.h2gis.h2spatialext.CreateSpatialExtension.initSpatialExtension";
CALL SPATIAL_INIT();

CALL FILE_TABLE('Data/routes.shp', 'ROADS');
SELECT * FROM ROADS;

CALL ST_GRAPH('ROADS', 'the_geom', 0.01, false);

ALTER TABLE ROADS_EDGES ADD COLUMN eo INT; -- eo: edge orientation

UPDATE ROADS_EDGES SET eo=1 WHERE SENS='Direct';
UPDATE ROADS_EDGES SET eo=-1 WHERE SENS='Inverse';
UPDATE ROADS_EDGES SET eo=0 WHERE SENS='Double';
UPDATE ROADS_EDGES SET eo=0 WHERE SENS='NC';

ALTER TABLE ROADS_EDGES ADD COLUMN w DOUBLE;  -- w: weight

UPDATE ROADS_EDGES SET w=ST_Lenght(the_geom);

-- Créer une table avec les points sélectionnés sur la carte, en coordonnées Lambert 93

CREATE TABLE points_selectionnes (the_geom GEOMETRY, name VARCHAR(50));

INSERT INTO points_selectionnes VALUES (

 ( ST_Transform(ST_GeomFromText('POINT(point_depart_lat point_depart_long)', 4326), 2154), 'point de depart'),
 ( ST_Transform(ST_GeomFromText('POINT(point_arrivee_lat point_arrivee_long)', 4326), 2154), 'point d arrivee'));


 -- il faudra importer point_depart_lat, point_depart_long, point_arrivee_lat, et point_arrivee_long depuis la view.

 -- 4326 : code EPSG pour latlong
 -- 2154 : code EPSG pour Lambert 93

 -- On cherche le noeud du graphe le plus proche du point de départ sélectionné sur la carte

SELECT id AS id_noeud_depart FROM ROADS_NODES
WHERE ST_Distance( ST_Transform(ST_GeomFromText('POINT(point_depart_lat point_depart_long)', 4326), 2154), the_geom) IN
(SELECT min(ST_Distance( ST_Transform(ST_GeomFromText('POINT(point_depart_lat point_depart_long)', 4326), 2154), the_geom)) FROM ROADS_NODES);


 -- On cherche le noeud du graphe le plus proche du point d'arrivée sélectionné sur la carte

SELECT id AS id_noeud_arrivee FROM ROADS_NODES
WHERE ST_Distance( ST_Transform(ST_GeomFromText('POINT(point_arrivee_lat point_arrivee_long)', 4326), 2154), the_geom) IN
(SELECT min(ST_Distance( ST_Transform(ST_GeomFromText('POINT(point_arrivee_lat point_arrivee_long)', 4326), 2154), the_geom)) FROM ROADS_NODES);

 -- Créer une table pour le noeud de départ et une pour le noeud d'arrivée

CREATE TABLE startnode AS SELECT * FROM ROADS_NODES where id=id_noeud_depart;

CREATE TABLE endnode AS SELECT * FROM ROADS_NODES where id=id_noeud_arrivee;

 -- On execute la fonction ST_ShortestPathLength entre ces 2 noeuds

CREATE TABLE distance AS SELECT * FROM ST_ShortestPathLength('ROADS_EDGES', 'directed - eo', 'w', id_noeud_depart, id_noeud_arrivee);