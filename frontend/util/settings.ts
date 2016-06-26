import {Injectable} from "@angular/core"

@Injectable()
export class FnbSettings {

    public settingsJson: Object = {}

    public siteTitle: string = "Untitled Site"
    public registerEnabled: boolean = false
    public logo: string = ""

    updateLogo(logo: string): void {
        this.logo = logo
    }

    updateSettings(settingsJson: string): void {
        let json = JSON.parse(settingsJson)
        this.settingsJson = json
        this.siteTitle = json["siteTitle"]
        this.registerEnabled = json["registerEnabled"]
    }

}