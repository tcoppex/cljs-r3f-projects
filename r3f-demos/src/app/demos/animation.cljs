(ns app.demos.animation
  (:require
    [app.utils.helpers :refer [pi bool-to-int]]
    [app.utils.wrapper :as w]
    [applied-science.js-interop :as j]))

(defn- animated-box []
  (let [[active set-active] (w/use-state false)
        spring (w/use-spring #js {:spring (bool-to-int active)
                                  :config {:mass 5
                                           :tension 400
                                           :friction 50
                                           :precision 0.0001}})
        scale (w/spring-to spring [1 3])
        rotation (w/spring-to spring [0.0 pi])
        color (w/spring-to spring ["#6246ea" "#e45858"])
        ]
    ; (println scale rotation)
    [w/a-mesh {:on-click #(set-active (not active))
               ; :rotation-y rotation
               ; :rotation rotation
               
               ; :scale-x scale
               ; :scale-z scale
               :scale scale}
     [:boxBufferGeometry]
     [w/a-meshBasicMaterial {:color color}]]))

(defn animation-demo []
  [w/canvas 
   ; {:camera {:position [0 0 5] :near 0.1 :far 100.0 :fov 60}}
   ; [w/orbit-controls]
   [:f> animated-box]])