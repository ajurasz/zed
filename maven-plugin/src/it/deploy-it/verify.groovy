def files = new File(System.getProperty("user.home") + "/.zed/deploy").list() as List
assert files.contains("guava-18.0.jar")