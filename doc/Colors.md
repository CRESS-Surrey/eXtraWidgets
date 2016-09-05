Just like colors in NetLogo, colors used in the extension can be represented by either a number in the 0 to 140 range, an RGB list or an RGBA list. You can usually ignore this when working with the extension (or NetLogo, for that matter), but it can sometimes be useful to have a deeper understanding. If you haven't already done so, please read the [**Colors** section of the NetLogo programming guide](http://ccl.northwestern.edu/netlogo/docs/programming.html#colors).

One crucial thing to understand is that the set of colors expressible by numbers in NetLogo's 0 to 140 color range is a _subset_ of all possible colors expressible by RGB lists. Some colors are expressible by both a number and a list, but others are only expressible by a list.

Having two ways to represent colors is not ideal, but it is an acceptable trade-off. Colors-as-lists are more flexible, but colors-as-numbers are much easier to use. In most cases, this cohabitation is unproblematic. Problems arise, however, if you start trying to compare the two...

Let's say we create three red turtles:
```nlogo
observer> ca
observer> crt 3 [ set color red ]
observer> show [ color ] of turtles
observer: [15 15 15]
```

`red` in NetLogo is just a constant for the number 15, so we get colors-as-numbers for our turtles.

Now let's find out what the RGB list is for the color `red` using NetLogo's [`extract-rgb`](http://ccl.northwestern.edu/netlogo/docs/dictionary.html#extract-rgb) reporter:
```nlogo
observer> show extract-rgb red
observer: [215 50 41]
```
OK. Let's use that as the color of one of our turtles:
```nlogo
observer> ask one-of turtles [ set color [215 50 41] ]
observer> show [ color ] of turtles
observer: [15 15 [215 50 41]]
```
All turtles are still exactly the same color, and they are shown as such in the view, but one of them is now different from the others: its color is stored as a list. And if you ask NetLogo for the `red` turtles, it won't be included:
```nlogo
observer> show turtles with [ color = red ]
observer: (agentset, 2 turtles)
```
The above command shows the turtles for which the color red is stored as the number `15` and excludes the turtle for which the color red is stored as the `[215 50 41]` list.

To get around this problem, the eXtraWidgets extension treats colors _slightly_ differently: when trying to store a color as a list, it will use the equivalent number instead _if there is one_.

To see how this works, let's create three red sliders:
```nlogo
observer> xw:create-tab "t1"
observer> foreach ["s1" "s2" "s3"] [ [s] -> xw:create-slider s [ xw:set-color red ] ]
observer> show [ xw:color ] xw:of xw:sliders
observer: [15 15 15]
```
Now let's try to set the color of one of those to the list representation of the color red, `[215 50 41]`:
```nlogo
observer> xw:ask one-of xw:sliders [ xw:set-color [215 50 41] ]
observer> show [ xw:color ] xw:of xw:sliders
observer: [15 15 15]
```
Hey! Still all numbers! Which means we can do:
```nlogo
observer> show xw:sliders xw:with [ xw:color = red ]
observer: ["s1" "s2" "s3"]
```
Now let's try with a slightly different shade of red, `[215 50 40]` instead of `[215 50 41]`:
```nlogo
observer> xw:ask one-of xw:sliders [ xw:set-color [215 50 40] ]
observer> show [ xw:color ] xw:of xw:sliders
observer: [15 [215 50 40 255] 15]
```
NetLogo cannot represent this particular shade of red in its colors-as-numbers space, so we store it as a list instead. But what's this `255` number tacked on at the end? It's the "alpha" value: the opacity of the color. `255` means that the color is fully opaque and `0` would mean it's completely transparent. The extension always uses RGBA lists (red/green/blue/alpha), even if the color is fully opaque. Again, this helps with comparisons: the RGB list `[215 50 40]` and the RGBA list `[215 50 40 255]` represent the same color, and always storing colors-as-lists as RGBA lists makes that obvious.

One possible downside of our approach is that you cannot be sure that colors that you put in as lists will still be lists when you get them back. To get around this, you can use the following reporter:
```nlogo
to-report extract-rbga [ c ]
  if is-number? c [ report lput 255 extract-rgb c ]
  if is-list? c [
    if length c = 3 [ report lput 255 c ]
    if length c = 4 [ report c ]
    error (word "Expected a list of size 3 or 4 but got " c " instead.")
  ]
  error (word "Expected a number or a list but got " c " instead.")
end
```
We can use it to extract all colors from our previous example as RGBA lists:
```nlogo
observer> show [ extract-rbga xw:color ] xw:of xw:sliders
observer: [[215 50 41 255] [215 50 40 255] [215 50 41 255]]
```
