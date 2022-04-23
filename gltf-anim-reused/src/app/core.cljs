;;
;; This project is based off "GLTF Animations re-used" from Paul Henschel.
;;   cf. https://codesandbox.io/s/gltf-animations-re-used-k8phr
;;
;; ----------------------------------------------------------------------------

(ns app.core
  (:require
    ["regenerator-runtime"] ; required for react-spring & drei.
    
    ["@react-three/fiber" :refer [Canvas useFrame useGraph]]
    ["@react-three/drei" :refer [useGLTF useTexture useAnimations]]
    ["@react-spring/three" :refer [a useSpring]]
    ["three-stdlib" :refer [SkeletonUtils]]
    ["three" :as THREE]
    ["react" :refer [useRef useEffect useMemo useState Suspense]]
    
    [applied-science.js-interop :as j]
    [reagent.core :as r]
    [reagent.dom :as rdom]))

;; ----------------------------------------------------------------------------
;; ReactJS redefine.

;; We need to adapt react js classes to reagent.
(defonce canvas (r/adapt-react-class Canvas))
(defonce suspense (r/adapt-react-class Suspense))

;; Same for react-spring animated components.
(defn- adapt-animated-class [key] (r/adapt-react-class (j/get a key)))
(defonce a-mesh (adapt-animated-class :mesh))
(defonce a-meshStandardMaterial (adapt-animated-class :meshStandardMaterial))

;; ----------------------------------------------------------------------------
;; Maths Utils.

(defonce pi js/Math.PI)
(defonce half-pi (* pi 0.5))

(defn lerp [a b t] 
  (.lerp THREE/MathUtils a b t))

;; ----------------------------------------------------------------------------
;; App configurations.

;; Path to the assets directory.
(defonce assets-path "/assets")

(defn create-asset-map [filenames]
  (zipmap [:model :texture] (map #(str assets-path "/" %) filenames)))

(def stacy-assets 
  (create-asset-map ["stacy.glb" "stacy.jpg"]))

;; Main camera setting.
(def camera-config 
  {:position [1.0 1.25 2.5] :near 0.1 :far 100.0 :fov 50})

;; ----------------------------------------------------------------------------
;; Controllers.

(defn- CameraMouseUpdate []
  "Update the camera based on the mouse screen position."
  (useFrame (fn [state] 
    (let [newpos #(+ 1.5 (/ (j/get-in state [:mouse %]) 4))]
      (mapv #(j/update-in! state [:camera :position %] lerp (newpos %) 0.075) [:x :y])))))

;; ----------------------------------------------------------------------------
;; Views.

(defn- Model [props]
  ""
  (let [;; States.
        [hovered hover] (useState false)
        [animation-index set-animation-index] (useState (:pose props))
        
        ;; Load model.
        _ (useGLTF (:model stacy-assets)) 
        scene (j/get _ :scene) 
        animations (j/get _ :animations)
        
        ;; Fetch texture separately & disable its vertical flip.
        texture (useTexture (:texture stacy-assets))
        _ (j/assoc! texture .flipY false)
                                
        ;; Skinned meshes cannot be re-used in threejs without cloning them.
        clone (useMemo #(.clone SkeletonUtils scene) #js [scene])
        
        ;; Creates two flat object collections for nodes and materials.
        nodes (j/get (useGraph clone) :nodes)
        geometry (j/get-in nodes [.stacy .geometry])
        skeleton (j/get-in nodes [.stacy .skeleton])
        
        ;; Extract animations actions.
        _ (useAnimations animations)
        ref (j/get _ :ref)
        names (j/get _ :names)
        actions (j/get _ :actions)
        
        ;; Callback to change current animation.
        anim-count (j/get names .length)
        change-anim! #(set-animation-index (rem (inc animation-index) anim-count))
        
        ;; Spring to animate the selection halo.
        _ (useSpring (if hovered #js {:scale 1.2 :color "tomato"} 
                                 #js {:scale 0.8 :color "bisque"}))
        spring-color (j/get _ :color)
        spring-scale (j/get _ :scale)]

    ;; Blend animations when changing.
    (useEffect 
      (fn [] (when-let [action (j/get actions (j/get names animation-index))]
               ;; Reset and fade-in animation after an index has been changed.
               (j/call (j/call (j/call action :reset) :fadeIn 0.5) :play)
               ;; Fade-out on clean-up phase.
               (fn [] (j/call action :fadeOut 0.5))))
      #js [animation-index actions names])
    
    ;; Character + selection halo.
    [:group (merge {:ref ref :dispose nil} props)
     ;; Animated character.
     [:group {:on-click change-anim!
              :on-pointer-over #(hover true)
              :on-pointer-out #(hover false)
              :rotation [half-pi 0 0]
              :scale [0.01 0.01 0.01]}
      [:primitive {:object (j/get nodes :mixamorigHips)}]
      [:skinnedMesh {:castShadow true
                     :receiveShadow true
                     :geometry geometry
                     :skeleton skeleton
                     :rotation [(- half-pi) 0 0]
                     :scale [100 100 100]}
        [:meshStandardMaterial {:map texture :skinning true}]]]
     ;; Selection halo.
     [a-mesh {:receiveShadow true
                     :position [0 1 -1]
                     :scale spring-scale}
      [:circleBufferGeometry {:args [0.6 64]}]
      [a-meshStandardMaterial {:color spring-color}]]]))

(defn- FloorMesh [props]
  "Transparent plane receiving shadows."
  [:mesh props
   [:planeBufferGeometry {:args [10 10 1 1]}]
   [:shadowMaterial {:transparent true :opacity 0.8}]])

(defn- <Canvas> []
  "Three reused animated characters changing animation when clicked on."
  [canvas {:shadows true
           :camera camera-config}
   ; Lights setup.
   [:ambientLight {:intensity 0.25}]
   [:directionalLight {:position [-5 5 5] 
                       :castShadow true
                       :shadow-map-width 1024 
                       :shadow-map-height 1024}]
   ; Animated models.
   [:group {:position [0 -1 0]}
    [suspense {:fallback nil}
      [:f> Model {:pose 4 :position [0 0 0]}]
      [:f> Model {:pose 1 :position [1 0 -1]}]
      [:f> Model {:pose 2 :position [-1 0 -1]}]]]
   ; Plane receiving shadows.
   [FloorMesh {:rotation [(- half-pi) 0 0]
               :position [0 -1 0]
               :receiveShadow true}]
   ; Update camera from mouse position.
   [:f> CameraMouseUpdate]])

(defn- app []
  (r/create-class {:reagent-render <Canvas>}))

;; ----------------------------------------------------------------------------
;; Initialize the app.

(defn ^:dev/after-load mount-root []
  (let [root-el (.getElementById js/document "root")]
    (rdom/render [app] root-el)))

(defn ^:export init! []
  (mount-root))
