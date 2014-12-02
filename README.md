![eXtraWidgets](doc/eXtraWidgets.png)

An extension for creating additional interface tabs in the [NetLogo](https://github.com/NetLogo/NetLogo) GUI and putting custom widgets on them.

[![build status](https://magnum.travis-ci.com/nicolaspayette/eXtraWidgets.svg?token=SMhSmaihoAiHTwEPS2Xp)](https://magnum.travis-ci.com/nicolaspayette/eXtraWidgets)

### An Extensible Extension

New widgets kinds can be added to the extension just by dropping a JAR in a folder under `xw/widgets`. [Developer documentation](https://github.com/nicolaspayette/eXtraWidgets/wiki/Developing-Extra-Widget-Kinds) is scarce at the moment, so your best bet is probably to [take a look at the source code of existing widgets](https://github.com/nicolaspayette/eXtraWidgets/tree/master/xw/widgets). The [ScalaDoc for the API](https://nicolaspayette.github.io/eXtraWidgets/) is also a good ressource.

If you encounter any difficulties, please either [open a new issue](https://github.com/nicolaspayette/eXtraWidgets/issues/new) or [ask a question on StackOverflow](http://stackoverflow.com/questions/tagged/netlogo).

### NetLogo Version Requirements

The extension has been developed and tested with NetLogo 5.1. There is a good chance that it will work with other versions of NetLogo >= 5.0, but we have not checked.
