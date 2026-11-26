# Lab 1 CI/CD

Foundly è un’applicazione Java 21 distribuita come WAR. La pipeline verifica che una modifica compili, superi i test e produca il pacchetto distribuibile.

## Workflow

`.github/workflows/ci.yml` parte sui push e sulle pull request verso `main` o `master`. Il job usa un runner Ubuntu 24.04 e un token con accesso in sola lettura ai contenuti del repository.

1. Checkout scarica la revisione associata all’evento.
2. Setup Java seleziona Eclipse Temurin 21 e riusa la cache delle dipendenze Maven quando la chiave corrisponde.
3. Il Maven Wrapper fissa Maven a 3.9.6. `./mvnw --batch-mode --no-transfer-progress clean verify` ricostruisce il progetto da zero, esegue la suite con JUnit 6.1.3 e Mockito 5.24.0, genera il report JaCoCo e produce il WAR se la verifica riesce.

## Riproducibilità e qualità

La stessa versione principale di Java e la stessa distribuzione Maven si usano in locale e in CI. Maven dichiara esplicitamente le dipendenze e i plugin principali. La cache riduce i download e la fase `clean` elimina i risultati della build precedente. La suite simula JDBC e il servizio di geocoding, quindi la pipeline può girare senza credenziali Aiven.

La verifica locale del 26 novembre 2026 ha eseguito 177 test con zero fallimenti, errori o test saltati, e ha prodotto il WAR. I report Surefire in `target/surefire-reports` e JaCoCo in `target/site/jacoco` permettono di approfondire gli esiti. Il risultato su un runner GitHub andrà verificato dopo il primo push.

## Confronto con SorrentoMarina

Il workflow di riferimento usa Temurin 21, inizializza SQLite, esegue Maven e aggiunge passaggi per coverage e pubblicazione. Ho riprodotto localmente la sua inizializzazione SQLite e la fase dei test sulla revisione `21b0e18`. Il risultato è 445 test rilevati, 444 passati e 1 saltato. In Foundly il job può eseguire i test unitari con i mock. La consegna del Lab 1 è concentrata sulla build e sulla verifica a ogni modifica. [Workflow di riferimento](https://github.com/dardin88/SorrentoMarina/blob/21b0e18f42da5ef2f9759a07f1d02f8a99970f3c/.github/workflows/build-test.yml)
