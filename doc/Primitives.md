[`xw:ask`](#xwask),
[`xw:clear-all`](#xwclear-all),
[<tt>xw:create-<i>&lt;kind&gt;</i></tt>](#xwcreate-kind),
[`xw:export`](#xwexport),
[`xw:get`](#xwget),
[`xw:import`](#xwimport),
[<tt>xw:<i>&lt;kinds&gt;</i></tt>](#xwkinds),
[`xw:of`](#xwof),
[<tt>xw:on-<i>&lt;property&gt;</i>-change</tt>](#xwon-property-change),
[<tt>xw:<i>&lt;property&gt;</i></tt>](#xwproperty),
[`xw:remove`](#xwremove),
[`xw:select-tab`](#xwselect-tab),
[`xw:set`](#xwset),
[<tt>xw:set-<i>&lt;property&gt;</i></tt>](#xwset-property),
[`xw:widgets`](#xwwidgets),
[`xw:with`](#xwwith)

---
## xw:ask

- <tt>xw:ask <i>widget-key</i> [ <i>commands</i> ]</tt>
- <tt>xw:ask <i>widget-keys</i> [ <i>commands</i> ]</tt>

Asks one widget (identified by <tt><i>widget-key</i></tt>) or many widgets (identified by a list of <tt><i>widget-keys</i></tt>) to run some <tt><i>commands</i></tt>. The commands are run in [the context](Widget-contexts) of each given widget.

---
## xw:clear-all

- `xw:clear-all`

Removes all tabs and all widgets on them. As it stands now, this is the equivalent of `foreach xw:tabs xw:remove`, but `xw:clear-all` is shorter and makes the intent clearer. It could also potentially do more in the future (if containers other than tabs were added to the extension, for example).

Note that NetLogo's [`clear-all`](http://ccl.northwestern.edu/netlogo/docs/dictionary.html#clear-all) command does _not_ clear the extra widgets and tabs.

---
## xw:create-<i>&lt;kind&gt;</i>

- <tt>xw:create-<i>&lt;kind&gt;</i> <i>widget-key</i></tt>
- <tt>xw:create-<i>&lt;kind&gt;</i> <i>widget-key</i> [ <i>commands</i> ]</tt>

Creates a widget of the specified kind. It can be one [the widget kinds bundled with the extension](Kinds) or [a widget kind potentially developed by a third party](Developing-Extra-Widget-Kinds).

The <tt><i>widget-key</i></tt> must be unique across all widget and tab keys. (You should usually call [`xw:clear-all`](#xwclear-all) before creating your interface.)

If <tt><i>commands</i></tt> are provided, they will be run in [the context](Widget-contexts) of the newly created widget.

By default, all new widgets are placed on _the last created tab_. If you want the widget to appear on a different tab, you need to explicitly set its `tab` property:

    xw:create-tab "t1"
    xw:create-tab "t2"         ; "t2" is the last created tab
    xw:create-slider "s1" [
      xw:set-tab "t1"          ; without this line, "s1" would go on "t2"
    ]

---
## xw:export

- <tt>xw:export <i>filename</i></tt>

Exports the all properties of all extra tabs and widgets to a [JSON](http://www.json.org/) formatted text file that can later be re-imported with [`xw:import`](#xwimport).

Using `xw:export` and looking at the content of the file is a good way to see all your widgets and the value of their properties, especially if you use a JSON pretty printer (like [this one](http://jsonprettyprint.com/), for example).

Note that some types of values (agents, anonymous procedures and extension objects, namely) can only be exported as strings. If you try to [import](#xwimport) them back, you won't get the original object back. As of now, items in a chooser of multi-chooser are the only things that can potentially cause this problem.

---
## xw:get

- <tt>xw:get <i>widget-key</i></tt>

Reports the value of the default property for the widget identified by the string <tt><i>widget-key</i></tt>.

The default property can be different (or even undefined) from one widget kind to another, but it is usually the one that you would consider to be the "value" of the widget, i.e. `value` for a slider, `selected-item` for a chooser, `selected?` for a checkbox, etc. Please refer to [the widget kinds bundled with the extension](Kinds) to learn about their default properties.

Note that, unlike the [<tt>xw:<i>&lt;property&gt;</i></tt>](#xwproperty) getter primitives, `xw:get` does _not_ operate in a widget context (which is why you need to specify a <tt><i>widget-key</i></tt>).

---
## xw:import

- <tt>xw:import <i>filename</i></tt>

Loads the content of an interface saved with [`xw:export`](#xwexport). You can use `xw:import` to import multiple interfaces in the same model. You just need to make sure that all widget and tab keys are unique.

Example:
```
xw:create-tab "t1"
xw:export "t1.json"
xw:clear-all
xw:create-tab "t2"
xw:export "t2.json"
xw:clear-all
xw:import "t1.json"
xw:import "t2.json"
print xw:tabs ; will print: ["t1" "t2"]
```

---
## xw:<i>&lt;kinds&gt;</i>

- <tt>xw:<i>&lt;kinds&gt;</i></tt>

Reports the list of keys of all widgets of the specified kind (where <tt><i>&lt;kinds&gt;</i></tt> is the plural form of the desired kind, e.g.: `xw:tabs`, `xw:buttons`, `xw:checkboxes`, etc.)

Widgets keys are listed in alphabetical order.

---
## xw:of

- <tt>[ <i>reporter</i> ] xw:of <i>widget-key</i></tt>
- <tt>[ <i>reporter</i> ] xw:of <i>widget-keys</i></tt>

Reports the value of the <tt><i>reporter</i></tt> for one widget (identified by <tt><i>widget-key</i></tt>), or a list of values for many widgets (identified by a list of <tt><i>widget-keys</i></tt>). The <tt><i>reporter</i></tt> is run in [the context](Widget-contexts) of each given widget.

---
## xw:on-<i>&lt;property&gt;</i>-change

- <tt>xw:on-<i>&lt;property&gt;</i>-change <i>command</i></tt>

Registers an event listener on <i>&lt;property&gt;</i>: each time the value of property changes, _command_ gets executed, with the new value as an argument to the command. (Note that <tt>xw:<i>&lt;property&gt;</i></tt> may have changed again by the time the event listener fires, thus the need for the command argument.) The command is executed in the context of the widget whose property just changed.

Example:
```
xw:create-chooser "c1" [
  xw:set-items base-colors
  xw:on-selected-item-change [ xw:set-color ? ]
]
```

---
## xw:<i>&lt;property&gt;</i>

- <tt>xw:<i>&lt;property&gt;</i></tt>

Reports the value of <i>&lt;property&gt;</i> for the current [widget context](Widget-contexts).

You will typically use this in the reporter block of [`xw:of`](#xwof):

    print [ xw:color ] xw:of xw:widgets

or [`xw:with`](#xwwith):

    print xw:widgets xw:with [ xw:color = red ]

but sometimes also in the command blocks of [`xw:ask`](#xwask):

    xw:create-slider "population" [ xw:set-label xw:key ]

or [<tt>xw:create-<i>&lt;kind&gt;</i></tt>](#xwcreate-kind):

    xw:ask xw:widgets [ xw:set-width xw:height * 5 ]

It is an error to use <tt>xw:<i>&lt;property&gt;</i></tt> in the context of a widget that does not support this particular property. Please refer to [[Kinds]] and [[Properties]] pages to learn all about the properties supported by different widget kinds.

---
## xw:remove

- <tt>xw:remove <i>widget-key</i></tt>

Removes the tab or widget identified by <tt><i>widget-key</i></tt>.

If `xw:remove` is used on a tab, _all the widgets on that tab are removed as well_.

If you want to remove multiple widgets at once, you can use this primitive in combination with [`foreach`](http://ccl.northwestern.edu/netlogo/docs/dictionary.html#foreach). For example, to remove all widgets, but keep the tabs:

    foreach xw:widgets xw:remove

---
## xw:select-tab

- <tt>xw:select-tab <i>tab-key</i></tt>
- <tt>xw:select-tab <i>tab-index</i></tt>

Gives the focus to the specified tab (just like if the user had clicked on the title of the tab).

If you want to select one of the extra tabs that you have created, you can use the tab's `key`:
```
xw:create-tab "my-tab"
xw:select-tab "my-tab"
```

If you want to select regular NetLogo tabs, you can do it by using their "tab index", which is basically the order that they appear on the screen, starting at 1. You can look at NetLogo's **Tabs** menu to see the index that you need to use: it is the same as the one you use in combination with the <kbd>Ctrl</kbd> key to active a tab. For example, before creating an extra tab: <kbd>Ctrl</kbd>+<kbd>1</kbd> for the Interface tab, <kbd>Ctrl</kbd>+<kbd>2</kbd> for the Info tab and <kbd>Ctrl</kbd>+<kbd>3</kbd> for the Code tab. Keep in mind that the extension inserts extra tabs after the Interface tab, but _before_ the info and code tabs, so these numbers can change.

A common use would be to select an extra tab on startup and go back to the main interface tab on setup:
```
extensions [xw]

to startup
  xw:create-tab "my-tab"
  xw:create-button "setup" [
    xw:set-label "Setup"
    xw:set-commands "setup"
  ]
  ; normally, you'd create a bunch of other widgets...
  xw:select-tab 2 ; select "my-tab"
end

to setup
  clear-all
  ; here, you'd initialize your model using values from extra widgets
  xw:select-tab 1 ; go back to main interface
end
```

---
## xw:set

- <tt>xw:set <i>widget-key value</i></tt>

Sets the value of the default property for the widget identified by the string <tt><i>widget-key</i></tt> to <tt><i>value</i></tt>.

The default property can be different (or even undefined) from one widget kind to another, but it is usually the one that you would consider to be the "value" of the widget, i.e. `value` for a slider, `selected-item` for a chooser, `selected?` for a checkbox, etc. Please refer to [the widget kinds bundled with the extension](Kinds) to learn about their default properties.

Note that, unlike the [<tt>xw:set-<i>&lt;property&gt;</i></tt>](#xwset-property) setter primitives, `xw:set` does _not_ operate in a widget context (which is why you need to specify a <tt><i>widget-key</i></tt>).

---
## xw:set-<i>&lt;property&gt;</i>

- <tt>xw:set-<i>&lt;property&gt; value</i></tt>

Sets the value of <i>&lt;property&gt;</i></tt> for the current [widget context](Widget-contexts).

You will typically use this the command blocks of [<tt>xw:create-<i>&lt;kind&gt;</i></tt>](#xwcreate-kind):

    xw:create-tab "black-tab" [
      xw:set-title "Black Tab"
      xw:set-color black
    ]
    foreach n-values 10 [ ? ] [
      xw:create-note (word ?) [
        xw:set-text (word ?)
        xw:set-y 10 + ? * 35
        xw:set-opaque? true
      ]
    ]

Or [`xw:ask`](#xwask):

    xw:ask xw:notes [
      xw:set-color one-of base-colors - 4
      xw:set-font-color xw:color + 6
    ]

It is an error to use <tt>xw:set-<i>&lt;property&gt;</i></tt> in the context of a widget that does not support this particular property. Please refer to [[Kinds]] and [[Properties]] pages to learn all about the properties supported by different widget kinds.

---
## xw:widgets

- `xw:widgets`

Similar to [<tt>xw:<i>&lt;kinds&gt;</i></tt>](#xwkinds), but reports a list of _all_ widget keys (i.e., excluding tabs).

Widgets keys are listed in alphabetical order.

---
## xw:with

- <tt><i>widget-keys</i> xw:with [ <i>reporter</i> ]</tt>

Reports the filtered list of <tt><i>widget-keys</i></tt> for which <tt><i>reporter</i></tt> is `true` when run in [the context](Widget-contexts) of each given widget.

Example:
```
xw:create-tab "t1"
; create 10 sliders:
foreach n-values 10 [ ? ] xw:create-slider
; make 5 of them red:
xw:ask n-of 5 xw:sliders [ xw:set-color red ]
; print the keys of the 5 red sliders:
print xw:sliders xw:with [ xw:color = red ]
```
