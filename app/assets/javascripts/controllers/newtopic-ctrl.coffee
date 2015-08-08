
class NewTopicCtrl

    constructor: (@$log, @$scope, @$routeParams, @$location, @ForumService) ->
        @$scope.forum = {}
        @$scope.forumStatus = new AjaxStatus (data) => @$scope.forum = data
        @$scope.insertStatus = new AjaxStatus (data) => @topicInserted(data)
        @$scope.newTopicData =
            title: "Dummy Title"
            htmlContent: "<p>Foo</p>"
        @$scope.newTopic = => @newTopic()
        @ForumService.initNewTopic(@$routeParams.id, @$scope.forumStatus)

    newTopic: ->
        @$scope.message = null
        @ForumService.newTopic(@$routeParams.id, @$scope.newTopicData, @$scope.insertStatus)

    topicInserted: (data) ->
        @$location.path("/topic/" + data.id)

controllersModule.controller('NewTopicCtrl', NewTopicCtrl)