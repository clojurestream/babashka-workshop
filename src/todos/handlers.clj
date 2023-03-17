(ns todos.handlers
  (:require
   [cheshire.core :as json]
   [clojure.java.io :as io]
   [pod.babashka.go-sqlite3 :as sqlite]))

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
  (let [todos (sqlite/query db ["SELECT * FROM todos"])]
    (respond todos)))

(defn todo
  [{:keys [db body]}]
  (let [{:keys [title due]} (json/parse-stream (io/reader body) true)
        id (random-uuid)]
    (sqlite/execute! db ["INSERT INTO todos (id, title, due) VALUES (?, ?, ?)" id title due])
    (respond {:id id})))

(defn done
  [{db :db
    {:keys [id]} :params}]
  (sqlite/execute! db ["DELETE FROM todos WHERE id = ?" id])
  (respond "Ok"))
