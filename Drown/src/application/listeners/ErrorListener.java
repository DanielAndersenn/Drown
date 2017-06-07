package application.listeners;

import application.MainController;
import de.yadrone.base.exception.ARDroneException;
import de.yadrone.base.exception.IExceptionListener;

public class ErrorListener implements IExceptionListener
{
	
	private final MainController controller;
	
    public ErrorListener(MainController controller) {
        this.controller = controller;
    }
	
    @Override
    public void exeptionOccurred(ARDroneException e) {
       controller.updateDroneError(e);
    }
}
