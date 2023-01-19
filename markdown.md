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