package uk.ac.surrey.xw.api

import java.awt.Color

import org.nlogo.theme.InterfaceColors

object ThemeColors {

  // "default" color properties are resolved at display time rather than stored
  // as fixed RGB values.  This lets xw widgets follow NetLogo's current theme
  // until the model explicitly sets a concrete color.
  def tabBackground: Color =
    InterfaceColors.interfaceBackground()

  def widgetBackground: Color =
    InterfaceColors.sliderBackground()

  def widgetText: Color =
    InterfaceColors.widgetText()

  // NetLogo 7 does not expose a single "generic widget border" colour because
  // its standard widgets paint their rounded outlines themselves.  tabBorder is
  // the neutral light/dark theme border colour; widgetHandle is for selection
  // handles and looks too dark for ordinary xw widget borders.
  def widgetBorder: Color =
    InterfaceColors.tabBorder()

  def buttonBackground: Color =
    InterfaceColors.buttonBackground()

  def buttonText: Color =
    InterfaceColors.buttonText()

  def noteBackground: Color =
    InterfaceColors.Transparent
}
