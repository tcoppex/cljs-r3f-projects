(ns app.components.Rings
  (:require
    ["@react-three/fiber" :refer [useFrame]]
    ["three" :refer [Color]]
    ["react" :refer [useRef useState]]
    [applied-science.js-interop :as j]))

;; ----------------------------------------------------------------------------

(defonce half-pi (/ js/Math.PI 2))

(defn new-color [[r g b] scale]
  (let [color (Color. r g b)]
    (j/call color :multiplyScalar scale)
    color
    ))

(defn- get-elapsed-time [state]
  (j/call-in state [:clock :getElapsedTime]))

;; ----------------------------------------------------------------------------

(defn- get-color-scale [dist]
  (if (> dist 2)
    (/ (- 1 (/ (- (Math/min dist 12) 2) 10.0)) 2.0)
    0.5))

(defn set-emissive-color! [mesh [r g b] scale]
  (j/assoc-in! mesh [:material :emissive] 
                (new-color [r g b] scale)))

(defn- move-ring [mesh i elapsed]
  (let [z (+ (* (- i 7) 3.5) (* 2 (rem (* 0.4 elapsed) 3.5)))
        dist (Math/abs z)
        scale (- 1 (* dist 0.04))
        color-scale (get-color-scale dist)]
    (j/call-in mesh [:position :set] 0 0 (- z))
    (j/call-in mesh [:scale :set] scale scale scale)
    (if (even? i)
      (set-emissive-color! mesh [0.1 0.7 3.0] color-scale)
      (set-emissive-color! mesh [6 0.15 0.7] color-scale))))

(defn- <Ring> [i]
  (let [mesh-ref (useRef)]
    ;; Move rings.
    (useFrame (fn [state delta]
                 (let [mesh (j/get mesh-ref :current)
                       ticks (get-elapsed-time state)]
                   (move-ring mesh i ticks)
                   )))
    [:mesh {:castShadow true
            :receiveShadow true
            :position [0 0 0]
            :key i
            :ref mesh-ref}
     [:torusGeometry {:args [3.35 0.05 16 100]}]
     [:meshStandardMaterial {:emissive [4 0.1 0.4]
                             :color [0 0 0]}]]))

;; ----------------------------------------------------------------------------

(defn <Rings> 
  ([]
   (<Rings> 14))
  ([n]
   (let [rings (range n)]
     (into [:group] (map #(<Ring> %) rings)))))

;; ----------------------------------------------------------------------------
