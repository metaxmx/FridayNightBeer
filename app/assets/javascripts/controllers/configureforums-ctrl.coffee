
class ConfigureForumsCtrl

    constructor: (@$log, @$scope, @ForumService) ->
        @$log.debug "constructing ForumsCtrl"
        @$scope.categories = []
        @$scope.forumsStatus = new AjaxStatus
        @$scope.startEdit = (entity, prop, event) => @startEdit(entity, prop, event)
        @$scope.saveEdit = (entity) => @saveEdit(entity)
        @$scope.cancelEdit = (entity, prop, event) => @cancelEdit(entity, prop, event)
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

    startEdit: (entity, prop, event) ->
        entity.edited=true
        entity[prop + '-edited'] = entity[prop]
        input = $(event.target).closest('.edit-container').find('input')
        window.setTimeout () -> input.focus()

    saveEdit: (entity) ->
        entity.edited=false

    cancelEdit: (entity, prop, event) ->
        entity.edited=false
        entity[prop] = entity[prop + '-edited']
        console.log(event);

controllersModule.controller('ConfigureForumsCtrl', ConfigureForumsCtrl)