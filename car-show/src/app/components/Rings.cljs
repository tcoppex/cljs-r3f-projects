(ns app.components.Rings
  (:require
    ["@react-three/fiber" :refer [useFrame]]
    ["three" :refer [Color]]
    ["react" :refer [useRef useState]]
    [applied-science.js-interop :as j]))

;; ----------------------------------------------------------------------------

(defonce half-pi (/ js/Math.PI 2))

(defn- get-elapsed-time [state]
  (j/call-in state [:clock :getElapsedTime]))

(defn- new-color [[r g b] scale]
  (let [color (Color. r g b)]
    (j/call color :multiplyScalar scale)))

(defn- set-emissive-color! [mesh [r g b] scale]
  (j/assoc-in! mesh [:material :emissive]
               (new-color [r g b] scale)))

(defn- set-position! [mesh x y z]
  (j/call-in mesh [:position :set] x y z))

(defn- set-scale! 
  ([mesh s]
   (set-scale! mesh s s s))
  ([mesh x y z]
   (j/call-in mesh [:scale :set] x y z)))

;; ----------------------------------------------------------------------------

(defn- get-color-scale [dist]
  (if (> dist 2)
    (/ (- 1 (/ (- (Math/min dist 12) 2) 10.0)) 2.0)
    0.5))

(defn- move-ring! [mesh i elapsed]
  ;; FIXME : calibrated for 14 rings.
  (let [z (+ (* (- i 7) 3.5) (* 2 (rem (* 0.4 elapsed) 3.5))) ;;
        dist (Math/abs z)
        scale (- 1 (* dist 0.04))
        color-scale (get-color-scale dist)
        emissive-col (if (even? i) [0.1 0.7 3.0] [6 0.15 0.7])]
    (set-position! mesh 0 0 (- z))
    (set-scale! mesh scale)
    (set-emissive-color! mesh emissive-col color-scale)))

(defn- <Ring> [i]
  (let [mesh-ref (useRef)]
    (useFrame (fn [state delta]
                (let [mesh (j/get mesh-ref :current)
                      ticks (get-elapsed-time state)]
                  (move-ring! mesh i ticks))))
    [:mesh {:key i
            :ref mesh-ref
            :position [0 0 0]
            :castShadow true
            :receiveShadow true}
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
