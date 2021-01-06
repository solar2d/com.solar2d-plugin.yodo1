# Plugin template

To make it work for Solar2D plugins directory add your plugin content into the plugins directory. Then in revision, which is minimum requirement to run the plugin. Repository name must me `com.publisher.name-plugin.name` as it would be in build settings `["plugin.name"] = { publisherId = "com.publisher.name"}`.

For example, `mkdir -p plugins/2020.2600/<platform>a`.


Example platforms are:
* `android-kindle` will use `android` if not found
* `android` any android platform
* `macos` only desktop build
* `mac-sim` desktop build or simulator
* `win32` only desktop build
* `win32-sim` desktop build or simulator
* `web` for html5 builds
* `html5` same as `web`
* `iphone` iOS device
* `iphone-sim` iOS simulator
* `tvos` AppleTV device
* `tvos-sim` Apple TV simulator
* `lua` used if no other applicable platform found
