# McOSM
A Minecraft OSM Shell Generator fully compatible with CubicChunks, Terra121 and the Build The Earth map projection

![](https://i.imgur.com/rreILw9.png)
![](https://i.imgur.com/GvREQrZ.jpg)

## Features

This mod works in CubicChunks or in normal world, and in the Terra121 earth world, And can be used to build in earth scale ontop of the Terra121 building outlines or build around the player. To use it use the ```/osm``` command.

Features:
* Making Building Shells.
    * Making Each House The Right Height Based On Its OSM Data Height.
    * Adding Windows On Buildings Based On How Many Floors They Have.
* Making Roads.
    * Creating Different Road Types Based On OSM Data.
    * Including Walkways, Stairs And ~~Corridors Inside Houses~~. (```TODO```)
* Making Trees Based On ASM Data
* Gui To Auto Download API Data And Build In One Command. (```TODO```)
* Load Data From A File Or From The API.
* Building In Cubic Or Non-Cubic Chunk Worlds.

## Usage

```
WHEN USING THE MOD IN A TERRA121 WORLD, TURN THE MAP DIRECTION TO UPRIGHT AND BTE PROJECTION!!
Also turn off roads and building outlines for the best result!
```


* Go on a map (like google maps), go to the top left and bottom right corners of the area part you want to download and copy the lat and lon coordinates from the urls, they should look like ```53.8375363,-9.351417``` where the first is lat and the second is lon.

* Then run the osm command like ```/osm [lat1] [lon1] [lat2] [lon2]```, it will open the builder gui

* Select the feautres you want on the left *(The [Global/Local] toggle changes the building placement from on the terra121 map location to locally around the player)*

* Change the Lat/Lon coord if the map doesn't vaguely line up *(THE MAP DOESN'T ENTIRELY LINE UP! As long as it isn't on the other side of the earth it should be fine)*

* Then click the ```Download``` Button and wait for it to download, If it wasn't successful it should say an error in the output box

* If it successfully downloaded, you can click on the ```Start``` button to start building *(you don't have to be in the chunks its building in for it to build)*

## Contributing

If you want to contribute:
* Fork the project
* Download the source
* Follow the instructions in the forge README
* Add Git to your IDE
* Once you are done making your edits, push the changes to your fork
* Then make a pull request

Or if you are too lazy to use git, you can message me your contribution on discord ```Bleach#0136``` or [join the discord server.](https://discord.gg/xPuZy3j)
