(ns todos.main
  (:require
   [org.httpkit.server :as http]
   [pod.babashka.go-sqlite3 :as sqlite]
   [ruuter.core :as ruuter]
   [todos.handlers :as handlers])
  (:gen-class))

(def db "todos.sqlite")

(defn handler
  [request]
  (ruuter/route
   [{:path "/"
     :method :get
     :response #(handlers/hello %)}
    {:path "/todos"
     :method :get
     :response #(handlers/ls (assoc % :db db))}
    {:path "/todos"
     :method :post
     :response #(handlers/todo (assoc % :db db))}
    {:path "/todos/:id"
     :method :delete
     :response #(handlers/done (assoc % :db db))}]
   request))

(defn migrate
  [db]
  (sqlite/execute! db ["CREATE TABLE IF NOT EXISTS todos (id TEXT PRIMARY KEY, title TEXT, due TEXT)"]))

(defn -main
  [& _]
  (migrate db)
  (let [port (parse-long (or (System/getenv "TODOS_PORT") "8080"))
        host (or (System/getenv "TODOS_HOST") "0.0.0.0")]
    (http/run-server handler
                     {:port port
                      :host host
                      :legacy-return-value? false})
    (println (format "Listening on %s:%d" host port))
    @(promise)))

(comment
  (migrate db)

  (def server
    (http/run-server #'handler
                     {:port 8080
                      :legacy-return-value? false}))

  @(http/server-stop! server))
