
class ForumService

    @headers = {'Accept': 'application/json', 'Content-Type': 'application/json'}
    @defaultConfig = { headers: @headers }

    constructor: (@$log, @$http, @$q) ->
        @$log.info "constructing ForumService"

    loadForums: () ->
        @$log.info "Fetch forums"
        deferred = @$q.defer()
        @$http.get('/api/forums/list')
        .success((data, status, headers) =>
                @$log.info("Successfully got forums - status #{status}")
                deferred.resolve(data)
            )
        .error((data, status, headers) =>
                @$log.error("Failed to get forums - status #{status}")
                deferred.reject(data)
            )
        deferred.promise

    loadForum: (id) ->
        @$log.info "Fetch forum $id"
        deferred = @$q.defer()
        @$http.get('/api/forum/' + id)
        .success((data, status, headers) =>
                @$log.info("Successfully got forum - status #{status}")
                deferred.resolve(data)
            )
        .error((data, status, headers) =>
                @$log.error("Failed to get forum - status #{status}")
                deferred.reject(data)
            )
        deferred.promise

    initNewTopic: (id) ->
        @$log.info "Fetch forum $id"
        deferred = @$q.defer()
        @$http.get('/api/newtopic/' + id)
        .success((data, status, headers) =>
                @$log.info("Successfully got forum - status #{status}")
                deferred.resolve(data)
            )
        .error((data, status, headers) =>
                @$log.error("Failed to get forum - status #{status}")
                deferred.reject(data)
            )
        deferred.promise

    newTopic: (id, form) ->
        @$log.info "Create new topic for forum $id"
        deferred = @$q.defer()
        @$http.post('/api/newtopic/' + id, form)
        .success((data, status, headers) =>
                @$log.info("Successfully created topic - status #{status}")
                deferred.resolve(data)
            )
        .error((data, status, headers) =>
                @$log.error("Failed to create topic - status #{status}")
                deferred.reject(data)
            )
        deferred.promise

servicesModule.service('ForumService', ForumService)
