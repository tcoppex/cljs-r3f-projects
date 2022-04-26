;;
;; XXX Work In Progress XXX
;;
;; Inspired by :
;;  https://github.com/binaryage/cljs-react-three-fiber
;;

(ns app.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [app.demos.box :refer [<Canvas>]]))

;; ----------------------------------------------------------------------------

;; DevNotes
;;  Right now we directly load the BoxDemo in core but ultimately we should
;;  create an webapp that dynamically load each demo on demand.

(defn- app []
  (r/create-class {:reagent-render <Canvas>}))

;; ----------------------------------------------------------------------------

(defonce root-el (.getElementById js/document "root"))

(defn ^:dev/after-load mount-root []
  (rdom/unmount-component-at-node root-el)
  (rdom/render [app] root-el))

(defn ^:export init! []
  (mount-root))

;; ----------------------------------------------------------------------------
