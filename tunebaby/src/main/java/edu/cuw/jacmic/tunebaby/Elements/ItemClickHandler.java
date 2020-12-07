package edu.cuw.jacmic.tunebaby.Elements;

public interface ItemClickHandler {

	public void onClicked();
	// reset the item to its normal state, but do not invoke its click method
	public void reset();
	public void onActivated();
	
}
