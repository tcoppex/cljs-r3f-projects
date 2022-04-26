(ns app.utils.wrapper
  (:require
    ["regenerator-runtime"] ; required for react-spring & drei.
    ["react" :refer [useRef useState]]
    ["@react-three/drei" :refer [OrbitControls]] 
    ["@react-three/fiber" :refer [Canvas useFrame]]
    ["@react-spring/three" :refer [a useSpring]]
    [applied-science.js-interop :as j] 
    [reagent.core :as r]))

;; ----------------------------------------------------------------------------
;; React.

(defonce use-ref useRef)
(defonce use-state useState)

;; ----------------------------------------------------------------------------
;; Drei.

(defonce orbit-controls OrbitControls)

;; ----------------------------------------------------------------------------
;; Fiber.

(defonce canvas (r/adapt-react-class Canvas))
(defonce use-frame useFrame)

;; ----------------------------------------------------------------------------
;; Spring.

(defn- adapt-animated-class [key] 
  (r/adapt-react-class (j/get a key)))

(defonce a-mesh (adapt-animated-class :mesh))
(defonce a-meshStandardMaterial (adapt-animated-class :meshStandardMaterial))
(defonce a-meshBasicMaterial (adapt-animated-class :meshBasicMaterial))

(defonce use-spring useSpring)

(defn spring-to 
  ([spring [bx by]]
   (spring-to spring [0 1] [bx by]))
  ([spring [ax ay] [bx by]]
   (j/call-in spring [:spring :to] #js [ax ay] #js [bx by])))

;; ----------------------------------------------------------------------------
