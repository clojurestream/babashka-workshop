(ns build
  (:require [clojure.tools.build.api :as b]))

(def uber-file "todos.jar")

(defn clean
  [_]
  (b/delete {:path "target"})
  (b/delete {:path uber-file}))

(defn uber
  [_]
  (let [basis     (b/create-basis {:project "deps.edn"})
        class-dir "target/classes"
        src-dirs  ["src"]]
    (clean nil)
    (b/write-pom {:class-dir class-dir
                  :lib       'workshop/todos
                  :version   "1.0.0"
                  :basis     basis
                  :src-dirs  src-dirs})
    (b/copy-dir {:src-dirs   src-dirs
                 :target-dir class-dir})
    (b/compile-clj {:basis        basis
                    :src-dirs     src-dirs
                    :class-dir    class-dir
                    :ns-compile   '[todos.main]
                    :compile-opts {:direct-linking true}})
    (b/uber {:class-dir class-dir
             :uber-file uber-file
             :basis     basis
             :main      'todos.main})))
