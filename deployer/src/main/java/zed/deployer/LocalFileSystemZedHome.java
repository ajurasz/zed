package zed.deployer;

import java.io.File;

public class LocalFileSystemZedHome implements ZedHome {

    @Override
    public File zedHome() {
        return assureDirectoryExistence(new File(System.getProperty("user.home"), ".zed"));
    }

    @Override
    public File deployDirectory() {
        return assureDirectoryExistence(new File(zedHome(), "deploy"));
    }

    private File assureDirectoryExistence(File directory) {
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directory;
    }

}
