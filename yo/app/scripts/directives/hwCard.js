'use strict';

angular.module('hopsWorksApp').directive('hwCard' , function() {
   return {
    restrict: 'E',
    scope: {
      cardColor: '=',
      cardLabel: '=',
      cardHader:'=',
      cardIndex:'=',
      cardDetails: '='
    },
    templateUrl:'views/card.html'
  };
});


