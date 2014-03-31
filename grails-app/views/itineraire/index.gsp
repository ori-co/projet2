<html>
<head>
	<meta charset="utf-8" />
	<title> Création d'un itinéraire </title>
	<link rel="stylesheet" href="http://cdn.leafletjs.com/leaflet-0.7.2/leaflet.css" />
	
</head>

<body>
		 <h1>Selectionner les points de depart et d'arrivée sur la carte</h1>

		<div id="map" style="height: 500px; width : 800px" ></div>
		<script src="http://cdn.leafletjs.com/leaflet-0.7.2/leaflet.js"></script>
		<script>
			// Initialiser la carte sur les coordonnées géographiques de Nantes
			var map = L.map('map').setView([47.213, -1.554], 16);

			// Ajouter une couche OSM sur la carte qu'on a initialisée
			L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
				//maxZoom: 18,
				// attribution  : 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a>'
			}).addTo(map);
			
			// Définition des markers 
				var Drapeaux = L.Icon.extend({
				options : {
					iconSize: [38, 38],
					iconAnchor: [1, 37],
					popupAnchor: [19, -24]
					}
				});

						// problème avec les url des images ?
				var depart = new Drapeaux({ iconUrl: '/Images/Drapeau-vert.png'}),
					arrivee = new Drapeaux({ iconUrl: '/Images/Drapeau-rouge.png'});
		
				L.icon = function (options) {
					return new L.Icon(options);
				};
				
			// Définition des fonctions associées aux clics
			var onMapClick1 = function(e) {			
				// Définir des nouvelles coordonnées pour le marqueur de point de départ
				point_depart = e.latlng;
				marker1 = L.marker(point_depart,{icon: depart});
				map.addLayer(marker1);
				marker1.bindPopup("Point de départ");
				
				// appeler le deuxième script
				map.removeEventListener('click', onMapClick1, false);
				map.addEventListener('click', onMapClick2, false);
			}

			var onMapClick2 = function(e) {
				// Définir des nouvelles coordonnées pour le marqueur de point d'arrivée
				point_arrivee = e.latlng;
				marker2 = L.marker(point_arrivee,{icon: arrivee});
				map.addLayer(marker2);
				marker2.bindPopup("Point d'arrivée");
				
				// appeler le troisème script
				map.removeEventListener('click', onMapClick2, false);
				map.addEventListener('click', onMapClick3, false);
			}
			
			var onMapClick3 = function(e) {
				// Affichage des coordonnées des points
				alert(' point de départ :'+point_depart+'\n point d\'arrivée :'+point_arrivee+'\n Distance : '+${distance});
				
				// Ré-initialisation de la carte
				map.removeLayer(marker1);
				map.removeLayer(marker2);

				// Rappeler le premier script
				map.removeEventListener('click', onMapClick3, false);
				map.addEventListener('click', onMapClick1, false);
			}
			
			map.addEventListener('click', onMapClick1, false);		
			var marker1;
			var marker2;
			var point_depart;
			var point_arrivee;

			// ajouter les points à la table points_latlng ??

		</script>		

</body>
</html>