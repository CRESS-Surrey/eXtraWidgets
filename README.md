![eXtraWidgets](doc/eXtraWidgets.png)

An extension for creating additional interface tabs in the [NetLogo](https://github.com/NetLogo/NetLogo) GUI and putting custom widgets on them.

[[Download](https://github.com/CRESS-Surrey/eXtraWidgets/releases/latest)] [[Documentation](https://github.com/CRESS-Surrey/eXtraWidgets/wiki)]

### Installing

To install the eXtraWidgets extension, assuming that you already have [NetLogo 5.3.1](https://ccl.northwestern.edu/netlogo/download.shtml) installed, [**download** the extension](https://github.com/CRESS-Surrey/eXtraWidgets/releases/latest) and unzip it NetLogo's `extensions/` folder, like you would for any other [NetLogo extension](http://ccl.northwestern.edu/netlogo/docs/extensions.html).

(The extension has [not yet been upgraded to NetLogo 6.0](https://github.com/CRESS-Surrey/eXtraWidgets/issues/155).)

### Using

Unlike regular NetLogo widgets, the extra widgets and tabs are created through code. While this may sound daunting at first, it has some advantages:

- You can [create dynamic interfaces](https://github.com/CRESS-Surrey/eXtraWidgets/wiki/Creating-dynamic-interfaces) that change with the circumstances (a bit like with the [Goo extension](https://github.com/NetLogo/Goo-Extension/), but more powerful.)
- The extra widgets provided here are a bit more flexible than the regular NetLogo widgets: you have control over colors and font sizes, the widgets can be hidden or disabled, their labels are independent from their names, etc.
- It can provides widget kinds that are unavailable in regular NetLogo. The only one of those at the moment is the [multi-chooser widget](https://github.com/CRESS-Surrey/eXtraWidgets/wiki/Bundled-Widget-Kinds#multichooser), but [more could be added](https://github.com/CRESS-Surrey/eXtraWidgets/wiki/Developing-Extra-Widget-Kinds).
- Writing code allows precise positioning: no fiddling with the mouse to get your widgets aligned.

And besides, it's really not that hard: the extension works in such a way that [manipulating widgets is just like manipulating turtles](https://github.com/CRESS-Surrey/eXtraWidgets/wiki/Widget-contexts)!

To learn more about how to use the extension, take a look at the [documentation in the wiki](https://github.com/CRESS-Surrey/eXtraWidgets/wiki), where you will learn, amongst other things, how to quickly produce something like this:

![a screenshot of the result](doc/demo.png)

If you encounter any difficulties, [ask a question on StackOverflow](http://stackoverflow.com/questions/tagged/netlogo). If you believe you have found a bug (or would like to request a feature) [open a new issue](https://github.com/CRESS-Surrey/eXtraWidgets/issues/new).

Or we can chat: [![Join the chat at https://gitter.im/CRESS-Surrey/eXtraWidgets](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/CRESS-Surrey/eXtraWidgets?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

### Extending

We believe that eXtraWidgets is the first "extensible extension" for NetLogo. New widgets kinds can be added to the extension just by dropping a JAR in a folder under `xw/widgets`. [Developer documentation](https://github.com/CRESS-Surrey/eXtraWidgets/wiki/Developing-Extra-Widget-Kinds) is scarce at the moment, so your best bet is probably to [take a look at the source code of existing widgets](https://github.com/CRESS-Surrey/eXtraWidgets/tree/master/xw/widgets). The [ScalaDoc for the API](https://CRESS-Surrey.github.io/eXtraWidgets/) is also a good ressource.

Just like users, developers encountering any difficulties can either [open a new issue](https://github.com/CRESS-Surrey/eXtraWidgets/issues/new) or [ask a question on StackOverflow](http://stackoverflow.com/questions/tagged/netlogo).

### Credits and licensing

The extension was developed by [Nicolas Payette](https://github.com/nicolaspayette) at the [Centre for Research in Social Simulation (CRESS)](http://cress.soc.surrey.ac.uk/), under the supervision of [Nigel Gilbert](http://cress.soc.surrey.ac.uk/web/people/director-cress) and [Jen Badham](http://cress.soc.surrey.ac.uk/web/people/researchers/76-jen-Badham). Its first use case was for a model that is part of the [TELL ME project](http://www.tellmeproject.eu/).

[Bryan Head](https://github.com/qiemem), from the [NetLogo](https://github.com/NetLogo) team, made some significant contributions.

The extension is distributed under the [MIT License](LICENSE.txt).

[NetLogo](http://ccl.northwestern.edu) itself is authored by [Uri Wilensky](http://ccl.northwestern.edu/uri/) and [distributed under the GPL](http://ccl.northwestern.edu/netlogo/docs/copyright.html).

---
[![Build Status](https://travis-ci.org/CRESS-Surrey/eXtraWidgets.svg)](https://travis-ci.org/CRESS-Surrey/eXtraWidgets)
