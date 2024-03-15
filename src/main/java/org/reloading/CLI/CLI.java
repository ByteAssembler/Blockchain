package org.reloading.CLI;

import java.io.IOException;

public class CLI {
    public static void main(String[] args) {
        CLIController controller = new CLIController();
        CLIView view = new CLIView();

        while (!controller.isStopped()) {
            view.display(controller.getMenu());
            try {
                controller.select(view);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
