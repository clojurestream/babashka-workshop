# Babashka Workshop

A simple TODO app backed by SQLite3 and a CLI intended for the workshop demonstrating the various features and aspects of Babashka.

## System Requirements

Latest versions recommended for the following

- [Babashka][babashka].
- Clojure [CLI][cli]
- JDK 11+.
- Docker or Podman.
- [Gum][gum].

To verify things are set up properly, you can run this:
```
bb --version

clojure --version
```

## Run

### Server

- Run tests: `make test`.
- Start app locally: `make start`. Set the env vars `TODOS_HOST` and `TODOS_PORT` to customise.
- Build container image: `make image`.

## Workshop Feedback

At the end of the workshop, please [provide short feedback][feedback-form].


[babashka]: https://github.com/babashka/babashka#installation
[feedback-form]: https://forms.gle/iZ8YMfftWdu3MsSPA
[gum]: https://github.com/charmbracelet/gum#installation
[cli]: https://clojure.org/guides/install_clojure
