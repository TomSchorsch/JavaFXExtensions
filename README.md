# JavaFXExtensions

<b>Contains a few stand alone control extensions:</b><br>
  - <b>FileChoiceBox</b> - maintains a history of previously chosen files so user can choose a new file from a filepicker OR select a previously chosen file from the pull-down.
  - <b>MappedRadioButtons</b> - creates a bunch of radio buttons which display strings (the mapped-to value) but when selected return the mapped-from value.  2 features include indented sub-radio buttons and multiple columns of radio buttons.   
  - <b>Config</b> - config is a helper utility for the above.  It enables the "history state" of the FileChoiceBox control and the selected items in the MappedRadioButtons control to be saved between successive executions of your program.

<b>Contains a LineChart Plot Editor</b>
The default JavaFX LineChart had some limitations and some of those limitations are addressed by this project.

<b>New LineChart Features</b>
  - 17 symbols:<br>
    - diamond, circle, square, 
    - h_diamond, v_diamond, h_ellipse, v_ellipse, h_rectangle, v_rectangle,
    - up_triangle, down_triangle, right_triangle, left_triangle,
    - star5, cross, plus, checkmark
  
There are 3 symbol styles for each symbol:
  filled, transparent, and white-filled
  
  A Chart title and sub-title can be at the top only.
  
  A legend that matches the Symbol and color of the data series can be at the bottom, left, or right.
  
 Typical plot features can be programmatically set and displayed, then the displayed chart can be edited in typical ways:
  - Chart title and sub title text and font sizes
  - Axis title font size, tic mark font size and rotation
  - Series Symbol, Color, symbol style, size
  - Series Line Color, width
  - Symbols and lines can visible or not as a whole or by series

Plots can be zoomed via typical mouse movements.

Plots can have classification markings (currently only SECRET) or not in upper-left, lower-right locations

Plots can have additional plot info saved in the lower left corner

Plots can be saved via a contextRight-Click menu

Pop-up editors are invoked by right-clicking on the chart or an item (data series) in the legend.

CallOuts (Annotations) can be added to the plot programaticaly and edited as well.
  - A CallOut consists of a line starting at a datapoint value and extending in some direction for some length with some text at the end that can either be horizontal or in-line with the CallOut line angle.
  - A plot can have several different "series" of callouts each with unique settings for the CallOuts.
  - All aspects of the CallOuts can be edited as a series or individually 
  
<b>Additional features:</b>
- Has a Seconds Since Midnight (SSM) axis which turns a double axis value into a HH:MM:SS.sss value (How much of HH:MM:SS.sss that is displayed depends on the zoomlevel).
- Has a hover capability for data items that will display additional info when the mouse hovers over a data item.

<b>Requirements:<b/>  Java 11+ and and JavaFX 11+

<b>Dependencies:</b>  This project depends of GitHub project https://github.com/gillius/jfxutils for several features
  - The Zooming and Panning
  - A foundation for SSM axis

<b>To download and use (in eclipse):</b>
Current project does not use gradle or maven or any other such system.

You will need to download this project and https://github.com/gillius/jfxutils as separate projects in eclipse.

This project expects a user Library (called JavaFX) 
  - Download JavaFX 11 (download the SDK not the Jmods): https://gluonhq.com/products/javafx/.

Setup a user library in eclipse and point it to the JavaFX /lib folder (where there is about 8 Javafx....jar files).
  - Add  a new user library in eclipse via Windows | Preferences | Java | Build Path | User Libraries (Call it JavaFX do not make it a System library)
  - Add jars so select "Add External Jars" and browse to the JavaFX /lib directory and select the .jar files

Set up a VMargument that will include the needed JavaFX jars
  - Modify the following vm argument line below so that it points to the JavaFX lib folder (and version) you downloaded above
  
--module-path C:\Users\...\JAVA_INSTALL\javafx-sdk-11.0.2\lib --add-modules=javafx.controls,javafx.fxml,javafx.web,javafx.graphics,javafx.media

  - Basically replace the C:\....\lib text above with your path to the lib directory of the version of JavaFX that you downloaded.

  - In eclispe go  to: windows | preferences | java | installed JREs 
  - Highlight your installed JRE and click edit.
  - Paste the modified argument in the "default VM arguments" field.
  
