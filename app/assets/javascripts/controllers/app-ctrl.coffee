
class AppCtrl

    constructor: (@$log, @$scope, @AuthenticationService) ->
        @$log.debug "constructing AppCtrl"
        @$scope.authdata = null
        @$scope.authenticated = false
        @$scope.refreshAuth = =>
            @refreshNavi()
        @initNavi()

    initNavi: () ->
        @$log.debug "initNavi()"
        @AuthenticationService.updateAuthData => @refreshNavi()

    refreshNavi: () ->
        @$log.debug "refreshNavi()"
        [@$scope.authenticated, @$scope.authdata] = [@AuthenticationService.authenticated, @AuthenticationService.authdata]

controllersModule.controller('AppCtrl', AppCtrl)