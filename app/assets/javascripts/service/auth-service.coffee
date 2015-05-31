
class AuthenticationService

    @headers = {'Accept': 'application/json', 'Content-Type': 'application/json'}
    @defaultConfig = { headers: @headers }

    constructor: (@$log, @$http, @$q) ->
        @$log.info "constructing AuthenticationService"
        @session = window.fnbsession;
        @$log.info "Session is #{@session}"
        @storeAuthdata(null)

    getApiConfig: () ->
        resultConfig =
            headers:
                Accept: 'application/json'
                'Content-Type': 'application/json'
                'x-fnb-session': @session
        resultConfig

    fetchAuthData: () ->
        @$log.info "Fetch auth information"
        deferred = @$q.defer()
        @$http.get('/authentication/info', @getApiConfig())
        .success((data, status, headers) =>
                @$log.info("Successfully got AuthInfo - status #{status}")
                deferred.resolve(data)
            )
        .error((data, status, headers) =>
                @$log.error("Failed to get AuthInfo - status #{status}")
                deferred.reject(data)
            )
        deferred.promise

    updateAuthData: (callback) ->
        @$log.info "Update auth information"
        @fetchAuthData().then(
            (data) =>
                @$log.debug "Promise returned auth data"
                @storeAuthdata(data)
                callback()
            ,
            (error) =>
                @$log.error "Unable to get auth data: #{error}"
                @storeAuthdata(null)
                callback()
            )

    login: (form) ->
        @$log.debug "login #{angular.toJson(form, true)}"
        deferred = @$q.defer()
        @$http.post('/authentication/login', form, @getApiConfig())
        .success((data, status, headers) =>
                @$log.info("Successfully got auth data after login attempt - status #{status}")
                deferred.resolve(data)
            )
        .error((data, status, headers) =>
                @$log.error("Failed to log in - status #{status}")
                deferred.reject(data)
            )
        deferred.promise

    logout: () ->
        @$log.debug "logout"
        deferred = @$q.defer()
        @$http.post('/authentication/logout', @getApiConfig())
        .success((data, status, headers) =>
                @$log.info("Successfully got auth data after logout attempt - status #{status}")
                deferred.resolve(data)
            )
        .error((data, status, headers) =>
                @$log.error("Failed to log out - status #{status}")
                deferred.reject(data)
            )
        deferred.promise

    storeAuthdata: (authdata) ->
        [@authenticated, @authdata] = if authdata? then [authdata.authenticated, authdata] else [false, null]

servicesModule.service('AuthenticationService', AuthenticationService)