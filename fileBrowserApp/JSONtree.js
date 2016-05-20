var indexLevel = [1, 1, 1, 1, 1, 1, 1, 1];	//Niveau d'indexage pour la gestion des ID des nodes
var rawData;					//Données brutes reçues par le backend
var jsonData = [];				//Représentation de l'arbre en JSON

/*
 * Envoie de la requête pour la liste des fichiers
 */

function getFiles(){
	var xhr = new XMLHttpRequest();
	xhr.open('GET', "http://localhost:8080/kiwishare/tree", true);
	xhr.send();
	xhr.addEventListener("readystatechange", processRequest, false);

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
		return jsonData;
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
	var tempData = jsData;
	for(var i = 0; i < path.length; i++){
		var res = comparePath(path,tempData,i)
		if(res != -1)
			tempData = jsData[res].nodes;
		else
			return path.splice(0,i);
	}
	return path.splice(0,i);
}

function comparePath(path,jsData,n){
	var tempData;
	for(var i = 0; i < data.length; i++){
		tempData = jsData[i];
		if(tempData.title == path[n])
			return i;
	}
	return -1;
}

/*
 * Renvoie la dernière node déjà existante dans le tree à laquelle on doit attacher
 * Exemple : "Path = Dossier1/Dossier11/Fichier" et "Tree = Dossier1/" => renvoie la node représentant Dossier1
 */

function getLastNodeOfPath(path,jsData,n){
	for(var data in jsData){
		if(data.title = path[n]){
			var ret = getLastNodeOfPath(path,data.nodes,n+1);
			if(ret== null){
				return data.nodes;
			}
		}
	}
	return null;
}

/*
 * Ajoute l'arbre à jsonData
 */

function constructComponent(pathParts,jsData){
	var lastNode = getLastNodeOfPath(pathParts,jsData,0);
	var pathToBuild = searchExistingPath(pathParts, jsData, 0);
	var tree = constructTreeToAdd(pathToBuild, pathParts):
	lastNode.nodes.push(tree);
}

/*
 * Génère l'ensemble de l'arbre qu'il faut attacher à la node de jsonData
 */

function constructTreeToAdd(path,completePath){
	var nodeList = [];

	for(var i = 0; i < path.length; i++){
		var index = constructId(completePath.splice(completePath.length-1-i,i));
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















