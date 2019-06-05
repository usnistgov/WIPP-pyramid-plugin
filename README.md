# README

## WIPP Pyramid Building plugin

WIPP Pyramid Building plugin based on [NIST WIPP Pyramid Building](https://github.com/usnistgov/WIPP-pyramid-building).

Reads multiple stitching vectors generated from [MIST](https://github.com/usnistgov/MIST) 
and creates one pyramid from tiled TIFF images per stitching vector.

## Docker distribution

This plugin is available on [DockerHub from the WIPP organization](https://hub.docker.com/r/wipp/wipp-pyramid-plugin)
```shell
docker pull wipp/wipp-pyramid-plugin
```

## Running the Docker container

```shell
docker run \
    -v "path/to/input/data/folder":/data/inputs \
    -v "path/to/output/folder":/data/outputs \
    wipp-pyramid-plugin \
    --inputImages /data/inputs/"inputCollectionTiledFolder"  
    --inputStitchingVector /data/inputs/"stitchingVectorFolder" 
    --blending overlay|max
    --output /data/outputs
 ```   


