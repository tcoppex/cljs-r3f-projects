(ns app.utils.helpers
  (:require 
    ["three" :as THREE]
    [applied-science.js-interop :as j]))

;; ----------------------------------------------------------------------------

(defonce pi js/Math.PI)
(defonce half-pi (/ pi 2))

(defn lerp 
  [a b t] 
   (.lerp THREE/MathUtils a b t))

(defn bool-to-int
  [t]
   ({false 0 true 1} t))

;; ----------------------------------------------------------------------------

(defn set-rotation!
  "Set the euler angles of a js object."
  [o x y z]
   (j/call-in o [:rotation :set] x y z))

(defn rotate-mesh!
  "Rotate a mesh to a given vector of euler angles."
  [meshref dv]
   (let [o (j/get meshref :current)
         e (j/get o :rotation)
         v [(j/get e .-x) (j/get e .-y) (j/get e .-z)]
         [x y z] (map #(+ %1 %2) v dv)]
     (set-rotation! o x y z)))

;; ----------------------------------------------------------------------------
