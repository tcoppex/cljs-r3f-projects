(ns app.core
  (:require
    ["@react-three/fiber" :refer [Canvas useFrame]]
    [react :refer [useRef useState]]
    [applied-science.js-interop :as j]
    [reagent.core :as r]
    [reagent.dom :as rdom]))

;; ----------------------------------------------------------------------------
;; ReactJS redefine.

;; We need to adapt react-three/* classes to reagent, whereas ThreeJS classes 
;; can be used directly as keyword or name.
(defonce canvas (r/adapt-react-class Canvas))

;; ----------------------------------------------------------------------------
;; App configurations.

;; Main camera setting.
(def camera-config {:position [0 1 3] :near 0.1 :far 100.0 :fov 60})

;; ----------------------------------------------------------------------------
;; Helpers.

(defn- set-rotation! 
  "Set the euler angles of a js object."
  [o x y z]
  (j/call-in o [.-rotation .-set] x y z))

(defn- rotate-mesh!
  "Rotate a mesh to a given vector of euler angles."
  [meshref dv]
  (let [o (j/get meshref :current)
        e (j/get o .-rotation)
        v [(j/get e .-x) (j/get e .-y) (j/get e .-z)]
        [x y z] (map #(+ %1 %2) v dv)]
    (set-rotation! o x y z)))

;; ----------------------------------------------------------------------------
;; Views.

;;
;; To use hooks (useFrame, useRef, useState) we need to wrap objects in a fragment 
;; tag (eg [:f> <Box>]).
;;
;; @see https://github.com/reagent-project/reagent/blob/master/doc/ReactFeatures.md#hooks=
;;

(defn- <Box>
  "Reactive box component."
  ([props]
   (let [defaults {:color "white" :size [1 1 1] :position [0 0 0]}
         {color :color size :size position :position} (merge defaults props)
         ; Reference to the THREE.Mesh object.
         mesh-ref (useRef)
         ; Hold state for hovered and clicked events.
         [hovered hover] (useState false)
         [clicked click] (useState false)
         ;; Events results.
         scale (if clicked 1.5 1.0)
         color (if hovered "hotpink" color)
         sign (js/Math.sign (get position 0))
         angles [0.01 (* sign -0.01) 0.0]]
     (useFrame #(rotate-mesh! mesh-ref angles))
     [:mesh {:ref              mesh-ref
             :on-click         #(click (not clicked))
             :on-pointer-over  #(hover true)
             :on-pointer-out   #(hover false)
             :position         position
             :scale            scale}
      [:boxBufferGeometry {:args size}]
      [:meshStandardMaterial {:color color}]]))
  ([] Box {}))

(defn- <Canvas>
  "Two rotating and interactive boxes with a light setup."
  []
   [canvas {:shadows true
            :camera camera-config}
    [:ambientLight {:intensity 0.5}]
    [:spotLight {:position [10 10 10] :angle 0.5 :penumbra 1}]
    [:pointLight {:position [-10 -10 -10]}]
    [:f> <Box> {:color "orange" :position [-1.2 0 0]}]
    [:f> <Box> {:color "red"    :position [+1.2 0 0]}]])

(defn- app []
  (r/create-class {:reagent-render <Canvas>}))

;; ----------------------------------------------------------------------------
;; Initialize the app.

(defn ^:dev/after-load mount-root []
  (let [root-el (.getElementById js/document "root")]
    (rdom/render [app] root-el)))

(defn ^:export init! []
  (mount-root))
