
class ShowTopicCtrl

    constructor: (@$log, @$scope, @$routeParams, @TopicService) ->
        @$log.debug "constructing ShowTopicCtrl"
        @$scope.topic = {}
        @$scope.topicStatus = new AjaxStatus
        @$scope.insertPost =
            visible: false
            form: {}
            status: new AjaxStatus
            execute: => @insertPost()
        @$scope.showInsertPost = => @showInsertPost()
        @$scope.hideInsertPost = => @hideInsertPost()
        @$scope.clearInsertPostErrors = => @clearInsertPostErrors()
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

    showInsertPost: () ->
        @$scope.insertPost.visible = true
        @$scope.insertPost.form = {}
        @$scope.insertPost.status.succeed()

    hideInsertPost: () ->
        @$scope.insertPost.visible = false

    clearInsertPostErrors: () ->
        @$scope.insertPost.status.succeed()

    insertPost: () ->
        @$log.debug "insertPost()"
        @$scope.insertPost.status.load()
        @TopicService.insertPost(@$routeParams.id, @$scope.insertPost.form)
        .then(
            (data) =>
                @$log.debug "Promise returned Success"
                @$scope.topic = data
                @$scope.insertPost.status.succeed()
                @hideInsertPost()
            ,
            (error) =>
                @$log.error "Unable to execute: #{error}"
                @$scope.insertPost.status.fail(error)
            )

controllersModule.controller('ShowTopicCtrl', ShowTopicCtrl)
