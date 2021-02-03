import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
 
class TicTacToeTest {
   
    public String board[];
    MinMax newMove;
   
    @BeforeEach
    void init() {
        board = new String[9];
        for(int i = 0; i < 9; i ++) {
            board[i] = "b";
        }
        newMove = new MinMax(board);
       
    }
   
    //check if object was created properly
    @Test
    void mainTest() {
        assertEquals("MinMax", newMove.getClass().getName(), "Did not initialize object properly");
    }
   
    //check the move list returns size of 9 for an empty game
    @Test
    void empty() {
        assertEquals(9, newMove.findMoves().size(), "Recommend proper move");
    }
   
    //check if it recommends one move when there is one left
    @Test
    void oneMoveLeft() {
        for(int i = 0; i < 8; i ++) {
            board[i] = "X";
        }
        newMove = new MinMax(board);
        assertEquals(1, newMove.findMoves().size(), "Did not stop player win vertical");
    }
   
    //check the move list returns size of 9 for an empty game
    @Test
    void full() {
        for(int i = 0; i < 9; i ++) {
            board[i] = "X";
        }
        newMove = new MinMax(board);
        assertEquals(0, newMove.findMoves().size(), "Recommend no move");
    }
   
    //check if correct move is returned when player is about to win Horizontal
    @Test
    void preventWin() {
        board[0] = "O";
        board[2] = "O";
        newMove = new MinMax(board);
        assertEquals(2, newMove.findMoves().get(0).getMovedTo(), "Did not stop player win horizontal");
    }
   
    //check if correct move is returned when player is about to win Vertical
    @Test
    void preventWin1() {
        board[0] = "O";
        board[6] = "O";
        newMove = new MinMax(board);
        assertEquals(4, newMove.findMoves().get(2).getMovedTo(), "Did not stop player win vertical");
    }
   
    //check if correct move is returned when player is about to win Diagonal
    @Test
    void preventWin2() {
        board[0] = "O";
        board[8] = "O";
        newMove = new MinMax(board);
        assertEquals(5, newMove.findMoves().get(3).getMovedTo(), "Did not stop player win vertical");
    }
   
    //check if it returned a winning move when possible
    @Test
    void win1() {
        board[0] = "X";
        board[2] = "X";
        newMove = new MinMax(board);
        assertEquals(2, newMove.findMoves().get(0).getMovedTo(), "Did not stop player win vertical");
    }
   
 
}