<html> 
<head>
	<meta charset="utf-8" />
	<title> Calcul d'itinéraire </title>
	<link rel="stylesheet" href="http://cdn.leafletjs.com/leaflet-0.7.2/leaflet.css" />
	
</head>

<body style="margin: 0; padding: 0;">
		 
		<div id="map" style="position: absolute; top: 0; bottom: 0; width: 100%;"></div>
		<script src="http://cdn.leafletjs.com/leaflet-0.7.2/leaflet.js"></script>
		<script>
			// Initialiser la carte sur les coordonnées géographiques de Nantes
			var map = L.map('map').setView([47.21647836591961, -1.5535354614257812], 14);

			// Ajouter une couche OSM sur la carte qu'on a initialisée
			L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
				maxZoom: 18,
				attribution  : 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a>'
			}).addTo(map);
			
			// Définition des markers 
			var Drapeaux = L.Icon.extend({
			options : {
				iconSize: [38, 38],
				iconAnchor: [1, 37],
				popupAnchor: [19, -24]
				}
			});

			var depart = new Drapeaux({ iconUrl: '../static/images/Drapeau-vert.png'}),
				arrivee = new Drapeaux({ iconUrl: '../static/images/Drapeau-rouge.png'});
		
			L.icon = function (options) {
				return new L.Icon(options);
			};
				
			// Définition des fonctions associées aux clics
			var onMapClick1 = function(e) {			
				// Définir des nouvelles coordonnées pour le marqueur de point de départ
				marker1 = L.marker(e.latlng, {icon: depart, draggable:true});  
				map.addLayer(marker1);
				marker1.bindPopup("Point de départ");
				point_depart = marker1.getLatLng();
					
				// appeler le deuxième script
				map.removeEventListener('click', onMapClick1, false);
				map.addEventListener('click', onMapClick2, false);

				document.getElementById("dep_lat").setAttribute("value",point_depart.lat);
				document.getElementById("dep_lng").setAttribute("value",point_depart.lng);
			}

			var onMapClick2 = function(e) {
				// Définir des nouvelles coordonnées pour le marqueur de point d'arrivée
				marker2 = L.marker(e.latlng, {icon: arrivee, draggable:true}); 
				map.addLayer(marker2);
				marker2.bindPopup("Point d'arrivée");
				point_arrivee = marker2.getLatLng();
					
				map.removeEventListener('click', onMapClick2, false);
				map.addEventListener('mouseout', miseAJour, false);

				document.getElementById("arr_lat").setAttribute("value",point_arrivee.lat);
				document.getElementById("arr_lng").setAttribute("value",point_arrivee.lng);
			}
				
			var miseAJour = function(){
				point_depart = marker1.getLatLng();
				document.getElementById("dep_lat").setAttribute("value",point_depart.lat);
				document.getElementById("dep_lng").setAttribute("value",point_depart.lng);

				point_arrivee = marker2.getLatLng();
				document.getElementById("arr_lat").setAttribute("value",point_arrivee.lat);
				document.getElementById("arr_lng").setAttribute("value",point_arrivee.lng);
			}				
				
			map.addEventListener('click', onMapClick1, false);		
			var marker1;
			var marker2;
			var point_depart;
			var point_arrivee;

		</script>

		<g:formRemote name="valider_form" url="[controller:'Itineraire', action:'resultat']" >
			<!--Depart : -->
  			<!--Latitude : --> <input id="dep_lat" type="text" name="dep_lat" value=47.23035166509681 /> <!--Longitude : --> <input id="dep_lng" type="hidden" name="dep_lng" value=-1.5997123718261719 />
			<!--Arrivée : -->
  			<!--Latitude : --> <input id="arr_lat" type="hidden" name="arr_lat" value=47.20067703735144 /> <!--Longitude : --> <input id="arr_lng" type="hidden" name="arr_lng" value=-1.5010929107666013 />
			
			<div style="position: absolute; top: 50%; border: 2.5px solid black; right: 3%; background-color: white;">
  			<input type="submit" value="OK" />
  			</div>
  			
  		</g:formRemote>
  		

</body>
</html>