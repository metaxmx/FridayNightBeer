@* Angular2 Config Template *@
@import services.SettingsDTO
@(settings: SettingsDTO)
(function() {

    this.FnbConfig = this.FnbConfig || {};
    this.FnbConfig.config = {
        siteTitle: "@{settings.siteTitle}",
        registerEnabled: @settings.registerEnabled,
        defaultTheme: "@{settings.defaultTheme}"
    };
    console.log("Configuration loaded");

})(window);