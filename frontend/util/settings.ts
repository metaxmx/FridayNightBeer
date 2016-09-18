import {Injectable} from "@angular/core"

declare module FnbConfig {

    interface FnbConfiguration {
        siteTitle: string
        registerEnabled: boolean
        defaultTheme: string
        logo: string
    }

    var config: FnbConfiguration

}

@Injectable()
export class FnbSettings {

    public siteTitle: string = FnbConfig.config.siteTitle
    public registerEnabled: boolean = FnbConfig.config.registerEnabled
    public logo: string = FnbConfig.config.logo
    public defaultTheme: string = FnbConfig.config.defaultTheme

}