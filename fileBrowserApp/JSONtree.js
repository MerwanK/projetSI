var rawData;					//Données brutes reçues par le backend
var jsonData = [];				//Représentation de l'arbre en JSON
var indexLevel = [1, 1, 1, 1, 1, 1, 1, 1];	//Niveau d'indexage pour la gestion des ID des nodes


/*
 * Envoie de la requête pour la liste des fichiers
 */

function getFiles(){
	var xhr = new XMLHttpRequest();
	xhr.open('GET', "http://localhost:8080/kiwishare/tree", true);
	xhr.send();
	xhr.addEventListener("readystatechange", processRequest, false);
}


/*
 * Handler de la réception de la réponse
 */

function processRequest(e) {
	if (xhr.readyState == 4) && xhr.status == 200) {
		var resp = JSON.parse(xhr.responseText);
		rawData = resp.files;
		for(var currentPath in rawData){
			var parts = currentPath.path.split("/");
			constructComponent(parts,jsonData);
		}
	}
}

/*
 * Construction de l'ID
 */


function constructId(pathParts){
	var idString;
	for(var i = 0; i < pathParts.length; i++){
		idString = idString + String(indexLevel[i]);
	}
	indexLevel[pathParts.length-1] = indexLevel[pathParts.length-1]+1;
	return idString;
}

/*
 * Cherche si une partie du path n'existe pas déjà, si oui renvoie uniquement le path non existant
 * Exemple : "Path = Dossier1/Dossier11/Fichier" et "Tree = Dossier1" => renvoie "Path = Dossier11/Fichier"
 */

function searchExistingPaths(path,jsData,n){
	for(var data in jsData){
		if(data.title = path[n]){
			var ret = searchExistingPath(path,data.nodes,n+1);
			if(ret== null){
				return path.splice(0,n);
			}
		}
	}
	return null;
}

/*
 * Renvoie la dernière node déjà existante dans le tree à laquelle on doit attacher
 * Exemple : "Path = Dossier1/Dossier11/Fichier" et "Tree = Dossier1/" => renvoie la node représentant Dossier1
 */

function getLastNodeOfPath(path,jsData,n){
	for(var data in jsData){
		if(data.title = path[n]){
			var ret = getLastNodeOfPath(path,data.nodes,n+1);
			if(ret== {}){
				return data.nodes;
			}
		}
	}
	return {};
}

/*
 * Ajoute l'arbre à jsonData
 */

function constructComponent(pathParts,jsonData){
	var index = constructId(pathParts);
	var lastNode = getLastNodeOfPath(pathParts,jsonData,0);
	var pathToBuild = searchExistingPath(pathParts, jsonData, 0);
	var tree = constructTreeToAdd(pathToBuild):
	lastNode.nodes.push(tree);
}

/*
 * Génère l'ensemble de l'arbre qu'il faut attacher à la node de jsonData
 */

function constructTreeToAdd(path){
	var nodeList = [];

	for(var i = 0; i < path.length; i++){
		var currentNode = {id : index, title : path[path.length-1-i], nodes : []}
		nodeList.push(currentNode);
	}

	var finalTree = nodeList[0];
	var it = finalTree;
	for(var i = 1; i < nodeList.length; i++){
		it.nodes.push(nodeList[i]);
		it = it.nodes[0];
	}

	return finalTree;
}




