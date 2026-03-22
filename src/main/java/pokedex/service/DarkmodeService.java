package pokedex.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class DarkmodeService
{
    private static final Logger LOGGER = LogManager.getLogger(DarkmodeService.class);

    private boolean darkmode;

    /**
     * Set the dark mode state
     * @param darkmode - true for dark mode, false for light mode
     */
    public void setDarkmode(boolean darkmode) {
    this.darkmode = darkmode;
    //this.applyDarkModeToBody();
}

    /**
     * Get the current dark mode state
     * @returns current dark mode state
     */
    public boolean isDarkmode() {
        return darkmode;
    }

    /**
     * Toggle dark mode on/off
     */
    public void toggleDarkMode() {
        setDarkmode(!darkmode);
    }

}
