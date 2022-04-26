(ns app.components.FloatingGrid
  (:require
    ["regenerator-runtime"]
    ["@react-three/drei" :refer [useTexture]]
    ["@react-three/fiber" :refer [useFrame useLoader]]
    ["three" :refer [RepeatWrapping TextureLoader]]
    ["react" :refer [useEffect]]
    [applied-science.js-interop :as j]))

;; ----------------------------------------------------------------------------

(defonce half-pi (/ js/Math.PI 2))

(def grid-texture-path "/assets/textures/grid-texture.png")

;; ----------------------------------------------------------------------------

(defn <FloatingGrid> []
  (let [diffuse (useTexture grid-texture-path)]
    ;; Sampling parameters.
    (useEffect (fn []
                (j/assoc! diffuse :wrapS RepeatWrapping)
                (j/assoc! diffuse :wrapT RepeatWrapping)
                (j/assoc! diffuse :anisotropy 4)
                (j/call-in diffuse [:repeat :set] 30 30)
                (j/call-in diffuse [:offset :set] 0 0)) 
               #js [diffuse])
    ;; Texture animation.
    (useFrame (fn [state delta]
                (let [t (j/call-in state [:clock :getElapsedTime])
                      t (* t -0.68)]
                  (j/call-in diffuse [:offset :set] 0 t))
                ))
    [:mesh {:rotation [(- half-pi) 0 0] 
            :position [0 0.425 0]
            :castShadow true
            :receiveShadow true}
      [:planeGeometry {:args [35 35]}]
      [:meshBasicMaterial {:color [1.0 1.0 1.0]
                           :opacity 0.25
                           :map diffuse
                           :alphaMap diffuse
                           :transparent true}]]))

;; ----------------------------------------------------------------------------
