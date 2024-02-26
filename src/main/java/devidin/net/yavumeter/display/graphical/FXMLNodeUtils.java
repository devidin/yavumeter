package devidin.net.yavumeter.display.graphical;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FXMLNodeUtils extends Application {
	
    public static List<Node> getAllNodesFromFXML(String fxmlPath) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        try {

            System.out.println("=".repeat(80));
            System.out.println("Inspecting: "+fxmlPath);
            System.out.println("=".repeat(80));
            Parent root = FXMLLoader.load(new FXMLNodeUtils().getClass().getResource(fxmlPath));

            List<Node> result= getAllNodes(root);
            System.out.println("=".repeat(80));
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static List<Node> getAllNodes(Node node) {
        List<Node> allNodes = new ArrayList<>();
        System.out.println("Node:"+node);
        
        addAllDescendants(node, allNodes);
        return allNodes;
    }

    private static void addAllDescendants(Node currentNode, List<Node> allNodes) {
        if (currentNode instanceof Pane) {
        	Pane parent = (Pane) currentNode;
            for (Node child : parent.getChildren()) {
                allNodes.add(child);
                System.out.println("Parent:"+currentNode +" Child (of Pane):"+child);
                addAllDescendants(child, allNodes);
            }
        } else if (currentNode instanceof Parent) {
            Parent parent = (Parent) currentNode;
            for (Node child : parent.getChildrenUnmodifiable()) {
                allNodes.add(child);
                System.out.println("Parent:"+currentNode +" Child          :"+child);
                addAllDescendants(child, allNodes);
            }
        }
    }

    public static void dump(String fxmlFile) {
        List<Node> allNodes = FXMLNodeUtils.getAllNodesFromFXML(fxmlFile);

        System.out.println("=".repeat(80));
        System.out.println("\nAll Nodes in the FXML-defined Node hierarchy in :"+fxmlFile);
        System.out.println("v".repeat(80));
        for (Node node : allNodes) {
            System.out.println(node);
        }
        System.out.println("^".repeat(80));
    }
    
    public static void main(String[] args) {
//    	dump("/test.fxml");
    	dump("/Splash.fxml");
    	dump("/GraphicalDisplayerBasic.fxml");
//    	dump("/YAvumeter.fxml");
    }

	@Override
	public void start(Stage primaryStage) throws Exception {
		// do nothing
		
	}
}
