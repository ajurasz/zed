import java.nio.file.Paths

import static org.apache.commons.lang3.SystemUtils.USER_HOME

def filesInMyWorkspace = Paths.get(USER_HOME, ".zed", "deploy", "myworkspace").toFile().list() as List
assert filesInMyWorkspace.contains("guava-18.0.jar")