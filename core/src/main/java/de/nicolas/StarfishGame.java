package de.nicolas;

import de.nicolas.screens.MenuScreen;
import de.nicolas.utils.game.BaseGame;

public class StarfishGame extends BaseGame {

    @Override
    public void create() {
        super.create();
        setActiveScreen(new MenuScreen());
    }
}
