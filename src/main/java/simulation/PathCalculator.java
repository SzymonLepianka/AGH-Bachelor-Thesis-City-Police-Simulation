package simulation;

import OSMToGraph.ImportedEdge;
import World.World;
import de.westnordost.osmapi.map.data.LatLon;
import de.westnordost.osmapi.map.data.Node;
import de.westnordost.osmapi.map.data.OsmLatLon;
import entities.Entity;
import entities.Patrol;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import utils.Haversine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PathCalculator extends Thread {

    private final AStarShortestPath<Node, ImportedEdge> pathCalculator = World.getInstance().getMap().getPathCalculator();
    private final HashMap<Long, Node> myNodes = World.getInstance().getMap().getMyNodes();
    private final Entity source;
    private final Entity target;

    public PathCalculator(Entity source, Entity target) {
        this.source = source;
        this.target = target;
    }

    @Override
    public void run() {
        var pathNodeList = getPathNodeList(source.getLatitude(), source.getLongitude(), target.getLatitude(), target.getLongitude());
        if (pathNodeList.size() == 1){
            ArrayList<Node> pathNodeList2 = new ArrayList<>();
            pathNodeList2.add(pathNodeList.get(0));
            ((Patrol.Transfer) ((Patrol) source).getAction()).setPathNodeList(pathNodeList2);
        }else{
            ((Patrol.Transfer) ((Patrol) source).getAction()).setPathNodeList((ArrayList<Node>) pathNodeList);
        }
    }

    public List<Node> getPathNodeList(double sourceLatitude, double sourceLongitude, double targetLatitude, double targetLongitude) {
        Node nearSourceNode = findNearestNode(new OsmLatLon(sourceLatitude, sourceLongitude));
        Node nearTargetNode1 = findNearestNode(new OsmLatLon(targetLatitude, targetLongitude));
        GraphPath<Node, ImportedEdge> path = pathCalculator.getPath(nearSourceNode, nearTargetNode1);

        // the case where the route between nodes does not exist
        if (path == null) {
            List<Node> forbiddenNodes = new ArrayList<>();
            while (path == null) {
                forbiddenNodes.add(nearSourceNode);
                forbiddenNodes.add(nearTargetNode1);
                nearSourceNode = findNearestNode(new OsmLatLon(sourceLatitude, sourceLongitude), forbiddenNodes);
                nearTargetNode1 = findNearestNode(new OsmLatLon(targetLatitude, targetLongitude), forbiddenNodes);

                // calculation of the route between two points in the case where initially there is no route between them, the simulation stops working smoothly
                System.out.println(nearSourceNode + " -to- " + nearTargetNode1 + " route calculation");

                while (nearSourceNode.equals(nearTargetNode1)) {
                    forbiddenNodes.add(nearSourceNode);
                    forbiddenNodes.add(nearTargetNode1);
                    nearSourceNode = findNearestNode(new OsmLatLon(sourceLatitude, sourceLongitude), forbiddenNodes);
                    nearTargetNode1 = findNearestNode(new OsmLatLon(targetLatitude, targetLongitude), forbiddenNodes);
                }
                path = pathCalculator.getPath(nearSourceNode, nearTargetNode1);
            }
        }
        return path.getVertexList();
    }

    public Node findNearestNode(LatLon point) {
        double distance = Double.MAX_VALUE;
        Node nearestNode = null;
        for (java.util.Map.Entry<Long, Node> me : myNodes.entrySet()) {
            LatLon nodePosition = me.getValue().getPosition();
            double tmpDistance = Haversine.distance(point.getLatitude(), point.getLongitude(), nodePosition.getLatitude(), nodePosition.getLongitude());
            if (tmpDistance < distance) {
                distance = tmpDistance;
                nearestNode = me.getValue();
            }
        }
        return nearestNode;
    }

    public Node findNearestNode(LatLon point, List<Node> forbiddenNodes) {
        double distance = Double.MAX_VALUE;
        Node nearestNode = null;
        for (java.util.Map.Entry<Long, Node> me : myNodes.entrySet()) {
            LatLon nodePosition = me.getValue().getPosition();
            double tmpDistance = Haversine.distance(point.getLatitude(), point.getLongitude(), nodePosition.getLatitude(), nodePosition.getLongitude());
            if (tmpDistance < distance && !forbiddenNodes.contains(me.getValue())) {
                distance = tmpDistance;
                nearestNode = me.getValue();
            }
        }
        return nearestNode;
    }
}