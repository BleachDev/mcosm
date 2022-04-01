# McOSM
A Minecraft OSM Shell Generator fully compatible with CubicChunks, Terra121 and the Build The Earth map projection

![](https://i.imgur.com/rreILw9.png)
![](https://i.imgur.com/GvREQrZ.jpg)

## Features

This mod works in CubicChunks or in normal world, and in the Terra121 earth world, And can be used to build in earth scale ontop of the Terra121 building outlines or build around the player. To use it use the ```/osm``` command.

Features:
* Making building shells.
    * Making each house the right height based on its OSM data height.
    * Adding windows on buildings based on how many floors they have.
    * Set building material based on OSM material data.
    * Set building material based on OSM building color data.
* Making roads.
    * Creating different road types based on OSM data.
    * Including walkways, stairs and ~~corridors inside houses.~~ (```TODO```)
    * ~~Automatically making tunnels.~~ (```TODO```)
    * ~~Adding pedestrian crossings to roads..~~ (```TODO```)
* ~~Making railroads.~~ (```TODO```)
    * ~~Building train platforms.~~ (```TODO```)
* Making vegetation
    * Making trees, tree rows and hedges based on OSM data.
* Gui to auto download API data and build in one command.
* Load data from a file or from the API.
* Building in Cubic or Non-Cubic chunk worlds.

## Commands

There are several commands to load map data into minecraft, here are all of them:

* ```/osm``` or ```/osm [lat1] [lon1] [lat2] [lon2]``` to open the download and build gui
* ```/osmshell``` to start building the building right under you without a gui
* ```/osmfile [path]``` to open data from a file and open the build gui
* ```/osmapi [lat1] [lon1] [lat2] [lon2]``` to copy a link of the api url to your clipboard to manually open it
* ```/osm stop``` to stop all current tasks
* ```/osmcoords <to/from> <x/lat> <z/lon>``` to convert Geo Coords to blocks

## Usage

```
WHEN USING THE MOD IN A TERRA121 WORLD, TURN THE MAP DIRECTION TO UPRIGHT AND BTE PROJECTION!!
Also turn off roads and building outlines for the best result!
```


* Go on a map (like google maps), go to the top left and bottom right corners of the area part you want to download and copy the latitude and longitude coordinates from the urls, they should look like ```53.8375363,-9.351417``` where the first is latitude and the second is longitude.

* Then run the ```/osm``` command which will open the builder gui

* Select the features you want on the left *(The [Global/Local] toggle changes the building placement from on the terra121 map location to locally around the player)*

* Then change the latitude/longitude coords to the area you want to fill (more than 10000-20000m² isn't recommended due to cubicchunks leaking insane memory and terra121 being slow at chunk loading)

* if the map doesn't vaguely line up *(THE MAP DOESN'T ENTIRELY LINE UP! As long as it isn't on the other side of the earth it should be fine)*

* Then click the ```Download``` Button and wait for it to download, If it wasn't successful it should say an error in the output box

* If it successfully downloaded, you can click on the ```Start``` button to start building *(you don't have to be in the chunks its building in for it to build)*

## Contributing

If you want to contribute:
* Fork and clone the project
* Follow the instructions in the README-FORGE file
* Once you are done making your edits, push the changes to your fork and make a pull request

Or if you are too lazy to use git, you can message me your contribution on discord ```Bleach#0136```
