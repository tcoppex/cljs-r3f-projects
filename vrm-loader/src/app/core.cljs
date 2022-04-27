(ns app.core
  (:require
    ["regenerator-runtime"] ; required for react-spring & drei.
    
    ["react" :refer [useState useEffect Suspense]]
    ["@react-three/fiber" :refer [Canvas useFrame]]
    ["@react-three/drei" :refer [useGLTF useAnimations PerspectiveCamera OrbitControls Backdrop]]
    ["@pixiv/three-vrm" :as THREE-VRM]
    ["three" :as THREE]
    
    [applied-science.js-interop :as j]
    [reagent.core :as r]
    [reagent.dom :as rdom]))

;; ----------------------------------------------------------------------------
;; Library wrappers.

(defonce suspense (r/adapt-react-class Suspense))
(defonce canvas (r/adapt-react-class Canvas))
(defonce perspective-camera (r/adapt-react-class PerspectiveCamera))
(defonce orbit-controls (r/adapt-react-class OrbitControls))
(defonce backdrop (r/adapt-react-class Backdrop))

(defn- get-elapsed-time [state]
  (j/call-in state [:clock :getElapsedTime]))

;; ----------------------------------------------------------------------------
;; Maths Utils.

(defonce pi js/Math.PI)
(defonce two-pi (* js/Math.PI 2))
(defonce half-pi (/ js/Math.PI 2))
(defonce quarter-pi (/ js/Math.PI 4))

(defn smoothstep 
  [a b t] 
  (.smoothstep THREE/MathUtils t a b))

(defn sine-cycle
  [x]
  (Math/sin (* pi x)))

;; ----------------------------------------------------------------------------
;; VRM Utils.

(defonce vrm-loaders {:main THREE-VRM/VRM
                      :debug THREE-VRM/VRMDebug})

(def vrm-loader (:main vrm-loaders))

(defn- get-vrm-bone-name 
  "Return a bone name from its VRM key."
  [key]
  (j/get-in THREE-VRM [:VRMSchema :HumanoidBoneName key]))

(defn- get-vrm-bone-node
  "Return a VRM bone node either from its VRM name or key."
  [vrm bone-name]
  (if (string? bone-name)
    (j/call-in vrm [:humanoid :getBoneNode] bone-name)
    (get-vrm-bone-node vrm (get-vrm-bone-name bone-name))))

(defn- reset-vrm-bone-manager!
  [vrm]
  (j/call-in vrm [:springBoneManager :reset]))

(defn update-vrm!
  [vrm delta-time]
  (j/call vrm :update delta-time))

(defn- rotate-node-axis!
  [node axis-key angle]
  (j/assoc-in! node [:rotation axis-key] angle))

(defn- rotate-joint!
  "Rotate a specific joint from a VRM file."
  [vrm bone-id axe angle]
  (rotate-node-axis! (get-vrm-bone-node vrm bone-id) axe angle))

(defn- set-vrm-blendshape!
  [vrm key value]
  (let [bs (j/get-in THREE-VRM [:VRMSchema :BlendShapePresetName key])]
   (j/call-in vrm [:blendShapeProxy :setValue] bs value)))

;; ----------------------------------------------------------------------------

(defn- postprocess-vrm!
  "Turn the character's hip to face the Z direction."
  [vrm]
  (rotate-joint! vrm :Hips :y pi)
  (reset-vrm-bone-manager! vrm))

(defn- setup-vrm!
  [gltf scene save-vrm]
  ; (.removeUnnecessaryVertices THREE-VRM/VRMUtils scene) ;; FIXME (not working)
  (.removeUnnecessaryJoints THREE-VRM/VRMUtils scene)
  (.then (.from vrm-loader gltf)
         (fn [vrm]
           (save-vrm vrm)
           (postprocess-vrm! vrm))))

(defn- animate-vrm!
  [vrm elapsed-time delta-time]
  (when vrm
    ; (println elapsed-time)
    (let [angle (* quarter-pi (sine-cycle elapsed-time))
          _ (sine-cycle (* 0.75 elapsed-time))
          blink (Math/pow (smoothstep -1 1 _) 1500)]
      ;; Joints.
      (mapv #(rotate-joint! vrm %1 %2 angle) 
           [:Neck :LeftUpperArm :RightUpperArm] 
           [:y :z :x])
      ;; Blendshapes.
      ; (set-vrm-blendshape! vrm :A (+ 0.5 bs))
      (mapv #(set-vrm-blendshape! vrm % blink) [:BlinkL :BlinkR]))
    (update-vrm! vrm delta-time)))

;; ----------------------------------------------------------------------------
;; App configurations.

(defonce assets-path "/assets")

(def asset-uri (str assets-path "/three-vrm-girl.vrm"))

(def camera-config {:makeDefault true 
                    :position [0 1 5] 
                    :near 0.1 
                    :far 100.0 
                    :fov 30})

(def grid-config {:size 10
                  :division 10
                  :axis-color "darksalmon"
                  :color "dimgray"})

;; ----------------------------------------------------------------------------
;; Components.

(defn- vrm-character
  []
  (let [gltf (useGLTF asset-uri)
        {:keys [scene]} (j/lookup gltf)
        [vrm set-vrm] (useState nil)]
    ;; Post-Process the vrm file once loaded.
    (useEffect #(setup-vrm! gltf scene set-vrm) 
               #js [gltf scene])
    ;; Animate the mesh.
    (useFrame (fn [state delta] 
                (animate-vrm! vrm (get-elapsed-time state) delta)))
    
    ;; TODO
    ;; Change its material to use flat colors, which works when useEffect is disabled.
    [:group
     [:primitive {:object scene}]]))

(defn- <Canvas>
  []
  [suspense {:fallback nil}
   [canvas {:shadows true}
    [perspective-camera camera-config]
    [orbit-controls {:target [0 1 0]
                     :sceenSpacePanning true}]
    
    [:ambientLight {:intensity 1.0}]
    [:directionalLight {:position [1 1 1] :intensity 0.25}]
    
    ; [:group {:position [0 0 -2]
    ;          :scale [5 2 1]
    ;          :receiveShadow true}
    ;  [backdrop {:floor 2.0
    ;             :segments 10}
    ;   [:meshStandardMaterial {:color "sienna"}]]]
    
    [:f> vrm-character]
    
    [:axesHelper {:position [0 0.001 0] :args [5]}]
    [:gridHelper {:args (vals grid-config)}]]])

(defn- app []
  (r/create-class {:reagent-render <Canvas>}))

;; ----------------------------------------------------------------------------
;; Initialize the app.

(defn ^:dev/after-load mount-root []
  (let [root-el (.getElementById js/document "root")]
    (rdom/render [app] root-el)))

(defn ^:export init! []
  (mount-root))
