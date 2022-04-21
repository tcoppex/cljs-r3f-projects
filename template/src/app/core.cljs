(ns app.core
  (:require
    ["react" :refer [useRef Suspense]]
    ["three" :as THREE]
    ["@react-three/fiber" :refer [Canvas useFrame]]
    ["@react-three/drei" :refer [PerspectiveCamera]]
    [re-frame.core :as re-frame]
    [reagent.core :as r]
    [reagent.dom :as rdom]))

;; -------------------------
;; Shortcuts

(defonce canvas (r/adapt-react-class Canvas))
; (defonce sky (r/adapt-react-class Sky))
; (defonce environment (r/adapt-react-class Environment))
; (defonce perspective-camera (r/adapt-react-class PerspectiveCamera))
; (defonce suspense (r/adapt-react-class Suspense))

;; -------------------------
;; Views

(defn home-page []
  [:div [:h2 "Welcome to Reagent !"]])

; (defn main-scene []
;   (r/create-class
;     {:reagent-render (fn []
;                        [canvas
;                         {:dpr [1 1.5]
;                          :shadows true
;                          :camera {:position [0 5 15] 
;                                   :near 1
;                                   :far 200
;                                   :fov 50}}
;                         [sky {:sun-position [100 10 100]
;                               :scale 1000}]
;                         [:ambientLight [:intensity 0.1]]])}))

;; -------------------------
;; Initialize app

(defn mount-root []
  (let [root-el (.getElementById js/document "root")]
    (rdom/render [home-page] root-el)))

(defn ^:export init! []
  (mount-root))
