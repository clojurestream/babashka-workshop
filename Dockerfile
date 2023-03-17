FROM docker.io/babashka/babashka

WORKDIR /opt

COPY todos.jar .

RUN bb -e "(babashka.pods/load-pod 'org.babashka/go-sqlite3 \"0.1.0\")"

ENTRYPOINT ["bb", "todos.jar"]
