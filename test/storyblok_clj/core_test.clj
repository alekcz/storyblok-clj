(ns storyblok-clj.core-test
  (:require [clojure.test :refer :all]
            [cheshire.core :as json]
            [storyblok-clj.core :as sb]
            [clojure.pprint :as pprint]))

(deftest a-test
  (testing "FIXME, I fail."
    (let [json-map(json/decode (slurp "destiny-fund.json") true)]
      (pprint/pprint (sb/richtext->hiccup (-> json-map :story :content :content)))
      (is (= 1 1)))))
