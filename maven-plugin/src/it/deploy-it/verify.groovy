def filesInMyWorkspace = new File(System.getProperty("user.home") + "/.zed/deploy/myworkspace").list() as List
assert filesInMyWorkspace.contains("guava-18.0.jar")