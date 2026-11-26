#!/usr/bin/env bash
set -euo pipefail
umask 077

project_dir=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd -P)
db_name=foundly
initialize_database=false
if [[ ${1:-} == --init-db ]]; then
    initialize_database=true
    shift
fi

case $# in
    0)
        host=foundly-salvolepore7.a.aivencloud.com
        port=21893
        ca_file="$project_dir/db/aiven-ca.pem"
        ;;
    3)
        host=$1
        port=$2
        ca_file=$3
        ;;
    *)
        echo "Uso: bash $0 [--init-db] [HOST PORTA /percorso/ca.pem]" >&2
        exit 2
        ;;
esac

if [[ ! $host =~ ^[A-Za-z0-9.-]+$ || ! $port =~ ^[1-9][0-9]{0,4}$ ]] || (( port > 65535 )); then
    echo "Usa l'host e la porta MySQL indicati nella console Aiven." >&2
    exit 2
fi
if [[ ! -r $ca_file ]]; then
    echo "Certificato CA non leggibile: $ca_file" >&2
    exit 1
fi

if "$initialize_database"; then
    schema="$project_dir/db/schemaDB.sql"
    if [[ ! -r $schema ]] || ! command -v mysql >/dev/null; then
        echo "Per inizializzare il database servono il client mysql e db/schemaDB.sql." >&2
        exit 1
    fi

    echo "Importazione di $db_name su $host:$port con l'utente avnadmin."
    {
        printf 'CREATE DATABASE IF NOT EXISTS %s CHARACTER SET utf8mb4;\nUSE %s;\n' "$db_name" "$db_name"
        cat "$schema"
        printf '\nSHOW TABLES;\n'
    } | mysql --protocol=TCP --host="$host" --port="$port" --user=avnadmin --password \
        --ssl-mode=VERIFY_IDENTITY --ssl-ca="$ca_file" --connect-timeout=10
    exit
fi

war="$project_dir/target/Foundly-1.0-SNAPSHOT.war"
if [[ -z ${JAVA_HOME:-} || ! -x $JAVA_HOME/bin/keytool || -z ${CATALINA_HOME:-} || ! -x $CATALINA_HOME/bin/catalina.sh || ! -f $war ]]; then
    echo "Servono JAVA_HOME, CATALINA_HOME e il WAR compilato." >&2
    exit 1
fi

trust_dir=$(mktemp -d /tmp/foundly-aiven.XXXXXXXX)
trap 'rm -f "$trust_dir/aiven.p12"; rmdir "$trust_dir"' EXIT
trust_store="$trust_dir/aiven.p12"
"$JAVA_HOME/bin/keytool" -importcert -noprompt -alias aiven-project-ca -file "$ca_file" \
    -keystore "$trust_store" -storetype PKCS12 -storepass changeit

export DB_USER=foundlyapp
IFS= read -r -s -p "Password MySQL di $DB_USER: " DB_PASSKEY
printf '\n'
if [[ -z $DB_PASSKEY ]]; then
    echo "Inserisci la password dell'utenza MySQL $DB_USER." >&2
    exit 1
fi
export DB_PASSKEY
export DB_URL="jdbc:mysql://$host:$port/$db_name?sslMode=VERIFY_IDENTITY&trustCertificateKeyStoreUrl=file:$trust_store&trustCertificateKeyStoreType=PKCS12&trustCertificateKeyStorePassword=changeit&connectTimeout=10000"

export CATALINA_BASE="$project_dir/target/tomcat-aiven"
mkdir -p "$CATALINA_BASE"/{conf,logs,temp,webapps,work}
cp -R "$CATALINA_HOME/conf/." "$CATALINA_BASE/conf/"
cp "$war" "$CATALINA_BASE/webapps/Foundly.war"
cat > "$CATALINA_BASE/conf/server.xml" <<'XML'
<?xml version="1.0" encoding="UTF-8"?>
<Server port="-1">
  <Listener className="org.apache.catalina.core.JreMemoryLeakPreventionListener" />
  <Listener className="org.apache.catalina.core.ThreadLocalLeakPreventionListener" />
  <Service name="Catalina">
    <Connector address="127.0.0.1" port="18081" protocol="HTTP/1.1" />
    <Engine name="Catalina" defaultHost="localhost">
      <Host name="localhost" appBase="webapps" unpackWARs="false" autoDeploy="false" />
    </Engine>
  </Service>
</Server>
XML

echo "Foundly: http://127.0.0.1:18081/Foundly/index"
"$CATALINA_HOME/bin/catalina.sh" run
