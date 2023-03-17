(ns todos.cli
  (:require
   [babashka.cli :as cli]
   [babashka.http-client :as http]
   [bblgum.core :as b]
   [cheshire.core :as json]
   [doric.core :as d]))

(defn bail!
  [msg]
  (binding [*out* *err*]
    (println msg))
  (System/exit 1))

(defn msg-of
  [resp]
  (-> resp
      :body
      (json/parse-string true)
      :message))

(defn exec
  [opts]
  (let [{:keys [status result]} (b/gum opts)]
    (if-not (zero? status)
      (bail! "Cancelled")
      (first result))))

(defn prompt
  [prompt]
  (exec {:cmd :input
         :opts {:placeholder prompt}}))

(defn all-todos
  [url]
  (msg-of (http/get (str url "/todos"))))

(defn ls
  [{{:keys [url]} :opts}]
  (let [todos (all-todos url)]
    (if-not (empty? todos)
      (->> todos
           (d/table [:id :title :due])
           (println))
      (println "All caught up!"))))

(defn todo
  [{{:keys [title due url]} :opts}]
  (let [title (or title (prompt "What to do?"))
        due (or due (prompt "By when? Specify in YYYY-MM-DD HH:MM:SS.SSS"))
        resp (http/post (str url "/todos")
                        {:body (json/generate-string {:title title :due due})})]
    (println "Created" (-> resp msg-of :id))))

(defn done
  [{{:keys [id url]} :opts}]
  (let [id (or id (exec {:cmd :choose
                         :args (map :id (all-todos url))}))]
    (http/delete (str url "/todos/" id))
    (println "Good job!")))

(defn -main
  [args]
  (let [table [{:cmds ["ls"] :fn ls}
               {:cmds ["todo"] :fn todo}
               {:cmds ["done"] :fn done}]]
    (cli/dispatch table args {:exec-args {:url "http://localhost:8080"}})))
