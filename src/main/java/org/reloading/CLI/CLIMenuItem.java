package org.reloading.CLI;

public class CLIMenuItem implements CLIItem {
    private final String option;
    private final Runnable exe;

    public CLIMenuItem(String option, Runnable exe) {
        this.option = option;
        this.exe = exe;
    }

    @Override
    public void display() {
        System.out.println("\t- " + option);
    }

    public void execute() {
        this.exe.run();
    }
}
