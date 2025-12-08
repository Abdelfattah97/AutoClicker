package com.mac.config;

import java.util.HashMap;
import java.util.Map;

public class IconConfig {

    public static Map<String, String> getIconPathMap() {
        Map<String, String> map = new HashMap<>();

        map.put("close-window", "icons/close-button-icon.svg");
        map.put("minimize-window", "icons/minimize-button-icon.svg");
        map.put("maximize-window", "icons/maximize-button-icon.svg");
        map.put("settings", "icons/settings-icon.svg");
        map.put("stop-button", "icons/black-square.svg");
        map.put("start-button", "icons/black-triangle-right.svg");
        map.put("app-icon", "icons/mouse.svg");
        map.put("delete-icon", "icons/trash.svg");

        return map;

    }

}
