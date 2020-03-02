(ns storyblok-clj.core
  (:require
    [clj-http.client :as client]
    [cheshire.core :as json]
    [clojure.string :as str]
    [hiccup.core :as hiccup]
    [markdown.core :as md]))

(defn- process-marks [node]
  (let [marks (:marks node) original (dissoc node :marks)]
    (if (or (nil? marks) (empty? marks))
        original
        (process-marks
            (let [mark (first marks) new-marks (rest marks)]
              { :type (:type mark)
                :attrs (:attrs mark)
                :content [(assoc original :marks new-marks)]})))))


(defn- parse-custom-properties [node prefix]
  (let [clean-node (dissoc node :_editable :_uid :body :component)
        keys (keys clean-node)]
      (for [x keys]
        [:div {:class (str prefix "__" (name x))} (x clean-node)])  ))

(defn- make-node [raw-node]
  (let [node (process-marks raw-node)]
    ;(clojure.pprint/pprint node)
    (let [node-type (str (or (:type node) (:component node)))]
      (case node-type
        "doc" (or (for [x (-> node :content)] (make-node x)) (make-node (-> node :content)))
        "horizontal_rule" [:hr ]
        "blockquote" [:blockquote]
        "bullet_list" [:ul]
        "code_block" [:code]
        "hard_break" [:br]
        "heading" [(keyword (str "h" (-> node :attrs :level)))]
        "image" [:img (:attrs node)]
        "list_item" [:li]
        "ordered_list" [:ol] 
        "paragraph" [:p (for [x (-> node :content)] (make-node x))]
        "a" [:a (:attrs node)]
        "link" [:a (:attrs node)]
        "italic" [:i]
        "bold" [:b]
        "underline" [:u]
        "text" (:text node)
        "styled" (or (for [x (-> node :content)] (make-node x)) (:text node))
        "blok" (for [x (-> node :attrs :body)] [:div {:class (str "blok-" (:component x))} 
                                                  (parse-custom-properties x (str "blok-" (:component x)))
                                                  (make-node (:body x))])
        ""))))

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
  (if (= "doc" (:type richtext-map))
      (:content richtext-map)
      nil))         

(defn richtext->html [richtext-map]
  (let [document (extract-doc richtext-map)]
    (if (nil? document)
      (md/md-to-html-string richtext-map)
      (hiccup/html (process-content document)))))
      ;(identity (process-content document)))))
     