import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static java.awt.Color.HSBtoRGB;

public class Main {
    final static Random rand = new Random();

    /**
     * Commandline arguments are read in as follows
     * Java Voranoi [image width] [image height] [cells wide] [cells high]
     * + image width    | How wide in pixels the output image will be
     * + image height   | How tall the image will be in pixels
     * + cells wide     | How many cells to fit width wise in relation to image width
     * + cells high     | How many cells to fit height wise in relation to image height
     * @param args Commandline arguments
     */
    public static void main(String[] args) {
        // no command line arguments = default
        if (args.length == 0) {
            makeVoranoi(1000, 500, 5, 250);
        } else if (args.length == 4) {
            makeVoranoi(
                    Integer.parseInt(args[0]),
                    Integer.parseInt(args[1]),
                    Integer.parseInt(args[2]),
                    Integer.parseInt(args[3])
            );
        } else {
            System.out.println(
                    """
                            Incorrect usage, correct usage:
                            Java Voranoi [image width] [image height] [cells wide] [cells high]
                            \timage width    | How wide in pixels the output image will be
                            \timage height   | How tall the image will be in pixels
                            \tcells wide     | How many cells to fit width wise in relation to image width
                            \tcells high     | How many cells to fit height wise in relation to image height"""
            );
            System.exit(-1);
        }
    }

    private static void makeVoranoi(final int width, final int height, final int cellsW, final int cellsH) {
        final int cellPixelW = width / cellsW;
        final int cellPixelH = height / cellsH;

        // Holds Point objects temporarily
        Point p;

        // Create the point map
        Point[][] pointMap = generatePointMap(cellsW, cellsH, cellPixelW, cellPixelH);

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

    private static Point[][] generatePointMap(int cellsW, int cellsH, int cellPixelW, int cellPixelH) {
        Point[][] pointMap = new Point[cellsW][cellsH];
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

    private static int pointDistance(int x1, int y1, int x2, int y2) {
        return (Math.abs(x2 - x1) * Math.abs(x2 - x1)) + (Math.abs(y2 - y1) * Math.abs(y2 - y1));
    }

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
}