import {Injectable} from "@angular/core"
import {TranslateService} from "ng2-translate";
import * as moment from "moment";

const defaultLanguage = "en";
const additionalLanguages = ["de"];
const languages: string[] = [defaultLanguage].concat(additionalLanguages);

@Injectable()
export class LanguageService {

    public constructor(private translate: TranslateService) {
    }

    private refreshView: () => void;

    public init(refreshView: () => void): void {
        console.debug("-- Registered Languages are: " + languages.join(", "));
        this.translate.setDefaultLang(defaultLanguage);
        this.translate.addLangs(additionalLanguages);

        let initLang = this.translate.getBrowserLang();
        if (languages.indexOf(initLang) === -1) {
            initLang = defaultLanguage;
        }
        this.translate.use(initLang);
        moment.locale(initLang);

        console.debug("-- Using Language " + this.translate.currentLang);
        this.refreshView = refreshView;
    }

    public switchLanguage(lang: string): void {
        if (lang === this.translate.currentLang) {
            return; // No Change
        }
        if (languages.indexOf(lang) === -1) {
            console.warn("Cannot change language to: " + lang + " (language is not registered)");
            return;
        }
        this.translate.use(lang).subscribe(() => {
            console.debug("-- Using Language " + this.translate.currentLang);
            moment.locale(lang);
            this.refreshView();
        })
    }

    public getLocale(): string {
        return this.translate.currentLang
    }

}