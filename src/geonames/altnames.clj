(ns geonames.altnames
  (:require [clucie.core :as core]
            [clucie.analysis :as analysis]
            [clucie.store :as store]))

(def index-store (store/disk-store "altnames"))

(def alternate-names-keys
  [:id
   :geoname-id
   :isolanguage
   :name
   :is-preferred-name
   :is-short-name
   :is-colloquial
   :is-historic
   :from
   :to])

(def analyzer (analysis/standard-analyzer))

(defn line->document [line]
  (zipmap
   alternate-names-keys
   (clojure.string/split line #"\t")))

(defn add-documents [lines]
  (core/add! index-store
             (map line->document lines)
             alternate-names-keys
             analyzer)
  (with-open [reader (store/store-reader index-store)]
    (println (.numDocs reader))))

(defn create-index []
  (with-open [rdr (clojure.java.io/reader "data/alternateNamesV2.txt")]
    (->> (line-seq rdr)
         (partition 10000)
         (map add-documents)
         doall)))

(defn translate-token [name lang]
  (let [geoname-id (-> (core/search index-store
                                    [{:name name} {:isolanguage "en"}]
                                    1)
                       first
                       :geoname-id)]
    (or (->> (core/search index-store [{:geoname-id geoname-id} {:isolanguage lang}] 1)
             first
             :name)
        name)))

(defn translate [address lang]
  (->> (clojure.string/split address #"\s*,\s*")
       (map #(translate-token % lang))
       (clojure.string/join ", ")))
