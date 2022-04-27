;;
;; This is a ClojureScript adaptation of @Domenicobrz "Car Show" demo.
;;   cf. https://github.com/Domenicobrz/R3F-in-practice
;;
;; Note that the original project use more recent versions of its dependencies,
;; except for threejs, bump from 0.129.0 to 0.139.2 for this project.
;; 
;; .DevNote.
;;  - When using threejs 0.139.2 with the "template" project it fails, whereas
;;  it works here. The version needed to be bumped to load the gltf car correctly.
;;
;;  - For some reasons textures fails to apply sometimes (fixed by triggering a reload).
;;

(ns app.core
  (:require
    ["regenerator-runtime"]
    ["@react-three/fiber" :refer [Canvas]]
    ["@react-three/drei" :refer [CubeCamera Environment OrbitControls PerspectiveCamera]]
    ["@react-three/postprocessing" :refer [EffectComposer DepthOfField Bloom ChromaticAberration]]
    ["postprocessing" :refer [BlendFunction]]
    [react :refer [useRef Suspense]]
    
    [app.components.Car :refer [<Car>]]
    [app.components.Boxes :refer [<Boxes>]]
    [app.components.FloatingGrid :refer [<FloatingGrid>]]
    [app.components.Ground :refer [<Ground>]]
    [app.components.Rings :refer [<Rings>]]
    
    [applied-science.js-interop :as j]
    [reagent.core :as r]
    [reagent.dom :as rdom]))

;; ----------------------------------------------------------------------------
;; JS Wrappers.

; React
(defonce suspense (r/adapt-react-class Suspense))
; Fiber
(defonce canvas (r/adapt-react-class Canvas))
; Drei
(defonce cube-camera (r/adapt-react-class CubeCamera))
(defonce environment (r/adapt-react-class Environment))
(defonce orbit-controls (r/adapt-react-class OrbitControls))
(defonce perspective-camera (r/adapt-react-class PerspectiveCamera))
; Postprocessing
(defonce effect-composer (r/adapt-react-class EffectComposer))
(defonce bloom (r/adapt-react-class Bloom))
(defonce chromatic-aberration (r/adapt-react-class ChromaticAberration))
; Blend Functions
(defonce add-blend-func (j/get BlendFunction .ADD))
(defonce normal-blend-func (j/get BlendFunction .NORMAL))

;; ----------------------------------------------------------------------------
;; Views.

(defn- <CarShow> []
  [:group
   ;; Camera.
   [perspective-camera {:makeDefault true 
                        :fov 50 
                        :position [3 2 5]}]
   ;; Controller.
   [orbit-controls {:target [0 0.35 0] 
                    :maxPolarAngle 1.45}]
   ;; Environment Map.
   [cube-camera {:resolution 256
                 :frames js/Infinity}
    ;; FIXME : envmap is not applied.
    (fn [texture] (r/as-element [:group
                                 ; [environment {:map texture}]  
                                 [:f> <Car> {:envMap texture}]]))]
   ;; Lights.
   [:spotLight {:color [1 0.25 0.7]
                :intensity 1.5
                :position [5  5 0]
                :angle 0.6
                :penumbra 0.5
                ;:shadow-bias -0.0001
                :castShadow true}]
   [:spotLight {:color [0.14 0.5 1.0]
                :intensity 2.0
                :position [-5  5 0]
                :angle 0.6
                :penumbra 0.5
                ; :shadow-bias -0.0001
                :castShadow true}]
   
   ;; Specialized components.
   [:f> <Ground>]
   [:f> <FloatingGrid>]  ; FIXME : sometimes the grid texture is not loaded.
   [:f> <Boxes>]         ; FIXME : sometimes cubes glitch out of existence.
   [:f> <Rings>]
   
   ;; PostProcessing.
   [effect-composer
    [bloom {:blendFunction add-blend-func
            :intensity 1.3
            :width 300
            :height 300
            ; :kernelSize 50
            :luminanceTheshold 0.15
            :luminanceSmoothing 0.025}]
    [chromatic-aberration {:blendFunction normal-blend-func
                           :offset [0.0005 0.0012]}]]])

(defn- <App> []
  [suspense {:fallback nil}
   [canvas {:shadows true}
    [<CarShow>]]])

(defn- app []
  (r/create-class {:reagent-render <App>}))

;; ----------------------------------------------------------------------------
;; Initialize the app.

(defn ^:dev/after-load mount-root []
  (let [root-el (.getElementById js/document "root")]
    (rdom/render [app] root-el)))

(defn ^:export init! []
  (mount-root))

;; ----------------------------------------------------------------------------
