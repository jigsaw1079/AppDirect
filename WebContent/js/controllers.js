/**
 * 
 */
angular.module('AppDirect.controllers', []).
service('SubscriptionService',
		function($http, $q){
	return({
		getAllUsers : getAllUsers,
		getAllOrders : getAllOrders
	});
	
	function getAllUsers() {
		var request = $http({
			method : "get",
			url : "/AppDirect/SubscriptionManage",
			params : {
				op : "get_all_users"
			}
		});
		return ( request.then( handleSuccess, handleError ) );
	}
	
	function getAllOrders(uuid) {
		var request = $http({
			method : "get",
			url : "/AppDirect/SubscriptionManage",
			params : {
				op : "get_all_orders",
				uuid : uuid
				//uuid : "dummy-account"
			}
		});
		return ( request.then( handleSuccess, handleError ) );
	}
	
	function handleError( response ) {
		if (
                ! angular.isObject( response.data ) ||
                ! response.data.message
                ) {
                return( $q.reject( "An unknown error occurred." ) );
            }
            // Otherwise, use expected error message.
            return( $q.reject( response.data.message ) );
    }

	function handleSuccess( response ) {
	    return( response.data );
	}
}).
controller('SubscriptionCtrl', function($scope, SubscriptionService){
	$scope.users = [];
	$scope.ordersByUser = [];
	$scope.getAllOrders = function(uuid) {
		SubscriptionService.getAllOrders(uuid).then(function(data){
			$scope.ordersByUser = data;
		});
	}
	SubscriptionService.getAllUsers().then(function(data){
		$scope.users = data;
	});
});