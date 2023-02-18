(ns todos.main
  (:require
   [next.jdbc :as jdbc]
   [org.httpkit.server :as http]
   [ruuter.core :as ruuter]
   [todos.handlers :as handlers])
  (:gen-class))

(def db {:dbtype "sqlite" :dbname "todos.sqlite"})

(def routes
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
    :response #(handlers/done (assoc % :db db))}])

(defn migrate
  [db]
  (jdbc/execute! db ["CREATE TABLE IF NOT EXISTS todos (id TEXT PRIMARY KEY, title TEXT, due TEXT)"]))

(defn -main
  [& _]
  (migrate db)
  (let [port (parse-long (or (System/getenv "TODOS_PORT") "8080"))
        host (or (System/getenv "TODOS_HOST") "0.0.0.0")]
    (http/run-server #(ruuter/route routes %)
                     {:port port
                      :host host
                      :legacy-return-value? false})
    (println (format "Listening on %s:%d" host port))
    @(promise)))

(comment
  (migrate db)

  (do
    @(http/server-stop! server)
    (def server
      (http/run-server #(ruuter/route routes %)
                       {:port 8080
                        :legacy-return-value? false}))))
