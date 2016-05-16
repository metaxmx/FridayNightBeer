
class AuthenticationService extends ApiService

    constructor: ($log, $http, $q) ->
        super($log, $http, $q)
        @storeAuthdata(null)

    fetchAuthData: ->
       @apiGet('/authentication').then(
           (data) => @storeAuthdata(data),
           (error) => @storeAuthdata(null)
       )

    login: (form) ->
        @apiPost('/authentication', form).then(
            (data) => @storeAuthdata(data),
            (error) => @storeAuthdata(null)
        )

    logout: ->
        @apiDelete('/authentication').then(
            (data) => @storeAuthdata(data),
            (error) => @storeAuthdata(null)
        )

    storeAuthdata: (authdata) ->
        [@authenticated, @authdata] = [authdata?.authenticated, authdata]
        authdata

servicesModule.service('AuthenticationService', AuthenticationService)
