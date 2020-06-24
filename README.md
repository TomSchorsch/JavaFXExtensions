# JavaFXExtensions

## Contains a LineChart Plot Editor</b><br>
The default JavaFX LineChart had some limitations in terms of available Symbols and Colors at runtime and some of those limitations are addressed by this project.  The "Editor" porition of this is to edit everything on the chart BUT the data... (i.e. colors, fonts, line sizes, etc.)

### New and Modified LineChart Features:
  - 17 symbols:<br>
    - diamond, circle, square, 
    - h_diamond, v_diamond, h_ellipse, v_ellipse, h_rectangle, v_rectangle,
    - up_triangle, down_triangle, right_triangle, left_triangle,
    - star5, cross, plus, checkmark 
  - There are 3 symbol styles for each symbol: 
     - filled, 
     - transparent, and 
     - white-filled
     
<img src="images/WhiteFilledUnFilledFilledSymbols.png" height = 300>     <img src="images/Symbols.png" height = 300>  

  - Chart title now includes an optional sub-title
  - Plots can have additional plot info saved in the lower left corner
  - CallOuts (Annotations)
    - A CallOut consists of a line starting at a datapoint value and extending in some direction for some length with some text at the end that can either be horizontal or in-line with the CallOut line angle.
    - A plot can have several different "series" of CallOuts each with unique settings for the CallOuts.
    - CallOuts can also be directly manipulated (moved, rotated)by the user
    
<img src="images/CallOutFontProperties.png" width = 430>  <img src="images/CallOutLineProperties.png" width = 430>  
        
### LineChart editing Features:
After the LineChart is created and displayed a user can modify all of the following features using pop-up editors.<br>
Pop-up editors are invoked by right-clicking on:
  - The chart (Plot Editor, Axis Editors, CallOut Series Editors)
  - An item (data series) in the legend (Series Editor)
  - A CallOut (CallOut Editor)
  
### Plot Editor:
  - Chart title and sub title text and font sizes
  - Legend visibility or position
  - Plot Info (additional text on the plot in the lower left corner)
  - For all Series
    - Symbol info: visibility, symbol, color, size, style
    - Line info: visibility, color, width

### Series Editor (for individual series):
  - Series name
  - Symbol info: visibility, symbol, color, size, style
  - Line info: visibility, color, width
  <img src="images/SeriesEditor.png" width = 200>
  
### Axis Editor:
  - Axis title, font size
  - Tic mark label font size and rotation
  
### Call Out Series Editor (for all callouts in a series):
  - Line Angle, Length, Width, and Color
  - Whether Text is rotated with the line
  - Text Size, Font Family, color, bold (or not), italicized (or not)

### Call Out Editor (for individual CallOut):
  - CallOut text
  - Line Angle, Length, Width, and Color
  - Whether Text is rotated with the line
  - Text Size, Font Family, color, bold (or not), italicized (or not)

## Additional features:
- Plots can be zoomed in/out via typical mouse movements.
- Right-click on chart Context Menu has plot save and save as options   
- A Seconds Since Midnight (SSM) axis which turns a double axis value into a HH:MM:SS.sss value (How much of HH:MM:SS.sss that is displayed depends on the zoomlevel).
- A Hover capability for data items that will display additional info when the mouse hovers over a data item.

## Requirements:
  - Java 11+ and and JavaFX 11+

## Dependencies:
  - This project depends of GitHub project https://github.com/gillius/jfxutils for several features
    - A foundation for SSM (Seconds Since Midnight) axis
    - The Zooming and Panning code was derived from ideas in jfxutils as well as from the StackOverFlow question at https://stackoverflow.com/questions/11087988/javafx-2-x-zoom-in-xychart-with-category-axys by Alberto acepsut

## To download and use (in eclipse):
<b>Note:Current project does not use gradle or maven or any other such system.</b>
  - Clone this project (https://github.com/TomSchorsch/JavaFXExtensions) to a local git repoisitory
      - In Eclipse select Git Repository | Clone a git repository | use the above url as the location to clone
      - JavaFXExtensions imported easily into a local git repository with Next | Next | Finish
  - Import the JavaFXExtensions from the local git repository into an eclipse project
    - In Eclipse select File | import... | Git | Projects from Git | Next
    - Then select Existing local repository | Next | JavaFXExtensions | Next | Next (make sure "Import existing Git projects" is selected) | Finish
  - Create a JavaFX user Library
    - Download JavaFX 11 (download the SDK not the Jmods): https://gluonhq.com/products/javafx/ and extract the contents
    - Add  a new user library in eclipse via Windows | Preferences | Java | Build Path | User Libraries (Call it JavaFX do not make it a System library)
    - Select "Add External Jars" and browse to the JavaFX /lib directory and select the .jar files in that directory
    - Open up the JavaFXExtensions Build Path and add to the Classpath the JavaFX User Library above that you just created
  - Set up a VMargument that will include the needed JavaFX jars
    - Modify the following vm argument line below so that it points to the JavaFX lib folder (and version) you downloaded above
--module-path C:\Users\...\JAVA_INSTALL\javafx-sdk-11.0.2\lib --add-modules=javafx.controls,javafx.fxml,javafx.web,javafx.graphics,javafx.media
    - Basically replace the C:\....\lib text above with your path to the lib directory of the version of JavaFX that you downloaded.
    - In eclispe go  to: windows | preferences | java | installed JREs 
    - Highlight your installed JRE and click edit.
    - Paste the modified argument you created above into the "default VM arguments" field. 
    
Note: 3 files from jfxutils (https://github.com/gillius/jfxutils) were incorporated into this project: StableTicksAxis.java, DefaultAxisTickFormatter.java, and AxisTickFormatter.java.  The files are in package javaFX.plots.axis in this project
    
## To run the tests associated with the project:
  - Go to the JavaFXExtensions Project to src/test/TestMain.java and execute it. 
  
