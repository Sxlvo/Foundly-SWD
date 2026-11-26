# Lab 2a · Containerizzazione di Foundly

Il `Dockerfile` compila Foundly con Java 21 in una prima fase e copia il WAR nell’immagine Tomcat. L’immagine finale contiene solo ciò che serve per avviare l’app.

Docker Compose avvia Foundly e MySQL in due container. Tomcat parte quando il controllo di salute di MySQL riesce. Il database usa il volume `db_data` e resta sulla rete Docker. Le password di prova vengono lette da file locali esclusi da Git e dal contesto di build.

## Guasto e recupero

Abbiamo fermato solo MySQL. Foundly rimaneva raggiungibile, ma una nuova registrazione restituiva HTTP 503 e mostrava «Registrazione non disponibile». Dopo il riavvio di MySQL la registrazione è riuscita con Tomcat ancora acceso.

Poi abbiamo ricreato i container e verificato che un utente già registrato potesse ancora fare login. Il volume ha conservato i suoi dati.

## Confronto e pubblicazione

SorrentoMarina copia nell’immagine un WAR già compilato e usa un file SQLite montato dal computer. In Foundly il Dockerfile compila il WAR e Compose gestisce MySQL come servizio separato con un volume nominato. Entrambi i workflow pubblicano dopo una CI riuscita su `main`. Foundly usa come tag il commit verificato e pubblica le immagini per `amd64` e `arm64`. [Workflow di riferimento](https://github.com/dardin88/SorrentoMarina/blob/21b0e18f42da5ef2f9759a07f1d02f8a99970f3c/.github/workflows/docker-build.yml) · [Pubblicazione verificata](https://github.com/Sxlvo/Foundly-SWD/actions/runs/45867699633) · [Tag su Docker Hub](https://hub.docker.com/r/sxlvo/foundly-swd/tags)
