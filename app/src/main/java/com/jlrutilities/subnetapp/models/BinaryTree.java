package com.jlrutilities.subnetapp.models;

import android.os.Parcel;
import android.os.Parcelable;


public class BinaryTree implements Parcelable {

    private Node root;
    private int flag = 0;


    public BinaryTree(){ root = null; }


    public Node getRoot() { return root; }


    public void setRoot(int cidr, String ipBinary, String ipAddress, int numberOfHosts, String broadcastIp, String fullIpRange, String usableIpRange, String netmask) {
        root = new Node(cidr, ipBinary, ipAddress, numberOfHosts, broadcastIp, fullIpRange, usableIpRange, netmask);
    }


    //** Find Nth node starting with root. */
    private Node nthPreorderNode(Node node, int N) {

        if (node == null) { return null; }

        if (flag <= N) {
            flag++;

            // returns the n-th node of preorder traversal
            if (flag == N) { return node; }

            // left-side traversal
            Node returnedNode = nthPreorderNode(node.getLeft(), N);
            if (returnedNode != null) { return returnedNode; }

            // right-side traversal
            returnedNode = nthPreorderNode(node.getRight(), N);
            if (returnedNode != null) { return returnedNode; }
        }
        return null;
    }


    //** Calculates size of binary tree. */
    private void size(Node node) {
        if (node == null) { return; }

        flag ++;

        size(node.getLeft());
        size(node.getRight());
    }


    //** Calculates size of nodes with no children. */
    private void sizeBottomLayer(Node node){
        if(node == null){
            return;
        }

        if (node.getLeft() == null && node.getRight() == null){
            flag++;
        }

        sizeBottomLayer(node.getLeft());
        sizeBottomLayer(node.getRight());
    }


    //** Finds parent node of child node. */
    public Node findParent(Node node, Node child){
        if (node == null) { return null; }

        // returns if the current node is a parent of target child node
        if (node.getLeft() == child || node.getRight() == child){
            return node;
        }

        // left-side traversal
        Node returnedNode = findParent(node.getLeft(), child);
        if (returnedNode != null) { return returnedNode; }

        // right-side traversal
        returnedNode = findParent(node.getRight(), child);
        if (returnedNode != null) { return returnedNode; }

        return null;
    }


    //** Sets child nodes to null up to relative root node. */
    public void merge(Node node){
        if (node == null) {
            return;
        }

        Node left = node.getLeft();
        Node right = node.getRight();

        if ( left == null && right == null) { return; }

        merge(node.getLeft());
        merge(node.getRight());

        node.setChildrenNull();
    }


    //** Counts number of tree nodes. */
    public int size() {
        size(root);
        int value = flag;
        flag = 0;
        return value;
    }


    //** Counts number of tree nodes w/o children. */
    public int sizeBottomLayer() {
        sizeBottomLayer(root);
        int value = flag;
        flag = 0;
        return value;
    }


    //** Returns nthPreorderNode using preorder traversal from root node. */
    public Node nthPreorderNode(int N) {
        Node node = nthPreorderNode(root, N);
        flag = 0;
        return node;
    }

    //** Returns parent node of child node. */
    public Node findParent(Node child) {
        Node node = findParent(root, child);
        return node;
    }


    //** Transcribes Binary Tree structure for saving and reloading during view transitions; */
    protected BinaryTree(Parcel in) {
        root = (Node) in.readValue(Node.class.getClassLoader());
        flag = in.readInt();
    }


    @Override
    public int describeContents() {
        return 0;
    }


    //** Writes base tree structure to Parcel. */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(root);
        dest.writeInt(flag);
    }


    //** Implements Parcel structure for Binary Tree. */
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<BinaryTree> CREATOR = new Parcelable.Creator<BinaryTree>() {
        @Override
        public BinaryTree createFromParcel(Parcel in) {
            return new BinaryTree(in);
        }


        @Override
        public BinaryTree[] newArray(int size) {
            return new BinaryTree[size];
        }
    };
}