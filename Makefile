.PHONY: test clean

test:
	clojure -X:test

start:
	clojure -M -m todos.main

nrepl:
	clojure -M:nrepl

uberjar:
	clojure -T:build uber

image: uberjar
	./script/image.sh

clean:
	rm -rf target todos.jar todos.sqlite
