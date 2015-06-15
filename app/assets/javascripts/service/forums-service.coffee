
class ForumsService

    @headers = {'Accept': 'application/json', 'Content-Type': 'application/json'}
    @defaultConfig = { headers: @headers }

    constructor: (@$log, @$http, @$q, @AuthenticationService) ->
        @$log.info "constructing ForumsService"

    loadForums: () ->
        @$log.info "Fetch forums"
        deferred = @$q.defer()
        @$http.get('/api/forums/list', @AuthenticationService.getApiConfig())
        .success((data, status, headers) =>
                @$log.info("Successfully got forums - status #{status}")
                deferred.resolve(data)
            )
        .error((data, status, headers) =>
                @$log.error("Failed to get forums - status #{status}")
                deferred.reject(data)
            )
        deferred.promise

servicesModule.service('ForumsService', ForumsService)