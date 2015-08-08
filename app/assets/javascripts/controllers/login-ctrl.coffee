
class LoginCtrl

    constructor: (@$log, @$location, @$scope, @AuthenticationService) ->
        @$scope.loginForm = {}
        @$scope.login = => @login()
        @$scope.logout = => @logout()
        @$scope.clearMsg = => @clearMsg()
        @$scope.message = null

    login: ->
        @AuthenticationService.login(@$scope.loginForm).then(
            (data) =>
                @$log.debug "Promise returned #{data}"
                if data.authenticated
                    @$scope.message = null
                    @$scope.loginForm = {}
                    @$location.path("/")
                    @$scope.refreshAuth()
                else
                    @$scope.loginForm.password = ""
                    @$scope.message = "Wrong password or username"
            ,
            (error) =>
                @$log.error "Unable to login: #{error}"
                @$scope.message = "Error logging in"
        )

    logout: ->
        @AuthenticationService.logout().then(
            (data) =>
                @$location.path("/")
                @$scope.refreshAuth()
            ,
            (error) =>
                @$log.error "Unable to logout: #{error}"
                @$scope.message = "Error logging out"
        )
    
    clearMsg: ->
        @$scope.message = null

controllersModule.controller('LoginCtrl', LoginCtrl)