(ns datenschutzerklaerung.generator
  (:require
   [clojure.java.io :as io]))


(defn baustein
  ([resource-name]
   (baustein resource-name nil))
  ([resource-name vars]
   (let [resource (io/resource (str (name resource-name) ".html"))
         _ (when-not resource
             (throw (ex-info (str "Missing Baustein: " resource-name)
                             {:baustein resource-name
                              :vars vars})))
         html (slurp resource)]
     (str "\n\n"
          (reduce
           (fn [html [k v]]
             (.replace html (str "$" (-> k name .toUpperCase)) v))
           html
           vars)))))


(defn generate-html
  [{:keys [verantwortlicher
           bausteine]
    :or {verantwortlicher "Unbekannt"
         bausteine [:vertragsabwicklung
                    :serverdaten
                    :cookies
                    :kontaktanfragen
                    :nutzerbeitraege
                    :google-analytics
                    :google-adwords
                    :google-adsense
                    :amazon-partner
                    :affilinet]}}]
  (let [verantwortlicher (-> verantwortlicher (.replace "\n" "<br>\n"))]
    (str (baustein :hauptteil {:verantwortlicher verantwortlicher})
         (apply str (map baustein bausteine))
         (baustein :credits))))


(defn testme []
  (->> {}
       generate-html
       (spit "target/testme.html")))

;; (testme)
