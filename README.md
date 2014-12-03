![eXtraWidgets](doc/eXtraWidgets.png)

An extension for creating additional interface tabs in the [NetLogo](https://github.com/NetLogo/NetLogo) GUI and putting custom widgets on them.

### Installing

To install the eXtraWidgets extension, assuming that you already have [NetLogo](https://ccl.northwestern.edu/netlogo/download.shtml) installed, [**download** the extension](https://github.com/nicolaspayette/eXtraWidgets/releases/latest) and unzip it NetLogo's `extensions/` folder, like you would for any other [NetLogo extension](http://ccl.northwestern.edu/netlogo/docs/extensions.html).

The extension was developed and tested with NetLogo 5.1.0. There is a good chance that it will work with other versions of NetLogo >= 5.0, but we have not checked.

### Using

Unlike regular NetLogo widgets, the extra widgets and tabs are created through code.

A good place to put that code is in the  [`startup`](http://ccl.northwestern.edu/netlogo/docs/dictionary.html#startup) procedure, which runs automatically when your model is opened.

Here is an example demonstrating some of the things that you can do with the extension. To learn more about how to use the extension, take a look at the [wiki](https://github.com/nicolaspayette/eXtraWidgets/wiki), especially the [Primitives](https://github.com/nicolaspayette/eXtraWidgets/wiki/Primitives) and [Bundled Widget Kinds](https://github.com/nicolaspayette/eXtraWidgets/wiki/Bundled-Widget-Kinds) pages.

```
extensions [xw]

to startup
  xw:clear-all
  xw:create-tab "t1" [
    xw:set-title "Parameters"
  ]
  xw:create-slider "pop" [
    xw:set-label "Population size"
    xw:set-y 10
  ]
  xw:create-multi-chooser "shapes" [
    xw:set-label "Other shapes to use"
    xw:set-items ["airplane" "arrow" "bug" "butterfly" "turtle"]
    xw:set-selected-items n-of 2 xw:items ; two random shapes by default
    xw:set-y 70
    xw:set-height 150
  ]
  xw:create-button "setup" [
    xw:set-label "Push me!"
    xw:set-commands "setup"
    xw:set-y 230
  ]
  xw:ask xw:widgets [
    let c one-of base-colors
    xw:set-x 10
    xw:set-color c + 3
    xw:set-text-color c - 3
  ]
end
```

```
to setup
  clear-all
  create-turtles xw:get "pop" [
    set shape one-of fput "default" xw:get "shapes"
    fd 10
    xw:select-tab 1 ; select the regular interface tab
  ]
end
```

### Extending

This is what we believe to be the first "extensible extension" for NetLogo. New widgets kinds can be added to the extension just by dropping a JAR in a folder under `xw/widgets`. [Developer documentation](https://github.com/nicolaspayette/eXtraWidgets/wiki/Developing-Extra-Widget-Kinds) is scarce at the moment, so your best bet is probably to [take a look at the source code of existing widgets](https://github.com/nicolaspayette/eXtraWidgets/tree/master/xw/widgets). The [ScalaDoc for the API](https://nicolaspayette.github.io/eXtraWidgets/) is also a good ressource.

If you encounter any difficulties, either [open a new issue](https://github.com/nicolaspayette/eXtraWidgets/issues/new) or [ask a question on StackOverflow](http://stackoverflow.com/questions/tagged/netlogo).

### Licensing

The extension was developed by [Nicolas Payette](https://github.com/nicolaspayette) at the [Centre for Research in Social Simulation (CRESS)](http://cress.soc.surrey.ac.uk/), under the supervision of [Nigel Gilbert](http://cress.soc.surrey.ac.uk/web/people/director-cress) and [Jen Badham](http://cress.soc.surrey.ac.uk/web/people/researchers/76-jen-Badham). Its first use case was for a model that is part of the [TELL ME project](http://www.tellmeproject.eu/).

The extension is distributed under the [MIT License](LICENSE.txt).

[NetLogo](http://ccl.northwestern.edu) itself is authored by [Uri Wilensky](http://ccl.northwestern.edu/uri/) and [distributed under the GPL](http://ccl.northwestern.edu/netlogo/docs/copyright.html).

---
[![build status](https://magnum.travis-ci.com/nicolaspayette/eXtraWidgets.svg?token=SMhSmaihoAiHTwEPS2Xp)](https://magnum.travis-ci.com/nicolaspayette/eXtraWidgets)
