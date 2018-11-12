# React Native: Native Intent Player

[![github home](http://img.shields.io/npm/v/react-native-intent-player.svg?style=flat)](https://www.npmjs.com/package/react-native-intent-player)
[![github home](https://img.shields.io/badge/gaetanozappi-react--native--intent--player-blue.svg?style=flat)](https://github.com/gaetanozappi/react-native-intent-player)
[![github issues](https://img.shields.io/github/issues/gaetanozappi/react-native-intent-player.svg?style=flat)](https://github.com/gaetanozappi/react-native-intent-player/issues)


![PNG](screenshot/react-native-intent-player.jpeg)

-   [Usage](#usage)
-   [License](#license)

### Android

Add `react-native-intent-player` to your `./android/settings.gradle` file as follows:

```diff
...
include ':app'
+ include ':react-native-intent-player'
+ project(':react-native-intent-player').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-intent-player/android/app')
```

Include it as dependency in `./android/app/build.gradle` file:

```diff
dependencies {
    ...
    compile "com.facebook.react:react-native:+"  // From node_modules
+   compile project(':react-native-intent-player')
}
```

Finally, you need to add the package within the `ReactInstanceManager` of your
MainActivity (`./android/app/src/main/java/your/bundle/MainActivity.java`):

```java
import com.reactlibrary.PlayerPackage;  // <---- import this one
...
@Override
protected List<ReactPackage> getPackages() {
    return Arrays.<ReactPackage>asList(
        new MainReactPackage(),
        new PlayerPackage()  // <---- add this line
    );
}
```

After that, you will need to recompile
your project with `react-native run-android`.

## Usage

```javascript
import Player from 'react-native-intent-player';
```

- API Way

```javascript
Player.play(url).then(a => {
  console.log(a);
}).catch(e => console.log(e));
```

## License
This library is provided under the Apache License.
