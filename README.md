# :last_quarter_moon_with_face: cljs-r3f-projects :first_quarter_moon_with_face:

A collection of projects using a *__ClojureScript / React-Three-Fiber__* stack.

<!-- ----------------------------------------------------------------------- -->

## Quickstart

<!-- 
**_Preamble_**<br/>
A ClojureScript (or _Reagent_) project can be created using  `npx create-cljs-project my-project` (respectively `lein new reagent-frontend my-project`) but the versionning of the stack (*especially for react-three/drei*) can become messed up. -->

To create a new project simply copy the `template` project :
```bash
cp -R template/ my-project
```

To start a project enter its directory and run the `start` command :
```bash
cd my-project
npm start
```

Finally, you can call `npm run clean` to clear the project cache and its built files.

<!-- ----------------------------------------------------------------------- -->

## Dependencies


This template use the following `npm` libraries :

- react & react-dom
- react-spring/three
- react-three (_fiber, drei, postprocessing_)
- three

and the following `Shadow-cljs` packages :

- [reagent](https://github.com/reagent-project/reagent)
- [re-frame](https://github.com/Day8/re-frame)
- [js-interop](https://github.com/applied-science/js-interop) 

<!-- ----------------------------------------------------------------------- -->
 
## Known issues

1. The package `regenerator-runtime` is required for `react-spring` and `react-three/drei` to work,
as well as specific versions of `react`, `shadow-cljs` & `threejs`.
2. After compilation the browser may sometimes fails to render the app. If it happens a workaround is to trigger
the live compilation until it renders again.

<!-- ----------------------------------------------------------------------- -->
<!-- 
## TODO

*Check devDependencies : webpack, eslint & babel, source-map-support.* -->

<!-- ----------------------------------------------------------------------- -->

## References

* [THREE.js](https://threejs.org/)
* [Reagent](https://github.com/reagent-project/reagent)
* [Reagent React Features](https://github.com/reagent-project/reagent/blob/master/doc/ReactFeatures.md)
* [ReactJS cheatsheet](https://devhints.io/react)
* [ClojureJS cheatsheet](https://cljs.info/cheatsheet/)
* [react-three/drei](https://github.com/pmndrs/drei) (and [v6.1.1](https://github.com/pmndrs/drei/tree/v6.1.1))
* [react-three/postprocessing](https://www.npmjs.com/package/@react-three/postprocessing)
* [react-three-fiber examples](https://docs.pmnd.rs/react-three-fiber/getting-started/examples#basic-examples=)
* [cljs-react-three-fiber](https://github.com/binaryage/cljs-react-three-fiber)
* [racing-game-cljs](https://github.com/ertugrulcetin/racing-game-cljs)
* [R3F in practice](https://github.com/Domenicobrz/R3F-in-practice)
* [React Spring](https://react-spring.io/guides/r3f)

<!-- ----------------------------------------------------------------------- -->

## License

This project is released under the *MIT License*.

<!-- ----------------------------------------------------------------------- -->
