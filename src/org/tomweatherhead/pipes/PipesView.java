package org.tomweatherhead.pipes;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class PipesView extends SurfaceView implements SurfaceHolder.Callback {
    // **** Start of pasted Javascript code ****

	// Pipes - Script.js - Javascript - February 5, 2011

	// **** Global Variable Declarations ****

	private static final int nNumDirections = 4;	// Up, right, down, and left (in clockwise order).
	private static final int[] aPowersOfTwo = {1, 2, 4, 8};		// aPowersOfTwo.length == nNumDirections
	private static final int[] adx = {0, 1, 0, -1};             // adx.length == nNumDirections
	private static final int[] ady = {-1, 0, 1, 0};             // ady.length == nNumDirections

	private static final int nMinGridWidth = 2;
	private static final int nMinGridHeight = 2;
	//private static final int nDefaultGridWidth = 8;
	//private static final int nDefaultGridHeight = 8;
	private static final int nMaxGridWidth = 20;
	private static final int nMaxGridHeight = 20;

	private boolean bVictory = false;

	// The values of these variables will be set in setGridDimensions().
	private int nGridWidth = 0;              	//nDefaultGridWidth
	private int nGridHeight = 0;             	//nDefaultGridHeight
	private int nGridArea = 0;               	//nGridWidth * nGridHeight
	private int[] aGridImageNumbers = null;    	//new Array(nGridArea)
	private boolean[] abImageIsGreen = null;	//new Array(nGridArea)

	private static final int[] aRedImageHandles = {
		R.drawable.image0, R.drawable.image1, R.drawable.image2, R.drawable.image3,
		R.drawable.image4, R.drawable.image5, R.drawable.image6, R.drawable.image7,
		R.drawable.image8, R.drawable.image9, R.drawable.image10, R.drawable.image11,
		R.drawable.image12, R.drawable.image13, R.drawable.image14, R.drawable.image15};
	private static final int[] aGreenImageHandles = {
		R.drawable.image16, R.drawable.image17, R.drawable.image18, R.drawable.image19,
		R.drawable.image20, R.drawable.image21, R.drawable.image22, R.drawable.image23,
		R.drawable.image24, R.drawable.image25, R.drawable.image26, R.drawable.image27,
		R.drawable.image28, R.drawable.image29, R.drawable.image30, R.drawable.image31};
	private Drawable[] aRedImages = new Drawable[16];
	private Drawable[] aGreenImages = new Drawable[16];
	
    /** Handle to the surface manager object we interact with */
    private SurfaceHolder mSurfaceHolder;

    private TextView mStatusText = null;

    private String mStandardMessage = "";
    private String mVictoryMessage = "";

    private int mSegmentWidth = 1;
    private int mSegmentHeight = 1;
    
    private int lastStatusTextHeight = 0;

    private ViewGroup mMainLayout = null;
    
	// **** Function Declarations ****

	private void setMessage(String strMessage) {

		//if (mStatusText != null) {
			mStatusText.setText(strMessage);
		//}
	}

	private void prepareForNewGame() {
		// TODO: Use a newline between these sentences: ?
	    //setMessage("Click on a square to rotate the pipe segment within it.  The goal is to connect all of the pipe segments together, so that they all turn green.");
		setMessage(mStandardMessage);
	}

	private void setGridDimensions() {
	    nGridArea = nGridWidth * nGridHeight;
	    aGridImageNumbers = new int[nGridArea];
	    abImageIsGreen = new boolean[nGridArea];
	}

	private void createSolution() {
	    int[] aBlobNumbers = new int[nGridArea];
	    int[] aOpenList = new int[nGridArea];
	    int openListLength = aOpenList.length;
	    int[] aDirectionIndices = new int[nNumDirections];    // Number of directions == 4
	    int numConnections = 0;

	    for (int i = 0; i < aGridImageNumbers.length; i++) {
	        aGridImageNumbers[i] = 0;
	    }

	    for (int i = 0; i < aBlobNumbers.length; i++) {
	        aBlobNumbers[i] = i;
	    }

	    for (int i = 0; i < aOpenList.length; i++) {
	        aOpenList[i] = i;
	    }

	    while (numConnections < nGridArea - 1) {
	        // Randomly select a member of the open list.
	        int openListIndex = (int)(Math.random() * openListLength);
	        int openListElement = aOpenList[openListIndex];
	        int blobNumber1 = aBlobNumbers[openListElement];
	        int row1 = openListElement / nGridWidth;
	        int col1 = openListElement % nGridWidth;

	        for (int i = 0; i < aDirectionIndices.length; i++) {
	            aDirectionIndices[i] = i;
	        }

	        boolean connectionCreatedDuringThisPass = false;
	        int numDirectionIndices = aDirectionIndices.length;

	        while (numDirectionIndices > 0 && !connectionCreatedDuringThisPass) {
	            int j = (int)(Math.random() * numDirectionIndices);
	            int directionIndex = aDirectionIndices[j];

	            numDirectionIndices--;
	            aDirectionIndices[j] = aDirectionIndices[numDirectionIndices];

	            int dx = adx[directionIndex];
	            int dy = ady[directionIndex];
	            int row2 = row1 + dy;
	            int col2 = col1 + dx;

	            if (row2 < 0 || row2 >= nGridHeight || col2 < 0 || col2 >= nGridWidth) {
	                continue;
	            }

	            int index2 = row2 * nGridWidth + col2;
	            int blobNumber2 = aBlobNumbers[index2];

	            if (blobNumber1 == blobNumber2) {
	                continue;
	            }

	            // Create the new connection.

	            aGridImageNumbers[openListElement] += aPowersOfTwo[directionIndex];
	            aGridImageNumbers[index2] += aPowersOfTwo[directionIndex ^ 2];  // TODO: Question: Is ^ the bitwise XOR operator in Java?

	            numConnections++;
	            connectionCreatedDuringThisPass = true;

	            int minBlobNumber = Math.min(blobNumber1, blobNumber2);
	            int maxBlobNumber = Math.max(blobNumber1, blobNumber2);

	            for (int i = 0; i < aBlobNumbers.length; i++) {

	                if (aBlobNumbers[i] == maxBlobNumber) {
	                    aBlobNumbers[i] = minBlobNumber;
	                }
	            }

	            // When the grid is fully constructed, all of the blob numbers will be 0.
	            // In other words, every square in the grid will be a member of blob number 0.
	        }

	        if (!connectionCreatedDuringThisPass) {
	            // The element at (row1, col1) has no neighbour belonging to a different blob;
	            // therefore we will remove it from the open list.

	            openListLength--;
	            aOpenList[openListIndex] = aOpenList[openListLength];
	        }
	    }
	}

	private void randomlyRotateImages() {
	    // Blank: 0
	    // i: 1, 2, 4, 8
	    // I: 5, 10
	    // L: 3, 6, 9, 12
	    // T: 7, 11, 13, 14
	    // +: 15
	    int[][] aaRotatedIndices = {
	        {0},            // 0
	        {2, 4, 8},      // 1
	        {1, 4, 8},      // 2
	        {6, 9, 12},     // 3
	        {1, 2, 8},      // 4
	        {10},           // 5
	        {3, 9, 12},     // 6
	        {11, 13, 14},   // 7
	        {1, 2, 4},      // 8
	        {3, 6, 12},     // 9
	        {5},            // 10
	        {7, 13, 14},    // 11
	        {3, 6, 9},      // 12
	        {7, 11, 14},    // 13
	        {7, 11, 13},    // 14
	        {15}            // 15
	    };

	    for (int i = 0; i < aGridImageNumbers.length; i++) {
	        int[] aRotationOptions = aaRotatedIndices[aGridImageNumbers[i]];
	        int j = (int)(Math.random() * aRotationOptions.length);
	        int rotatedIndex = aRotationOptions[j];

	        aGridImageNumbers[i] = rotatedIndex;
	    }
	}

	private void setGridImageNumbers() {
	    createSolution();
	    randomlyRotateImages();
	}

	private int findGreenSubtree(int row1, int col1) {
	    // Unnecessary.
	    //if (row1 < 0 || row1 >= nGridHeight || col1 < 0 || col1 >= nGridWidth) {
	    //    return 0
	    //}

	    int index1 = row1 * nGridWidth + col1;

	    if (abImageIsGreen[index1]) {   // Avoid infinite loops.
	        return 0;
	    }

	    abImageIsGreen[index1] = true;

	    int numGreenImagesInSubtree = 1;
	    int image1 = aGridImageNumbers[index1];

	    for (int i = 0; i < nNumDirections; i++) {
	        int row2 = row1 + ady[i];
	        int col2 = col1 + adx[i];

	        if (row2 < 0 || row2 >= nGridHeight || col2 < 0 || col2 >= nGridWidth) {
	            continue;
	        }

	        int index2 = row2 * nGridWidth + col2;
	        int image2 = aGridImageNumbers[index2];

	        if ((image1 & aPowersOfTwo[i]) != 0 && (image2 & aPowersOfTwo[i ^ 2]) != 0) {
	            // There is a connection between the square at (row1, col1) and the square at (row2, col2).
	            numGreenImagesInSubtree += findGreenSubtree(row2, col2);
	        }
	    }

	    return numGreenImagesInSubtree;
	}

	private int findGreenTree() {

	    for (int i = 0; i < abImageIsGreen.length; i++) {
	        abImageIsGreen[i] = false;
	    }

	    return findGreenSubtree(nGridHeight / 2, nGridWidth / 2);
	}

	private void constructGrid() {
	    prepareForNewGame();
	    setGridDimensions();
	    setGridImageNumbers();
	    findGreenTree();
	}

	private int rolNybble(int nybble) {
	    nybble %= 16;    // For safety.  Probably unnecessary.
	    nybble *= 2;

	    if (nybble >= 16) { // The "carry" bit is 1.
	        nybble -= 16;    // To remove the bit that has been rotated into the "carry" bit.
	        nybble++;        // To set bit 0 of the nybble from the "carry" bit.
	    }

	    return nybble;
	}

	private int setImageSourcesAfterClick() {
	    int numGreenImages = findGreenTree();
	    
	    renderBitmap();
	    
	    return numGreenImages;
	}

	private void imageClicked(int index) {

	    if (bVictory) {
	        // Start a new game.
	        bVictory = false;
	        prepareForNewGame();
	        setGridImageNumbers();
	        setImageSourcesAfterClick();
	        return;
	    }

	    aGridImageNumbers[index] = rolNybble(aGridImageNumbers[index]);

	    if (setImageSourcesAfterClick() == nGridArea) {
	        bVictory = true;
	        //setMessage("Victory!  Click on any square to start a new puzzle.");
	        setMessage(mVictoryMessage);
	    }
	}

	/*
	function resizeGrid() {
	    var tbWidth = document.getElementById("txtWidth")
	    var tbHeight = document.getElementById("txtHeight")
	    var strWidth = tbWidth.value
	    var strHeight = tbHeight.value
	    var nWidth = nDefaultGridWidth
	    var nHeight = nDefaultGridHeight

	    if (strWidth != null && strWidth != "") {
	        nWidth = parseInt(strWidth, 10)

	        if (isNaN(nWidth)) {
	            alert("Error: Width is not a number")
	            return
	        }

	        if (nWidth < nMinGridWidth) {
	            alert("Warning: Minimum width is " + nMinGridWidth)
	            nWidth = nMinGridWidth
	        } else if (nWidth > nMaxGridWidth) {
	            alert("Warning: Maximum width is " + nMaxGridWidth)
	            nWidth = nMaxGridWidth
	        }
	    }

	    if (strHeight != null && strHeight != "") {
	        nHeight = parseInt(strHeight, 10)

	        if (isNaN(nHeight)) {
	            alert("Error: Height is not a number")
	            return
	        }

	        if (nHeight < nMinGridHeight) {
	            alert("Warning: Minimum height is " + nMinGridHeight)
	            nHeight = nMinGridHeight
	        } else if (nHeight > nMaxGridHeight) {
	            alert("Warning: Maximum height is " + nMaxGridHeight)
	            nHeight = nMaxGridHeight
	        }
	    }

	    window.location = "index.html?GridWidth=" + nWidth + "&GridHeight=" + nHeight;
	}
	*/

    // **** End of pasted Javascript code ****

    public PipesView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Register our interest in hearing about changes to our surface.
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        
        loadResources(context);

        setFocusable(true); // make sure we get key events
    }

    private void loadResources(Context context) {
        Resources res = context.getResources();

    	for (int i = 0; i < 16; ++i) {
           	aRedImages[i] = res.getDrawable(aRedImageHandles[i]);
           	aGreenImages[i] = res.getDrawable(aGreenImageHandles[i]);
    	}
    	
        mSegmentWidth = aRedImages[0].getIntrinsicWidth();
        mSegmentHeight = aRedImages[0].getIntrinsicHeight();
        
    	mStandardMessage = (String) res.getText(R.string.standard_message);
    	mVictoryMessage = (String) res.getText(R.string.victory_message);
    }
    
    public void renderBitmap() {
            Canvas c = null;
            Drawable image = null;
            int index = 0;
            
            try {
                c = mSurfaceHolder.lockCanvas(null);

                int yTop = 0;
                
                for (int y = 0; y < nGridHeight; ++y) {
                	int xLeft = 0;
                	
                	for (int x = 0; x < nGridWidth; ++x) {
                		int nybble = aGridImageNumbers[index];
                		
                		if (abImageIsGreen[index]) {
                			image = aGreenImages[nybble];
                		} else {
                			image = aRedImages[nybble];
                		}
                		
                        image.setBounds(xLeft, yTop, xLeft + mSegmentWidth, yTop + mSegmentHeight);
                        image.draw(c);
                        xLeft += mSegmentWidth;
                        ++index;
                	}
                	
                	yTop += mSegmentHeight;
                }
            } finally {
                // do this in a finally so that if an exception is thrown
                // during the above, we don't leave the Surface in an
                // inconsistent state
                if (c != null) {
                    mSurfaceHolder.unlockCanvasAndPost(c);
            }
        }
    }

    /**
     * Installs a pointer to the text view used for messages.
     */
    public void setTextView(TextView textView) {
        mStatusText = textView;
    }

    public void updateStatusTextHeight() {
    	int h = mStatusText.getHeight();
    	
    	if (h != lastStatusTextHeight) {
    		lastStatusTextHeight = h;
    		mMainLayout.invalidate();
    	}
    }
    
    public void setMainLayout(ViewGroup mainLayout) {
    	mMainLayout = mainLayout;
    }
    
    /* Callback invoked when the surface dimensions change. */
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    	nGridWidth = width / mSegmentWidth;
    	nGridHeight = height / mSegmentHeight;
        constructGrid();
    	renderBitmap();
    }
    
    /*
     * Callback invoked when the Surface has been created and is ready to be
     * used.
     */
    public void surfaceCreated(SurfaceHolder holder) {
    	/*
    	nGridWidth = nDefaultGridWidth;
    	nGridHeight = nDefaultGridHeight;
        constructGrid();
     	renderBitmap();
     	*/
    }

    /*
     * Callback invoked when the Surface has been destroyed and must no longer
     * be touched. WARNING: after this method returns, the Surface/Canvas must
     * never be touched again!
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    public void doTouchDown(int x, int y) {
    	int location[] = new int[2];
    	
    	getLocationOnScreen(location);
    	
    	// Subtract the view's offset from x and y.
    	x -= location[0];
    	y -= location[1];
    	
    	if (x >= 0 && x < nGridWidth * mSegmentWidth &&
    			y >= 0 && y < nGridHeight * mSegmentHeight) {
        	int col = x / mSegmentWidth;
        	int row = y / mSegmentHeight;
        	int index = row * nGridWidth + col;
        	
        	imageClicked(index);
    	}
    }

    @Override 
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	/*
    	 * Ensure that the MandelbrotView does not push the TextView or
    	 * the RelativeLayout containing the buttons off of the bottom of the screen.
    	 * To do this, we must measure the heights of those two other items,
    	 * and then subtract those heights from parentHeight below.
    	 * Also, account for the top margin of the MandelbrotView.
    	 */
    	LayoutParams lp = (LayoutParams) getLayoutParams();
    	int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
    	int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
    	
    	parentWidth -= lp.leftMargin + lp.rightMargin;
    	parentHeight -= lp.topMargin + lp.bottomMargin + lastStatusTextHeight;

    	int newGridWidth = parentWidth / mSegmentWidth;
    	int newGridHeight = parentHeight / mSegmentHeight;
    	
    	if (newGridWidth < nMinGridWidth) {
    		newGridWidth = nMinGridWidth;
    	} else if (newGridWidth > nMaxGridWidth) {
    		newGridWidth = nMaxGridWidth;
    	}
    	
    	if (newGridHeight < nMinGridHeight) {
    		newGridHeight = nMinGridHeight;
    	} else if (newGridHeight > nMaxGridHeight) {
    		newGridHeight = nMaxGridHeight;
    	}

    	this.setMeasuredDimension(newGridWidth * mSegmentWidth, newGridHeight * mSegmentHeight);
    }
}
