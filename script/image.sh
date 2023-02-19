engine=$(command -v podman || command -v docker || { echo "Podman or Docker is required." >&2; exit 1; })

$engine build -t docker.io/clojurestream/todo .
