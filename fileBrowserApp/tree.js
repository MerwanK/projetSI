var MyAppClient = angular.module('MyAppClient', ['ui.tree'])

MyAppClient.controller('TreeController', ['$scope', function($scope) {
  $scope.greeting = 'Hola!';
}]);

