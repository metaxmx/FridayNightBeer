
class NewTopicCtrl

    constructor: (@$log, @$scope, @$routeParams, @ForumsService) ->
        @$log.debug "constructing ShowForumCtrl"
        @$scope.newTopic = => @newTopic()
        @$scope.forum = {}
        @$scope.message = null
        @$scope.newTopicData =
            title: "Dummy Title"
            htmlContent: "<p>Foo</p>"
        @getForum()

    getForum: () ->
        @$log.debug "getForum()"
        @ForumsService.initNewTopic(@$routeParams.id)
        .then(
            (data) =>
                @$log.debug "Promise returned Forum"
                @$scope.forum = data
            ,
            (error) =>
                @$log.error "Unable to get Forums: #{error}"
                @$scope.forum = {}
            )

    newTopic: () ->
        @$log.debug "createTopic()"
        @$scope.message = null
        @ForumsService.newTopic(@$routeParams.id, @$scope.newTopicData)
        .then(
            (data) =>
                @$log.debug "Promise returned success"
            ,
            (error) =>
                @$log.error "Unable to create new topic: #{error}"
                @$scope.message = error
            )

controllersModule.controller('NewTopicCtrl', NewTopicCtrl)