package maekawa_algo1_client;

import java.util.*;

/*
 * Binary Tree Class implements the binary tree Abstract data type.
 * It implements methods to add the nodes to the tree
 * Add checks if the replied servers taken in an array forms a quorum
 * 
 */
public class BinaryTree implements BinaryTreeADT {
    protected String data;
    protected BinaryTree lchild;
    protected BinaryTree rchild;
    
    public BinaryTree()
    {
        data = null;
    }
    
    public boolean emptyp()
    {
        return ( data == null );
    }
    
    public int height()
    {
        if ( emptyp() ) {
            return 0;
        } else {
            return 1 + max(lchild.height(),rchild.height());
        }
    }
    
    private int max(int a, int b)
    {
        if ( a > b ) { return a; } else { return b; }
    }
    
    public void visit()
    {
        System.out.println(data.toString());
    }

    public String key()
    {
        return (String)data;
    }

    public void add(String string, String dir)
    {
        if ( dir.equals("") ) {
            data = string;
            lchild = new BinaryTree();
            rchild = new BinaryTree();
        } else if ( dir.substring(0,1).equalsIgnoreCase("L") ) {
            lchild.add(string,dir.substring(1));
        } else if ( dir.substring(0,1).equalsIgnoreCase("R") ) {
            rchild.add(string,dir.substring(1));
        } else {
            System.out.println("Error in add of BinaryTree!");
            System.exit(-1);
        }
    }

	public boolean IsQuorum(String [] QuorumSet) {
	    Boolean result = null;
	    Boolean r1,r2,r3 = null;
	    if ( emptyp() ){
	    	System.out.println("Tree is empty");
	    } else {
	    	if(height() == 1){
	    		//visit();
	    		Boolean b = checkQuorumSet(key(),QuorumSet);
	    		//System.out.println("Value of b in IsQuorum function:"+b);
	    		return b;
	    	} else {
	    		if(height() > 1) {
	    			//visit();
	    			//System.out.println(height());
	    			r1 = checkQuorumSet(key(),QuorumSet) && lchild.IsQuorum(QuorumSet); 
	    			r2 = checkQuorumSet(key(),QuorumSet) && rchild.IsQuorum(QuorumSet); 
	    			r3 = lchild.IsQuorum(QuorumSet) && rchild.IsQuorum(QuorumSet);
	    			//System.out.println("Result1:"+r1);
	    			//System.out.println("Result2:"+r2);
	    			//System.out.println("Result3:"+r3);
	    			result = (r1 | r2 | r3);
	    		}
	    	}
	    }
	    return result;
	}	
	    
	public boolean checkQuorumSet(String S,String[] QuorumSet){
		String[] QSet = QuorumSet;
	    if (Arrays.asList(QSet).contains(S)){
	    	return true;
	    } else {
	    	return false;
	    }
	}
}