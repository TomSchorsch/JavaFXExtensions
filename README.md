# JavaFXExtensions

Contains a few standalone control extensions:<br>
  - FileChoiceBox - maintains a history of previously chosen files so user can choose a new file from a filepicker OR select a previously chosen file from the pull-down.
  - MappedRadioButtons - creates a bunch of radio buttons which display strings (the mapped-to value) but when selected return the mapped-from value.  2 features include indented sub-radio buttons and multiple columns of radio buttons.   
  - Config - config is a helper utility for the above.  It enables the "history state" of the FileChoiceBox control and the selected items in the MappedRadioButtons control to be saved between successive executions of your program.

Contains Plot editor that has rewritten some of the LineChart capabilities
There are 17 symbols:<br> 
  - diamond, circle, square, 
  - h_diamond, v_diamond, h_ellipse, v_ellipse, h_rectangle, v_rectangle,
  - up_triangle, down_triangle, right_triangle, left_triangle,
  - star5, cross, plus, checkmark
  
There are 3 symbol styles for each symbol:
  filled, transparent, and white-filled

The Plot editor relies on https://github.com/gillius/jfxutils so that must be downloaded as well to fully 
