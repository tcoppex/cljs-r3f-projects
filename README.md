# :last_quarter_moon_with_face: cljs-r3f-projects :first_quarter_moon_with_face:

A collection of samples using a *__ClojureScript / React-Three-Fiber__* stack.

<!-- ----------------------------------------------------------------------- -->

## Quickstart

<!-- 
**_Preamble_**<br/>
A ClojureScript (or _Reagent_) project can be created using  `npx create-cljs-project my-project` (respectively `lein new reagent-frontend my-project`) but the versionning of the stack (*especially for react-three/drei*) can become messed up. -->

To create a new project, copy the `template` project :
```bash
cp -R template/ my-project
```

Then start it a project by entering its directory and running the `start` command :
```bash
cd my-project
npm start
```

Finally, you can clear its cache and built files by calling the clean command :
```bash
npm run clean
```

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

1. After compilation the browser may sometimes fails to render the app. If it happens try either to refresh your browser or to trigger
the live compilation until it renders again.
2. The package `regenerator-runtime` is required for `react-spring` and `react-three/drei` to work.
3. Upgrading packages versions can be tricky for build integrity, especially with `drei` `react`, `shadow-cljs` & `threejs`.

<!-- ----------------------------------------------------------------------- -->
<!-- 
## TODO

*Check devDependencies : webpack, eslint & babel, source-map-support.* -->

<!-- ----------------------------------------------------------------------- -->

## References & Documentations

* [ClojureJS cheatsheet](https://cljs.info/cheatsheet/)
* [ReactJS cheatsheet](https://devhints.io/react)
* [ShadowCLJS Users Guide](https://shadow-cljs.github.io/docs/UsersGuide.html)
* [Reagent](https://github.com/reagent-project/reagent)
* [Reagent React Features](https://github.com/reagent-project/reagent/blob/master/doc/ReactFeatures.md)
* [React Spring](https://react-spring.io/guides/r3f)
* [THREE.js](https://threejs.org/)
* [react-three/drei](https://github.com/pmndrs/drei) (and [v6.1.1](https://github.com/pmndrs/drei/tree/v6.1.1))
* [react-three/postprocessing](https://www.npmjs.com/package/@react-three/postprocessing)
* [react-three-fiber examples](https://docs.pmnd.rs/react-three-fiber/getting-started/examples#basic-examples=)
* [cljs-react-three-fiber](https://github.com/binaryage/cljs-react-three-fiber)
* [racing-game-cljs](https://github.com/ertugrulcetin/racing-game-cljs)
* [R3F in practice](https://github.com/Domenicobrz/R3F-in-practice)

<!-- ----------------------------------------------------------------------- -->

## License

This project is released under the *MIT License*.

<!-- ----------------------------------------------------------------------- -->
