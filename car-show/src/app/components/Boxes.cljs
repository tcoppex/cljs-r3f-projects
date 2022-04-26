(ns app.components.Boxes
  (:require
    ["@react-three/fiber" :refer [useFrame]]
    ["three" :refer [Vector3]]
    ["react" :refer [useRef useState]]
    
    [applied-science.js-interop :as j]
    [reagent.core :as r]
    ))

;; ----------------------------------------------------------------------------

(defonce half-pi (/ js/Math.PI 2))

(defn- randrange [a b]
  (+ a (* (rand) (- b a))))

(defn vector3
  ([] (Vector3.))
  ([x y z] (Vector3. x y z)))

;; ----------------------------------------------------------------------------

(defn- get-initial-position []
  (let [x (* (randrange -1 1) 3)
        y (randrange 0.1 2.6)
        z (* (randrange -1 1) 15)
        v (vector3 x y z)]
    (cond (< x 0) (j/update! v :x - 1.75)
          (> x 0) (j/update! v :x + 1.75))
    v))

(defn- get-reset-position []
  (let [x (* (randrange -1 1) 3)
        y (randrange 0.1 2.6)
        z (randrange 10 20)
        v (vector3 x y z)]
    (cond (< x 0) (j/update! v :x - 1.75)
          (> x 0) (j/update! v :x + 1.75))
    v))

;; ----------------------------------------------------------------------------

(defn- <Box> [{key :key color :color}]
  (let [mesh-ref (useRef)
        time (useRef 0.0) ; use global one instead ?
        [initial-position set-initial-position] (useState (get-initial-position))
        reset-position #(set-initial-position (get-reset-position))
        [x-rot-speed] (useState #(rand))
        [y-rot-speed] (useState #(rand))
        [scale] (useState (+ 0.05 (* 0.5 (Math/pow (rand) 2))))]
    ; Update events.
    (useFrame (fn [state delta]
                ;; Update time tracker.
                (j/update! time :current + (* 0.8 delta))
                ;;
                (let [x (j/get initial-position :x)
                      y (j/get initial-position :y)
                      z (j/get initial-position :z)
                      new-z (- z (j/get time :current))]
                  ;; Reset position / time when reaching end of grid.
                  (when (< new-z -10.0)
                    (reset-position)
                    (j/assoc! time :current 0.0))
                  ; update positon on Z axis.
                  (j/call-in mesh-ref [:current :position :set] x y new-z))
                ;; Update Cube rotation on X & Y axis.
                (j/update-in! mesh-ref [:current :rotation :x] + (* delta x-rot-speed))
                (j/update-in! mesh-ref [:current :rotation :y] + (* delta y-rot-speed)))
              [x-rot-speed y-rot-speed initial-position])
    ;; Cube mesh.
    [:mesh {:key key
            :ref mesh-ref
            :rotation [half-pi 0 0]
            :scale scale
            :castShadow true}
     [:boxGeometry {:args [1 1 1]}]
     [:meshStandardMaterial {:color color
                             :envMapIntensity 0.15}]]))

(defn create-box [i]
  [:f> <Box> {:color (if (even? i) [0.4 0.1 0.1] [0.05 0.15 0.4])
              :key i}])

(defn <Boxes> 
  ([n] 
   (into [:group] (map create-box (range n))))
  ([] 
   (<Boxes> 50)))