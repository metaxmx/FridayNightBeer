
class ConfigureForumsCtrl

    constructor: (@$log, @$scope, @ForumService) ->
        @$log.debug "constructing ForumsCtrl"
        @$scope.categories = []
        @$scope.forumsStatus = new AjaxStatus
        @$scope.startEdit = (entity, prop) => @startEdit(entity, prop)
        @$scope.saveEdit = (entity) => @saveEdit(entity)
        @$scope.cancelEdit = (entity, prop) => @cancelEdit(entity, prop)
        @getAllForums()

    getAllForums: () ->
        @$log.debug "getAllForums()"
        @$scope.forumsStatus.load()
        @ForumService.loadForums()
        .then(
            (data) =>
                @$log.debug "Promise returned #{data.length} Forums/Categories"
                @$scope.categories = data
                @$scope.forumsStatus.succeed()
            ,
            (error) =>
                @$log.error "Unable to get Forums: #{error}"
                @$scope.categories = []
                @$scope.forumsStatus.fail(error)
            )

    startEdit: (entity, prop) ->
        entity.edited=true
        entity[prop + '-edited'] = entity[prop]

    saveEdit: (entity) ->
        entity.edited=false

    cancelEdit: (entity, prop) ->
        entity.edited=false
        entity[prop] = entity[prop + '-edited']

controllersModule.controller('ConfigureForumsCtrl', ConfigureForumsCtrl)