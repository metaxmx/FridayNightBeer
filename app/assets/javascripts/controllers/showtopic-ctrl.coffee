
class ShowTopicCtrl

    constructor: (@$log, @$scope, @$routeParams, @TopicService) ->
        @$log.debug "constructing ShowTopicCtrl"
        @$scope.topic = {}
        @$scope.topicStatus = new AjaxStatus
        @getTopic()

    getTopic: () ->
        @$log.debug "getTopic()"
        @$scope.topicStatus.load()
        @TopicService.loadTopic(@$routeParams.id)
        .then(
            (data) =>
                @$log.debug "Promise returned Topic"
                @$scope.topic = data
                @$scope.topicStatus.succeed()
            ,
            (error) =>
                @$log.error "Unable to get Topic: #{error}"
                @$scope.topic = {}
                @$scope.topicStatus.fail(error)
            )

controllersModule.controller('ShowTopicCtrl', ShowTopicCtrl)
