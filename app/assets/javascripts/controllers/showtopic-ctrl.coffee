
class ShowTopicCtrl

    constructor: (@$log, @$scope, @$routeParams, @TopicService) ->
        @$log.debug "constructing ShowTopicCtrl"
        @getTopic()

    getTopic: () ->
        @$log.debug "getTopic()"
        @TopicService.loadTopic(@$routeParams.id)
        .then(
            (data) =>
                @$log.debug "Promise returned Topic"
                @$scope.topic = data
            ,
            (error) =>
                @$log.error "Unable to get Topic: #{error}"
                @$scope.topic = {}
            )

controllersModule.controller('ShowTopicCtrl', ShowTopicCtrl)
