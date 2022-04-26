(ns app.utils.helpers
  (:require [applied-science.js-interop :as j]))

;; ----------------------------------------------------------------------------

(defn- set-rotation! [o x y z]
  "Set the euler angles of a js object."
  (j/call-in o [.-rotation .-set] x y z))

;; ----------------------------------------------------------------------------

(defn rotate-mesh! [meshref dv]
  "Rotate a mesh to a given vector of euler angles."
  (let [o (j/get meshref :current)
        e (j/get o .-rotation)
        v [(j/get e .-x) (j/get e .-y) (j/get e .-z)]
        [x y z] (map #(+ %1 %2) v dv)]
    (set-rotation! o x y z)))

;; ----------------------------------------------------------------------------
