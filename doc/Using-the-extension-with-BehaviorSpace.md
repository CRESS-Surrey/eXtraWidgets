You can use the extension in combination with [NetLogo's BehaviorSpace tool](http://ccl.northwestern.edu/netlogo/docs/behaviorspace.html). It may, however, require a bit of boilerplate code in order to do so. This is because, unlike "regular" NetLogo widgets, the extra widgets are not associated with global variables that you can ask BehaviorSpace to automatically vary. So if you want to use BehaviorSpace to vary some parameters, you will need to create global variables for these _in addition to_ the extra widgets that you may have defined. And you will need to somehow keep those synchronized.

Hopefully, a small example will make this clearer.

Let's suppose that you have a model with a _population_ parameter, that you set via a slider on an extra tab. Here is the code for creating the interface:
```
extensions [ xw ]

to startup
  xw:clear-all
  xw:create-tab "t1"
  xw:create-slider "population"
end
```

As long as you don't care about BehaviorSpace, you can just use `xw:get` on that slider to get your _population_ value at setup:
```
to setup
  clear-all
  create-turtles xw:get "population"
end
```

Now, what if you want to create a BehaviorSpace experiment that varies the population from, let's say, 1 to 100 turtles? Well, for that, you will need to create a `population` global _in addition to_ the slider that we've already created on the extra tab:
```
globals [ population ]
```

And then you can add `["population" 1 1 100]` as part of your experiment definition like you'd normally do. But that's not enough, because your `setup` procedure still uses `xw:get "population"` to get the value of your parameter, but NetLogo currently does not know that the `"population"` slider and the `population` global variable are supposed to be connected.

You can approach this problem from two different directions.

## The Globals-to-Widgets Direction

In this scenario, you override the value of the widget with the value of the global, but _you only do this if you are running a BehaviorSpace experiment_. (When you are not running BehaviorSpace all your globals are automatically set to `0`, and you most probably _don't_ want to set all your widgets to `0`.) You can check that BehaviorSpace is running using the [`behaviorspace-run-number`](http://ccl.northwestern.edu/netlogo/docs/dictionary.html#behaviorspace-run-number) reporter, which will report `0` when BehaviorSpace is not running:

```
to setup
  clear-all
  if behaviorspace-run-number != 0 [
    xw:set "population" population
  ]
  create-turtles xw:get "population"
end
```

## The Widgets-to-Globals Direction

In this other scenario, you initialize the values of the globals using values from the widgets, but you only do this if BehaviorSpace is _not_ running. (If it _is_ running, you don't want the widgets to interfere with the values of the global variables that BehaviorSpace is controlling.) The code is basically the reverse of what we have seen above:

```
to setup
  clear-all
  if behaviorspace-run-number = 0 [
    set population xw:get "population" 
  ]
  create-turtles population
end
```

## Pros and Cons

You may have your own reasons from preferring one direction over another, but as far as I can see, it comes down to a trade-off between speed and what I'd call "interactivity".

Both of those trade-offs only come into play if you're actually doing something with your parameters _during_ a run. To make this clear, let's add a trivial `go` procedure that gives each turtle a 1% chance of dying and replaces the dead ones. Here's what it would look like when using the Globals-to-Widgets direction:
```
to go
  ask turtles [ if random 100 < 1 [ die ] ]
  create-turtles (xw:get "population" - count turtles)
end
```

It uses `xw:get` on the slider widget to get the parameter value, which is _slower_ than accessing a global variable. On the other hand, it gives the user of your model the option of fiddling with the population slider while the model is running and have the model react to it in real time.

The Widgets-to-Globals version uses the global variable directly:
```
to go
  ask turtles [ if random 100 < 1 [ die ] ]
  create-turtles (population - count turtles)
end
```

It is a bit faster, but less flexible: since the value of the global variable has been set from the slider during `setup`, changes to the slider while the model is running are not taken into account. (To make this clear to your users, you may want to do `xw:ask xw:widgets [ xw:set-enabled? false ]` at the end of your `setup`.) If you don't care about that kind of interactivity, then Widgets-to-Globals is probably the way to go, especially if you are going to run a lot of experiments and care about speed.

# Reducing Boilerplate

You are going to have to create the globals. There is no way around that. But does it mean that you are also going to have to transfer their values one by one? In the example above, we had only the _population_ parameter, so it was no big deal. In the Widgets-to-Globals direction, it was just:

```
if behaviorspace-run-number = 0 [
  set population xw:get "population" 
]
```

But what if you have _a whole lot_ of parameters? (Which may very well be the case because why else would you want a bunch of extra tabs?) You could do something like this:

```
if behaviorspace-run-number = 0 [
  set alpha xw:get "alpha" 
  set beta xw:get "beta" 
  set gamma xw:get "gamma" 
  set delta xw:get "delta" 
  set epsilon xw:get "epsilon" 
  set zeta xw:get "zeta" 
  ; etc.
]
```

But here is another way:
```
to set-globals-from-widgets
  xw:ask xw:widgets [
    run (word "set " xw:key " " format-for-format xw:get xw:key)
  ]
end

to-report format-for-run [ value ]
  report ifelse-value is-string? value
    [ (word "\"" value "\"") ]
    [ ifelse-value is-list? value
      [ reduce word (sentence "[" (map format-for-run value) "]") ]
      [ value ]
    ]
end
```
The above code dynamically generates _strings_ like `"set alpha xw:get \"alpha\""` and _runs_ them using NetLogo's [`run`](http://ccl.northwestern.edu/netlogo/docs/dictionary.html#run) command. You'd call this in your `setup` procedure:
```
if behaviorspace-run-number = 0 [
  set-globals-from-widgets
]
```

The example code uses all `xw:widgets`, but you may want to filter them, e.g., by excluding notes: `xw:widgets with [ xw:kind != "NOTE" ]`.

To do it in the other direction, you'd have:
```
to set-widgets-from-globals
  xw:ask xw:widgets [
    run (word "xw:set \"" xw:key "\" " xw:key)
  ]
end
```
(Leaving `format-for-run` as it was.) You'd call it with:
```
if behaviorspace-run-number != 0 [
  set-widgets-from-globals
]
```