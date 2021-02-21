(ns swing-test.core
  (:import
   [javax.swing JFrame JPanel JLabel JButton])
  (:gen-class))

;; https://stuartsierra.com/2010/01/03/doto-swing-with-clojure

(defmacro on-action [component event & body]
  `(.addActionListener ~component
      (proxy [java.awt.event.ActionListener] []
        (actionPerformed [~event] ~@body))))

(defn counter-text [c]
  (str "Counter: " c))

(defn counter-app []
  (let [counter (atom 0)
        label (JLabel. (counter-text @counter))
        inc-btn (doto (JButton. "Add 1")
                  (on-action evnt (.setText label (counter-text (swap! counter inc)))))
        dec-btn (doto (JButton. "Take 1")
                  (on-action evnt (.setText label (counter-text (swap! counter dec)))))
        panel (doto (JPanel.)
                (.setOpaque true)
                (.add label)
                (.add dec-btn)
                (.add inc-btn))]
    (doto (JFrame. "Counter App")
      (.setContentPane panel)
      (.setSize 300 100)
      (.setVisible true))))

(comment
  (counter-app)
  )

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
