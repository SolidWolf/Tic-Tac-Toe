import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;


public class FindNextMove extends Thread{
	//init a empty board
	public String board[] = {"b","b","b","b","b","b","b","b","b"};
	public int moveRec;
	public int difficulty;
    int moveEasy;
    int moveHard;
	
    private int getExpertMove(ArrayList<Node> movesList) {
    	int moveRec = 0;
    	
    	// generate hard move
		for(int x = 0; x < movesList.size(); x++)
		{
			Node temp = movesList.get(x);
			
			if(temp.getMinMax() == 10 || temp.getMinMax() == 0)
			{
				//returns best move ////////////////////////////////////////////////
				//put an if statement if want to do difficulty
				moveRec = temp.getMovedTo();
				break;
			}
		}
		return moveRec;
    }
    
    private int getEasyMove(ArrayList<Node> movesList) {
    	
    	Collections.shuffle(movesList);
    	
    	return movesList.get(0).getMovedTo();
    }
    
	public void run() {
		
		synchronized(board) {
			MinMax newMove = new MinMax(board);
			
			ArrayList<Node> movesList;
			
			movesList = newMove.findMoves();
						
            if(difficulty == 1){ // easy
            	moveRec = getEasyMove(movesList);
            } else if(difficulty == 2) { // medium, randomly pick between hard and easy
                
    			ArrayList<Integer> randList = new ArrayList<Integer>();
    			
    			randList.add(1);
    			randList.add(2);
            	
    	    	Collections.shuffle(randList);
 
                if(randList.get(0) == 1) { //randomly expert
                	moveRec = getExpertMove(movesList);
                }
                else { 								//randomly easy
                	moveRec = getEasyMove(movesList);
                }
                
            } else { 			//expert move 
            	moveRec = getExpertMove(movesList);
            }
		}
	}
	

}
