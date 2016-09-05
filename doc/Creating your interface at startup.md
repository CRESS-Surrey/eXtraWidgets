In most cases, you will want your extra tabs to appear right away when a user loads your model. NetLogo offers a way to do that, through the [`startup`](http://ccl.northwestern.edu/netlogo/docs/dict/startup.html) procedure, which runs automatically when a model is opened. Just call your interface creation code from there.

If any global variables are used in the creation of your interface, you should remember to initialize them in `startup` (instead of `setup`, as is normally the case in NetLogo models).

Another caveat is that, as stated in [`startup`'s documentation](http://ccl.northwestern.edu/netlogo/docs/dict/startup.html):

> `startup` does not run when a model is run headless from the command line, or by parallel BehaviorSpace.

If you want `startup` to run from BehaviorSpace, you can put a call to it in the **Setup commands** section of your experiment definition (see [the documentation of BehaviorSpace](http://ccl.northwestern.edu/netlogo/docs/behaviorspace.html)).

Another thing to keep in mind is that, unlike for regular widgets, data entered in the extra widgets by the user is _not_ saved in the NetLogo file. When you load the model again, the extra tabs and widgets are recreated from scratch and their values are reset to the defaults (or to values you explicitly set in interface creation code). This is arguably a good thing: accidentally overwriting default widget values when saving a model is a common source of mistakes in NetLogo. On the other hand, if you _do_ want your model users to be able to change the default widgets values, you will need to provide them with a way to do so: either by showing them how to edit the code or by providing a save/load mechanism (perhaps via [`xw:export`](Primitives.md#xwexport)/[`xw:import`](Primitives.md#xwimport)).
