/*
 * SocialNetworksApp.java
 */

package cl.b9.socialNetwork;

import cl.b9.socialNetwork.gui.SocialNetworksView;
import cl.b9.socialNetwork.persistence.ObjectManager;
import java.util.EventObject;
import org.jdesktop.application.Application;
import org.jdesktop.application.Application.ExitListener;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class SocialNetworksApp extends SingleFrameApplication implements ExitListener {

    SocialNetworksView view;
    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        addExitListener(this);
        view = new SocialNetworksView(this);
        show(view);
        
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of SocialNetworksApp
     */
    public static SocialNetworksApp getApplication() {
        return Application.getInstance(SocialNetworksApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(SocialNetworksApp.class, args);
    }

    public boolean canExit(EventObject event) {
        return ObjectManager.getInstance().canExit();
    }

    public void willExit(EventObject event) {
        ObjectManager.getInstance().shutdown();
    }
}
