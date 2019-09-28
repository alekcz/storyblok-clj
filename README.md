# storyblok-clj

A clojure library for working with [Storyblok](https://www.storyblok.com/)

## API
There is currently only one method in the API.

```
(require '[storyblok-clj.core :as sb])

(def rich-text-map (-> response-map :story :content :rich-text-field))

(sb/rich-text-to-html rich-text-map)

```

## License

Copyright © 2019 Alexander Oloo

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
