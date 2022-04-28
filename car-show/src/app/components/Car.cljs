(ns app.components.Car
  (:require
    ["regenerator-runtime"]
    ["@react-three/drei" :refer [useGLTF]]
    ["@react-three/fiber" :refer [useFrame]]
    ["three" :refer [Mesh]]
    ["react" :refer [useEffect]]
    [applied-science.js-interop :as j]
    [reagent.core :as r]))

;; ----------------------------------------------------------------------------

(def public-url "/assets")

(defonce half-pi (/ js/Math.PI 2))

(defn- get-elapsed-time [state]
  (j/call-in state [:clock :getElapsedTime]))

;; ----------------------------------------------------------------------------

(def car-model-path (str public-url "/models/car/scene.gltf"))

(defn- setup-sub-mesh [o]
  (when (= (type o) Mesh)
    (j/assoc! o :castShadow true)
    (j/assoc! o :receiveShadow true)
    (j/assoc-in! o [:material :envMapIntensity] 18)))

(defn- setup-car [scene]
  (j/call-in scene [:scale :set] 0.005 0.005 0.005)
  (j/call-in scene [:position :set] 0.0 -0.035 0.0)
  (j/call scene :traverse setup-sub-mesh))

(defn- rotate-car-wheels [wheels angle]
  (let [rotate-x! #(j/assoc-in! wheels [:children % :rotation .-x] angle)]
    (mapv rotate-x! [0 2 4 6])))

;; ----------------------------------------------------------------------------

(defn <Car> []
  (let [gltf (useGLTF car-model-path)
        scene (j/get gltf :scene)]
    ;; Setup.
    (useEffect #(setup-car scene) #js [gltf])
    ;; Update.
    (useFrame (fn [state _] 
                (let [wheels (j/get-in scene [:children 0 :children 0 :children 0])
                      angle (* 2 (get-elapsed-time state))]
                  (rotate-car-wheels wheels angle))))
    [:primitive {:object scene}]))
  
;; ----------------------------------------------------------------------------
