(ns app.demos.box
  (:require 
    [app.utils.helpers :refer [rotate-mesh!]]
    [app.utils.wrapper :as w]
    [applied-science.js-interop :as j]))

;; ----------------------------------------------------------------------------
;; App configurations.

(def camera-config 
  {:position [0 1 3] :near 0.1 :far 100.0 :fov 60})

;; ----------------------------------------------------------------------------
;; Views.

(defn- <Box> [props]
  "Reactive box component."
  ([] Box {})
  (let [defaults {:color "white" :size [1 1 1] :position [0 0 0]}
        {color :color size :size position :position} (merge defaults props)
        ; Reference to the THREE.Mesh object.
        mesh-ref (w/use-ref)
        ; Hold state for hovered and clicked events.
        [hovered hover] (w/use-state false)
        [clicked click] (w/use-state false)
        ;; Events results.
        scale (if clicked 1.5 1.0)
        color (if hovered "hotpink" color)
        sign (js/Math.sign (get position 0))
        angles [0.01 (* sign -0.01) 0.0]]
    (w/use-frame #(rotate-mesh! mesh-ref angles))
    [:mesh {:ref              mesh-ref
            :on-click         #(click (not clicked))
            :on-pointer-over  #(hover true)
            :on-pointer-out   #(hover false)
            :position         position
            :scale            scale}
     [:boxBufferGeometry {:args size}]
     [:meshStandardMaterial {:color color}]]))

(defn box-demo []
  "Two rotating and interactive boxes with a light setup."
  [w/canvas {:shadows true
             :camera camera-config}
   [:ambientLight {:intensity 0.5}]
   [:spotLight {:position [10 10 10] :angle 0.5 :penumbra 1}]
   [:pointLight {:position [-10 -10 -10]}]
   [:f> <Box> {:color "orange" :position [-1.2 0 0]}]
   [:f> <Box> {:color "red"    :position [+1.2 0 0]}]])

;; ----------------------------------------------------------------------------
