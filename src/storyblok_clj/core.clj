(ns storyblok-clj.core
  (:require
    [clj-http.client :as client]
    [cheshire.core :as json]
    [clojure.string :as str]
    [hiccup.core :as hiccup]))

(defn- process-marks [node]
  (let [marks (:marks node) original (dissoc node :marks)]
    (if (or (nil? marks) (empty? marks))
        original
        (process-marks
            (let [mark (first marks) new-marks (rest marks)]
              { :type (:type mark)
                :attrs (:attrs mark)
                :content [(assoc original :marks new-marks)]})))))

(defn- make-node [raw-node]
  (let [node (process-marks raw-node)]
    (let [node-type (str (:type node))]
      (case node-type
        "horizontal_rule" [:hr ]
        "blockquote" [:blockquote]
        "bullet_list" [:ul]
        "code_block" [:code]
        "hard_break" [:br]
        "heading" [(keyword (str "h" (-> node :attrs :level)))]
        "image" [:img (:attrs node)]
        "list_item" [:li]
        "ordered_list" [:ol] 
        "paragraph" [:p]
        "a" [:a (:attrs node)]
        "link" [:a (:attrs node)]
        "italic" [:i]
        "bold" [:b]
        "text" (:text node)
        :default ""))))

(defn- process-content [node]
  (if (vector? node)
      (for [x node]
        (process-content x))
      (if (not (contains? node :content)) 
            (if (contains? node :marks)
              (let [new-node (process-marks node)]
                (process-content new-node))
              (make-node node))
            (let [new-node (make-node node)]
              (conj new-node
                  (for [x (:content node)] 
                    (process-content x)))))))

(defn- extract-doc [richtext-map]
  (println (:content richtext-map))
  (if (= "doc" (:type richtext-map))
      (:content richtext-map)
      nil))         

(defn richtext->html [richtext-map]
  (hiccup/html (process-content (extract-doc richtext-map))))