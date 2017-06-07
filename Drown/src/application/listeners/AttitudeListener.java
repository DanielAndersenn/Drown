package application.listeners;

import application.MainController;

public class AttitudeListener implements de.yadrone.base.navdata.AttitudeListener
{
    private final MainController controller;

    public AttitudeListener(MainController controller) {
        this.controller = controller;
    }
    
    @Override
    public void attitudeUpdated(float pitch, float roll, float yaw) {
        controller.updateNavigationInfo(pitch, roll, yaw);
    }
    
    @Override
    public void attitudeUpdated(float pitch, float roll) { }
    
    @Override
    public void windCompensation(float pitch, float roll) { }
}