var KiwiShareClient = angular.module('KiwiShareClient', ['ui.tree'])

KiwiShareClient.controller('TreeController', ['$scope','$http', function($scope,$http) {

      $scope.remove = function (scope) {
        scope.remove();
      };

      $scope.toggle = function (scope) {
        scope.toggle();
      };

      $scope.moveLastToTheBeginning = function () {
        var a = $scope.data.pop();
        $scope.data.splice(0, 0, a);
      };

      $scope.newSubItem = function (scope) {
        var nodeData = scope.$modelValue;
        nodeData.nodes.push({
          id: nodeData.id * 10 + nodeData.nodes.length,
          title: nodeData.title + '.' + (nodeData.nodes.length + 1),
          nodes: []
        });
      };
/*
      $scope.collapseAll = function () {
        $scope.$broadcast('angular-ui-tree:collapse-all');
      };

      $scope.expandAll = function () {
        $scope.$broadcast('angular-ui-tree:expand-all');
      };
*/
      $scope.createFolder = function (){
        var folder = document.getElementById('create').value;
        window.open("http://localhost:8080/kiwishare/mkdir?path="+folder);
      }

       $scope.deleteFiles = function (){
        var delet = document.getElementById('delete').value;
        window.open("http://localhost:8080/kiwishare/rm?path="+delet);
      }

      $scope.driveAdresse = function(){
        window.open("https://accounts.google.com/o/oauth2/v2/auth?response_type=code&scope=https://www.googleapis.com/auth/drive&client_id=462659653340-ckldp4re47tg7slfj8q3tsvc6ur59657.apps.googleusercontent.com&redirect_uri=http://localhost:8080/kiwishare/callbackDrive")
      }

      $scope.shareFilesDrive = function(){
        var partage = document.getElementById('share').value;

//de la merde
       // var res = ""+partage;

        $http({
        method: 'GET',
        url: 'http://localhost:8080/kiwishare/share?path=logo.png'
       }).then(function successCallback(response) {
    // this callback will be called asynchronously
    // when the response is available
  }, function errorCallback(response) {
    // called asynchronously if an error occurs
    // or server returns response with an error status.
  });

        var re = new RegExp("/(https:\/\/db.tt\/)\w*/g");
        var resFinal = re.exec(response);
        window.open(resFinal);
      }

      $scope.shareFilesDropbox = function(){
        var partage = document.getElementById('share').value;
        window.open("http://localhost:8080/kiwishare/share?path="+partage)
      }

      $scope.data = [
      {
        "id": 1,
        "title": "node1",
        "nodes": [
      {
        "id": 11,
        "title": "node1.1",
        "nodes": [
          {
            "id": 111,
            "title": "node1.1.1",
            "nodes": []
          }
        ]
      },
      {
        "id": 12,
        "title": "node1.2",
        "nodes": []
      }
      ]
      },
  {
    "id": 2,
    "title": "node2",
    "nodrop": true,
    "nodes": [
      {
        "id": 21,
        "title": "node2.1",
        "nodes": []
      },
      {
        "id": 22,
        "title": "node2.2",
        "nodes": []
      }
    ]
  },
  {
    "id": 3,
    "title": "node3",
    "nodes": [
      {
        "id": 31,
        "title": "node3.1",
        "nodes": []
      }
    ]
  }
]; 
}]);

