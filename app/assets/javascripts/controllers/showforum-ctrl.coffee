
class ShowForumCtrl

    constructor: (@$log, @$scope, @$routeParams, @ForumService) ->
        @$log.debug "constructing ShowForumCtrl"
        @$scope.forum = {}
        @$scope.forumStatus = new AjaxStatus
        @getForum()

    getForum: () ->
        @$log.debug "getForum()"
        @$scope.forumStatus.load()
        @ForumService.loadForum(@$routeParams.id)
        .then(
            (data) =>
                @$log.debug "Promise returned Forum"
                @$scope.forum = data
                @$scope.forumStatus.succeed()
            ,
            (error) =>
                @$log.error "Unable to get Forums: #{error}"
                @$scope.forum = {}
                @$scope.forumStatus.fail(error)
            )

controllersModule.controller('ShowForumCtrl', ShowForumCtrl)