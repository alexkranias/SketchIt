# SketchIt
SketchIt is a image/video manipulation software that procedurally renders raw images and videos to appear as if they were hand-sketched by a real person.
Created in Java 8. 
Supports MP4, MOV, PNG, and JPG file types as input.

# The Algorithim

### IMAGE PRE-PROCESSING
1) Receive an image
2) Decolorize the image
	- process the entire image as black and white into an int[row][column] then fetching values from that two dimensional array
3) Standardize the values. Divide all values by 255. Limiting the range of "brightness" of each pixel between 0-1. 0 = black, 1 = white.
4) Adjust "contrast" of pixels by putting each "brightness" value, b, to some user selected contrast constant, c, to create a "new_brightness" value = b^c. each element in the photo array
   should be reassigned the value b^c where b is the element's "brightness" value and c is the contrast contant.
5) Create a null copy of the array created in step 2, process the equivalent of a Guassian Blur, so for each pixel, treat it as a center point of an n x n grid, find the average "new_brightness" of said grid, 
   give the center pixel this value and repeat.
	- for pixels lying along the edge of the frame, just find the average of the pixels that exist within its cut grid (the real grid gets caught off by the edge of the frame) 
	  give the pixel this average value
6) Imaging having a three-dimensional graph where the pixels of the image were plotted along the x and y axis, so x is the row position and y is the column position and it starts 
   at x = 0 and y = 0, then the z-axis represents the brightness of said pixel. Lets let b=brightness, for each pixel we want to estimate what db/dx and db/dy is, this should be done
   by taking the pixel immediately above and below or immediately left and right and finding their delta(b) and dividing it by 2 and that's it. For edge corners use the center pixel 
   itself in placement of the missing pixel. 

### OUTLINING
7) Create a blank BufferImage with the user-selected background color that is the exact aspect ratio of the original image.
8) So a line is really like a contrast boundary point where very suddenly there is a large dip in brightness in the x or y direction. So the image should simply look at the db/dx and db/dy values 
   and where there is a very sudden spike it starts at that line and finds a dy/dx value that makes b remain close to constant with the origin being at that originally discovered point, then the 
   algorithm continues down the image at that dy/dx direction until the difference between the current pixel down the line with the origin pixel is greater than some variable (or maybe do until 
   the db/dx and db/dy values are near 0), line_limit, which should fluctuate around an established constant value using a bit of randomization.
9) This should be done by dividing the image into a n x m grid. First parsing through each grid column by randomly selecting a pixel column within each of the n grid columns, and each time a point 
   is found that matches the characteristics described in step 6 then it generates a line. This process is repreated by parsing through each grid row and randomly selecting a pixel row within each 
   grid.

## ADDITIONAL PREPROCESSING NOTES

It actually may be more benficial to keep the image colorized because in some cases the program will fail 
to see contrast between two objects because they have the same brightness but are two completely different
colors. Hence creating a three dimensional array int[row][column][R, G, B] and doing steps 3- but for each
element of [R, G, B]. Issue is this should increase the program's resource usage noticably.
   
# What is a Line?
- has an unprecise start and end
- is continuous
- has some concavity
- the start of one line is often connected to the end of another

