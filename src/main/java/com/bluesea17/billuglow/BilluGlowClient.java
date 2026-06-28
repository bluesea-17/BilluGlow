package com.bluesea17.billuglow;

import net.fabricmc.api.ClientModInitializer;

public class BilluGlowClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BilluGlowCommand.register();
    }
}
