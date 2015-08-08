
class ForumService extends ApiService

    constructor: (@$log, @$http, @$q) ->
        super($log, $http, $q)

    loadForums: (ajaxStatus) ->
        @apiGet('/forums/list', ajaxStatus)

    loadConfigureForums: (ajaxStatus) ->
        @apiGet('/forums/configure', ajaxStatus)

    loadForum: (id, ajaxStatus) ->
        @apiGet('/forum/' + id, ajaxStatus)

    initNewTopic: (id, ajaxStatus) ->
        @apiGet('/newtopic/' + id, ajaxStatus)

    newTopic: (id, form, ajaxStatus) ->
        @apiPost('/newtopic/' + id, form, ajaxStatus)

servicesModule.service('ForumService', ForumService)
