# A what?
A Voranoi diagram,
>In mathematics, a Voronoi diagram is a partition of a plane into regions close to each of a given set of objects. In the simplest case, these objects are just finitely many points in the plane (called seeds, sites, or generators). For each seed there is a corresponding region, called a Voronoi cell, consisting of all points of the plane closer to that seed than to any other. The Voronoi diagram of a set of points is dual to that set's Delaunay triangulation.
>
>The Voronoi diagram is named after mathematician Georgy Voronoy, and is also called a Voronoi tessellation, a Voronoi decomposition, a Voronoi partition, or a Dirichlet tessellation (after Peter Gustav Lejeune Dirichlet). Voronoi cells are also known as Thiessen polygons. Voronoi diagrams have practical and theoretical applications in many fields, mainly in science and technology, but also in visual art.
>
> -[**Wikipedia**](https://en.wikipedia.org/wiki/Voronoi_diagram)

Effectively imagine the following scenario.
You have a grid of pixels, 100 wide by 100 high, you create 2 points, point **a** and point **b** and you assign a colour to each point, different of course. Now, for each pixel, find which point is the closest, and assign that pixel the colour assigned to that point. And that's it.

# Let's start then
## What am I including and why?
```java
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static java.awt.Color.HSBtoRGB;
```
| **Package**       | **Why?**                                                                                                               |
|-------------------|------------------------------------------------------------------------------------------------------------------------|
| **ImageIO**       | ImageIO allows us to write the content of a BufferedImage object to a file in a given format.                          |
| **BufferedImage** | BufferedImage allows us to create a sort of bitmap type image, without having to worry about actual image format specs |
| **File**          | File allows us to write our data to a file                                                                             |
| **IOException**   | IOException will handle our file opening/saving exceptions                                                             |
| **Random**        | We use random to generate random point locations and colours                                                           |
| **TYPE_INT_RGB**  | The type of image format                                                                                               |
| **HSBtoRGB**      | To create a colour from HSV/HSB value to the required RGB type                                                         |

## Entry point
```java
public static void main(String[] args) {
    // no command line arguments = default
    if (args.length == 0) {
        makeVoranoi(10000, 10000, 100, 100);
    } else if (args.length == 4) {
        makeVoranoi(
            Integer.parseInt(args[0]),
            Integer.parseInt(args[1]),
            Integer.parseInt(args[2]),
            Integer.parseInt(args[3])
        );
    } else {
        System.out.println(
            "Incorrect usage, correct usage:\n" +
            "Java Voranoi [image width] [image height] [cells wide] [cells high]\n" +
            "\timage width    | How wide in pixels the output image will be\n" +
            "\timage height   | How tall the image will be in pixels\n" +
            "\tcells wide     | How many cells to fit width wise in relation to image width\n" +
            "\tcells high     | How many cells to fit height wise in relation to image height"
        );
        System.exit(-1);
    }
}
```
To allow a bit of flexibility, I've implemented command line arguments in the form of image-width, image-height, cells-wide, cells-high.
By default with no command line arguments the program will generate a 10kx10k image with 100 cells wide and 100 cells high.

# Make Voranoi!
```java
private static void makeVoranoi(final int width, final int height, final int cellsW, final int cellsH) {
    final int cellPixelW = width / cellsW;
    final int cellPixelH = height / cellsH;

    // Holds Point objects temporarily
    Point p;

    // Create the point map
    Point pointMap[][] = generatePointMap(cellsW, cellsH, cellPixelW, cellPixelH);

    // Create blank image
    BufferedImage image = new BufferedImage(width, height, TYPE_INT_RGB);

    // Set every pixel
    for (int imageX = 0; imageX < width; imageX++) {
        for (int imageY = 0; imageY < height; imageY++) {
            // Get the closest point to the image x and y
            p = getClosestPoint(imageX, imageY, cellPixelW, cellPixelH, pointMap);

            // Set this pixel to the colour of the closest point
            image.setRGB(imageX, imageY, p.getColour());
        }
    }

    // Write the image to a file
    File outputfile = new File("output.png");
    try {
        ImageIO.write(image, "png", outputfile);
    } catch (IOException e) {
        System.out.println("There was an error processing the file!");
    }
}
```
Here's the function used to generate and save the voranoi diagram, theres quite a bit to go over here so I'll do it one bit at a time.

```java
final int cellPixelW = width / cellsW;
final int cellPixelH = height / cellsH;
```
The program doesn't take a fixed size cell, in fact, it calculates the size of the cells given the desired number of cells.
So we calculate the width of the cells and the height of the cells before we can do anything.

```java
 // Holds Point objects temporarily
Point p;

// Create the point map
Point pointMap[][] = generatePointMap(cellsW, cellsH, cellPixelW, cellPixelH);

// Create blank image
BufferedImage image = new BufferedImage(width, height, TYPE_INT_RGB);
```
We will need to process points at some point, so I've created a point variable **p** to hold them and hopefully avoid creating new point objects repeatedly.
The **pointMap** is, simply put, a 2-dimensional array of *Point*s. A Point contains the coordinate (x, y) and the colour of the point. We will use the coordinate and the colour later when colouring the entire image.
Finally, the **image** is what will store our pixel data for the entire image.

```java
// Set every pixel
for (int imageX = 0; imageX < width; imageX++) {
    for (int imageY = 0; imageY < height; imageY++) {
        // Get the closest point to the image x and y
        p = getClosestPoint(imageX, imageY, cellPixelW, cellPixelH, pointMap);

        // Set this pixel to the colour of the closest point
        image.setRGB(imageX, imageY, p.getColour());
    }
}
```
Finally, the interesting part, or at least, the part that actually sets the data for our image.
Here we iterate over each pixel 0 through **width** and 0 through **height**. For each pixel, we get the closest point using the magic
function **getClosestPoint** and then we set the pixel to the appropriate colour.

```java
// Write the image to a file
File outputfile = new File("output.png");
try {
    ImageIO.write(image, "png", outputfile);
} catch (IOException e) {
    System.out.println("There was an error processing the file!");
}
```
This part isn't all too interesting, we create a file, write the BufferedImage **image** to the file and, that's it!

# Generating the point map
## Firstly, what's a point?
A point in this context is simply a collection of 3 things, an x coordinate, y coordinate, and a colour. The specification used for
the Point object is simply:
```java
public class Point {
    private int x;
    private int y;
    private int colour;

    public Point(int x, int y, int colour) {
        this.x = x;
        this.y = y;
        this.colour = colour;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getColour() {
        return colour;
    }
}
```

## So make the point map!
```java
private static Point[][] generatePointMap(int cellsW, int cellsH, int cellPixelW, int cellPixelH) {
    Point pointMap[][] = new Point[cellsW][cellsH];
    int pX, pY, pC;

    for (int cellX = 0; cellX < cellsW; cellX++) {
        for (int cellY = 0; cellY < cellsH; cellY++) {
            pX = (cellPixelW * cellX) + rand.nextInt(cellPixelW);
            pY = (cellPixelH * cellY) + rand.nextInt(cellPixelH);
            pC = HSBtoRGB(
                    rand.nextInt(360) / 360.0f,
                    0.8f,
                    0.8f
            );

            pointMap[cellX][cellY] = new Point(pX, pY, pC);
        }
    }

    return pointMap;
}
```
Alright, what's going on here?
As I said before, a point map in this context is simply a 2d array of Point objects. So here I've simply created a 2d-array of size
**cellsW** by **cellsH** and filled it with new Point objects. You'll notice the Point objects we create contain just a bit of randomness.
I've defined the x and y coordinate as being a random location plus some offset, but why? Why am i using this point map instead of a simple list of Point objects?  
Ok, here's where some explaining might be necessary. If we used a list of points, hereon entitled **pointList** we would need to consider the number of points we would have to check when finding the closest point to a given location.
Imagine you had 10 points, and you picked a random location. In order to find the closest point to the location you chose, you would have to calculate the distance from locationA to PointA, then PointB... and so on. That is
the number of points you have to check for each pixel, would be exactly equal to the number of Points you have for your image. Meaning the **BigO**
would increase at a scale of **O(pixels ^ Points)**, which is huge.  
  
So how does a point map help? Simple, imagine a 5x5 grid where each cell contains exatly 1 point, like this
```
.  0     1     2     3     4
+-----+-----+-----+-----+-----+
| A   |     |     |  D  |     |  0
|     |  B  |   C |     |   E |
+-----+-----+-----+-----+-----+
|     |  I  |  H  |     | F   |  1
|   J |     |     |  G  |     |
+-----+-----+-----+-----+-----+
|     |     | M   |   N |     |  2
| K   | L   |     |     | O   |
+-----+-----+-----+-----+-----+
|     |     | R   |     | P   |  3
|   T |  S  |     |  Q  |     |
+-----+-----+-----+-----+-----+
|   U |  V  |     |  X  |     |  4
|     |     | W   |     |  Y  |
+-----+-----+-----+-----+-----+
```
Now imagine we were trying to colour a pixel in the same cell as point **A** (Cell 0,0). Instead of checking every point **A** through **Y**
we can see we only have to check the surrounding cells. so we check cells (0,0), (0,1), (1,1) and (1,0).
So the worst case for us now is that we have to check 9 cells, specifically 9 Points, so now our BigO is locked to **O(pixels ^ 9)**.

# Let's get the closest point
```java
private static Point getClosestPoint(int imageX, int imageY, int cellPixelW, int cellPixelH, Point[][] pointMap) {
    // Where are we in the pixel map?
    final int mapX = imageX / cellPixelW;
    final int mapY = imageY / cellPixelH;

    Point closestPoint = new Point(0, 0, 0);
    Point temp;
    int closestDistance = Integer.MAX_VALUE;
    int distance;

    for (int deltaX = -1; deltaX < 2; deltaX++) {
        if (mapX + deltaX < 0 || mapX + deltaX >= pointMap.length) {
            continue;
        }
        for (int deltaY = -1; deltaY < 2; deltaY++) {
            if (mapY + deltaY < 0 || mapY + deltaY >= pointMap[0].length) {
                continue;
            }
            temp = pointMap[mapX + deltaX][mapY + deltaY];
            distance = pointDistance(imageX, imageY, temp.getX(), temp.getY());

            if (distance < closestDistance) {
                closestDistance = distance;
                closestPoint = temp;
            }
        }
    }

    return closestPoint;
}
```
>Alright, thats cool but how does it work  
>-You

If that's not you, move along.  
So, firstly we need to get the Cell coordinate of the pixel we are currently trying to colour.
It's simple math, but we calculate the **mapX** and **mapY** by divifing the image coordinate by the respective cell dimension.
Now, on to the actual bulk of the function.

```java
for (int deltaX = -1; deltaX < 2; deltaX++) {
    if (mapX + deltaX < 0 || mapX + deltaX >= pointMap.length) {
        continue;
    }
    for (int deltaY = -1; deltaY < 2; deltaY++) {
        if (mapY + deltaY < 0 || mapY + deltaY >= pointMap[0].length) {
            continue;
        }
        temp = pointMap[mapX + deltaX][mapY + deltaY];
        distance = pointDistance(imageX, imageY, temp.getX(), temp.getY());

        if (distance < closestDistance) {
            closestDistance = distance;
            closestPoint = temp;
        }
    }
}
```
We use two variables, **deltaX** and **deltaY** to navigate the surrounding cells. Note that if we move 1 in each direction of our map coordinate **mapX**
and **mapY**, we navigate the surrounding cells. The steps are simple, for each surrounding cell we take the Point stored there,
calculate its distance to the image coordinate, and remember it if it is the smallest distance found thus far.
Finally, we simply return the result which will be the closest point.  
  
It's not entirely important, but there's a slight efficiency to be made when calculating the distance between two points.
The **pointDistance** function is simply:
```java
private static int pointDistance(int x1, int y1, int x2, int y2) {
    return (Math.abs(x2 - x1) * Math.abs(x2 - x1)) + (Math.abs(y2 - y1) * Math.abs(y2 - y1));
}
```
While the typical formula for the distance between two points is
> distance = sqrt( abs(x2 - x1)^2 + abs(y2 - y1)^2 )  

In our function, we don't calculate the square root. This is because the square root is only necessary if we want the exact precise distance between two points.
If we ignore the squareroot, the distance we calculate between any points can still be compared because larger distances will grow just as much as smaller distances.

# That's a wrap!
So that's it, fairly small, nothing too complex. Below are some sample images using various inputs just for fun!