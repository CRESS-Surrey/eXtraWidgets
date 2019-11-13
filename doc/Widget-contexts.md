The semantics of widget manipulation in the eXtraWidgets extension are very much inspired by the semantics of agent manipulation in NetLogo.

Just like you can [`ask`](http://ccl.northwestern.edu/netlogo/docs/dictionary.html#ask) turtles, patches and links to run commands, you can [`xw:ask`](Primitives#xwask) widgets to run commands. Just like you can use [`of`](http://ccl.northwestern.edu/netlogo/docs/dictionary.html#of) to extract information from an agent or agentset, you can use [`xw:of`](Primitives#xwof) to do the same with widgets. Finally, just like [`with`](http://ccl.northwestern.edu/netlogo/docs/dictionary.html#with) can be used to filter an agentset, [`xw:with`](Primitives#xwwith) can be used to filter a list of widget keys.

All three primitives use code blocks: a command block for [`xw:ask`](Primitives#xwask) and reporter blocks for [`xw:of`](Primitives#xwof) and [`xw:with`](Primitives#xwwith). The important thing to understand is that each of these code blocks introduce a new "widget context" and that the behavior of some other primitives is affected by the current context: they operate on the current widget. Other primitives that introduce a new widget context are widget constructors ([<tt>xw:create-<i>&lt;kind&gt;</i></tt>](Primitives#xwcreate-kind)).

The primitives affected by the widget context are the property getters ([<tt>xw:<i>&lt;property&gt;</i></tt>](Primitives#xwproperty)) and setters ([<tt>xw:set-<i>&lt;property&gt;</i></tt>](Primitives#xwset-property)).

Suppose that you create a tab with three sliders on it:

    to startup
      xw:clear-all
      xw:create-tab "t1"
      foreach ["alpha" "beta" "gamma"] [
        xw:create-slider ? [
          xw:set-color one-of base-colors
          xw:set-label xw:key
        ]
      ]
    end

Here, we are using NetLogo's [`foreach`](http://ccl.northwestern.edu/netlogo/docs/dictionary.html#foreach) command to avoid repeating ourselves three times. Inside the `foreach` block, the `?` character stands, in turn, for each member of the list `["alpha" "beta" "gamma"]`, so we create a new slider for each of these three keys. The `xw:create-slider` statement is followed by a command block that introduces a new widget context, where getters and setters will operate on the widget that is being created. The `xw:set-color one-of base-colors` statement will thus set the color of that particular widget to a random color amongst NetLogo's base colors. The `xw:set-label xw:key` statement uses both a setter (`xw:set-label`) and a getter (`xw:key`) to set the label of that widget to the key of that widget.

Widget contexts can also be nested. You may have noticed that the `startup` procedure shown above creates all three sliders on top of one another. You could, of course, remedy to this by setting the `y` property of `"beta"` and `"gamma"` to an appropriate absolute value, but it may be wiser to position them _relative_ to the widget above them. Here is how to do it for `"beta"`:

    xw:ask "beta" [ xw:set-y [ xw:y + xw:height + 10 ] xw:of "alpha" ]

Notice the two nested code blocks. The first one is introduced by `xw:ask` and puts us in the context of `"beta"`, so `xw:set-y` will set the `y` property of `"beta"`. The inner code block, introduced with `xw:of`, puts us in the context of `"alpha"` instead, so the `xw:y + xw:height + 10` expression reports a `y` coordinate that is 10 points below the bottom of `"alpha"` (and this is where we place the top of `"beta"`, using `xw:set-y`).

You would do the same thing for `"gamma"`, setting its `y` property relative to the bottom of `"beta"`:

    xw:ask "gamma" [ xw:set-y [ xw:y + xw:height + 10 ] xw:of "beta" ]

By the way, if you had many different sliders (or other widgets) that you wanted to lay out this way, you could generalize this pattern to something like:

    (foreach (but-last xw:sliders) (but-first xw:sliders) [
      xw:ask ?2 [ xw:set-y [ xw:y + xw:height + 10 ] xw:of ?1 ]
    ])

(Note that we've used the `xw:sliders` reporter to list all our sliders. It worked for us because [<tt>xw:<i>&lt;kinds&gt;</i></tt>](Primitives#xwkinds) reporters like `xw:sliders` always list widgets in alphabetical order and that happened to be the order we wanted. In a real-life model, though, it is unlikely to be the case, so you may have to create a list where you explicitly place your widgets in the order you want them.)