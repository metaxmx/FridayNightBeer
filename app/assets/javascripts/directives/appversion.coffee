

directivesModule.directive('appVersion', ['version', (version) ->
        (scope, elm, attrs) ->
            elm.text(version)
    ])

directivesModule.directive('siteTitle', ['fnbSettings', (fnbSettings) ->
        (scope, elm, attrs) ->
            elm.text(fnbSettings.siteTitle)
    ])