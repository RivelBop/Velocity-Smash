package com.rivelbop.velocitysmash;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import static com.rivelbop.velocitysmash.VelocitySmash.HEIGHT;
import static com.rivelbop.velocitysmash.VelocitySmash.WIDTH;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.useVsync(true);
        config.setWindowedMode(WIDTH, HEIGHT);
        config.setTitle("Velocity Smash");
        new Lwjgl3Application(new VelocitySmash(), config);
    }
}