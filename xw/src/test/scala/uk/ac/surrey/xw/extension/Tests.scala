package uk.ac.surrey.xw.extension

import org.nlogo.headless.TestLanguage

class Tests extends TestLanguage(Seq(new java.io.File("xw/tests.txt").getCanonicalFile))
