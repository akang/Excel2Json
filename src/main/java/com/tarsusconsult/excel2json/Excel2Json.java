package com.tarsusconsult.excel2json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads a comma separated file that is nested in a JSON structure and coverts it into json
 * @author aka
 * @date: 06 July 2017
 */
public class Excel2Json {


    private char nodeSeparator = ',';

    public void doConversion(String inputFile){

        File file = new File(inputFile);


        List<Node> rootNodes = new ArrayList();

        try  {

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            int currentDepth = 0;
            Node previousNode = null; //the node after the first

            while ((line = br.readLine()) != null) {

                char firstChar = line.charAt(0);

                // process the line.
                System.out.println("Processing line: " + line);
                if(firstChar == nodeSeparator && rootNodes.isEmpty()){

                    throw new Exception("The document must start with a root node");

                }else if(firstChar != nodeSeparator && rootNodes.isEmpty()){ //root node
                    System.out.println("** found root node");
                    Node rootNode = new Node();
                    rootNode.setName(this.removeAllNodeSeparator(line, nodeSeparator));
                    currentDepth = 0;

                    previousNode = rootNode;
                    rootNodes.add(rootNode);

                }else{ //child node
                    System.out.println("** found child node");

                    int depth = this.countDepth(line);
                    Node childNode = new Node();
                    childNode.setName(this.removeAllNodeSeparator(line, nodeSeparator));

                    if(depth > currentDepth){
                        previousNode.getChildNodes().add(childNode);
                        System.out.println("Attaching " + childNode.getName() + " to : " + previousNode.getName());
                        childNode.setParentNode(previousNode);

                    }else if(depth == currentDepth){
                        previousNode.getParentNode().getChildNodes().add(childNode);
                        childNode.setParentNode(previousNode.getParentNode());
                        System.out.println("Attaching " + childNode.getName() + " to : " + previousNode.getParentNode().getName());

                    }else{ //depth is before
                        int counter = depth;
                        Node siblingNode = null;
                        while(counter != currentDepth){
                            siblingNode = previousNode.getParentNode();
                            counter--;
                        }
                        if(siblingNode.getParentNode()!=null) {
                            System.out.println("Attaching " + childNode.getName() + " to : " + siblingNode.getParentNode().getName());
                            siblingNode.getParentNode().getChildNodes().add(childNode);
                            childNode.setParentNode(siblingNode.getParentNode());
                        }else{
                            System.out.println("Attaching " + childNode.getName() + " to " + siblingNode.getName());
                            siblingNode.getChildNodes().add(childNode);
                            childNode.setParentNode(siblingNode);
                        }
                    }
                    currentDepth = depth;
                    previousNode = childNode;
                }
            }
            System.out.println("Finished processing. Copy paste output to https://jsonlint.com/ for verification and beautification\nOutput:\n\n");
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            for(Node rootNode:rootNodes){
                sb.append(this.printNode(rootNode));
            }

            //remove the trailing ','
            String output = sb.toString();
            output = output.substring(0, output.length() - 1);
            output = output + "}";

            System.out.println(output);

        }catch(Exception e){
            e.printStackTrace();
        }


    }


    private String printNode(Node node){

        String output = "";
        if(node.getChildNodes().isEmpty()){
            output = "\"" + node.getName() + "\":\"\",";
        }else{
            output = "\"" + node.getName() + "\":{";
            for(Node childNode: node.getChildNodes()){
                output += printNode(childNode);
            }
            //remove the trailing ','
            output = output.substring(0, output.length() - 1);
            output += "},";
        }
        return output;
    }

    public static void main(String[] args){
        System.out.println("Usage: Excel2Json {inputfile}");
        String fullPathToExcel = args[0];
        System.out.println("Reading file: " + fullPathToExcel);

        Excel2Json e2j = new Excel2Json();
        e2j.doConversion(fullPathToExcel);


    }

    private int countDepth(String line){
        int depth = 0;
        while(line.charAt(depth) == nodeSeparator){
            depth ++;
        }
        return depth;
    }

    private String removeAllNodeSeparator(String line, char nodeSeparator){
        return line.replaceAll(Character.toString(nodeSeparator),"");
    }

    class Node{
        private String name;
        private Node parentNode;
        private List<Node> childNodes = new ArrayList();

        public Node getParentNode() {
            return parentNode;
        }

        public void setParentNode(Node parentNode) {
            this.parentNode = parentNode;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Node> getChildNodes() {
            return childNodes;
        }

        public void setChildNodes(List<Node> childNodes) {
            this.childNodes = childNodes;
        }


    }


}
