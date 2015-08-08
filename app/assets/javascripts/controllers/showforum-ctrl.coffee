
class ShowForumCtrl

    constructor: (@$log, @$scope, @$routeParams, @ForumService) ->
        @$scope.forum = {}
        @$scope.forumStatus = new AjaxStatus (data) => @$scope.forum = data
        @ForumService.loadForum(@$routeParams.id, @$scope.forumStatus)

controllersModule.controller('ShowForumCtrl', ShowForumCtrl)