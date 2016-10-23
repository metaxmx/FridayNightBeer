import {Injectable} from "@angular/core"

declare module FnbConfig {

    interface FnbConfiguration {
        siteTitle: string
        registerEnabled: boolean
        defaultTheme: string
        logo: string
    }

    var config: FnbConfiguration

    interface FnbEnvironmentConfiguration {
        cordova: boolean
        assetRoot: string
        apiRoot: string
        appResourcesRoot: string
    }

    var environment: FnbEnvironmentConfiguration

}

@Injectable()
export class FnbSettings {

    public siteTitle: string = FnbConfig.config.siteTitle
    public registerEnabled: boolean = FnbConfig.config.registerEnabled
    public logo: string = FnbConfig.config.logo
    public defaultTheme: string = FnbConfig.config.defaultTheme

}

@Injectable()
export class FnbEnvironment {

    public cordova: boolean = FnbConfig.environment.cordova
    public assetRoot: string = FnbConfig.environment.assetRoot
    public apiRoot: string = FnbConfig.environment.apiRoot
    public appResourcesRoot: string = FnbConfig.environment.appResourcesRoot

}