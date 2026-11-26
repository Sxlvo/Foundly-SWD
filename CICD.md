# Lab 1 CI/CD

Il workflow `ci.yml` esegue i test e crea il WAR di Foundly a ogni push o pull request verso `main` o `master`. Usa Java 21 e il Maven Wrapper con Maven 3.9.6. I test usano dipendenze simulate, quindi GitHub Actions può eseguirli senza credenziali Aiven (servizio di hosting gratuito fortunamente trovato).

## Problema incontrato

Al primo avvio Maven Central ha risposto con un errore **HTTP 429** mentre il wrapper scaricava Maven. La pipeline si è fermata prima di avviare i test. Abbiamo rilanciato il job sullo stesso commit (`17c8c6c`). Al secondo tentativo il download è riuscito, **tutti i 177 test sono passati** e il WAR è stato generato. [Log della build riuscita](https://github.com/Sxlvo/Foundly-SWD/actions/runs/45854471636/job/140031308493)

## Confronto con SorrentoMarina

Abbiamo provato anche la fase di test del progetto di riferimento con SQLite. Il risultato è stato di **444 test passati e 1 saltato**. Per Foundly abbiamo mantenuto lo stesso JDK e usato i mock già presenti nella suite per simulare il database. [Workflow di SorrentoMarina](https://github.com/dardin88/SorrentoMarina/blob/21b0e18f42da5ef2f9759a07f1d02f8a99970f3c/.github/workflows/build-test.yml)