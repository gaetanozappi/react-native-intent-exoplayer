# React Native: react-native-intent-exoplayer

[![GitHub package version](https://img.shields.io/github/package-json/v/gaetanozappi/react-native-intent-exoplayer.svg?style=flat&colorB=2b7cff)](https://github.com/gaetanozappi/react-native-intent-exoplayer)
[![github home](http://img.shields.io/npm/v/react-native-intent-exoplayer.svg?style=flat)](https://www.npmjs.com/package/react-native-intent-exoplayer)
![platforms](https://img.shields.io/badge/platforms-Android-brightgreen.svg?style=flat&colorB=191A17)
[![github home](https://img.shields.io/badge/gaetanozappi-react--native--intent--exoplayer-blue.svg?style=flat)](https://github.com/gaetanozappi/react-native-intent-exoplayer)
[![npm](https://img.shields.io/npm/dm/react-native-exoplayer-player.svg?style=flat&colorB=007ec6)](https://www.npmjs.com/package/react-native-intent-exoplayer)

[![github issues](https://img.shields.io/github/issues/gaetanozappi/react-native-intent-exoplayer.svg?style=flat)](https://github.com/gaetanozappi/react-native-intent-exoplayer/issues)
[![github closed issues](https://img.shields.io/github/issues-closed/gaetanozappi/react-native-intent-exoplayer.svg?style=flat&colorB=44cc11)](https://github.com/gaetanozappi/react-native-intent-exoplayer/issues?q=is%3Aissue+is%3Aclosed)
[![Issue Stats](https://img.shields.io/issuestats/i/github/gaetanozappi/react-native-intent-exoplayer.svg?style=flat&colorB=44cc11)](http://github.com/gaetanozappi/react-native-intent-exoplayer/issues)
[![github license](https://img.shields.io/github/license/gaetanozappi/react-native-intent-exoplayer.svg)]()

![GIF](screenshot/exoplayer-ui.gif)

-   [Usage](#-usage)
-   [License](#-license)

## 📖 Getting started

`$ npm install react-native-intent-exoplayer --save`

`$ react-native link react-native-intent-exoplayer`

#### Android

Add `react-native-intent-exoplayer` to your `./android/settings.gradle` file as follows:

```diff
...
include ':app'
+ include ':react-native-intent-exoplayer'
+ project(':react-native-intent-exoplayer').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-intent-exoplayer/android/app')
```

Include it as dependency in `./android/app/build.gradle` file:

```diff
dependencies {
    ...
    compile "com.facebook.react:react-native:+"  // From node_modules
+   compile project(':react-native-intent-exoplayer')
}
```

Finally, you need to add the package within the `ReactInstanceManager` of your
MainActivity (`./android/app/src/main/java/your/bundle/MainActivity.java`):

```java
import com.zappi.ui.exoplayer.PlayerPackage;  // <---- import this one
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

## 💻 Usage

```javascript
import React, { Component } from 'react';
import Player from 'react-native-intent-exoplayer';

type Props = {};
export default class App extends Component<Props> {
  
  constructor(props) {
    super(props);
    this.state = {
      title: 'Big Buck Bunny',
      url: 'https://www.w3schools.com/html/mov_bbb.mp4',
      sub: 'https://pastebin.com/raw/A0fDHxgK',
      subShow: true
    };
  }

  componentDidMount() {
    Player.play(this.state.title,this.state.url,this.state.sub,this.state.subShow)
      .then(a => {
        console.log(a);
      })
      .catch(e => console.log(e));
  }

  render() {
    return null;
  }
}
```

## 📜 License
This library is provided under the Apache License.
