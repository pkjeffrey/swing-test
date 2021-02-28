(ns temp-conv
  (:import
   [javax.swing JLabel JPanel JFrame JTextField]
   [javax.swing.event DocumentListener]
   [java.awt GridBagLayout GridBagConstraints Insets]))

;; https://stuartsierra.com/2010/01/06/heating-up-clojure-swing

(defn f->c [f]
  (* (- f 32) 5/9))

(defn c->f [c]
  (+ (* c 9/5) 32))

(defn parse [s]
  (try
    (Double/parseDouble (.trim s))
    (catch NumberFormatException e
      nil)))

(defn display [n]
  (str (Math/round (float n))))

(comment
  (f->c 212)
  (c->f 0)
  (parse "22.5")
  (parse " 22 ")
  (parse "rubbish")
  (display 22.5)
  (display 22/7))

(defn update-temp [source target convert]
  (when (.isFocusOwner source)
    (if-let [temp (parse (.getText source))]
      (.setText target (display (convert temp)))
      (.setText target ""))))

(defn listen-temp [source target convert]
  (.. source getDocument
      (addDocumentListener
       (proxy [DocumentListener] []
         (insertUpdate [e] (update-temp source target convert))
         (removeUpdate [e] (update-temp source target convert))
         (changeUpdate [e])))))

(defmacro set-grid! [constraints field value]
  `(set! (. ~constraints ~(symbol (name field)))
         ~(if (keyword? value)
            `(. GridBagConstraints ~(symbol (name value)))
            value)))

(defmacro grid-bag-layout [container & body]
  (let [c (gensym "c")
        cntr (gensym "cntr")]
    `(let [~c (new java.awt.GridBagConstraints)
           ~cntr ~container]
       ~@(loop [result '() body body]
           (if (empty? body)
             (reverse result)
             (let [expr (first body)]
               (if (keyword? expr)
                 (recur (cons `(set-grid! ~c ~expr ~(second body)) result)
                        (next (next body)))
                 (recur (cons `(.add ~cntr ~expr ~c) result)
                        (next body)))))))))

(defn temp-app []
  (let [celsius (JTextField. 3)
        fahrenheit (JTextField. 3)
        panel (doto (JPanel. (GridBagLayout.))
                (grid-bag-layout
                 :gridx 0, :gridy 0, :anchor :LINE_END, :insets (Insets. 5 5 5 5)
                 (JLabel. "Degrees Celcius:")
                 :gridy 1
                 (JLabel. "Degrees Fahrenheit:")
                 :gridx 1, :gridy 0, :anchor :LINE_START
                 celsius
                 :gridy 1
                 fahrenheit))]
    (listen-temp celsius fahrenheit c->f)
    (listen-temp fahrenheit celsius f->c)
    (doto (JFrame. "Temperature Converter")
      (.setContentPane panel)
      (.pack)
      (.setVisible true))))

(comment
  (temp-app))