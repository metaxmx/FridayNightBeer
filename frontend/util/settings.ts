import {Injectable} from "@angular/core"

@Injectable()
export class FnbSettings {

    public siteTitle: string = FnbConfig.config.siteTitle
    public registerEnabled: boolean = FnbConfig.config.registerEnabled
    public logo: string = FnbConfig.config.logo
    public defaultTheme: string = FnbConfig.config.defaultTheme

}