package org.reloading.cli;

import java.util.ArrayList;

public class CLI {
    public static class CLIMenu {
        public static class CLIMenuItem {
            private final String description;
            private final Runnable action;

            public CLIMenuItem(String description, Runnable action) {
                this.description = description;
                this.action = action;
            }

            void run() {
                action.run();
            }
        }

        private final ArrayList<CLIMenuItem> menuItems;

        public CLIMenu() {
            menuItems = new ArrayList<>();
        }

        public CLIMenu(ArrayList<CLIMenuItem> items) {
            menuItems = items;
        }

        public void addMenuItem(String description, Runnable action) {
            this.menuItems.add(new CLIMenuItem(description, action));
        }
    }
}
