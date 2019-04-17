package com.company;

import java.util.ArrayList;
import java.util.Collections;

public class Pathfinding {

    public static int runs = 0;

    public static ArrayList<Coordinate> findPath(Building building, float startX, float startY, float targetX, float targetY, String id){
        //runs++;
        //System.out.println("pathfinding runs: " + runs);

        //Create lists to store data
        ArrayList<Coordinate> openList = new ArrayList<>();
        ArrayList<Coordinate> closedList = new ArrayList<>();

        //Add starting node
        openList.add(new Coordinate(startX, startY, 0, Helper.distance(startX,startY,targetX, targetY)));

        //Loop until we find the target node
        while(openList.size() > 0){

            //Find the lowest f value in the list
            int currentIndex = 0;
            Coordinate currentNode = openList.get(currentIndex);

            for(int i = 0; i < openList.size(); i++){
                if(openList.get(i).f < currentNode.f){
                    currentNode = openList.get(i);
                }
            }

            //System.out.println("current node x: " + currentNode.x + " y: " + currentNode.y + " f: " + currentNode.f);

            openList.remove(currentNode);
            closedList.add(currentNode);

            //Checking if we have reached the target
            if(Helper.approximatelyEqual(currentNode.x, targetX, 4) && Helper.approximatelyEqual(currentNode.y, targetY, 4)){
                System.out.println("Target Reached");

                ArrayList<Coordinate> pathFromEnd = new ArrayList<>();
                pathFromEnd.add(currentNode);
                while(currentNode.parent != null){
                    currentNode = currentNode.parent;
                    pathFromEnd.add(currentNode);
                }
                Collections.reverse(pathFromEnd);
                return pathFromEnd;
            }

            Coordinate dimensions = building.getDimensions();
            ArrayList<Coordinate> neighbours = currentNode.generateNeighbours(dimensions.x, dimensions.y);

            for(Coordinate c: neighbours){

                //If neighbouring node is already in the closed list
                if(coordinateInList(closedList, c.x,c.y)){
                    continue;
                }

                //If node is not traversable, add to closed list
                if(!building.isTraversable(c.x, c.y, id)){
                    closedList.add(c);
                    continue;
                }

                //Calculate values
                c.g = currentNode.g + 4;
                c.h = Helper.distance(c.x, c.y, targetX, targetY);
                c.f = c.h + c.g;

                useLowestGValue(openList, c);
            }
        }
        System.out.println("no path found");
        return null;
    }

    private static boolean coordinateInList(ArrayList<Coordinate> list, float x, float y){
        for(int i = 0; i < list.size(); i++){
            if(list.get(i).x == x && list.get(i).y == y){
                return true;
            }
        }
        return false;
    }

    private static void useLowestGValue(ArrayList<Coordinate> list, Coordinate c){
        for(int i = 0; i < list.size(); i++){
            if(list.get(i).x == c.x && list.get(i).y == c.y){
                if(list.get(i).g > c.g){
                    list.get(i).g = c.g;
                    return;
                }
                else{
                    return;
                }
            }
        }
        //If coordinate is not already in the list
        list.add(c);

    }
}
