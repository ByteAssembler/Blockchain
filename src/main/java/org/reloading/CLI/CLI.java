package org.reloading.CLI;

public class CLI {
    public static void main(String[] args) {
        CLIController controller = new CLIController();
        CLIView view = new CLIView();

        while (!controller.isStopped()) {
            view.display(controller.getMenu());
            controller.select(view);
        }
    }
}
