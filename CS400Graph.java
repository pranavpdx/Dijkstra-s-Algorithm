// --== CS400 File Header Information ==--
// Name: Pranav Sharma
// Email: pnsharma@wisc.edu
// Team: DS
// TA: Sourabhi
// Lecturer: Florian
// Notes to Grader:
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.NoSuchElementException;

public class CS400Graph<NodeType, EdgeType extends Number> implements GraphADT<NodeType, EdgeType> {

  /**
   * Vertex objects group a data field with an adjacency list of weighted directed edges that lead
   * away from them.
   */
  protected class Vertex {
    public NodeType data; // vertex label or application specific data
    public LinkedList<Edge> edgesLeaving;

    public Vertex(NodeType data) {
      this.data = data;
      this.edgesLeaving = new LinkedList<>();
    }
  }

  /**
   * Edge objects are stored within their source vertex, and group together their target destination
   * vertex, along with an integer weight.
   */
  protected class Edge {
    public Vertex target;
    public EdgeType weight;

    public Edge(Vertex target, EdgeType weight) {
      this.target = target;
      this.weight = weight;
    }
  }

  protected Hashtable<NodeType, Vertex> vertices; // holds graph verticies, key=data

  public CS400Graph() {
    vertices = new Hashtable<>();
  }

  /**
   * Insert a new vertex into the graph.
   * 
   * @param data the data item stored in the new vertex
   * @return true if the data can be inserted as a new vertex, false if it is already in the graph
   * @throws NullPointerException if data is null
   */
  public boolean insertVertex(NodeType data) {
    if (data == null)
      throw new NullPointerException("Cannot add null vertex");
    if (vertices.containsKey(data))
      return false; // duplicate values are not allowed
    vertices.put(data, new Vertex(data));
    return true;
  }

  /**
   * Remove a vertex from the graph. Also removes all edges adjacent to the vertex from the graph
   * (all edges that have the vertex as a source or a destination vertex).
   * 
   * @param data the data item stored in the vertex to remove
   * @return true if a vertex with *data* has been removed, false if it was not in the graph
   * @throws NullPointerException if data is null
   */
  public boolean removeVertex(NodeType data) {
    if (data == null)
      throw new NullPointerException("Cannot remove null vertex");
    Vertex removeVertex = vertices.get(data);
    if (removeVertex == null)
      return false; // vertex not found within graph
    // search all vertices for edges targeting removeVertex
    for (Vertex v : vertices.values()) {
      Edge removeEdge = null;
      for (Edge e : v.edgesLeaving)
        if (e.target == removeVertex)
          removeEdge = e;
      // and remove any such edges that are found
      if (removeEdge != null)
        v.edgesLeaving.remove(removeEdge);
    }
    // finally remove the vertex and all edges contained within it
    return vertices.remove(data) != null;
  }

  /**
   * Insert a new directed edge with a positive edge weight into the graph.
   * 
   * @param source the data item contained in the source vertex for the edge
   * @param target the data item contained in the target vertex for the edge
   * @param weight the weight for the edge (has to be a positive integer)
   * @return true if the edge could be inserted or its weight updated, false if the edge with the
   *         same weight was already in the graph
   * @throws IllegalArgumentException if either source or target or both are not in the graph, or if
   *                                  its weight is < 0
   * @throws NullPointerException     if either source or target or both are null
   */
  public boolean insertEdge(NodeType source, NodeType target, EdgeType weight) {
    if (source == null || target == null)
      throw new NullPointerException("Cannot add edge with null source or target");
    Vertex sourceVertex = this.vertices.get(source);
    Vertex targetVertex = this.vertices.get(target);
    if (sourceVertex == null || targetVertex == null)
      throw new IllegalArgumentException("Cannot add edge with vertices that do not exist");
    if (weight.doubleValue() < 0)
      throw new IllegalArgumentException("Cannot add edge with negative weight");
    // handle cases where edge already exists between these verticies
    for (Edge e : sourceVertex.edgesLeaving)
      if (e.target == targetVertex) {
        if (e.weight.doubleValue() == weight.doubleValue())
          return false; // edge already exists
        else
          e.weight = weight; // otherwise update weight of existing edge
        return true;
      }
    // otherwise add new edge to sourceVertex
    sourceVertex.edgesLeaving.add(new Edge(targetVertex, weight));
    return true;
  }

  /**
   * Remove an edge from the graph.
   * 
   * @param source the data item contained in the source vertex for the edge
   * @param target the data item contained in the target vertex for the edge
   * @return true if the edge could be removed, false if it was not in the graph
   * @throws IllegalArgumentException if either source or target or both are not in the graph
   * @throws NullPointerException     if either source or target or both are null
   */
  public boolean removeEdge(NodeType source, NodeType target) {
    if (source == null || target == null)
      throw new NullPointerException("Cannot remove edge with null source or target");
    Vertex sourceVertex = this.vertices.get(source);
    Vertex targetVertex = this.vertices.get(target);
    if (sourceVertex == null || targetVertex == null)
      throw new IllegalArgumentException("Cannot remove edge with vertices that do not exist");
    // find edge to remove
    Edge removeEdge = null;
    for (Edge e : sourceVertex.edgesLeaving)
      if (e.target == targetVertex)
        removeEdge = e;
    if (removeEdge != null) { // remove edge that is successfully found
      sourceVertex.edgesLeaving.remove(removeEdge);
      return true;
    }
    return false; // otherwise return false to indicate failure to find
  }

  /**
   * Check if the graph contains a vertex with data item *data*.
   * 
   * @param data the data item to check for
   * @return true if data item is stored in a vertex of the graph, false otherwise
   * @throws NullPointerException if *data* is null
   */
  public boolean containsVertex(NodeType data) {
    if (data == null)
      throw new NullPointerException("Cannot contain null data vertex");
    return vertices.containsKey(data);
  }

  /**
   * Check if edge is in the graph.
   * 
   * @param source the data item contained in the source vertex for the edge
   * @param target the data item contained in the target vertex for the edge
   * @return true if the edge is in the graph, false if it is not in the graph
   * @throws NullPointerException if either source or target or both are null
   */
  public boolean containsEdge(NodeType source, NodeType target) {
    if (source == null || target == null)
      throw new NullPointerException("Cannot contain edge adjacent to null data");
    Vertex sourceVertex = vertices.get(source);
    Vertex targetVertex = vertices.get(target);
    if (sourceVertex == null)
      return false;
    for (Edge e : sourceVertex.edgesLeaving)
      if (e.target == targetVertex)
        return true;
    return false;
  }

  /**
   * Return the weight of an edge.
   * 
   * @param source the data item contained in the source vertex for the edge
   * @param target the data item contained in the target vertex for the edge
   * @return the weight of the edge (a Number that represents 0 or a positive value)
   * @throws IllegalArgumentException if either sourceVertex or targetVertex or both are not in the
   *                                  graph
   * @throws NullPointerException     if either sourceVertex or targetVertex or both are null
   * @throws NoSuchElementException   if edge is not in the graph
   */
  public EdgeType getWeight(NodeType source, NodeType target) {
    if (source == null || target == null)
      throw new NullPointerException("Cannot contain weighted edge adjacent to null data");
    Vertex sourceVertex = vertices.get(source);
    Vertex targetVertex = vertices.get(target);
    if (sourceVertex == null || targetVertex == null)
      throw new IllegalArgumentException(
          "Cannot retrieve weight of edge between vertices that do not exist");
    for (Edge e : sourceVertex.edgesLeaving)
      if (e.target == targetVertex)
        return e.weight;
    throw new NoSuchElementException("No directed edge found between these vertices");
  }

  /**
   * Return the number of edges in the graph.
   * 
   * @return the number of edges in the graph
   */
  public int getEdgeCount() {
    int edgeCount = 0;
    for (Vertex v : vertices.values())
      edgeCount += v.edgesLeaving.size();
    return edgeCount;
  }

  /**
   * Return the number of vertices in the graph
   * 
   * @return the number of vertices in the graph
   */
  public int getVertexCount() {
    return vertices.size();
  }

  /**
   * Check if the graph is empty (does not contain any vertices or edges).
   * 
   * @return true if the graph does not contain any vertices or edges, false otherwise
   */
  public boolean isEmpty() {
    return vertices.size() == 0;
  }

  /**
   * Path objects store a discovered path of vertices and the overal distance of cost of the
   * weighted directed edges along this path. Path objects can be copied and extended to include new
   * edges and verticies using the extend constructor. In comparison to a predecessor table which is
   * sometimes used to implement Dijkstra's algorithm, this eliminates the need for tracing paths
   * backwards from the destination vertex to the starting vertex at the end of the algorithm.
   */
  protected class Path implements Comparable<Path> {
    public Vertex start; // first vertex within path
    public double distance; // sumed weight of all edges in path
    public List<NodeType> dataSequence; // ordered sequence of data from vertices in path
    public Vertex end; // last vertex within path

    /**
     * Creates a new path containing a single vertex. Since this vertex is both the start and end of
     * the path, it's initial distance is zero.
     * 
     * @param start is the first vertex on this path
     */
    public Path(Vertex start) {
      this.start = start;
      this.distance = 0.0D;
      this.dataSequence = new LinkedList<>();
      this.dataSequence.add(start.data);
      this.end = start;
    }

    /**
     * This extension constructor makes a copy of the path passed into it as an argument without
     * affecting the original path object (copyPath). The path is then extended by the Edge object
     * extendBy. Use the doubleValue() method of extendBy's weight field to get a double
     * representation of the edge's weight.
     * 
     * @param copyPath is the path that is being copied
     * @param extendBy is the edge the copied path is extended by
     */
    public Path(Path copyPath, Edge extendBy) {
      // TODO: Implement this constructor in Step 5.
      this.start = copyPath.start;
      this.distance = copyPath.distance + extendBy.weight.doubleValue();
      this.dataSequence = new LinkedList<>();
      for (int i = 0; i < copyPath.dataSequence.size(); i++) {
        this.dataSequence.add(copyPath.dataSequence.get(i));
      }
      this.dataSequence.add(extendBy.target.data);
      this.end = extendBy.target;
    }

    /**
     * Allows the natural ordering of paths to be increasing with path distance. When path distance
     * is equal, the string comparison of end vertex data is used to break ties.
     * 
     * @param other is the other path that is being compared to this one
     * @return -1 when this path has a smaller distance than the other, +1 when this path has a
     *         larger distance that the other, and the comparison of end vertex data in string form
     *         when these distances are tied
     */
    public int compareTo(Path other) {
      int cmp = Double.compare(this.distance, other.distance);
      if (cmp != 0)
        return cmp; // use path distance as the natural ordering
      // when path distances are equal, break ties by comparing the string
      // representation of data in the end vertex of each path
      return this.end.data.toString().compareTo(other.end.data.toString());
    }
  }


  /**
   * Uses Dijkstra's shortest path algorithm to find and return the shortest path between two
   * vertices in this graph: start and end. This path contains an ordered list of the data within
   * each node on this path, and also the distance or cost of all edges that are a part of this
   * path.
   * 
   * @param start data item within first node in path
   * @param end   data item within last node in path
   * @return the shortest path from start to end, as computed by Dijkstra's algorithm
   * @throws NoSuchElementException when no path from start to end can be found, including when no
   *                                vertex containing start or end can be found
   */
  protected Path dijkstrasShortestPath(NodeType start, NodeType end) {
    PriorityQueue<Path> pq = new PriorityQueue<Path>();
    Hashtable<Vertex, Double> distances = new Hashtable<Vertex, Double>();
    List<Vertex> visited = new ArrayList<Vertex>();

    // if the parameters are null or if they don't lead to vertices in our HashTable then we throw
    // an exception
    if (start == null || end == null || vertices.get(start) == null || vertices.get(end) == null) {
      throw new NoSuchElementException();
    }
    // adding all vertices to the distance map and maximizing their values
    Set<NodeType> keys = vertices.keySet();
    for (NodeType key : keys) {
      distances.put(vertices.get(key), Double.MAX_VALUE);
    }
    // adds the initial path to our priority queue
    pq.add(new Path(vertices.get(start)));
    distances.replace(vertices.get(start), (double) 0.0);

    // keep looping until all the nodes are visited, if they are and no path is found, then no path
    // exists
    while (visited.size() != vertices.size()) {
      if (pq.isEmpty()) {
        throw new NoSuchElementException();
      }
      Path temp = pq.remove();
      if (visited.contains(temp.end))
        continue;
      // adds not visited note to the list
      visited.add(temp.end);


      double edgeDistance = -1;
      double newDistance = -1;

      Vertex lastVisited = temp.end;
      // gets all the nodes connected to the last node in the path and adds those paths to the
      // priority queue
      for (int i = 0; i < lastVisited.edgesLeaving.size(); i++) {
        Vertex target = lastVisited.edgesLeaving.get(i).target;

        if (!visited.contains(target)) {
          edgeDistance = lastVisited.edgesLeaving.get(i).weight.doubleValue();
          newDistance = distances.get(temp.end) + edgeDistance;

          if (newDistance < distances.get(target)) {
            distances.replace(target, newDistance);
          }

          pq.add(new Path(temp, lastVisited.edgesLeaving.get(i)));
        }
      }
      // returns if a shortest path is found
      if (temp != null && temp.end.data == end) {
        return temp;
      }

    }
    // exception thrown otherwise
    throw new NoSuchElementException();
  }

  /**
   * Returns the shortest path between start and end. Uses Dijkstra's shortest path algorithm to
   * find the shortest path.
   * 
   * @param start the data item in the starting vertex for the path
   * @param end   the data item in the destination vertex for the path
   * @return list of data item in vertices in order on the shortest path between vertex with data
   *         item start and vertex with data item end, including both start and end
   * @throws NoSuchElementException when no path from start to end can be found including when no
   *                                vertex containing start or end can be found
   */
  public List<NodeType> shortestPath(NodeType start, NodeType end) {
    return dijkstrasShortestPath(start, end).dataSequence;
  }

  /**
   * Returns the cost of the path (sum over edge weights) between start and end. Uses Dijkstra's
   * shortest path algorithm to find the shortest path.
   * 
   * @param start the data item in the starting vertex for the path
   * @param end   the data item in the end vertex for the path
   * @return the cost of the shortest path between vertex with data item start and vertex with data
   *         item end, including all edges between start and end
   * @throws NoSuchElementException when no path from start to end can be found including when no
   *                                vertex containing start or end can be found
   */
  public double getPathCost(NodeType start, NodeType end) {
    return dijkstrasShortestPath(start, end).distance;
  }
  
 
  public List<NodeType> minimumSpanningTree(NodeType start, int type, int dayofWeek) {
    // TODO Auto-generated method stub
    return minimalSpanningPath(start, type, dayofWeek).dataSequence;
  }

  public double minimumSpanningTreeCost(NodeType start, int type, int dayofWeek) {
    // TODO Auto-generated method stub
    return minimalSpanningPath(start, type, dayofWeek).distance;
  }

  protected Path minimalSpanningPath(NodeType start, int type, int dayofWeek) {
    PriorityQueue<Path> pq = new PriorityQueue<Path>();
    // if the parameters are null or if they don't lead to vertices in our HashTable then we throw
    // an exception
    if (start == null || vertices.get(start) == null) {
      throw new NoSuchElementException();
    }
    // adds the initial path to our priority queue
    pq.add(new Path(vertices.get(start)));

    // keep looping until all the nodes are visited, if they are and no path is found, then no path
    // exists
    Path temp = new Path(vertices.get(start));
    
    while (!pq.isEmpty()) {
      temp = pq.remove();
      Vertex lastVisited = temp.end;
      // gets all the nodes connected to the last node in the path and adds those paths to the
      // priority queue
      for (int i = 0; i < lastVisited.edgesLeaving.size(); i++) {
        Vertex target = lastVisited.edgesLeaving.get(i).target;
        if (!temp.dataSequence.contains(target.data)) {
          Path temp2 = new Path(temp, lastVisited.edgesLeaving.get(i));
          if (temp2 != null && temp2.dataSequence.size() == vertices.size()) {
            return temp2;
          }
          pq.add(temp2);
        }
      }
    }
    // exception thrown otherwise
    throw new NoSuchElementException();//
  }


}