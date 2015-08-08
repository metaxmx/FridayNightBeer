
class ApiService

    @headers = {'Accept': 'application/json', 'Content-Type': 'application/json'}
    @defaultConfig = { headers: @headers }
    @apiBaseUrl = '/api/1'

    constructor: (@$log, @$http, @$q) ->

    __callMethod: (method, apiUrl, data, config = ApiService.defaultConfig) ->
        method = @$http[method]
        if data?
            method.call(this, apiUrl, data, config)
        else
            method.call(this, apiUrl, config)

    apiRequest: (method, url, data, ajaxStatus, config) ->
        apiUrl = ApiService.apiBaseUrl + url
        deferred = @$q.defer()
        ajaxStatus?.load()
        @__callMethod(method, apiUrl, data, config)
        .success((resultData, status, headers) =>
                @$log.info("API Request Successful: #{url} - status #{status}")
                ajaxStatus?.succeed(resultData, status, headers)
                deferred.resolve(resultData)
            )
        .error((error, status, headers) =>
                @$log.error("API Request Failed:  #{url} - status #{status}")
                ajaxStatus?.fail(error, status, headers)
                deferred.reject(error)
            )
        deferred.promise

    @apiRequestWithData: (method) -> (url, data = {}, ajaxStatus, config) ->
        @apiRequest(method, url, data, ajaxStatus, config)

    @apiRequestWithoutData: (method) -> (url, ajaxStatus, config) ->
        @apiRequest(method, url, null, ajaxStatus, config)

    apiGet: @apiRequestWithoutData('get')

    apiHead: @apiRequestWithoutData('head')

    apiDelete: @apiRequestWithoutData('delete')

    apiPost: @apiRequestWithData('post')

    apiPut: @apiRequestWithData('put')

    apiPatch: @apiRequestWithData('patch')

# Expose class
@ApiService = ApiService
