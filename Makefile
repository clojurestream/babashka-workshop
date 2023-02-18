.PHONY: test

test:
	clojure -X:test

start:
	clojure -M -m todos.main

uberjar:
	clojure -T:build uber

image: uberjar
	podman build -t docker.io/workshop/todo .
