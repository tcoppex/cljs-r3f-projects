;;
;; This project is based off "GLTF Animations re-used" from Paul Henschel.
;;   cf. https://codesandbox.io/s/gltf-animations-re-used-k8phr
;;
;; ----------------------------------------------------------------------------

(ns app.core
  (:require
    ["regenerator-runtime"] ; required for react-spring & drei.
    ["react" :refer [useRef useEffect useMemo useState Suspense]]
    ["@react-spring/three" :refer [a useSpring]]
    ["@react-three/fiber" :refer [Canvas useFrame useGraph]]
    ["@react-three/drei" :refer [useGLTF useTexture useAnimations useCursor]]
    ["three-stdlib" :refer [SkeletonUtils]]
    ["three" :as THREE]
    [applied-science.js-interop :as j]
    [reagent.core :as r]
    [reagent.dom :as rdom]))

;; ----------------------------------------------------------------------------
;; Redefines.

;; React
(defonce suspense (r/adapt-react-class Suspense))

;; Fiber.
(defonce canvas (r/adapt-react-class Canvas))

;; React-Spring.
(defn- adapt-animated-class [key] 
  (r/adapt-react-class (j/get a key)))
(defonce a-mesh (adapt-animated-class :mesh))
(defonce a-meshStandardMaterial (adapt-animated-class :meshStandardMaterial))

;; ----------------------------------------------------------------------------

(defn skeleton-utils-clone [o]
  (.clone SkeletonUtils o))

; (defn use-memo [func o]
;   (useMemo #(func o) #js [o]))

(defn js-gets [o & keys] 
  "Retrieve multiples keys from a js object."
  (map #(j/get o %) keys))

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

(defn- get-mouse-coord [axis]
  (j/get-in state [:mouse axis]))

(defn- CameraMouseUpdate []
  "Update the camera based on the mouse screen position."
  (useFrame (fn [state] 
    (let [newpos #(+ 1.5 (/ (get-mouse-coord %) 4))]
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
        [scene animations] (js-gets _ :scene :animations)
        
        ;; Fetch texture separately & disable its vertical flip.
        texture (useTexture (:texture stacy-assets))
        _ (j/assoc! texture .flipY false)
                                
        ;; Skinned meshes cannot be re-used in threejs without cloning them.
        clone (useMemo #(skeleton-utils-clone scene) #js [scene])
        
        ;; Creates two flat object collections for nodes and materials.
        nodes (j/get (useGraph clone) :nodes)
        geometry (j/get-in nodes [:stacy :geometry])
        skeleton (j/get-in nodes [:stacy :skeleton])
        
        ;; Extract animations actions.
        _ (useAnimations animations)
        [ref names actions] (js-gets _ :ref :names :actions)
        
        ;; Callback to change current animation.
        anim-count (j/get names :length)
        next-anim-index (rem (inc animation-index) anim-count)
        change-anim! #(set-animation-index next-anim-index)
        
        ;; Spring to animate the selection halo.
        _ (useSpring (if hovered #js {:scale 1.2 :color "tomato"} 
                                 #js {:scale 0.8 :color "bisque"}))
        [color scale] (js-gets _ :color :scale)]
    ;; Change cursor when pointer is on the model.
    (useCursor hovered)
    
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
                     :scale scale}
      [:circleBufferGeometry {:args [0.6 64]}]
      [a-meshStandardMaterial {:color color}]]]))

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
