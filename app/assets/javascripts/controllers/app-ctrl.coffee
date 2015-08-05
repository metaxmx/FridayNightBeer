
class AppCtrl

    constructor: (@$log, @$scope, @AuthenticationService) ->
        @$log.debug "constructing AppCtrl"
        @$scope.authdata = null
        @$scope.authenticated = false
        @$scope.refreshAuth = => @refreshNavi()
        @$scope.checkPermission = (permission) => @checkPermission(permission)
        @initNavi()

    initNavi: () ->
        @$log.debug "initNavi()"
        @AuthenticationService.updateAuthData => @refreshNavi()

    refreshNavi: () ->
        @$log.debug "refreshNavi()"
        [@$scope.authenticated, @$scope.authdata] = [@AuthenticationService.authenticated, @AuthenticationService.authdata]

    checkPermission: (permission) ->
        $.inArray(permission, @$scope.authdata?.globalPermissions) > -1

controllersModule.controller('AppCtrl', AppCtrl)