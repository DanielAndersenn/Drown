package application.listeners;

import application.MainController;
import javafx.application.Platform;

public class BatteryListener implements de.yadrone.base.navdata.BatteryListener
{
    private final MainController controller;

    public BatteryListener(MainController controller) {
        this.controller = controller;
    }
    
    @Override
    public void batteryLevelChanged(int i) {
    	Platform.runLater(new Runnable() {
    		
    		@Override
    		public void run() {
    			controller.updateBatteryLabel(i);
    		}
    	});
    }

    @Override
    public void voltageChanged(int i) { }
}