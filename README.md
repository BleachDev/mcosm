# McOSM
A Minecraft OSM Generator fully compatible with CubicChunks, Terra121 and the Build The Earth map projection

![](https://i.imgur.com/rreILw9.png)
![](https://i.imgur.com/GvREQrZ.jpg)

## Usage

```
WHEN USING THE MOD IN A TERRA121 WORLD, TURN OFF ROADS!
Or the old roads will overwrite the new ones, and potentially mess up buildings.
```

There isn't an automatic api grabber yet, but you can still test the generator out from minecraft:
* Go on a map (like google maps), go to the top left and bottom right coners of the area part you want to download and copy the lat and lon coordinates from the urls, they should look like ```53.8375363,-9.351417``` where the first is lat and the second is lon.

* Then run the osmapi command like ```/osmapi [lat1] [lon1] [lat2] [lon2]```, it will copy a url on your clipboard, paste that in your browser

* Then save the json that is on the page you pasted somewhere on your computer

* Then run the osm command like ```/osm [path] [local:true/false]``` where path is the json file path and local is an optional parameter to specify if its build from x0 z0 (global) or around the player (local), it will then generate the building ontop where they should be on the earth if you are using the Build The Earth map projection

## Contributing

If you want to contribute:
* Fork the project
* Download the source
* Follow the instructions in the forge README
* Add Git to your IDE
* Once you are done making your edits, push the changes to your fork
* Then make a pull request

Or if you are too lazy to use git, you can message me your contribution on discord ```Bleach#0136``` or [join the discord server.](https://discord.gg/xPuZy3j)
