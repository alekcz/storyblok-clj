# storyblok-clj

A clojure library for working with [Storyblok](https://www.storyblok.com/)

## API
There is currently only one method in the API.

```clojure
(require '[storyblok-clj.core :as sb])

(def rich-text-map (-> response-map :story :content :rich-text-field))

(sb/richtext->html rich-text-map)

;You can use this as yogthos/Selmer filter like so

(require '[selmer.filters :as sf])

(sf/add-filter! :richtext (fn [richtext] [:safe (sb/richtext->html richtext)]))

```

## License

Copyright © 2019 Alexander Oloo

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
