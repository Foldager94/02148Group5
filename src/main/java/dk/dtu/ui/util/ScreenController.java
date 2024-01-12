package dk.dtu.ui.util;

import dk.dtu.ui.StartScreen;

public class ScreenController {
    	// Constructor is used to receive Model and View
    ScreenSize screenSize;
    StartScreen startScreen;

	public ScreenController(ScreenSize screenSize, StartScreen startScreen) {
		this.screenSize = screenSize;
		this.startScreen = startScreen;
	}

    public StartScreen getStartSceeen() {
		return startScreen;
	}
    
}
