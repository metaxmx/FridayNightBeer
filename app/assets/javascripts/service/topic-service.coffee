
class TopicService extends ApiService

    constructor: (@$log, @$http, @$q) ->
        super($log, $http, $q)

    loadTopic: (id, ajaxStatus) ->
        @apiGet('/topic/' + id, ajaxStatus)

    insertPost: (id, form, ajaxStatus) ->
        @apiPost('/topic/' + id, form, ajaxStatus)

servicesModule.service('TopicService', TopicService)
