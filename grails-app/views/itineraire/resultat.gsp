<html>
<head>
	<title> Test de récupération de données </title>
	<meta charset="utf-8" />
	<link rel="stylesheet" href="http://cdn.leafletjs.com/leaflet-0.7.2/leaflet.css" />
</head>

<body>
 <h1>Résultats</h1>

		<div id="map" style="height: 500px; width : 800px" ></div>
		<script src="http://cdn.leafletjs.com/leaflet-0.7.2/leaflet.js"></script>
		<script>
			// Initialiser la carte sur les coordonnées géographiques de Nantes
			var map = L.map('map');
			map.fitBounds([[${depart_lat},${depart_lng}],[${arrivee_lat},${arrivee_lng}]]);

			// Définition des markers 
			var Drapeaux = L.Icon.extend({
			options : {
				iconSize: [38, 38],
				iconAnchor: [1, 37],
				popupAnchor: [19, -24]
				}
			});

					// problème avec les url des images ?
			var depart = new Drapeaux({ iconUrl: '../static/images/Drapeau-vert.png'}),
				arrivee = new Drapeaux({ iconUrl: '../static/images/Drapeau-rouge.png'});
	
			L.icon = function (options) {
				return new L.Icon(options);
			};
			
			// Ajouter une couche OSM sur la carte qu'on a initialisée
			L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
				//maxZoom: 18,
				// attribution  : 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a>'
			}).addTo(map);

			var marker1 = L.marker([${depart_lat},${depart_lng}], {icon: depart, draggable:true});
			map.addLayer(marker1);
			marker1.bindPopup("Point de départ");			
			
			var marker2 = L.marker([${arrivee_lat},${arrivee_lng}], {icon: arrivee, draggable:true});
			map.addLayer(marker2);
			marker2.bindPopup("Point d'arrivée");
			 
			// marker2.bindPopup("Point d'arrivée <br/> Distance : ${distance}").openPopup();
			
			
			var myStyle = {
			    "color": "blue",
			    "weight": 5,
			    "opacity": 1
			};
			
			var itineraire = L.geoJson(${raw(trajet)}, {style: myStyle}).addTo(map);

			map.fitBounds(itineraire.getBounds());	

			// Définition des fonctions associées aux clics
			var onMapClick1 = function(e) {			
				// Définir des nouvelles coordonnées pour le marqueur de point de départ
				marker1.setLatLng(e.latlng);  
				point_depart = marker1.getLatLng();
				
				// appeler le deuxième script
				map.removeEventListener('click', onMapClick1, false);
				map.addEventListener('click', onMapClick2, false);

				document.getElementById("dep_lat").setAttribute("value",point_depart.lat);
				document.getElementById("dep_lng").setAttribute("value",point_depart.lng);
			}

			var onMapClick2 = function(e) {
				// Définir des nouvelles coordonnées pour le marqueur de point d'arrivée
				marker2.setLatLng(e.latlng);
				point_arrivee = marker2.getLatLng();
				
				map.removeEventListener('click', onMapClick2, false);

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
			map.addEventListener('mouseout', miseAJour, false);	
			var point_depart;
			var point_arrivee;
				
 		 </script>

<p> 
<!--  Point de départ : (${depart_lat},${depart_lng}) <br/>
et <br/>
Point d'arrivée : (${arrivee_lat},${arrivee_lng}) <br/> -->
Distance : ${distance} 
</p>

<g:formRemote name="valider_form" url="[controller:'Itineraire', action:'resultat']">
<!--Depart : -->
  <!--Latitude : --> <input id="dep_lat" type="hidden" name="dep_lat" /> <!--Longitude : --> <input id="dep_lng" type="hidden" name="dep_lng" />
<!--Arrivée : -->
  <!--Latitude : --> <input id="arr_lat" type="hidden" name="arr_lat" /> <!--Longitude : --> <input id="arr_lng" type="hidden" name="arr_lng" />
  <input type="submit" value="OK" />
</g:formRemote>


</body>
</html>
