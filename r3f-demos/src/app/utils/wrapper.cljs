(ns app.utils.wrapper
  (:require ["@react-three/fiber" :refer [Canvas]]
            [reagent.core :as r]))

;; ----------------------------------------------------------------------------

(defonce canvas (r/adapt-react-class Canvas))

;; ----------------------------------------------------------------------------
