package application.listeners;

import application.MainController;

public class BatteryListener implements de.yadrone.base.navdata.BatteryListener
{
    private final MainController controller;

    public BatteryListener(MainController controller) {
        this.controller = controller;
    }
    
    @Override
    public void batteryLevelChanged(int i) {
        controller.updateBatteryLabel(i);
    }

    @Override
    public void voltageChanged(int i) { }
}