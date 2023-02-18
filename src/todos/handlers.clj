(ns todos.handlers
  (:require
   [cheshire.core :as json]
   [clojure.java.io :as io]
   [next.jdbc :as jdbc]
   [next.jdbc.result-set :as rs]))

(defn respond
  ([msg]
   (respond msg 200))
  ([msg status]
   {:status status
    :headers {"Content-Type" "application/json"}
    :body (json/generate-string {:message msg})}))

(defn hello
  [_]
  (respond "Hello!"))

(defn ls
  [{db :db}]
  (let [todos (jdbc/execute! db ["SELECT * FROM todos"] {:builder-fn rs/as-unqualified-lower-maps})]
    (respond todos)))

(defn todo
  [{:keys [db body]}]
  (let [{:keys [title due]} (json/parse-stream (io/reader body) true)
        id (random-uuid)]
    (jdbc/execute-one! db ["INSERT INTO todos (id, title, due) VALUES (?, ?, ?)" id title due])
    (respond {:id id})))

(defn done
  [{db :db
    {:keys [id]} :params}]
  (jdbc/execute-one! db ["DELETE FROM todos WHERE id = ?" id])
  (respond "Ok"))
