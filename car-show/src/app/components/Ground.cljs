(ns app.components.Ground
  (:require
    ["regenerator-runtime"]
    ["@react-three/drei" :refer [MeshReflectorMaterial useTexture]]
    ["@react-three/fiber" :refer [useFrame]]
    ["three" :refer [LinearEncoding RepeatWrapping]]
    ["react" :refer [useEffect]]    
    [applied-science.js-interop :as j]
    [reagent.core :as r]))

;; ----------------------------------------------------------------------------
;; Wrapper.

(def mesh-reflector-material (r/adapt-react-class MeshReflectorMaterial))

;; ----------------------------------------------------------------------------
;; Utils.

(defonce half-pi (/ js/Math.PI 2))

(defn- set-texture-wrap-mode! [tex x y]
  (j/assoc! tex :wrapS x)
  (j/assoc! tex :wrapT y))

(defn- set-texture-repeat! [tex x y]
  (j/call-in tex [:repeat :set] x y))

(defn- set-texture-offset! [tex x y]
  (j/call-in tex [:offset :set] x y))

(defn- set-texture-encoding! [tex enc]
  (j/assoc! tex :encoding enc))

(defn- set-sampler! [tex]
  (set-texture-wrap-mode! tex RepeatWrapping RepeatWrapping)
  (set-texture-repeat! tex 5 5)
  (set-texture-offset! tex 0 0))

(defn- get-elapsed-time [state]
  (j/call-in state [:clock :getElapsedTime]))

;; ----------------------------------------------------------------------------

(def public-url "assets/")

(defn- load-textures! [name types]
  (let [texture-paths (map #(str public-url "textures/" name "-" % ".jpg") types)]
    (map useTexture texture-paths)))

;; ----------------------------------------------------------------------------

(defn <Ground> []
  (let [textures (load-textures! "terrain" ["normal" "roughness"])
        [normal roughness] textures]

    ;; Set texture sampling.
    (useEffect (fn []
                 (set-sampler! normal)
                 (set-sampler! roughness)
                 (set-texture-encoding! normal LinearEncoding))
               #js [normal roughness])
    
    ;; Set texture animation.
    (useFrame (fn [state delta]
                (let [t (get-elapsed-time state)
                      t (* t -0.128)
                      t (rem t 1)]
                  (set-texture-offset! normal 0 t)
                  (set-texture-offset! roughness 0 t))))
    
    [:mesh {:rotation [(- half-pi) 0 0] 
            :castShadow true
            :receiveShadow true}
            
      [:planeGeometry {:args [30 30]}]
      ; [:meshStandardMaterial {:dithering true
      ;                         :color [0.015 0.015 0.015]
      ;                         :envMapIntensity 0
      ;                         :normalMap normal
      ;                         :normalScale [0.15 0.15]
      ;                         :roughnessMap roughness
      ;                         :roughness 0.7}]
    
      ; https://github.com/pmndrs/drei/tree/v8.20.2#meshreflectormaterial
      [mesh-reflector-material {:blur [1000 400]
                                :mixBlur 30
                                :mixStrength 80
                                :mixContrast 1
                                :resolution 1024
                                :mirror 0
                                :depthScale 0.01
                                :minDepthThreshold 0.9
                                :maxDepthThreshold 1
                                :depthToBlurRatioBias 0.25
                                :debug 0
                                
                                :reflectorOffset 0.2
                                
                                :dithering true
                                :color [0.015 0.015 0.015]
                                :envMapIntensity 0
                                :normalMap normal
                                :normalScale [0.15 0.15]
                                :roughnessMap roughness
                                :roughness 0.7}]
      
      ]))

;; ----------------------------------------------------------------------------
