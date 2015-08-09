
class ShowTopicCtrl

    constructor: (@$log, @$scope, @$routeParams, @TopicService) ->
        @$scope.topic = {}
        @$scope.topicStatus = new AjaxStatus (data) => @$scope.topic = data
        @$scope.insertPost =
            visible: false
            form: {}
            status: new AjaxStatus (data) => @afterPostInserted(data)
            execute: => @insertPost()
        @$scope.showInsertPost = => @showInsertPost()
        @$scope.hideInsertPost = => @hideInsertPost()
        @$scope.clearInsertPostErrors = => @clearInsertPostErrors()
        @TopicService.loadTopic(@$routeParams.id, @$scope.topicStatus)

    showInsertPost: ->
        @$scope.insertPost.visible = true
        @$scope.insertPost.form = {}
        @$scope.insertPost.status.reset()

    hideInsertPost: ->
        @$scope.insertPost.visible = false

    clearInsertPostErrors: ->
        @$scope.insertPost.status.succeed()

    afterPostInserted: (data) ->
        @$scope.topic = data
        @hideInsertPost()

    insertPost: ->
        @TopicService.insertPost(@$routeParams.id, @$scope.insertPost.form, @$scope.insertPost.status)

controllersModule.controller('ShowTopicCtrl', ShowTopicCtrl)
