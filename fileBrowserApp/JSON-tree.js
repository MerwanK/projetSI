var rawData;
var jsonData = [];
var indexLevel = [1, 1, 1, 1, 1, 1, 1, 1];

var xhr = new XMLHttpRequest();
xhr.open('GET', "http://localhost:8080/kiwishare/tree", true);
xhr.send();
xhr.addEventListener("readystatechange", processRequest, false);

function processRequest(e) {
	if (xhr.readyState == 4) && xhr.status == 200) {
		var resp = JSON.parse(xhr.responseText);
		rawData = resp.files;
		for(var currentPath in rawData){
			var parts = currentPath.path.split("/");
			var nodeToAdd = fillTree(parts,null);
			jsonData.push(nodeToAdd);
		}
	}
}

function fillTree(pathParts,childNode){
	var index = constructId(pathParts);
	if(childNode){
			var newParent = {id : index, title : pathParts[pathParts.length-1], nodes : []};
			newParent.nodes.push(childNode);
			if(pathParts.length > 1){
				fillTree(pathParts.splice(pathParts.length-1), newParent);
			}else{
				return newParent;
			}
		}

	}else{
		var children = {id : index, title : pathParts[pathParts.length-1], nodes : []};
		if(pathParts.length > 1){
			fillTree(pathParts.splice(pathParts.length-1), children);
		}else{
			return children;
		}
	}
}

function constructId(pathParts){
	var idString;
	for(var i = 0; i < pathParts.length; i++){
		idString = idString + String(indexLevel[i]);
	}
	indexLevel[pathParts.length-1] = indexLevel[pathParts.length-1]+1;
	return idString;
}

function searchExistingPaths(path,n,jsData){
	var p = null;
	for(var i = 0; i < jsData.length; i++){
		data = jsData[i];
		if(data.title == path[n]){
			p = searchExistingPaths(path,n+1,jsData[i].nodes);
			if(p == null){
				return jsData[i];
			}
		}
	}
	return null;
}

