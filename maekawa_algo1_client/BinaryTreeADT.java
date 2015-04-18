package maekawa_algo1_client;
/*
 * Abstract Data Type for binary tree 
 */
public interface BinaryTreeADT {
	public boolean emptyp();
    public int height();
    public void add(String s, String dir);
    public void visit();
    public String key();
    public boolean IsQuorum(String[] QuorumSet);
    public boolean checkQuorumSet(String S, String[] QuorumSet);
}
