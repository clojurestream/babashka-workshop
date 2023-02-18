(ns todos.handlers-test
  (:require
   [cheshire.core :as json]
   [clojure.java.io :as io]
   [clojure.test :refer [deftest is testing]]
   [todos.handlers :as h]
   [todos.main :as main]))

(deftest response-test
  (testing "default response"
    (is (= {:status 200
            :headers {"Content-Type" "application/json"}
            :body "{\"message\":\"Ok\"}"}
           (h/respond "Ok"))))
  (testing "custom response"
    (is (= {:status 202
            :headers {"Content-Type" "application/json"}
            :body "{\"message\":\"Could be Ok\"}"}
           (h/respond "Could be Ok" 202)))))

(deftest handlers-test
  (testing "hello-handler"
    (is (= {:status 200
            :headers {"Content-Type" "application/json"}
            :body "{\"message\":\"Hello!\"}"}
           (h/hello {}))))
  (testing "todo-handler"
    (let [db-file "todos-test.sqlite"
          db {:dbtype "sqlite" :dbname db-file}
          _ (main/migrate db)
          {:keys [status body]} (h/todo {:db db
                                         :body (io/input-stream "test/payload.json")})]
      (is (= 200 status))
      (is (contains? (:message (json/parse-string body true)) :id))
      (io/delete-file db-file)))
  (testing "done-handler"
    (let [db-file "todos-test.sqlite"
          db {:dbtype "sqlite" :dbname db-file}
          _ (main/migrate db)
          {:keys [body]} (h/todo {:db db
                                  :body (io/input-stream "test/payload.json")})
          {:keys [status body]} (h/done {:db db
                                         :params {:id (-> body
                                                          (json/parse-string true)
                                                          :message
                                                          :id)}})]
      (is (= 200 status))
      (is (= "Ok" (:message (json/parse-string body true))))
      (io/delete-file db-file))))
