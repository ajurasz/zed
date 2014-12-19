File touchFile = new File(basedir, "target/touch.txt");
assert touchFile.isFile()

def files = new File(System.getProperty("user.home") + "/.zed/deploy").list() as List
assert files.contains("guava-18.0.jar")