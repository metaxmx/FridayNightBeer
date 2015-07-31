
class TopicService

    @headers = {'Accept': 'application/json', 'Content-Type': 'application/json'}
    @defaultConfig = { headers: @headers }

    constructor: (@$log, @$http, @$q) ->
        @$log.info "constructing TopicService"

    loadTopic: (id) ->
        @$log.info "Fetch topic $id"
        deferred = @$q.defer()
        @$http.get('/api/topic/' + id)
        .success((data, status, headers) =>
                @$log.info("Successfully got topic - status #{status}")
                deferred.resolve(data)
            )
        .error((data, status, headers) =>
                @$log.error("Failed to get topic - status #{status}")
                deferred.reject(data)
            )
        deferred.promise
        
    insertPost: (id, form) ->
        @$log.info "Insert new post for topic $id"
        deferred = @$q.defer()
        @$http.post('/api/topic/' + id, form)
        .success((data, status, headers) =>
                @$log.info("Successfully inserted post for topic - status #{status}")
                deferred.resolve(data)
            )
        .error((data, status, headers) =>
                @$log.error("Failed to insert post for topic - status #{status}")
                deferred.reject(data)
            )
        deferred.promise

servicesModule.service('TopicService', TopicService)
