# :last_quarter_moon_with_face: cljs-r3f-projects :first_quarter_moon_with_face:

A collection of projects using a *__ClojureJS / React-Three-Fiber__* stack.

<!-- ----------------------------------------------------------------------- -->

## Quickstart

### Creating a new project

A basic or reagent clojureJS project can be created using respectively
`npx create-cljs-project my-project` or `lein new reagent-frontend my-project` but the versionning of the stack (*especially react-three/drei*) can become messed up.

So it is advised to start with a copy of the `template` project :
```bash
cp -R template/ my-project
```

The template has the following dependencies.

NPM :

- react & react-dom
- react-three (fiber, drei, postprocessing)
- three

Shadow-cljs :

- [reagent](https://github.com/reagent-project/reagent)
- [re-frame](https://github.com/Day8/re-frame)
- [devtools](https://github.com/binaryage/cljs-devtools)

### Running the app

To start a project, enter its directory and type :
```bash
npm run start
```

<!-- ----------------------------------------------------------------------- -->

## Known issues

`react-three/drei` requires the added package `regenerator-runtime` to be compiled as well as specific versions of other libraries for it to work.
Without `drei`, precise versionning might be less of an issue.

<!-- ----------------------------------------------------------------------- -->

## TODO

*Check devDependencies : webpack, eslint & babel, source-map-support.*

<!-- ----------------------------------------------------------------------- -->

## References

* [THREE.js](https://threejs.org/)
* [Reagent](https://github.com/reagent-project/reagent)
* [ReactJS cheatsheet](https://devhints.io/react)
* [ClojureJS cheatsheet](https://cljs.info/cheatsheet/)
* [react-three/drei](https://github.com/pmndrs/drei)
* [react-three/postprocessing](https://www.npmjs.com/package/@react-three/postprocessing)
* [R3F in practice](https://github.com/Domenicobrz/R3F-in-practice)
* [racing-game-cljs](https://github.com/ertugrulcetin/racing-game-cljs)
* [herfi](https://github.com/ertugrulcetin/herfi) (without Fiber).

<!-- ----------------------------------------------------------------------- -->
