import {TranslateLoader} from "ng2-translate";
import {BehaviorSubject, Observable} from "rxjs";

const i18nDE: any = require("./de.json")
const i18nEN: any = require("./en.json")

export class TranslateBundledLoader implements TranslateLoader {

    private translations: any = {
        'de': i18nDE,
        'en': i18nEN
    }

    public getTranslation(lang: string): Observable<any> {
        console.debug(`>> Requested language: ${lang}`)
        if (this.translations.hasOwnProperty(lang)) {
            let translation: any = this.translations[lang]
            console.log(">> Returned: ", translation);
            return Observable.of(translation);
        }
        throw new Error(`Unknown language: ${lang}`)
    }

}
