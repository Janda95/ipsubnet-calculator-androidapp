package com.jlrutilities.subnetapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.jlrutilities.subnetapp.adapters.IpArrayAdapter;
import com.jlrutilities.subnetapp.fragments.DetailFragment;
import com.jlrutilities.subnetapp.fragments.HelpDialogFragment;
import com.jlrutilities.subnetapp.models.BinaryTree;
import com.jlrutilities.subnetapp.models.Node;
import com.jlrutilities.subnetapp.models.SubnetCalculator;
import com.jlrutilities.subnetapp.R;

import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeDirection;


//** Creates and populates splitter activity view, providing a user interface for rapid subnet splitting. */
public class SplitterActivity extends AppCompatActivity {

    private static final String MY_TREE = "my_tree";
    SubnetCalculator subnetCalc;
    BinaryTree tree;

    private Node[] nodes;
    private String[] nodeIps;
    private int[] nodeLocations = null;
    private int[] cidrArr;
    private int[] numOfHostsArr;
    private int pos = -1;

    private ListView list;
    SwipeActionAdapter mAdapter;
    private boolean mTwoPane;


    //** Creates and populates view on creation, phone rotation, and 2 pane view with larger screen size devices*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splitter);
        subnetCalc = new SubnetCalculator();

        // Check if MasterDetailView Available
        ViewGroup fragmentContainer = findViewById(R.id.fragment_detail_container);
        if (fragmentContainer != null) {
            mTwoPane = true;
        }

        // Saved tree instance on phone rotation
        if(savedInstanceState == null){
            Intent intent = getIntent();
            String ipFormatted = intent.getStringExtra(MainActivity.IP_STRING_MESSAGE);
            String cidrString = intent.getStringExtra(MainActivity.CIDR_NETMASK_MESSAGE);

            int cidr = Integer.parseInt(cidrString);
            String ipBinary = subnetCalc.ipFormatToBinary(ipFormatted);

            int numOfHosts = subnetCalc.numberOfHosts(cidr);
            String netmask = subnetCalc.subnetMask( cidr );
            String range = subnetCalc.rangeOfAddresses(ipBinary, cidr);
            String usableRange = subnetCalc.usableIpAddresses(ipBinary, cidr);

            String broadcast = subnetCalc.broadcastAddress(ipBinary, cidr);

            tree = new BinaryTree();
            tree.setRoot(cidr, ipBinary, ipFormatted, numOfHosts, broadcast, range, usableRange , netmask);

        } else {
            tree = savedInstanceState.getParcelable(MY_TREE);
        }

        list = findViewById(R.id.android_list);
        refreshList();

        // List OnClick Listener Action
        list.setOnItemClickListener((parent, view, position, id) -> {
            if(mTwoPane){
                FragmentManager fragmentManager = getSupportFragmentManager();

                int refPosition = nodeLocations[position];
                Node node = nodes[refPosition];

                DetailFragment fragment = DetailFragment.newInstance(node);
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_detail_container, fragment)
                        .commit();

            } else {
                // Transition to Description Activity
                Intent intent1 = new Intent(getApplicationContext(), DescriptionActivity.class);

                int refPosition = nodeLocations[position];
                Node node = nodes[refPosition];
                intent1.putExtra("node_key", node);

                startActivity(intent1);
            }
        });
    }


    //** Inflate available menu options if present in view. */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.splitter_menu, menu);
        return true;
    }


    //** Saves network tree state for photo rotation and returning to view. */
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable(MY_TREE, tree);
    }


    //** Finds parent node and removes all node branches and current node if applicable. */
    private void merge(){
        int refPosition = nodeLocations[pos];
        Node node = nodes[refPosition];

        if ( node == tree.getRoot() ){
            Toast.makeText(getApplicationContext(),"Cannot merge root",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Node parent = tree.findParent(node);

        if (parent == null){
            Toast.makeText(getApplicationContext(),"Unable to merge",
                    Toast.LENGTH_SHORT).show();
            return;

        } else {
            tree.merge(parent);
            refreshList();
            Toast.makeText(getApplicationContext(),"Merged",
                    Toast.LENGTH_SHORT).show();
        }
    }


    //** Splits current list item node to 2 subnet nodes and refreshes list. */
    private void split(){
        int refPosition = nodeLocations[pos];
        Node node = nodes[refPosition];
        int cidr = node.getCidr();
        String binaryIp = node.getIpBinary();

        if (node.getLeft() == null && node.getRight() == null && cidr != 32) {
            // Split current node and set child nodes
            String splitIp = subnetCalc.ipSplit(node.getIpBinary(), cidr);
            String formatIp = subnetCalc.ipBinaryToFormat(splitIp);
            int numOfHosts = subnetCalc.numberOfHosts(cidr+1);
            String newNetmask = subnetCalc.subnetMask( cidr+1 );

            // Left Split
            String leftRange = subnetCalc.rangeOfAddresses(binaryIp, cidr+1);
            String leftUsableRange = subnetCalc.usableIpAddresses(binaryIp, cidr+1);
            String leftBroadcast = subnetCalc.broadcastAddress(binaryIp, cidr+1);

            // Right Split
            String rightRange = subnetCalc.rangeOfAddresses(splitIp, cidr+1);
            String rightUsableRange = subnetCalc.usableIpAddresses(splitIp, cidr+1);
            String rightBroadcast = subnetCalc.broadcastAddress(splitIp, cidr+1);

            node.setLeft(cidr+1, node.getIpBinary(), node.getIpAddress(), numOfHosts, leftBroadcast, leftRange, leftUsableRange, newNetmask);
            node.setRight(cidr+1, splitIp, formatIp, numOfHosts, rightBroadcast, rightRange, rightUsableRange, newNetmask );

            refreshList();
            Toast.makeText(getApplicationContext(),"Split",
                    Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(getApplicationContext(),"Cannot Split /32",
                    Toast.LENGTH_SHORT).show();
        }
    }


    //** Refreshes list using current available tree structure. */
    private void refreshList(){
        // Bottom Layer Nodes
        nodes = new Node[tree.size()];
        nodeIps = new String[tree.sizeBottomLayer()];
        nodeLocations = new int[nodeIps.length];
        cidrArr = new int[nodeIps.length];
        numOfHostsArr = new int[nodeIps.length];

        for(int i = 0; i < nodes.length; i++){
            nodes[i] = tree.nthPreorderNode(i+1);
        }

        int counter = 0;
        for(int i = 0; i < nodes.length; i++) {
            Node node = nodes[i];
            if(node.getLeft() == null && node.getRight() == null){
                nodeIps[counter] = node.getIpAddress();
                cidrArr[counter] = node.getCidr();
                numOfHostsArr[counter] = node.getNumberOfHosts();
                nodeLocations[counter] = i;
                counter++;
            }
        }

        ArrayAdapter aa = new IpArrayAdapter(this, nodeIps, cidrArr, numOfHostsArr);
        mAdapter = new SwipeActionAdapter(aa);
        mAdapter.setListView(list);
        list.setAdapter(mAdapter);

        setDefaultSwipeFunctionality();
    }


    //* Sets actions when swiping list items. */
    /**
     * Sets actions when swiping list items.
     */
    private void setDefaultSwipeFunctionality(){
        // Background colors during swiping animation
        mAdapter.addBackground(SwipeDirection.DIRECTION_NORMAL_LEFT,R.layout.row_bg_left)
                .addBackground(SwipeDirection.DIRECTION_NORMAL_RIGHT,R.layout.row_bg_right);

        mAdapter.setSwipeActionListener(new SwipeActionAdapter.SwipeActionListener(){
            @Override
            public boolean hasActions(int position, SwipeDirection direction){
                return direction.isLeft() || direction.isRight();
            }


            @Override
            public boolean shouldDismiss(int position, SwipeDirection direction) {
                switch (direction) {
                    case DIRECTION_NORMAL_LEFT:
                    case DIRECTION_FAR_LEFT:
                    case DIRECTION_NORMAL_RIGHT:
                    case DIRECTION_FAR_RIGHT:
                        return true;
                    case DIRECTION_NEUTRAL:
                    default:
                        return false;
                }

            }


            @Override
            public void onSwipe(int[] positionList, SwipeDirection[] directionList) {
                for(int i=0;i<positionList.length;i++) {
                    SwipeDirection direction = directionList[i];
                    int position = positionList[i];

                    switch (direction) {
                        case DIRECTION_NORMAL_LEFT:
                        case DIRECTION_FAR_LEFT:
                            pos = position;
                            merge();
                            break;
                        case DIRECTION_NORMAL_RIGHT:
                        case DIRECTION_FAR_RIGHT:
                            pos = position;
                            split();
                            break;
                        default:
                            break;
                    }
                }
            }
        });
    }


    /**
     * Provides help dialog.
     *
     * @param item The menu item that triggered this action
     */
    public void displayHelpDialog(MenuItem item) {
        HelpDialogFragment dialogFragment = new HelpDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "DIALOG_FRAGMENT");
    }
}
