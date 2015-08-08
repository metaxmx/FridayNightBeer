
class AppCtrl

    constructor: (@$log, @$scope, @AuthenticationService) ->
        [@$scope.authenticated, @$scope.authdata] = [null, false]
        @$scope.refreshAuth = => @refreshNavi()
        @$scope.checkPermission = (permission) => @checkPermission(permission)
        @AuthenticationService.fetchAuthData().then => @refreshNavi()

    refreshNavi: ->
        [@$scope.authenticated, @$scope.authdata] = [@AuthenticationService.authenticated, @AuthenticationService.authdata]

    checkPermission: (permission) ->
        $.inArray(permission, @$scope.authdata?.globalPermissions) > -1

controllersModule.controller('AppCtrl', AppCtrl)