# geonames-clj

## Get started

Download http://download.geonames.org/export/dump/alternateNamesV2.zip ,
and put the `alternateNamesV2.txt` to the `data` directory.


Create index from the `alternateNamesV2.txt`.

```
% lein repl

user=> (require '[geonames.altnames :as al])
user=> (al/create-index)
```

When the lecene index has created, you can translate an address.

```
user=> (al/translate "Talca Province, Maule Region, Chile" "ja")
"タルカ県, Maule Region, チリ"
```
